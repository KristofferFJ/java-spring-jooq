package io.kristofferfj.javaspringjooq;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.kristofferfj.javaspringjooq.domain.company.CompanyApi;
import io.kristofferfj.javaspringjooq.domain.tenancy.TenancyApi;
import io.kristofferfj.javaspringjooq.domain.tenant.TenantApi;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class CrudApiTest {

    private final MockMvc mockMvc;
    private final Flyway flyway;
    private final ObjectMapper objectMapper = new Jackson2ObjectMapperBuilder().modules(new JavaTimeModule()).build();

    public CrudApiTest(@Autowired MockMvc mockMvc, @Autowired Flyway flyway) {
        this.mockMvc = mockMvc;
        this.flyway = flyway;
    }

    @BeforeEach
    public void rebuildDatabase() {
        flyway.clean();
        flyway.migrate();
    }

    @Test
    public void testCrud() throws Exception {
        List<Integer> smallList = List.of(1, 2);

        String newCompanyRawResponse = mockMvc.perform(post("/company")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"virksomhed\"}"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Long createdCompanyId = objectMapper.readValue(newCompanyRawResponse, CompanyApi.CreatedCompanyId.class).id();

        List<Long> tenancyIds = smallList.stream().map(index -> {
            Long id;
            try {
                id = objectMapper.readValue(mockMvc.perform(post("/tenancy")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(String.format("{\"name\":\"lejemål %d\", \"companyId\": %d}", index, createdCompanyId)))
                        .andExpect(status().isOk())
                        .andReturn()
                        .getResponse()
                        .getContentAsString(), TenancyApi.CreatedTenancyId.class).id();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            return id;
        }).toList();

        List<Long> tenantIds = smallList.stream().map(index -> tenancyIds.stream().map(tenancyId -> {
            Long id;
            try {
                id = objectMapper.readValue(mockMvc.perform(post("/tenant")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(String.format("{\"name\":\"lejemål\", \"email\":\"abc@def.gh\", \"tenancyId\": %d}", tenancyId)))
                        .andExpect(status().isOk())
                        .andReturn()
                        .getResponse()
                        .getContentAsString(), TenantApi.CreatedTenantId.class).id();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            return id;
        }).toList()).flatMap(List::stream).toList();

        smallList.forEach(index -> tenantIds.forEach(tenantId -> {
            try {
                mockMvc.perform(post("/bill")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(String.format("{\"tenantId\":%d, \"amount\":123.02, \"due\": \"2023-12-24\"}", tenantId)))
                        .andExpect(status().isOk());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }));
    }
}
