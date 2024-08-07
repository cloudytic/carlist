package converters;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import pojos.PSAuth;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import java.lang.reflect.Type;

@Converter
public class PSAuthConverter implements AttributeConverter<PSAuth, String> {
    private final static Gson GSON = new Gson();
    private Type type = new TypeToken<PSAuth>() {}.getType();
    @Override
    public String convertToDatabaseColumn(PSAuth auth) {
        try {
            if(auth == null) return "";
            return GSON.toJson(auth, type);
        } catch (Exception ex) {
            return null;
        }
    }

    @Override
    public PSAuth convertToEntityAttribute(String dbData) {
        try {
            if(dbData == null) return new PSAuth();
            PSAuth auth = GSON.fromJson(dbData, type);
            return auth;

        } catch (Exception ex) {
            return null;
        }
    }
}
