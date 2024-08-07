package formatters;

import play.data.format.Formatters;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DateFormatter extends Formatters.AnnotationFormatter<DateFormat, Date>{
	@Override
	public Date parse(DateFormat arg0, String text, Locale arg2) {
		String format = arg0.format();
		SimpleDateFormat formatter = new SimpleDateFormat(format);
		try {
			return formatter.parse(text);
		} catch (Exception e) { return null; }
	}

	@Override
	public String print(DateFormat arg0, Date object, Locale arg2) {
		String format = arg0.format();
		SimpleDateFormat formatter = new SimpleDateFormat(format);
		try {
			return formatter.format(object);
		} catch (Exception e) { return null; }
	}

	static SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
	public static Date convert(String text) {
		try {
			return formatter.parse(text);
		} catch (Exception e) { return null; }
	}
}
