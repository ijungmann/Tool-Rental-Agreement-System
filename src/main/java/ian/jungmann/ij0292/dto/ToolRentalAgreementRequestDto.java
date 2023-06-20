package ian.jungmann.ij0292.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(builderClassName = "Builder")
public class ToolRentalAgreementRequestDto {

    @NotBlank(message = "Tool code must not be blank")
    private String toolCode;
    @Min(value = 1, message = "Rental day count must be greater than 0")
    private Integer rentalDays;
    @Min(value = 0, message = "Discount percent must be greater than or equal to 0")
    @Max(value = 100, message = "Discount percent must be less than or equal to 100")
    private Integer discountPercent;
    @NotNull(message = "Checkout date must not be null")
    private LocalDate checkoutDate;

}
