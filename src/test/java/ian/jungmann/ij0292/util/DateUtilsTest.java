package ian.jungmann.ij0292.util;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.util.Set;
import org.junit.jupiter.api.Test;

class DateUtilsTest {

    @Test
    void getHolidays() {
        //These dates were all found online to confirm against

        Set<LocalDate> holidays = DateUtils.getHolidays(2025);
        //Labor day
        assertTrue(holidays.contains(LocalDate.of(2025, 9, 1)));
        //Independence day
        assertTrue(holidays.contains(LocalDate.of(2025, 7, 4)));

        holidays = DateUtils.getHolidays(2020);
        //Labor day
        assertTrue(holidays.contains(LocalDate.of(2020, 9, 7)));
        //Independence day (on a Saturday, so observed on Friday)
        assertTrue(holidays.contains(LocalDate.of(2020, 7, 3)));

        holidays = DateUtils.getHolidays(2021);
        //Labor day
        assertTrue(holidays.contains(LocalDate.of(2021, 9, 6)));
        //Independence day (on a Sunday, so observed on Monday)
        assertTrue(holidays.contains(LocalDate.of(2021, 7, 5)));
    }

}