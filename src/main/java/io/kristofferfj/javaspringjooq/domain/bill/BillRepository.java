package io.kristofferfj.javaspringjooq.domain.bill;

import io.kristofferfj.jooq.public_.tables.records.BillRecord;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static io.kristofferfj.javaspringjooq.domain.bill.BillState.INITIAL;
import static io.kristofferfj.jooq.public_.tables.Bill.BILL;
import static io.kristofferfj.jooq.public_.tables.Tenancy.TENANCY;
import static io.kristofferfj.jooq.public_.tables.Company.COMPANY;
import static io.kristofferfj.jooq.public_.tables.Tenant.TENANT;

@Repository
public class BillRepository {

    private final DSLContext dslContext;

    public BillRepository(DSLContext dslContext) {
        this.dslContext = dslContext;
    }

    @Transactional(readOnly = true)
    public List<Bill> getBills() {
        return dslContext.selectFrom(BILL).fetch()
                .map(billRecord -> new Bill(
                                billRecord.getId(),
                                billRecord.getTenantId(),
                                billRecord.getAmount(),
                                BillState.valueOf(billRecord.getState()),
                                billRecord.getDue()
                        )
                );
    }

    @Transactional(readOnly = true)
    public Bill getBillReadyForProcessing() {
        List<BillRecord> result = dslContext.select(BILL.fields())
                .from(BILL.join(TENANT).on(TENANT.ID.eq(BILL.TENANT_ID))
                        .join(TENANCY).on(TENANCY.ID.eq(TENANT.TENANCY_ID))
                        .join(COMPANY).on(COMPANY.ID.eq(TENANCY.COMPANY_ID)))
                .where(BILL.STATE.eq(INITIAL.name())
                        .and(COMPANY.BM_PAY_ID.isNotNull()))
                .limit(1).fetchInto(BILL);
        if (result.size() != 1) return null;
        BillRecord billRecord = result.get(0);
        return new Bill(
                billRecord.getId(),
                billRecord.getTenantId(),
                billRecord.getAmount(),
                BillState.valueOf(billRecord.getState()),
                billRecord.getDue()
        );
    }

    @Transactional
    public void updateStateOfBill(Bill bill, BillState billState) {
        dslContext.update(BILL).set(BILL.STATE, billState.name()).where(BILL.ID.eq(bill.id())).execute();
    }

    @Transactional
    public Bill creteNewBill(Long tenantId, BigDecimal amount, LocalDate due) {
        BillRecord createdBill = dslContext.insertInto(BILL)
                .columns(BILL.TENANT_ID, BILL.AMOUNT, BILL.DUE, BILL.STATE)
                .values(tenantId, amount, due, INITIAL.name())
                .returning()
                .fetchSingle();
        return new Bill(
                createdBill.getId(),
                createdBill.getTenantId(),
                createdBill.getAmount(),
                BillState.valueOf(createdBill.getState()),
                createdBill.getDue()
        );
    }
}
