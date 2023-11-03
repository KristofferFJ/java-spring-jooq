package io.kristofferfj.javaspringjooq.domain.company;

import io.kristofferfj.javaspringjooq.domain.tenant.Tenant;
import io.kristofferfj.jooq.public_.tables.records.CompanyRecord;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static io.kristofferfj.jooq.public_.tables.Company.COMPANY;
import static io.kristofferfj.jooq.public_.tables.Tenancy.TENANCY;

@Repository
public class CompanyRepository {

    private final DSLContext dslContext;

    public CompanyRepository(DSLContext dslContext) {
        this.dslContext = dslContext;
    }

    @Transactional(readOnly = true)
    public List<Company> getCompanies() {
        return dslContext.selectFrom(COMPANY).fetch()
                .map(this::toCompany);
    }

    @Transactional
    public Company createNewCompany(String name) {
        CompanyRecord createdCompany = dslContext.insertInto(COMPANY)
                .columns(COMPANY.NAME)
                .values(name)
                .returning()
                .fetchSingle();
        return toCompany(createdCompany);
    }

    @Transactional(readOnly = true)
    public Company getCompanyForTenant(Tenant tenant) {
        CompanyRecord companyRecord = dslContext.select(COMPANY.fields()).from(
                        COMPANY.join(TENANCY).on(TENANCY.COMPANY_ID.eq(COMPANY.ID))
                ).where(TENANCY.ID.eq(tenant.tenancyId()))
                .fetchSingle().into(COMPANY);
        return toCompany(companyRecord);
    }

    @Transactional(readOnly = true)
    public Company findById(Long id) {
        CompanyRecord companyRecord = dslContext.selectFrom(COMPANY).where(COMPANY.ID.eq(id)).fetchSingle();
        return toCompany(companyRecord);
    }

    @Transactional
    public void setBmPayId(Company company, Long bmPayId) {
        dslContext.update(COMPANY).set(COMPANY.BM_PAY_ID, bmPayId).where(COMPANY.ID.eq(company.id())).execute();
    }

    private Company toCompany(CompanyRecord companyRecord) {
        return new Company(companyRecord.getId(), companyRecord.getName(), companyRecord.getBmPayId());
    }
}
