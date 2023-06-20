package ian.jungmann.ij0292.service;

import static ian.jungmann.ij0292.util.DateUtils.WEEKDAYS;
import static ian.jungmann.ij0292.util.DateUtils.WEEKENDS;
import static ian.jungmann.ij0292.util.DateUtils.getHolidays;

import ian.jungmann.ij0292.dto.ToolRentalAgreementRequestDto;
import ian.jungmann.ij0292.dto.ToolRentalAgreementResponseDto;
import ian.jungmann.ij0292.entity.ToolEntity;
import ian.jungmann.ij0292.entity.ToolRentalAgreementEntity;
import ian.jungmann.ij0292.mapper.ToolRentalAgreementDtoEntityMapper;
import ian.jungmann.ij0292.repository.ToolRentalAgreementRepository;
import java.math.BigDecimal;
import java.math.RoundingMode;
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
    private final ToolRentalAgreementDtoEntityMapper mapper;

    public ToolRentalAgreementResponseDto createToolRentalAgreement(ToolRentalAgreementRequestDto requestDto) {
        // Fetch the tool entity based on the code.
        ToolEntity toolEntity = toolService.getToolEntity(requestDto.getToolCode());

        LocalDate requestDate = requestDto.getCheckoutDate();
        LocalDate dueDate = requestDto.getCheckoutDate().plusDays(requestDto.getRentalDays());

        // This is to prevent us from recalculating the same holidays over and over again.
        // This could be a cache for the class if we feel the calculation time vs memory tradeoff is worth it.
        Map<Integer, Set<LocalDate>> yearToHolidays = new HashMap<>();
        yearToHolidays.put(requestDate.getYear(), getHolidays(requestDate.getYear()));
        AtomicInteger daysChargedAtomic = new AtomicInteger(0);

        // Go through the date between checkout and due, excluding the due date, to calculate how many days to charge for.
        requestDto.getCheckoutDate().datesUntil(dueDate).forEach(date -> {
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
                .dueDate(dueDate)
                .chargeDays(daysChargedAtomic.get())
                .preDiscountCharge(initialCost)
                .discountAmount(discountAmount)
                .finalCharge(calculateFinalCost(initialCost, discountAmount))
                .build();
        entity = toolRentalAgreementRepository.save(entity);
        return mapper.mapEntityToResponse(entity);
    }

    // initial cost minus discount amount
    private static BigDecimal calculateFinalCost(BigDecimal initialCost, BigDecimal discountAmount) {
        return BigDecimal.valueOf(initialCost.doubleValue() - discountAmount.doubleValue())
                .setScale(2, RoundingMode.HALF_UP);
    }

    // initial cost times discount percent
    private static BigDecimal calculateDiscountAmount(ToolRentalAgreementRequestDto requestDto, BigDecimal initialCost) {
        return BigDecimal.valueOf(initialCost.doubleValue() * (requestDto.getDiscountPercent() / 100.0))
                .setScale(2, RoundingMode.HALF_UP);
    }

    // days charged times daily charge
    private static BigDecimal calculateInitialCost(ToolEntity toolEntity, AtomicInteger daysChargedAtomic) {
        return BigDecimal.valueOf(daysChargedAtomic.get() * toolEntity.getType().getDailyCharge().doubleValue())
                .setScale(2, RoundingMode.HALF_UP);
    }

    // Check if the provided date should be counted towards the charge days.
    protected boolean shouldCountDate(ToolEntity toolEntity, LocalDate date, Set<LocalDate> holidays) {
        // Check if the date is a holiday.  If it is, regardless of whether we change or not, we need to skip the
        // weekend and weekday checks.  This is why it's broken up into two checks.
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
}