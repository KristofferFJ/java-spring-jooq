package io.kristofferfj.javaspringjooq.domain.tenant;

import io.kristofferfj.jooq.public_.tables.records.TenantRecord;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static io.kristofferfj.jooq.public_.tables.Tenant.TENANT;

@Repository
public class TenantRepository {

    private final DSLContext dslContext;

    public TenantRepository(DSLContext dslContext) {
        this.dslContext = dslContext;
    }

    @Transactional(readOnly = true)
    public List<Tenant> getTenants() {
        return dslContext.selectFrom(TENANT).fetch()
                .map(tenancyRecord -> new Tenant(
                                tenancyRecord.getId(),
                                tenancyRecord.getName(),
                                tenancyRecord.getEmail(),
                                tenancyRecord.getTenancyId()
                        )
                );
    }

    @Transactional
    public Tenant createNewTenancy(String name, String email, Long tenancyId) {
        TenantRecord createdTenant = dslContext.insertInto(TENANT)
                .columns(TENANT.NAME, TENANT.EMAIL, TENANT.TENANCY_ID)
                .values(name, email, tenancyId)
                .returning()
                .fetchSingle();
        return new Tenant(createdTenant.getId(), createdTenant.getName(), createdTenant.getEmail(), createdTenant.getTenancyId());
    }
}
