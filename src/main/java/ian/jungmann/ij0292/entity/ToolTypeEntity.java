package ian.jungmann.ij0292.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
@Data
@Table(name = "tool_type")
public class ToolTypeEntity {

    @Id
    private String name;

    private BigDecimal dailyCharge;

    private boolean weekdayCharge;

    private boolean weekendCharge;

    private boolean holidayCharge;
}
