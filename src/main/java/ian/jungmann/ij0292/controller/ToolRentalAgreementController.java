package ian.jungmann.ij0292.controller;

import static org.apache.commons.lang3.ArrayUtils.toArray;

import ian.jungmann.ij0292.dto.ToolRentalAgreementRequestDto;
import ian.jungmann.ij0292.dto.ToolRentalAgreementResponseDto;
import ian.jungmann.ij0292.service.ToolRentalAgreementService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@AllArgsConstructor
public class ToolRentalAgreementController {
    private final ToolRentalAgreementService toolRentalAgreementService;

    // Handle ResponseStatusExceptions by building a message from the errors and wrapping them in a ResponseEntity
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity handleException(MethodArgumentNotValidException e) {
        String message = StringUtils.join(
                e.getBindingResult()
                        .getFieldErrors().stream()
                        .map(FieldError::getDefaultMessage)
                        .toArray(),
                "; ");
        return new ResponseEntity<>(message, HttpStatus.BAD_REQUEST);
    }

    // Handle ResponseStatusExceptions by wrapping them in a ResponseEntity
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity handleException(ResponseStatusException e) {
        return new ResponseEntity<>(e.getReason(), HttpStatus.BAD_REQUEST);
    }

    /**
     * POST to create a new tool rental agreement.
     */
    @PostMapping("tools/rental-agreements")
    public ToolRentalAgreementResponseDto createToolRentalAgreement(
            @Valid @RequestBody ToolRentalAgreementRequestDto toolRentalAgreementRequestDto) {
        return toolRentalAgreementService.createToolRentalAgreement(toolRentalAgreementRequestDto);
    }
}
