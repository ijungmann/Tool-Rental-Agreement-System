package ian.jungmann.ij0292.e2e;

import static org.junit.jupiter.api.Assertions.*;

import com.github.dockerjava.zerodep.shaded.org.apache.hc.core5.http.HttpStatus;
import ian.jungmann.ij0292.dto.ToolRentalAgreementRequestDto;
import ian.jungmann.ij0292.dto.ToolRentalAgreementResponseDto;

import static org.assertj.core.api.Assertions.*;
import static org.springframework.test.context.jdbc.SqlConfig.TransactionMode.ISOLATED;

import jakarta.transaction.Transactional;
import jakarta.validation.constraints.AssertTrue;
import java.math.BigDecimal;
import java.time.LocalDate;
import javax.sql.DataSource;
import org.junit.jupiter.api.Test;
import org.junit.platform.commons.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import org.springframework.web.server.ResponseStatusException;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@Testcontainers
@ActiveProfiles("test")
class ToolRentalAgreementE2ETest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private DataSource dataSource;

    @Container
    @ServiceConnection
    private static final MySQLContainer mySQLContainer = new MySQLContainer("mysql:latest")
            .withDatabaseName("tool_db")
            .withUsername("root")
            .withPassword("root");

    @DynamicPropertySource
    private static void setupProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", mySQLContainer::getJdbcUrl);
        registry.add("flyway.url", mySQLContainer::getJdbcUrl);
        registry.add("spring.datasource.username", mySQLContainer::getUsername);
        registry.add("spring.datasource.password", mySQLContainer::getPassword);
    }

    @Test
    void createRentalAgreement__JAKR__DiscountTooHigh() {
        ToolRentalAgreementRequestDto requestDto = buildRequestDto()
                .toolCode("JAKR")
                .checkoutDate(LocalDate.of(2015, 9, 3))
                .rentalDays(5)
                .discountPercent(101)
                .build();
        ResponseEntity<String> response = restTemplate.postForEntity(
                getUrl(), requestDto, String.class);
        assertEquals(HttpStatus.SC_BAD_REQUEST, response.getStatusCodeValue());
        String body = response.getBody();
        assertEquals("Discount percent must be less than or equal to 100", body);
    }

    @Test
    void createRentalAgreement__LADW() {
        ToolRentalAgreementRequestDto requestDto = buildRequestDto()
                .toolCode("LADW")
                .checkoutDate(LocalDate.of(2020, 7, 2))
                .rentalDays(3)
                .discountPercent(10)
                .build();
        ResponseEntity<ToolRentalAgreementResponseDto> response = restTemplate.postForEntity(
                getUrl(), requestDto, ToolRentalAgreementResponseDto.class);
        assertEquals(HttpStatus.SC_OK, response.getStatusCodeValue());
        ToolRentalAgreementResponseDto dto = response.getBody();
        assertNotNull(dto);
        ToolRentalAgreementResponseDto expected = buildResponseDto()
                .id(dto.getId())
                .brand("Werner")
                .type("Ladder")
                .code("LADW")
                .dailyRentalCharge(BigDecimal.valueOf(1.99).setScale(2))
                .rentalDays(3)
                .chargeDays(2)
                .preDiscountCharge(BigDecimal.valueOf(3.98).setScale(2))
                .discountAmount(BigDecimal.valueOf(0.40).setScale(2))
                .discountPercent(10)
                .finalCharge(BigDecimal.valueOf(3.58).setScale(2))
                .checkoutDate(LocalDate.of(2020, 7, 2))
                .dueDate(LocalDate.of(2020, 7, 5))
                .build();

        assertThat(dto).usingRecursiveComparison().isEqualTo(expected);
    }

    @Test
    void createRentalAgreement__CHNS() {
        ToolRentalAgreementRequestDto requestDto = buildRequestDto()
                .toolCode("CHNS")
                .checkoutDate(LocalDate.of(2015, 7, 2))
                .rentalDays(5)
                .discountPercent(25)
                .build();
        ResponseEntity<ToolRentalAgreementResponseDto> response = restTemplate.postForEntity(
                getUrl(), requestDto, ToolRentalAgreementResponseDto.class);
        assertEquals(HttpStatus.SC_OK, response.getStatusCodeValue());
        ToolRentalAgreementResponseDto dto = response.getBody();
        assertNotNull(dto);
        ToolRentalAgreementResponseDto expected = buildResponseDto()
                .id(dto.getId())
                .brand("Stihl")
                .type("Chainsaw")
                .code("CHNS")
                .dailyRentalCharge(BigDecimal.valueOf(1.49).setScale(2))
                .rentalDays(5)
                .chargeDays(3)
                .preDiscountCharge(BigDecimal.valueOf(4.47).setScale(2))
                .discountAmount(BigDecimal.valueOf(1.12).setScale(2))
                .discountPercent(25)
                .finalCharge(BigDecimal.valueOf(3.35).setScale(2))
                .checkoutDate(LocalDate.of(2015, 7, 2))
                .dueDate(LocalDate.of(2015, 7, 7))
                .build();

        assertThat(dto).usingRecursiveComparison().isEqualTo(expected);
    }

    @Test
    void createRentalAgreement__JAKD() {
        ToolRentalAgreementRequestDto requestDto = buildRequestDto()
                .toolCode("JAKD")
                .checkoutDate(LocalDate.of(2015, 9, 3))
                .rentalDays(6)
                .discountPercent(0)
                .build();
        ResponseEntity<ToolRentalAgreementResponseDto> response = restTemplate.postForEntity(
                getUrl(), requestDto, ToolRentalAgreementResponseDto.class);
        assertEquals(HttpStatus.SC_OK, response.getStatusCodeValue());
        ToolRentalAgreementResponseDto dto = response.getBody();
        assertNotNull(dto);
        ToolRentalAgreementResponseDto expected = buildResponseDto()
                .id(dto.getId())
                .brand("DeWalt")
                .type("Jackhammer")
                .code("JAKD")
                .dailyRentalCharge(BigDecimal.valueOf(2.99).setScale(2))
                .rentalDays(6)
                .chargeDays(3)
                .preDiscountCharge(BigDecimal.valueOf(8.97).setScale(2))
                .discountAmount(BigDecimal.valueOf(0).setScale(2))
                .discountPercent(0)
                .finalCharge(BigDecimal.valueOf(8.97).setScale(2))
                .checkoutDate(LocalDate.of(2015, 9, 3))
                .dueDate(LocalDate.of(2015, 9, 9))
                .build();

        assertThat(dto).usingRecursiveComparison().isEqualTo(expected);
    }

    @Test
    void createRentalAgreement__JAKR__NoDiscount() {
        ToolRentalAgreementRequestDto requestDto = buildRequestDto()
                .toolCode("JAKR")
                .checkoutDate(LocalDate.of(2015, 7, 3))
                .rentalDays(9)
                .discountPercent(0)
                .build();
        ResponseEntity<ToolRentalAgreementResponseDto> response = restTemplate.postForEntity(
                getUrl(), requestDto,
                ToolRentalAgreementResponseDto.class);
        assertEquals(HttpStatus.SC_OK, response.getStatusCodeValue());
        ToolRentalAgreementResponseDto dto = response.getBody();
        assertNotNull(dto);
        ToolRentalAgreementResponseDto expected = buildResponseDto()
                .id(dto.getId())
                .brand("Ridgid")
                .type("Jackhammer")
                .code("JAKR")
                .dailyRentalCharge(BigDecimal.valueOf(2.99).setScale(2))
                .rentalDays(9)
                .chargeDays(5)
                .preDiscountCharge(BigDecimal.valueOf(14.95).setScale(2))
                .discountAmount(BigDecimal.valueOf(0).setScale(2))
                .discountPercent(0)
                .finalCharge(BigDecimal.valueOf(14.95).setScale(2))
                .checkoutDate(LocalDate.of(2015, 7, 3))
                .dueDate(LocalDate.of(2015, 7, 12))
                .build();

        assertThat(dto).usingRecursiveComparison().isEqualTo(expected);
    }
    @Test
    void createRentalAgreement__JAKR__FiftyPercentDiscount() {
        ToolRentalAgreementRequestDto requestDto = buildRequestDto()
                .toolCode("JAKR")
                .checkoutDate(LocalDate.of(2020, 7, 2))
                .rentalDays(4)
                .discountPercent(50)
                .build();
        ResponseEntity<ToolRentalAgreementResponseDto> response = restTemplate.postForEntity(
                getUrl(), requestDto,
                ToolRentalAgreementResponseDto.class);
        assertEquals(HttpStatus.SC_OK, response.getStatusCodeValue());
        ToolRentalAgreementResponseDto dto = response.getBody();
        assertNotNull(dto);
        ToolRentalAgreementResponseDto expected = buildResponseDto()
                .id(dto.getId())
                .brand("Ridgid")
                .type("Jackhammer")
                .code("JAKR")
                .dailyRentalCharge(BigDecimal.valueOf(2.99).setScale(2))
                .rentalDays(4)
                .chargeDays(1)
                .preDiscountCharge(BigDecimal.valueOf(2.99).setScale(2))
                .discountAmount(BigDecimal.valueOf(1.50).setScale(2))
                .discountPercent(50)
                .finalCharge(BigDecimal.valueOf(1.49).setScale(2))
                .checkoutDate(LocalDate.of(2020, 7, 2))
                .dueDate(LocalDate.of(2020, 7, 6))
                .build();

        assertThat(dto).usingRecursiveComparison().isEqualTo(expected);
    }

    @Test
    void createRentalAgreement__JAKR__DiscountTooLow() {
        ToolRentalAgreementRequestDto requestDto = buildRequestDto()
                .toolCode("JAKR")
                .checkoutDate(LocalDate.of(2015, 9, 3))
                .rentalDays(5)
                .discountPercent(-1)
                .build();
        ResponseEntity<String> response = restTemplate.postForEntity(
                getUrl(), requestDto, String.class);
        assertEquals(HttpStatus.SC_BAD_REQUEST, response.getStatusCodeValue());
        String body = response.getBody();
        assertEquals( "Discount percent must be greater than or equal to 0", body);
    }

    @Test
    void createRentalAgreement__JAKR__RentalDaysTooLow() {
        ToolRentalAgreementRequestDto requestDto = buildRequestDto()
                .toolCode("JAKR")
                .checkoutDate(LocalDate.of(2015, 9, 3))
                .rentalDays(0)
                .discountPercent(1)
                .build();
        ResponseEntity<String> response = restTemplate.postForEntity(
                getUrl(), requestDto, String.class);
        assertEquals(HttpStatus.SC_BAD_REQUEST, response.getStatusCodeValue());
        String body = response.getBody();
        assertEquals( "Rental day count must be greater than 0", body);
    }

    @Test
    void createRentalAgreement__JAKR__CheckoutDateNull() {
        ToolRentalAgreementRequestDto requestDto = buildRequestDto()
                .toolCode("JAKR")
                .checkoutDate(null)
                .rentalDays(2)
                .discountPercent(1)
                .build();
        ResponseEntity<String> response = restTemplate.postForEntity(
                getUrl(), requestDto, String.class);
        assertEquals(HttpStatus.SC_BAD_REQUEST, response.getStatusCodeValue());
        String body = response.getBody();
        assertEquals( "Checkout date must not be null", body);
    }

    @Test
    void createRentalAgreement__JAKR__ToolCodeNull() {
        ToolRentalAgreementRequestDto requestDto = buildRequestDto()
                .toolCode(null)
                .checkoutDate(LocalDate.now())
                .rentalDays(2)
                .discountPercent(1)
                .build();
        ResponseEntity<String> response = restTemplate.postForEntity(
                getUrl(), requestDto, String.class);
        assertEquals(HttpStatus.SC_BAD_REQUEST, response.getStatusCodeValue());
        String body = response.getBody();
        assertEquals( "Tool code must not be blank", body);
    }

    @Test
    void createRentalAgreement__JAKR__ToolCodeBlank() {
        ToolRentalAgreementRequestDto requestDto = buildRequestDto()
                .toolCode("")
                .checkoutDate(LocalDate.now())
                .rentalDays(2)
                .discountPercent(1)
                .build();
        ResponseEntity<String> response = restTemplate.postForEntity(
                getUrl(), requestDto, String.class);
        assertEquals(HttpStatus.SC_BAD_REQUEST, response.getStatusCodeValue());
        String body = response.getBody();
        assertEquals( "Tool code must not be blank", body);
    }

    @Test
    void createRentalAgreement__InvalidType() {
        ToolRentalAgreementRequestDto requestDto = buildRequestDto()
                .toolCode("Fake")
                .checkoutDate(LocalDate.of(2020, 7, 2))
                .rentalDays(4)
                .discountPercent(50)
                .build();

        ResponseEntity<String> response = restTemplate.postForEntity(
                getUrl(), requestDto, String.class);
        assertTrue(response.getStatusCode().is4xxClientError());
        assertEquals("Tool with code Fake not found", response.getBody());
    }

    private ToolRentalAgreementRequestDto.Builder buildRequestDto() {
        return ToolRentalAgreementRequestDto.builder()
                .toolCode("LADW")
                .checkoutDate(LocalDate.now())
                .rentalDays(1)
                .discountPercent(0);
    }

    private ToolRentalAgreementResponseDto.Builder buildResponseDto() {
        return ToolRentalAgreementResponseDto.builder()
                .code("LADW")
                .type("Ladder")
                .brand("Werner")
                .rentalDays(1)
                .checkoutDate(LocalDate.now())
                .dueDate(LocalDate.now().plusDays(1))
                .dailyRentalCharge(BigDecimal.valueOf(1.99))
                .chargeDays(1)
                .preDiscountCharge(BigDecimal.valueOf(1.99))
                .discountPercent(0)
                .discountAmount(BigDecimal.valueOf(0))
                .finalCharge(BigDecimal.valueOf(1.99));
    }

    private String getUrl() {
        return "http://localhost:" + port + "/tools/rentalAgreements";
    }
}