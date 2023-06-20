package ian.jungmann.ij0292.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true, builderClassName = "Builder")
public class ToolRentalAgreementResponseDto {
    private Long id;
    private String code;
    private String type;
    private String brand;
    private Integer rentalDays;
    private LocalDate checkoutDate;
    private LocalDate dueDate;
    private BigDecimal dailyRentalCharge;
    private Integer chargeDays;
    private BigDecimal preDiscountCharge;
    private Integer discountPercent;
    private BigDecimal discountAmount;
    private BigDecimal finalCharge;

    public String toString() {
        return String.format(
            "Tool code: %s\n" +
            "Tool type: %s\n" +
            "Tool brand: %s\n" +
            "Rental days: %d\n" +
            "Checkout date: %s\n" +
            "Due date: %s\n" +
            "Daily rental charge: $%.2f\n" +
            "Charge days: %d\n" +
            "Pre-discount charge: $%.2f\n" +
            "Discount percent: %d%%\n" +
            "Discount amount: $%.2f\n" +
            "Final charge: $%.2f\n",
            code,
            type,
            brand,
            rentalDays,
            checkoutDate,
            dueDate,
            dailyRentalCharge,
            chargeDays,
            preDiscountCharge,
            discountPercent,
            discountAmount,
            finalCharge
        );
    }
}
