package ian.jungmann.ij0292.service;

import ian.jungmann.ij0292.entity.ToolEntity;
import ian.jungmann.ij0292.repository.ToolRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@AllArgsConstructor
public class ToolService {

    private ToolRepository toolRepository;

    public ToolEntity getToolEntity(String code) {
        return toolRepository.findById(code).orElseThrow(() ->
                new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        String.format("Tool with code %s not found", code)
        ));
    }

}
