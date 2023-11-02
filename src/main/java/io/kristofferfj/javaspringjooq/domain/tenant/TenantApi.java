package io.kristofferfj.javaspringjooq.domain.tenant;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("tenant")
public class TenantApi {

    private final TenantRepository tenantRepository;

    public TenantApi(TenantRepository tenantRepository) {
        this.tenantRepository = tenantRepository;
    }

    @PostMapping
    public ResponseEntity<CreatedTenantId> createTenant(@RequestBody NewTenant newTenant) {
        return ResponseEntity.ok(
                new CreatedTenantId(
                        tenantRepository.createNewTenancy(newTenant.name, newTenant.email, newTenant.tenancyId).id()
                )
        );
    }

    @GetMapping("all")
    public ResponseEntity<TenantsDto> getAllTenants() {
        return ResponseEntity.ok(new TenantsDto(tenantRepository.getTenants()));
    }

    public record NewTenant(String name, String email, Long tenancyId) {
    }

    public record CreatedTenantId(Long id) {
    }

    public record TenantsDto(List<Tenant> tenants) {
    }
}
