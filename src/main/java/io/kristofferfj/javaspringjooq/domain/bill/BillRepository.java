package io.kristofferfj.javaspringjooq.domain.bill;

import io.kristofferfj.jooq.public_.tables.records.BillRecord;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static io.kristofferfj.jooq.public_.tables.Bill.BILL;

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

    @Transactional
    public Bill creteNewBill(Long tenantId, BigDecimal amount, LocalDate due) {
        BillRecord createdBill = dslContext.insertInto(BILL)
                .columns(BILL.TENANT_ID, BILL.AMOUNT, BILL.DUE, BILL.STATE)
                .values(tenantId, amount, due, BillState.INITIAL.name())
                .returning()
                .fetchSingle();
        return new Bill(
                createdBill.getId(),
                createdBill.getTenantId(),
                createdBill.getAmount(),
                BillState.valueOf(createdBill.getState()),
                createdBill.getDue());
    }
}
