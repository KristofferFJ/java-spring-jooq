package io.kristofferfj.javaspringjooq.domain.tenancy;

import io.kristofferfj.jooq.public_.tables.records.TenancyRecord;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static io.kristofferfj.jooq.public_.tables.Tenancy.TENANCY;

@Repository
public class TenancyRepository {

    private final DSLContext dslContext;

    public TenancyRepository(DSLContext dslContext) {
        this.dslContext = dslContext;
    }

    @Transactional(readOnly = true)
    public List<Tenancy> getCompanies() {
        return dslContext.selectFrom(TENANCY).fetch()
                .map(tenancyRecord -> new Tenancy(
                                tenancyRecord.getId(),
                                tenancyRecord.getName(),
                                tenancyRecord.getCompanyId()
                        )
                );
    }

    @Transactional
    public Tenancy createNewTenancy(String name, Long companyId) {
        TenancyRecord createdTenancy = dslContext.insertInto(TENANCY)
                .columns(TENANCY.NAME, TENANCY.COMPANY_ID)
                .values(name, companyId)
                .returning()
                .fetchSingle();
        return new Tenancy(createdTenancy.getId(), createdTenancy.getName(), createdTenancy.getCompanyId());
    }
}
