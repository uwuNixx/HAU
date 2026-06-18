package com.hau.labhau.service;

import com.hau.labhau.dto.request.ApplicationFilterRequest;
import com.hau.labhau.dto.request.CreateApplicationRequest;
import com.hau.labhau.dto.request.UpdateApplicationStatusRequest;
import com.hau.labhau.dto.response.ApplicationPageResponse;
import com.hau.labhau.dto.response.ApplicationResponse;
import com.hau.labhau.dto.response.ApplicationStatsResponse;
import com.hau.labhau.entity.*;
import com.hau.labhau.exception.BadRequestException;
import com.hau.labhau.exception.ForbiddenException;
import com.hau.labhau.exception.ResourceNotFoundException;
import com.hau.labhau.mapper.ApplicationMapper;
import com.hau.labhau.repository.ApplicationRepository;
import com.hau.labhau.repository.ApartmentRepository;
import com.hau.labhau.repository.ServiceCategoryRepository;
import com.hau.labhau.repository.UserRepository;
import com.hau.labhau.repository.specification.ApplicationSpecification;
import com.hau.labhau.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ApplicationService {

    private final ApplicationRepository applicationRepository;
    private final ApartmentRepository apartmentRepository;
    private final ServiceCategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final ApplicationMapper applicationMapper;

    @Transactional(readOnly = true)
    public ApplicationPageResponse findAll(ApplicationFilterRequest filter) {
        return toPage(applicationRepository.findAll(buildSpec(filter), pageable(filter)));
    }

    @Transactional(readOnly = true)
    public ApplicationPageResponse findByAuthor(UUID authorId, ApplicationFilterRequest filter) {
        Specification<Application> spec = buildSpec(filter)
                .and(ApplicationSpecification.hasAuthorId(authorId));
        return toPage(applicationRepository.findAll(spec, pageable(filter)));
    }

    @Transactional(readOnly = true)
    public ApplicationPageResponse findByBuilding(UUID buildingId, ApplicationFilterRequest filter) {
        Specification<Application> spec = buildSpec(filter)
                .and(ApplicationSpecification.hasBuildingId(buildingId));
        return toPage(applicationRepository.findAll(spec, pageable(filter)));
    }

    @Transactional(readOnly = true)
    public ApplicationResponse findById(UUID id, CustomUserDetails currentUser) {
        Application application = getApplicationOrThrow(id);
        assertCanView(application, currentUser);
        return applicationMapper.toResponse(application);
    }

    @Transactional(readOnly = true)
    public ApplicationStatsResponse getStats() {
        long total = applicationRepository.count();
        Map<String, Long> byStatus = Arrays.stream(ApplicationStatus.values())
                .collect(Collectors.toMap(
                        Enum::name,
                        status -> applicationRepository.count(
                                ApplicationSpecification.hasStatus(status))));
        return new ApplicationStatsResponse(total, byStatus);
    }

    @Transactional
    public ApplicationResponse create(CreateApplicationRequest request, UUID authorId) {
        User author = userRepository.findById(authorId)
                .orElseThrow(() -> new ResourceNotFoundException("Пользователь не найден"));

        UUID residentId = request.residentId() != null ? request.residentId() : authorId;
        Apartment apartment = apartmentRepository.findByResidentId(residentId)
                .orElseThrow(() -> new BadRequestException("У жильца не привязана квартира"));

        ServiceCategory category = categoryRepository.findById(request.categoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Категория услуги не найдена"));

        Application application = Application.builder()
                .title(request.title())
                .description(request.description())
                .status(ApplicationStatus.NEW)
                .priority(request.priority())
                .apartment(apartment)
                .category(category)
                .author(author)
                .build();

        return applicationMapper.toResponse(applicationRepository.save(application));
    }

    @Transactional
    public ApplicationResponse updateStatus(UUID id, UpdateApplicationStatusRequest request) {
        Application application = getApplicationOrThrow(id);
        application.setStatus(request.status());
        if (request.status() == ApplicationStatus.DONE) {
            application.setCompletedAt(LocalDateTime.now());
        }
        return applicationMapper.toResponse(applicationRepository.save(application));
    }

    @Transactional
    public void delete(UUID id) {
        Application application = getApplicationOrThrow(id);
        applicationRepository.delete(application);
    }

    private Application getApplicationOrThrow(UUID id) {
        return applicationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Заявка не найдена"));
    }

    private void assertCanView(Application application, CustomUserDetails currentUser) {
        boolean isAdmin = hasRole(currentUser, "ADMIN");
        boolean isDispatcher = hasRole(currentUser, "DISPATCHER");
        boolean isAuthor = application.getAuthor().getId().equals(currentUser.getUserId());

        if (!isAdmin && !isDispatcher && !isAuthor) {
            throw new ForbiddenException("Нет доступа к этой заявке");
        }
    }

    private boolean hasRole(CustomUserDetails user, String role) {
        return user.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_" + role));
    }

    private Specification<Application> buildSpec(ApplicationFilterRequest filter) {
        return Specification.where(ApplicationSpecification.hasStatus(filter.status()))
                .and(ApplicationSpecification.hasCategoryId(filter.categoryId()))
                .and(ApplicationSpecification.hasBuildingId(filter.buildingId()))
                .and(ApplicationSpecification.createdAfter(filter.from()))
                .and(ApplicationSpecification.createdBefore(filter.to()));
    }

    private PageRequest pageable(ApplicationFilterRequest filter) {
        return PageRequest.of(filter.pageOrDefault(), filter.sizeOrDefault());
    }

    private ApplicationPageResponse toPage(Page<Application> page) {
        return new ApplicationPageResponse(
                page.getContent().stream().map(applicationMapper::toResponse).toList(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages()
        );
    }
}
