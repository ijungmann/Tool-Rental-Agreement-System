package ian.jungmann.ij0292.service;

import ian.jungmann.ij0292.entity.ToolEntity;
import ian.jungmann.ij0292.repository.ToolRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class ToolService {

    private ToolRepository toolRepository;

    public ToolEntity getToolReference(String code) {
        return toolRepository.getReferenceById(code);
    }

}
