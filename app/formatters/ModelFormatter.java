package formatters;

import models.ModelInterface;
import org.apache.commons.lang3.reflect.FieldUtils;
import play.data.format.Formatters;
import services.DB;

import java.util.Locale;

public class ModelFormatter extends Formatters.AnnotationFormatter<ModelFormat, Object> {
	@Override
	public Object parse(ModelFormat clazz, String id, Locale arg2) {
		return DB.findOne((Class<ModelInterface>)clazz.value(), id);
	}

	@Override
	public String print(ModelFormat clazz, Object model, Locale arg2) {
		try {
			return FieldUtils.readField(model, "id", true).toString();
		} catch (IllegalAccessException e) {
		}
		throw new IllegalArgumentException("Cannot convert " + model + " into String id!");
	}
}
