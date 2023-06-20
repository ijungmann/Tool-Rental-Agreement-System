package ian.jungmann.ij0292.util;

import static java.time.temporal.TemporalAdjusters.firstInMonth;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Set;
import lombok.experimental.UtilityClass;

@UtilityClass
public class DateUtils {
    public static DateTimeFormatter MMddyyFormatter = DateTimeFormatter.ofPattern("MM/dd/yy");
    public static final Set<DayOfWeek> WEEKENDS = Set.of(DayOfWeek.SATURDAY, DayOfWeek.SUNDAY);
    public static final Set<DayOfWeek> WEEKDAYS = Set.of(DayOfWeek.MONDAY, DayOfWeek.TUESDAY,
            DayOfWeek.WEDNESDAY, DayOfWeek.THURSDAY, DayOfWeek.FRIDAY);

    public static String formatDateAsMMddyy(LocalDate date) {
        return date.format(MMddyyFormatter);
    }

    // Get holidays for a given year.
    public static Set<LocalDate> getHolidays(int year) {
        return Set.of(
                getIndependenceDay(year),
                getLaborDay(year)
        );
    }

    // Labor day is the first Monday in September.
    protected static LocalDate getLaborDay(int year) {
        return LocalDate.of(year, 9, 1)
                .with(firstInMonth(DayOfWeek.MONDAY));
    }

    // Independence day is July 4th, unless it falls on a weekend, in which case it is the closest weekday.
    protected static LocalDate getIndependenceDay(int year) {
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
