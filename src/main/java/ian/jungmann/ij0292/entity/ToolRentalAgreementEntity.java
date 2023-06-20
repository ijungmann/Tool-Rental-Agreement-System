package ian.jungmann.ij0292.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
@Table(name = "tool_rental_agreement")
public class ToolRentalAgreementEntity {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    private ToolEntity tool;

    private Integer rentalDays;
    private LocalDate checkoutDate;
    private LocalDate dueDate;
    private Integer chargeDays;
    private BigDecimal preDiscountCharge;
    private Integer discountPercent;
    private BigDecimal discountAmount;
    private BigDecimal finalCharge;

}
