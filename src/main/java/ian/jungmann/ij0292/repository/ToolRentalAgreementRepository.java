package ian.jungmann.ij0292.repository;

import ian.jungmann.ij0292.entity.ToolRentalAgreementEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ToolRentalAgreementRepository extends JpaRepository<ToolRentalAgreementEntity, Long> {

}
