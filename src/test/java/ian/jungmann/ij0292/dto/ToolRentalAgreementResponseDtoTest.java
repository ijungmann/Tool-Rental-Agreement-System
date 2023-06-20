package ian.jungmann.ij0292.dto;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;

class ToolRentalAgreementResponseDtoTest {

    @Test
    void testToString() {
        String responsesString = ToolRentalAgreementResponseDto.builder()
                .id(1L)
                .code("LADW")
                .type("Ladder")
                .brand("Werner")
                .rentalDays(3)
                .checkoutDate(LocalDate.of(2020, 7, 2))
                .dueDate(LocalDate.of(2020, 7, 2))
                .dailyRentalCharge(BigDecimal.valueOf(1.99))
                .chargeDays(2)
                .preDiscountCharge(BigDecimal.valueOf(3.98))
                .discountPercent(10)
                .discountAmount(BigDecimal.valueOf(0.40))
                .finalCharge(BigDecimal.valueOf(3.58))
                .build()
                .toString();
        assertEquals(
                "Tool code: LADW\n" +
                "Tool type: Ladder\n" +
                "Tool brand: Werner\n" +
                "Rental days: 3\n" +
                "Checkout date: 07/02/20\n" +
                "Due date: 07/02/20\n" +
                "Daily rental charge: $1.99\n" +
                "Charge days: 2\n" +
                "Pre-discount charge: $3.98\n" +
                "Discount percent: 10%\n" +
                "Discount amount: $0.40\n" +
                "Final charge: $3.58\n",
                responsesString
        );
    }

}