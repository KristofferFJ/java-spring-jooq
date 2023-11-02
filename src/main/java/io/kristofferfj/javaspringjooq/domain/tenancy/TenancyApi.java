package io.kristofferfj.javaspringjooq.domain.tenancy;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("tenancy")
public class TenancyApi {

    private final TenancyRepository tenancyRepository;

    public TenancyApi(TenancyRepository tenancyRepository) {
        this.tenancyRepository = tenancyRepository;
    }

    @PostMapping
    public ResponseEntity<CreatedTenancyId> createTenancy(@RequestBody NewTenancy newTenancy) {
        return ResponseEntity.ok(
                new CreatedTenancyId(
                        tenancyRepository.createNewTenancy(newTenancy.name, newTenancy.companyId()).id()
                )
        );
    }

    @GetMapping("all")
    public ResponseEntity<TenanciesDto> getAllTenancies() {
        return ResponseEntity.ok(new TenanciesDto(tenancyRepository.getCompanies()));
    }

    public record NewTenancy(String name, Long companyId) {
    }

    public record CreatedTenancyId(Long id) {
    }

    public record TenanciesDto(List<Tenancy> tenancies) {
    }
}
