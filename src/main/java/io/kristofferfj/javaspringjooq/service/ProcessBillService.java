package io.kristofferfj.javaspringjooq.service;

import io.kristofferfj.javaspringjooq.domain.bill.Bill;
import io.kristofferfj.javaspringjooq.domain.bill.BillRepository;
import io.kristofferfj.javaspringjooq.domain.bill.BillState;
import io.kristofferfj.javaspringjooq.domain.company.Company;
import io.kristofferfj.javaspringjooq.domain.company.CompanyRepository;
import io.kristofferfj.javaspringjooq.domain.tenant.Tenant;
import io.kristofferfj.javaspringjooq.domain.tenant.TenantRepository;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@Service
public class ProcessBillService {

    private final BillRepository billRepository;
    private final TenantRepository tenantRepository;
    private final CompanyRepository companyRepository;

    private final DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE;

    public ProcessBillService(BillRepository billRepository,
                              TenantRepository tenantRepository,
                              CompanyRepository companyRepository) {
        this.billRepository = billRepository;
        this.tenantRepository = tenantRepository;
        this.companyRepository = companyRepository;
    }

    @Transactional
    public void sendBillToBmPay(Bill bill) {
        billRepository.updateStateOfBill(bill, BillState.PENDING);
        Tenant tenant = tenantRepository.getTenantForBill(bill);
        Company company = companyRepository.getCompanyForTenant(tenant);

        String urlTemplate = String.format("http://localhost:7777/api/1.0/payment/%d/create", company.bmPayId());

        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth("1");

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("payer", Map.of(
                "name", bill.tenantId(),
                "reference", bill.tenantId()
        ));
        requestBody.put("idempotency_key", bill.id());
        requestBody.put("amount", bill.amount());
        requestBody.put("description", String.format("Bill for tenant.id=%s, due=%s and amount=%s",
                bill.tenantId(), DateTimeFormatter.ISO_DATE.format(bill.due()), bill.amount()));
        requestBody.put("currency", "DKK");
        requestBody.put("pay_by_subscription", false);
        requestBody.put("due", DateTimeFormatter.ISO_DATE.format(bill.due()));
        requestBody.put("paymentNameAndAddressDto", Map.of(
                "name", tenant.name(),
                "addressLine1", "Langgade 10",
                "addressLine2", "",
                "postalCode", "9000"
        ));

        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestBody, headers);

        PaymentApiCreatedPaymentV1 response = restTemplate.postForObject(urlTemplate, requestEntity, PaymentApiCreatedPaymentV1.class, company.bmPayId());
    }

    record PaymentNameAndAddressDto(
            String name,
            String addressLine1,
            String addressLine2,
            String postalCode
    ) {
    }

    record PaymentApiNewV1(
            PaymentApiNewV1Payer payer,
            String idempotency_key,
            String amount,
            String description,
            String currency,
            boolean pay_by_subscription,
            String due,
            PaymentNameAndAddressDto paymentNameAndAddressDto
    ) {
    }


    record PaymentApiNewV1Payer(
            String name,
            String reference
    ) {
    }

    record PaymentApiCreatedPaymentV1(
            Long payment_id,
            PaymentApiV1DKFik dk_fik_info
    ) {
    }

    record PaymentApiV1DKFik(
            String customernumber,
            String fik
    ){
    }
}
