package ian.jungmann.ij0292.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

import ian.jungmann.ij0292.entity.ToolEntity;
import ian.jungmann.ij0292.repository.ToolRepository;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ToolServiceTest {
    @InjectMocks
    ToolService toolService;
    @Mock
    ToolRepository toolRepository;

    @Test
    void getToolEntity() {
        ToolEntity toolEntity = new ToolEntity();
        when(toolRepository.findById("LADW")).thenReturn(Optional.of(toolEntity));
        assertEquals(toolEntity, toolService.getToolEntity("LADW"));
    }

    @Test
    void getMissingToolEntity() {
        when(toolRepository.findById("LADW")).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> toolService.getToolEntity("LADW"));
    }

}