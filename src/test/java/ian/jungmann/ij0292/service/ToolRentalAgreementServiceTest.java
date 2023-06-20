package ian.jungmann.ij0292.service;

import static java.time.temporal.TemporalAdjusters.firstInMonth;
import static org.junit.jupiter.api.Assertions.*;

import ian.jungmann.ij0292.entity.ToolEntity;
import ian.jungmann.ij0292.entity.ToolTypeEntity;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ToolRentalAgreementServiceTest {
    @InjectMocks
    private ToolRentalAgreementService toolRentalAgreementService;

    @Test
    void getHolidays() {
        //These dates were all found online to confirm against

        Set<LocalDate> holidays = toolRentalAgreementService.getHolidays(2025);
        //Labor day
        assertTrue(holidays.contains(LocalDate.of(2025, 9, 1)));
        //Independence day
        assertTrue(holidays.contains(LocalDate.of(2025, 7, 4)));

        holidays = toolRentalAgreementService.getHolidays(2020);
        //Labor day
        assertTrue(holidays.contains(LocalDate.of(2020, 9, 7)));
        //Independence day (on a Saturday, so observed on Friday)
        assertTrue(holidays.contains(LocalDate.of(2020, 7, 3)));

        holidays = toolRentalAgreementService.getHolidays(2021);
        //Labor day
        assertTrue(holidays.contains(LocalDate.of(2021, 9, 6)));
        //Independence day (on a Sunday, so observed on Monday)
        assertTrue(holidays.contains(LocalDate.of(2021, 7, 5)));
    }

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



}