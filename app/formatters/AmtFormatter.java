package formatters;

import play.data.format.Formatters;

import java.text.DecimalFormat;
import java.util.Locale;

public class AmtFormatter extends Formatters.AnnotationFormatter<AmtFormat, Double>{

	@Override
	public Double parse(AmtFormat arg0, String text, Locale arg2) {
		return Double.parseDouble(text.replaceAll(",", ""));
	}

	@Override
	public String print(AmtFormat arg0, Double object, Locale arg2) {
		DecimalFormat df = new DecimalFormat("#,###");
		return df.format(object);
	}
}
