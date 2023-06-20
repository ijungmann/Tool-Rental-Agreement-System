package ian.jungmann.ij0292.util;

import java.time.format.DateTimeFormatter;
import lombok.experimental.UtilityClass;

@UtilityClass
public class DateUtils {
    public static DateTimeFormatter MMddyyFormatter = DateTimeFormatter.ofPattern("MM/dd/yy");
}
