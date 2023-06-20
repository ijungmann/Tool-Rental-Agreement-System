package ian.jungmann.ij0292.repository;

import ian.jungmann.ij0292.entity.ToolEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ToolRepository extends JpaRepository<ToolEntity, String> {

}
