package com.hau.labhau.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hau.labhau.dto.request.CreateApplicationRequest;
import com.hau.labhau.dto.response.ApplicationResponse;
import com.hau.labhau.dto.response.FileInfoResponse;
import com.hau.labhau.entity.Application;
import com.hau.labhau.entity.FileEntity;
import com.hau.labhau.entity.User;
import com.hau.labhau.exception.BadRequestException;
import com.hau.labhau.exception.FileStorageException;
import com.hau.labhau.exception.ResourceNotFoundException;
import com.hau.labhau.mapper.ApplicationMapper;
import com.hau.labhau.mapper.FileMapper;
import com.hau.labhau.repository.ApplicationRepository;
import com.hau.labhau.repository.FileRepository;
import com.hau.labhau.repository.UserRepository;
import com.hau.labhau.util.SecurityUtils;
import jakarta.annotation.PostConstruct;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Set;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class FileService {

    private final FileRepository fileRepository;
    private final FileMapper fileMapper;
    private final UserRepository userRepository;
    private final ApplicationRepository applicationRepository;
    private final ApplicationService applicationService;
    private final ApplicationMapper applicationMapper;
    private final ObjectMapper objectMapper;
    private final Validator validator;

    @Value("${app.upload.dir:${user.home}/labhau/uploads}")
    private String uploadDir;

    private Path uploadPath;

    @PostConstruct
    public void init() {
        uploadPath = Paths.get(uploadDir).toAbsolutePath().normalize();
        try {
            Files.createDirectories(uploadPath);
            log.info("Upload directory created at: {}", uploadPath);
        } catch (IOException e) {
            throw new FileStorageException("Could not create upload directory: " + uploadPath, e);
        }
    }

    @Transactional
    public FileInfoResponse upload(MultipartFile file) {
        String contentType = file.getContentType();
        if (contentType == null || (!contentType.equals("application/json") && !contentType.equals("application/octet-stream"))) {
            throw new BadRequestException("Файл должен быть в формате JSON");
        }

        String originalName = file.getOriginalFilename();
        if (originalName == null || originalName.isBlank()) {
            throw new FileStorageException("Имя файла обязательно");
        }

        if (!originalName.toLowerCase().endsWith(".json")) {
            throw new BadRequestException("Файл должен быть с расширением .json");
        }

        String jsonContent;
        try {
            jsonContent = new String(file.getBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new FileStorageException("Не удалось прочитать файл", e);
        }

        CreateApplicationRequest createRequest;
        try {
            createRequest = objectMapper.readValue(jsonContent, CreateApplicationRequest.class);
        } catch (IOException e) {
            throw new BadRequestException("Некорректный JSON: " + e.getMessage());
        }

        Set<ConstraintViolation<CreateApplicationRequest>> violations = validator.validate(createRequest);
        if (!violations.isEmpty()) {
            StringBuilder sb = new StringBuilder("Ошибки валидации: ");
            for (var v : violations) {
                sb.append(v.getPropertyPath()).append(": ").append(v.getMessage()).append("; ");
            }
            throw new BadRequestException(sb.toString());
        }

        var currentUserDetails = SecurityUtils.currentUser();
        ApplicationResponse createdApp = applicationService.create(createRequest, currentUserDetails.getUserId());

        Application application = applicationRepository.findById(createdApp.id())
                .orElseThrow(() -> new ResourceNotFoundException("Заявка не найдена"));

        String storedName = UUID.randomUUID() + "_" + originalName.replaceAll("[^a-zA-Z0-9._\\-]", "_");
        Path targetLocation = uploadPath.resolve(storedName);

        try {
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new FileStorageException("Не удалось сохранить файл " + originalName, e);
        }

        User currentUser = userRepository.findById(currentUserDetails.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("Пользователь не найден"));

        FileEntity fileEntity = FileEntity.builder()
                .originalName(originalName)
                .storedName(storedName)
                .contentType("application/json")
                .size(file.getSize())
                .storagePath(targetLocation.toString())
                .uploadedBy(currentUser)
                .application(application)
                .build();

        fileEntity = fileRepository.save(fileEntity);
        log.info("File uploaded: {} (id={}) for application id={}", originalName, fileEntity.getId(), application.getId());
        return fileMapper.toResponse(fileEntity);
    }

    @Transactional(readOnly = true)
    public Resource downloadByApplicationId(UUID applicationId) {
        Application application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new ResourceNotFoundException("Заявка не найдена"));

        ApplicationResponse appResponse = applicationMapper.toResponse(application);

        String json;
        try {
            json = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(appResponse);
        } catch (IOException e) {
            throw new FileStorageException("Не удалось сгенерировать JSON для заявки", e);
        }

        String fileName = "application_" + applicationId + ".json";
        Path tempFile = uploadPath.resolve("temp_" + UUID.randomUUID() + "_" + fileName);
        try {
            Files.writeString(tempFile, json, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new FileStorageException("Не удалось создать временный файл", e);
        }

        try {
            Resource resource = new UrlResource(tempFile.toUri());
            if (resource.exists() && resource.isReadable()) {
                return resource;
            } else {
                throw new FileStorageException("Не удалось прочитать сгенерированный файл");
            }
        } catch (MalformedURLException e) {
            throw new FileStorageException("Ошибка при создании ресурса", e);
        }
    }

    @Transactional
    public void delete(UUID fileId) {
        FileEntity fileEntity = fileRepository.findById(fileId)
                .orElseThrow(() -> new ResourceNotFoundException("Файл не найден"));

        Path filePath = Paths.get(fileEntity.getStoragePath()).normalize();
        try {
            Files.deleteIfExists(filePath);
        } catch (IOException e) {
            log.warn("Could not delete physical file: {}", filePath, e);
        }

        fileRepository.delete(fileEntity);
        log.info("File deleted: {} (id={})", fileEntity.getOriginalName(), fileId);
    }

    @Transactional(readOnly = true)
    public FileInfoResponse getInfo(UUID id) {
        return fileMapper.toResponse(findEntityById(id));
    }

    private FileEntity findEntityById(UUID id) {
        return fileRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Файл не найден"));
    }
}