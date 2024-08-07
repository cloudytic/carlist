package formatters;

import play.data.format.Formatters;

import java.util.Locale;

public class ChkFormatter extends Formatters.AnnotationFormatter<ChkFormat, Boolean>{

	@Override
	public Boolean parse(ChkFormat arg0, String text, Locale arg2) {
		return text.equals("on");
	}

	@Override
	public String print(ChkFormat arg0, Boolean object, Locale arg2) {
		return object ? "on" : "";
	}
}
