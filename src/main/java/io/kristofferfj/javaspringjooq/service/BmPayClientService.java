package io.kristofferfj.javaspringjooq.service;

import io.kristofferfj.javaspringjooq.domain.company.Company;
import io.kristofferfj.javaspringjooq.domain.company.CompanyRepository;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
public class BmPayClientService {

    private final CompanyRepository companyRepository;

    public BmPayClientService(CompanyRepository companyRepository) {
        this.companyRepository = companyRepository;
    }

    @Transactional
    public void createCompanyInBmPay(Company company) {
        String urlTemplate = "http://localhost:7777/api/1.0/client";

        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth("1");

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("name", company.name());
        requestBody.put("reference", company.id());
        requestBody.put("dk_cvr", "42535141");

        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestBody, headers);

        NewClientDtoResult response = restTemplate.postForObject(urlTemplate, requestEntity, NewClientDtoResult.class);
        companyRepository.setBmPayId(company, response.id());
    }

    record NewClientDtoResult(Long id) {
    }
    record ClientApiNew(String name, String reference, String dk_cvr) {
    }

}
