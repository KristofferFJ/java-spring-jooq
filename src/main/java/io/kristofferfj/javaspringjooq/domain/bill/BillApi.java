package io.kristofferfj.javaspringjooq.domain.bill;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("bill")
public class BillApi {

    private final BillRepository billRepository;

    public BillApi(BillRepository billRepository) {
        this.billRepository = billRepository;
    }

    @PostMapping
    public ResponseEntity<CreatedBillId> createBill(@RequestBody NewBillDto newBillDto) {
        return ResponseEntity.ok(
                new CreatedBillId(
                        billRepository.creteNewBill(newBillDto.tenantId, newBillDto.amount, newBillDto.due).id()
                )
        );
    }

    @GetMapping("all")
    public ResponseEntity<BillDto> getAllTenants() {
        return ResponseEntity.ok(new BillDto(billRepository.getBills()));
    }

    public record NewBillDto(Long tenantId, BigDecimal amount, LocalDate due) {
    }

    public record CreatedBillId(Long id) {
    }

    public record BillDto(List<Bill> bills) {
    }
}
