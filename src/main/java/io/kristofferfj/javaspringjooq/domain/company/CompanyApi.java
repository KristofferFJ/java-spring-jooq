package io.kristofferfj.javaspringjooq.domain.company;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("company")
public class CompanyApi {

    private final CompanyRepository companyRepository;

    public CompanyApi(CompanyRepository companyRepository) {
        this.companyRepository = companyRepository;
    }

    @PostMapping
    public ResponseEntity<CreatedCompanyId> createCompany(@RequestBody NewCompany newCompany) {
        return ResponseEntity.ok(
                new CreatedCompanyId(
                        companyRepository.createNewCompany(newCompany.name).id()
                )
        );
    }

    @GetMapping("all")
    public ResponseEntity<CompaniesDto> getAllCompanies() {
        return ResponseEntity.ok(new CompaniesDto(companyRepository.getCompanies()));
    }

    public record NewCompany(String name) {
    }

    public record CreatedCompanyId(Long id) {
    }

    public record CompaniesDto(List<Company> companies) {

    }
}
