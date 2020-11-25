package uy.gub.agesic.pdi.pys.fil.evaluator.formatter;

import org.springframework.stereotype.Component;
import uy.gub.agesic.pdi.pys.domain.Factor;
import uy.gub.agesic.pdi.pys.fil.evaluator.Formatter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class DateFormatter implements Formatter {
    private final List<String> formatsDateTime = Stream.of(
            "yyyyMMddHHmmss",
            "yyyy-MM-dd HHmmss",
            "dd-MM-yyyyHHmmss",
            "dd-MM-yyyy HHmmss"
    ).collect(Collectors.toList());

    private final List<String> formatsDate = Stream.of(
            "yyyy-MM-dd",
            "dd-MM-yyyy"
    ).collect(Collectors.toList());

    @Override
    public Factor.ValueType getType() {
        return Factor.ValueType.DATETIME;
    }

    @Override
    public String format(String value, String format) {
        long second;

        if (this.formatsDateTime.contains(format)) {
            second = LocalDateTime.parse(value, DateTimeFormatter.ofPattern(format))
                    .atZone(ZoneId.systemDefault())
                    .toEpochSecond();
        } else  if (this.formatsDate.contains(format)) {
            second = LocalDate.parse(value, DateTimeFormatter.ofPattern(format))
                    .atStartOfDay(ZoneId.systemDefault())
                    .toEpochSecond();
        } else {
            second = LocalTime.parse(value, DateTimeFormatter.ofPattern(format))
                    .toSecondOfDay();
        }

        return String.valueOf(second);
    }
}
