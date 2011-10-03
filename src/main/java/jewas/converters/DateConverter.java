package jewas.converters;

import jewas.lang.Objects;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateConverter implements Converter<String, Date> {

    public static final String DATE_FORMAT = "yyyy-MM-dd";

    @Override
    public Date to(String from) {
        try {
            return new SimpleDateFormat(DATE_FORMAT).parse(from);
        } catch (ParseException e) {
            return Objects.NULL(Date.class);
        }
    }

    @Override
    public String from(Date to) {
        return new SimpleDateFormat(DATE_FORMAT).format(to);
    }
}
