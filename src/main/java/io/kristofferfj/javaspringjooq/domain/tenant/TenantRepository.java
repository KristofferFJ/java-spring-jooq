package io.kristofferfj.javaspringjooq.domain.tenant;

import io.kristofferfj.javaspringjooq.domain.bill.Bill;
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

    @Transactional(readOnly = true)
    public Tenant getTenantForBill(Bill bill) {
        TenantRecord tenantRecord = dslContext.selectFrom(TENANT).where(TENANT.ID.eq(bill.tenantId())).fetchSingle();
        return toTenant(tenantRecord);
    }

    @Transactional
    public Tenant createNewTenancy(String name, String email, Long tenancyId) {
        TenantRecord createdTenant = dslContext.insertInto(TENANT)
                .columns(TENANT.NAME, TENANT.EMAIL, TENANT.TENANCY_ID)
                .values(name, email, tenancyId)
                .returning()
                .fetchSingle();
        return toTenant(createdTenant);
    }

    private Tenant toTenant(TenantRecord tenantRecord) {
        return new Tenant(tenantRecord.getId(), tenantRecord.getName(), tenantRecord.getEmail(), tenantRecord.getTenancyId());
    }
}
