package io.kristofferfj.javaspringjooq.domain.bill;

import java.math.BigDecimal;
import java.time.LocalDate;

public record Bill(Long id, Long tenantId, BigDecimal amount, BillState state, LocalDate due) {

}

enum BillState {
    INITIAL,
    PENDING,
    PAID
}
