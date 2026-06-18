package com.hau.labhau.service;

import com.hau.labhau.dto.request.ApplicationFilterRequest;
import com.hau.labhau.dto.response.ApplicationPageResponse;
import com.hau.labhau.entity.Application;
import com.hau.labhau.entity.ApplicationStatus;
import com.hau.labhau.mapper.ApplicationMapper;
import com.hau.labhau.repository.ApplicationRepository;
import com.hau.labhau.repository.ApartmentRepository;
import com.hau.labhau.repository.ServiceCategoryRepository;
import com.hau.labhau.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ApplicationServiceTest {

    @InjectMocks
    private ApplicationService applicationService;

    @Mock
    private ApplicationRepository applicationRepository;
    @Mock
    private ApartmentRepository apartmentRepository;
    @Mock
    private ServiceCategoryRepository categoryRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ApplicationMapper applicationMapper;

    @Test
    void findAll_returnsPage() {
        Application application = new Application();
        when(applicationRepository.findAll(any(Specification.class), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(application)));
        when(applicationMapper.toResponse(application)).thenReturn(null);

        ApplicationPageResponse result = applicationService.findAll(
                new ApplicationFilterRequest(ApplicationStatus.NEW, null, null, null, null, 0, 10));

        assertEquals(1, result.totalElements());
    }
}
