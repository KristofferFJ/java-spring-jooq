package io.kristofferfj.javaspringjooq.domain.tenant;

public record Tenant(Long id, String name, String email, Long tenancyId) {
}
