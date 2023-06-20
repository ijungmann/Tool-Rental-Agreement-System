package ian.jungmann.ij0292.service;

import static java.time.temporal.TemporalAdjusters.firstInMonth;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import ian.jungmann.ij0292.dto.ToolRentalAgreementRequestDto;
import ian.jungmann.ij0292.dto.ToolRentalAgreementResponseDto;
import ian.jungmann.ij0292.entity.ToolEntity;
import ian.jungmann.ij0292.entity.ToolRentalAgreementEntity;
import ian.jungmann.ij0292.entity.ToolTypeEntity;
import ian.jungmann.ij0292.mapper.ToolRentalAgreementDtoEntityMapper;
import ian.jungmann.ij0292.repository.ToolRentalAgreementRepository;
import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ToolRentalAgreementServiceTest {
    @InjectMocks
    private ToolRentalAgreementService toolRentalAgreementService;
    @Mock
    private ToolRentalAgreementRepository toolRentalAgreementRepository;
    @Mock
    private ToolService toolService;
    @Mock
    private ToolRentalAgreementDtoEntityMapper mapper;

    @Test
    void shouldCountDate() {
        ToolTypeEntity toolTypeEntity = ToolTypeEntity.builder()
                .holidayCharge(true)
                .weekendCharge(false)
                .weekdayCharge(false)
                .build();
        ToolEntity toolEntity = ToolEntity.builder()
                .type(toolTypeEntity)
                .build();

        Set<LocalDate> holidays = Set.of(LocalDate.of(2023, 1, 1));

        // Count a holiday
        assertTrue(toolRentalAgreementService.shouldCountDate(toolEntity,
                LocalDate.of(2023, 1, 1), holidays));
        // Don't count a weekend
        assertFalse(toolRentalAgreementService.shouldCountDate(toolEntity,
                LocalDate.of(2023, 1, 1).with(firstInMonth(DayOfWeek.SATURDAY)), Set.of()));
        // Don't count a weekday
        assertFalse(toolRentalAgreementService.shouldCountDate(toolEntity,
                LocalDate.of(2023, 1, 1).with(firstInMonth(DayOfWeek.MONDAY)), Set.of()));

        toolEntity = toolEntity.toBuilder()
                .type(toolTypeEntity.toBuilder()
                        .holidayCharge(false)
                        .weekendCharge(true)
                        .weekdayCharge(false)
                        .build())
                .build();

        // Don't count a holiday
        assertFalse(toolRentalAgreementService.shouldCountDate(toolEntity,
                LocalDate.of(2023, 1, 1), holidays));
        // Count a weekend
        assertTrue(toolRentalAgreementService.shouldCountDate(toolEntity,
                LocalDate.of(2023, 1, 1).with(firstInMonth(DayOfWeek.SATURDAY)), Set.of()));
        // Don't count a weekday
        assertFalse(toolRentalAgreementService.shouldCountDate(toolEntity,
                LocalDate.of(2023, 1, 1).with(firstInMonth(DayOfWeek.MONDAY)), Set.of()));

        toolEntity = toolEntity.toBuilder()
                .type(toolTypeEntity.toBuilder()
                        .holidayCharge(false)
                        .weekendCharge(false)
                        .weekdayCharge(true)
                        .build())
                .build();

        // Don't count a holiday
        assertFalse(toolRentalAgreementService.shouldCountDate(toolEntity,
                LocalDate.of(2023, 1, 1), holidays));
        // Don't count a weekend
        assertFalse(toolRentalAgreementService.shouldCountDate(toolEntity,
                LocalDate.of(2023, 1, 1).with(firstInMonth(DayOfWeek.SATURDAY)), Set.of()));
        // Count a weekday
        assertTrue(toolRentalAgreementService.shouldCountDate(toolEntity,
                LocalDate.of(2023, 1, 1).with(firstInMonth(DayOfWeek.MONDAY)), Set.of()));
    }


    @Test
    void createRentalAgreement__LADW() {
        mockToolEntityLADW();
        stubMapper();
        ToolRentalAgreementRequestDto requestDto = ToolRentalAgreementRequestDto.builder()
                .toolCode("LADW")
                .checkoutDate(LocalDate.of(2020, 7, 2))
                .rentalDays(3)
                .discountPercent(10)
                .build();
        when(toolRentalAgreementRepository.save(any())).thenAnswer(invocation -> {
            ToolRentalAgreementEntity entity = invocation.getArgument(0);
            entity.setId(1L);
            return entity;
        });
        ToolRentalAgreementResponseDto dto =
                toolRentalAgreementService.createToolRentalAgreement(requestDto);
        ToolRentalAgreementResponseDto expected = ToolRentalAgreementResponseDto.builder()
                .id(1L)
                .brand("Werner")
                .type("Ladder")
                .code("LADW")
                .dailyRentalCharge(BigDecimal.valueOf(1.99).setScale(2))
                .rentalDays(3)
                .chargeDays(2)
                .preDiscountCharge(BigDecimal.valueOf(3.98).setScale(2))
                .discountAmount(BigDecimal.valueOf(0.40).setScale(2))
                .discountPercent(10)
                .finalCharge(BigDecimal.valueOf(3.58).setScale(2))
                .checkoutDate(LocalDate.of(2020, 7, 2))
                .dueDate(LocalDate.of(2020, 7, 5))
                .build();

        assertThat(dto).usingRecursiveComparison().isEqualTo(expected);
    }

    @Test
    void createRentalAgreement__CHNS() {
        mockToolEntityCHNS();
        stubMapper();
        ToolRentalAgreementRequestDto requestDto = ToolRentalAgreementRequestDto.builder()
                .toolCode("CHNS")
                .checkoutDate(LocalDate.of(2015, 7, 2))
                .rentalDays(5)
                .discountPercent(25)
                .build();
        when(toolRentalAgreementRepository.save(any())).thenAnswer(invocation -> {
            ToolRentalAgreementEntity entity = invocation.getArgument(0);
            entity.setId(1L);
            return entity;
        });
        ToolRentalAgreementResponseDto dto =
                toolRentalAgreementService.createToolRentalAgreement(requestDto);
        ToolRentalAgreementResponseDto expected = ToolRentalAgreementResponseDto.builder()
                .id(dto.getId())
                .brand("Stihl")
                .type("Chainsaw")
                .code("CHNS")
                .dailyRentalCharge(BigDecimal.valueOf(1.49).setScale(2))
                .rentalDays(5)
                .chargeDays(3)
                .preDiscountCharge(BigDecimal.valueOf(4.47).setScale(2))
                .discountAmount(BigDecimal.valueOf(1.12).setScale(2))
                .discountPercent(25)
                .finalCharge(BigDecimal.valueOf(3.35).setScale(2))
                .checkoutDate(LocalDate.of(2015, 7, 2))
                .dueDate(LocalDate.of(2015, 7, 7))
                .build();

        assertThat(dto).usingRecursiveComparison().isEqualTo(expected);
    }

    @Test
    void createRentalAgreement__JAKD() {
        mockToolEntityJAKD();
        stubMapper();
        ToolRentalAgreementRequestDto requestDto = ToolRentalAgreementRequestDto.builder()
                .toolCode("JAKD")
                .checkoutDate(LocalDate.of(2015, 9, 3))
                .rentalDays(6)
                .discountPercent(0)
                .build();
        when(toolRentalAgreementRepository.save(any())).thenAnswer(invocation -> {
            ToolRentalAgreementEntity entity = invocation.getArgument(0);
            entity.setId(1L);
            return entity;
        });
        ToolRentalAgreementResponseDto dto =
                toolRentalAgreementService.createToolRentalAgreement(requestDto);
        ToolRentalAgreementResponseDto expected = ToolRentalAgreementResponseDto.builder()
                .id(dto.getId())
                .brand("DeWalt")
                .type("Jackhammer")
                .code("JAKD")
                .dailyRentalCharge(BigDecimal.valueOf(2.99).setScale(2))
                .rentalDays(6)
                .chargeDays(3)
                .preDiscountCharge(BigDecimal.valueOf(8.97).setScale(2))
                .discountAmount(BigDecimal.valueOf(0).setScale(2))
                .discountPercent(0)
                .finalCharge(BigDecimal.valueOf(8.97).setScale(2))
                .checkoutDate(LocalDate.of(2015, 9, 3))
                .dueDate(LocalDate.of(2015, 9, 9))
                .build();

        assertThat(dto).usingRecursiveComparison().isEqualTo(expected);
    }

    @Test
    void createRentalAgreement__JAKR__NoDiscount() {
        mockToolEntityJAKR();
        stubMapper();
        ToolRentalAgreementRequestDto requestDto = ToolRentalAgreementRequestDto.builder()
                .toolCode("JAKR")
                .checkoutDate(LocalDate.of(2015, 7, 3))
                .rentalDays(9)
                .discountPercent(0)
                .build();
        when(toolRentalAgreementRepository.save(any())).thenAnswer(invocation -> {
            ToolRentalAgreementEntity entity = invocation.getArgument(0);
            entity.setId(1L);
            return entity;
        });
        ToolRentalAgreementResponseDto dto =
                toolRentalAgreementService.createToolRentalAgreement(requestDto);
        ToolRentalAgreementResponseDto expected = ToolRentalAgreementResponseDto.builder()
                .id(dto.getId())
                .brand("Ridgid")
                .type("Jackhammer")
                .code("JAKR")
                .dailyRentalCharge(BigDecimal.valueOf(2.99).setScale(2))
                .rentalDays(9)
                .chargeDays(5)
                .preDiscountCharge(BigDecimal.valueOf(14.95).setScale(2))
                .discountAmount(BigDecimal.valueOf(0).setScale(2))
                .discountPercent(0)
                .finalCharge(BigDecimal.valueOf(14.95).setScale(2))
                .checkoutDate(LocalDate.of(2015, 7, 3))
                .dueDate(LocalDate.of(2015, 7, 12))
                .build();

        assertThat(dto).usingRecursiveComparison().isEqualTo(expected);
    }
    @Test
    void createRentalAgreement__JAKR__FiftyPercentDiscount() {
        mockToolEntityJAKR();
        stubMapper();
        ToolRentalAgreementRequestDto requestDto = ToolRentalAgreementRequestDto.builder()
                .toolCode("JAKR")
                .checkoutDate(LocalDate.of(2020, 7, 2))
                .rentalDays(4)
                .discountPercent(50)
                .build();
        when(toolRentalAgreementRepository.save(any())).thenAnswer(invocation -> {
            ToolRentalAgreementEntity entity = invocation.getArgument(0);
            entity.setId(1L);
            return entity;
        });
        ToolRentalAgreementResponseDto dto =
                toolRentalAgreementService.createToolRentalAgreement(requestDto);
        ToolRentalAgreementResponseDto expected = ToolRentalAgreementResponseDto.builder()
                .id(dto.getId())
                .brand("Ridgid")
                .type("Jackhammer")
                .code("JAKR")
                .dailyRentalCharge(BigDecimal.valueOf(2.99).setScale(2))
                .rentalDays(4)
                .chargeDays(1)
                .preDiscountCharge(BigDecimal.valueOf(2.99).setScale(2))
                .discountAmount(BigDecimal.valueOf(1.50).setScale(2))
                .discountPercent(50)
                .finalCharge(BigDecimal.valueOf(1.49).setScale(2))
                .checkoutDate(LocalDate.of(2020, 7, 2))
                .dueDate(LocalDate.of(2020, 7, 6))
                .build();

        assertThat(dto).usingRecursiveComparison().isEqualTo(expected);
    }
    private ToolTypeEntity createToolTypeLadder() {
        return ToolTypeEntity.builder()
                .name("Ladder")
                .dailyCharge(BigDecimal.valueOf(1.99))
                .weekdayCharge(true)
                .weekendCharge(true)
                .holidayCharge(false)
                .build();
    }
    private ToolTypeEntity createToolTypeChainsaw() {
        return ToolTypeEntity.builder()
                .name("Chainsaw")
                .dailyCharge(BigDecimal.valueOf(1.49))
                .weekdayCharge(true)
                .weekendCharge(false)
                .holidayCharge(true)
                .build();
    }
    private ToolTypeEntity createToolTypeJackhammer() {
        return ToolTypeEntity.builder()
                .name("Jackhammer")
                .dailyCharge(BigDecimal.valueOf(2.99))
                .weekdayCharge(true)
                .weekendCharge(false)
                .holidayCharge(false)
                .build();
    }

    private void mockToolEntityLADW() {
        ToolEntity toolEntity = ToolEntity.builder()
                .code("LADW")
                .type(createToolTypeLadder())
                .brand("Werner")
                .build();
        when(toolService.getToolEntity("LADW")).thenReturn(toolEntity);
    }
    private void mockToolEntityCHNS() {
        ToolEntity toolEntity = ToolEntity.builder()
                .code("CHNS")
                .type(createToolTypeChainsaw())
                .brand("Stihl")
                .build();
        when(toolService.getToolEntity("CHNS")).thenReturn(toolEntity);
    }
    private void mockToolEntityJAKR() {
        ToolEntity toolEntity = ToolEntity.builder()
                .code("JAKR")
                .type(createToolTypeJackhammer())
                .brand("Ridgid")
                .build();
        when(toolService.getToolEntity("JAKR")).thenReturn(toolEntity);
    }
    private void mockToolEntityJAKD() {
        ToolEntity toolEntity = ToolEntity.builder()
                .code("JAKD")
                .type(createToolTypeJackhammer())
                .brand("DeWalt")
                .build();
        when(toolService.getToolEntity("JAKD")).thenReturn(toolEntity);
    }

    private void stubMapper() {
        when(mapper
                .mapRequestToEntity(any(ToolRentalAgreementRequestDto.class)))
                .thenAnswer(invocation -> {
                    ToolRentalAgreementRequestDto entity = invocation.getArgument(0);
                    return ToolRentalAgreementEntity.builder()
                            .rentalDays(entity.getRentalDays())
                            .checkoutDate(entity.getCheckoutDate())
                            .discountPercent(entity.getDiscountPercent())
                            . build();
                });
        when(mapper.mapEntityToResponse(any(ToolRentalAgreementEntity.class))).thenAnswer(
                invocation -> {
                    ToolRentalAgreementEntity entity = invocation.getArgument(0);
                    return ToolRentalAgreementResponseDto.builder()
                            .id(entity.getId())
                            .code(entity.getTool().getCode())
                            .type(entity.getTool().getType().getName())
                            .brand(entity.getTool().getBrand())
                            .dailyRentalCharge(entity.getTool().getType().getDailyCharge())
                            .chargeDays(entity.getChargeDays())
                            .preDiscountCharge(entity.getPreDiscountCharge())
                            .discountPercent(entity.getDiscountPercent())
                            .discountAmount(entity.getDiscountAmount())
                            .finalCharge(entity.getFinalCharge())
                            .checkoutDate(entity.getCheckoutDate())
                            .dueDate(entity.getDueDate())
                            .rentalDays(entity.getRentalDays())
                            .build();
                });

    }

}