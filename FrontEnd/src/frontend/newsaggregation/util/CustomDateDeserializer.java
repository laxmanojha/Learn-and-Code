package frontend.newsaggregation.util;

import com.google.gson.*;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.*;

public class CustomDateDeserializer implements JsonDeserializer<Date> {
    private static final List<String> formats = List.of(
        "yyyy-MM-dd",
        "MMM d, yyyy",     // "Jul 4, 2025"
        "MMMM d, yyyy"     // "July 4, 2025"
    );

    @Override
    public Date deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
            throws JsonParseException {
        String dateStr = json.getAsString();
        for (String format : formats) {
            try {
                return new SimpleDateFormat(format, Locale.ENGLISH).parse(dateStr);
            } catch (Exception ignored) {}
        }
        throw new JsonParseException("Unparseable date: " + dateStr);
    }
}

