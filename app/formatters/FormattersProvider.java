package formatters;

import play.data.format.Formatters;
import play.data.format.Formatters.SimpleFormatter;
import play.i18n.MessagesApi;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;
import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Singleton
public class FormattersProvider implements Provider<Formatters> {

    private final MessagesApi messagesApi;

    @Inject
    public FormattersProvider(MessagesApi messagesApi) {
        this.messagesApi = messagesApi;
    }

    @Override
    public Formatters get() {
        Formatters formatters = new Formatters(messagesApi);

        formatters.register(Double.class, new AmtFormatter());
        formatters.register(Integer.class, new IntFormatter());
        formatters.register(Date.class, new DateFormatter());
        formatters.register(Object.class, new ModelFormatter());
        formatters.register(List.class, new CommaFormatter());
        formatters.register(Boolean.class, new ChkFormatter());

        return formatters;
    }
}