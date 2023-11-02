package io.kristofferfj.javaspringjooq.domain.company;

import io.kristofferfj.jooq.public_.tables.records.CompanyRecord;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static io.kristofferfj.jooq.public_.tables.Company.COMPANY;

@Repository
public class CompanyRepository {

    private final DSLContext dslContext;

    public CompanyRepository(DSLContext dslContext) {
        this.dslContext = dslContext;
    }

    @Transactional(readOnly = true)
    public List<Company> getCompanies() {
        return dslContext.selectFrom(COMPANY).fetch()
                .map(companyRecord -> new Company(companyRecord.getId(), companyRecord.getName()));
    }

    @Transactional
    public Company createNewCompany(String name) {
        CompanyRecord createdCompany = dslContext.insertInto(COMPANY)
                .columns(COMPANY.NAME)
                .values(name)
                .returning()
                .fetchSingle();
        return new Company(createdCompany.getId(), createdCompany.getName());
    }
}
