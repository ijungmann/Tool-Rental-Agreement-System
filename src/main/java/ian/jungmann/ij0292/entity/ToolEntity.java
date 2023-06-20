package ian.jungmann.ij0292.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "tool")
@Builder(builderClassName = "Builder", toBuilder = true)
public class ToolEntity {

    @Id
    private String code;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "type")
    private ToolTypeEntity type;

    private String brand;

}
