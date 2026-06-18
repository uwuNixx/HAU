package com.hau.labhau.repository.specification;

import com.hau.labhau.entity.Application;
import com.hau.labhau.entity.ApplicationStatus;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;
import java.util.UUID;

public final class ApplicationSpecification {

    private ApplicationSpecification() {
    }

    public static Specification<Application> hasAuthorId(UUID authorId) {
        return (root, query, cb) -> authorId == null ? cb.conjunction()
                : cb.equal(root.get("author").get("id"), authorId);
    }

    public static Specification<Application> hasStatus(ApplicationStatus status) {
        return (root, query, cb) -> status == null ? cb.conjunction()
                : cb.equal(root.get("status"), status);
    }

    public static Specification<Application> hasCategoryId(UUID categoryId) {
        return (root, query, cb) -> categoryId == null ? cb.conjunction()
                : cb.equal(root.get("category").get("id"), categoryId);
    }

    public static Specification<Application> hasBuildingId(UUID buildingId) {
        return (root, query, cb) -> buildingId == null ? cb.conjunction()
                : cb.equal(root.get("apartment").get("building").get("id"), buildingId);
    }

    public static Specification<Application> createdAfter(LocalDateTime from) {
        return (root, query, cb) -> from == null ? cb.conjunction()
                : cb.greaterThanOrEqualTo(root.get("createdAt"), from);
    }

    public static Specification<Application> createdBefore(LocalDateTime to) {
        return (root, query, cb) -> to == null ? cb.conjunction()
                : cb.lessThanOrEqualTo(root.get("createdAt"), to);
    }
}
