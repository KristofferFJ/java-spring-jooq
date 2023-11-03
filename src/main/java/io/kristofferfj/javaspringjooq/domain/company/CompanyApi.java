package io.kristofferfj.javaspringjooq.domain.company;

import io.kristofferfj.javaspringjooq.service.BmPayClientService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("company")
public class CompanyApi {

    private final CompanyRepository companyRepository;
    private final BmPayClientService bmPayClientService;

    public CompanyApi(CompanyRepository companyRepository,
                      BmPayClientService bmPayClientService) {
        this.companyRepository = companyRepository;
        this.bmPayClientService = bmPayClientService;
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

    @PostMapping("{companyId}/register-bmpay")
    public ResponseEntity<Boolean> registerCompanyBmPay(@PathVariable Long companyId) {
        Company company = companyRepository.findById(companyId);
        bmPayClientService.createCompanyInBmPay(company);
        return ResponseEntity.ok(true);
    }

    public record NewCompany(String name) {
    }

    public record CreatedCompanyId(Long id) {
    }

    public record CompaniesDto(List<Company> companies) {

    }
}
