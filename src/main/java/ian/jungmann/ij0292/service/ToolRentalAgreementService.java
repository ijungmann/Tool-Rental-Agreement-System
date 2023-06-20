package ian.jungmann.ij0292.service;

import static java.time.temporal.TemporalAdjusters.firstInMonth;

import ian.jungmann.ij0292.dto.ToolRentalAgreementRequestDto;
import ian.jungmann.ij0292.dto.ToolRentalAgreementResponseDto;
import ian.jungmann.ij0292.entity.ToolEntity;
import ian.jungmann.ij0292.entity.ToolRentalAgreementEntity;
import ian.jungmann.ij0292.mapper.ToolRentalAgreementDtoEntityMapper;
import ian.jungmann.ij0292.repository.ToolRentalAgreementRepository;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class ToolRentalAgreementService {

    private final ToolRentalAgreementRepository toolRentalAgreementRepository;
    private final ToolService toolService;
    private ToolRentalAgreementDtoEntityMapper mapper;

    private static final Set<DayOfWeek> WEEKENDS = Set.of(DayOfWeek.SATURDAY, DayOfWeek.SUNDAY);
    private static final Set<DayOfWeek> WEEKDAYS = Set.of(DayOfWeek.MONDAY, DayOfWeek.TUESDAY,
            DayOfWeek.WEDNESDAY, DayOfWeek.THURSDAY, DayOfWeek.FRIDAY);

    public ToolRentalAgreementResponseDto createToolRentalAgreement(ToolRentalAgreementRequestDto requestDto) {
        ToolEntity toolEntity = toolService.getToolReference(requestDto.getToolCode());

        LocalDate requestDate = requestDto.getCheckoutDate();
        LocalDate returnDate = requestDto.getCheckoutDate().plusDays(requestDto.getRentalDays());

        Map<Integer, Set<LocalDate>> yearToHolidays = new HashMap<>();
        yearToHolidays.put(requestDate.getYear(), getHolidays(requestDate.getYear()));
        AtomicInteger daysChargedAtomic = new AtomicInteger(0);

        requestDto.getCheckoutDate().datesUntil(returnDate).forEach(date -> {
            Set<LocalDate> holidays = yearToHolidays.get(date.getYear());
            if (holidays == null) {
                holidays = getHolidays(date.getYear());
                yearToHolidays.put(date.getYear(), holidays);
            }
            if(shouldCountDate(toolEntity, date, holidays)) {
                daysChargedAtomic.incrementAndGet();
            };
        });
        BigDecimal initialCost = calculateInitialCost(toolEntity, daysChargedAtomic);
        BigDecimal discountAmount = calculateDiscountAmount(requestDto, initialCost);
        ToolRentalAgreementEntity entity = mapper.mapRequestToEntity(requestDto)
                .toBuilder()
                .tool(toolEntity)
                .dueDate(requestDto.getCheckoutDate().plusDays(requestDto.getRentalDays()))
                .chargeDays(daysChargedAtomic.get())
                .preDiscountCharge(initialCost)
                .discountAmount(discountAmount)
                .finalCharge(calculateFinalCost(initialCost, discountAmount))
                .build();
        entity = toolRentalAgreementRepository.save(entity);
        return mapper.mapEntityToResponse(entity);
    }

    protected static BigDecimal calculateFinalCost(BigDecimal initialCost, BigDecimal discountAmount) {
        return BigDecimal.valueOf(initialCost.doubleValue() - discountAmount.doubleValue())
                .setScale(2, RoundingMode.HALF_UP);
    }

    protected static BigDecimal calculateDiscountAmount(ToolRentalAgreementRequestDto requestDto, BigDecimal initialCost) {
        return BigDecimal.valueOf(initialCost.doubleValue() * (requestDto.getDiscountPercent() / 100.0))
                .setScale(2, RoundingMode.HALF_UP);
    }

    protected static BigDecimal calculateInitialCost(ToolEntity toolEntity, AtomicInteger daysChargedAtomic) {
        return BigDecimal.valueOf(daysChargedAtomic.get() * toolEntity.getType().getDailyCharge().doubleValue())
                .setScale(2, RoundingMode.HALF_UP);
    }

    protected boolean shouldCountDate(ToolEntity toolEntity, LocalDate date, Set<LocalDate> holidays) {
        if (holidays.contains(date)) {
            if (toolEntity.getType().isHolidayCharge()) {
                return true;
            }
        } else if (WEEKENDS.contains(date.getDayOfWeek()) && toolEntity.getType().isWeekendCharge()) {
            return true;
        } else if (WEEKDAYS.contains(date.getDayOfWeek()) && toolEntity.getType().isWeekdayCharge()) {
            return true;
        }
        return false;
    }
    protected Set<LocalDate> getHolidays(int year) {
        return Set.of(
                getIndependenceDay(year),
                getLaborDay(year)
        );
    }

    private LocalDate getLaborDay(int year) {

        return LocalDate.of(year, 9, 1)
                .with(firstInMonth(DayOfWeek.MONDAY));
    }

    private LocalDate getIndependenceDay(int year) {
        LocalDate fourthOfJuly = LocalDate.of(year, 7, 4);
        if (fourthOfJuly.getDayOfWeek() == DayOfWeek.SATURDAY) {
            return fourthOfJuly.minusDays(1);
        } else if (fourthOfJuly.getDayOfWeek() == DayOfWeek.SUNDAY) {
            return fourthOfJuly.plusDays(1);
        } else {
            return fourthOfJuly;
        }
    }
}