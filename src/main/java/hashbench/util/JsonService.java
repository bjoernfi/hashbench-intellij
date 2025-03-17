package hashbench.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.Service;

@Service
public final class JsonService {

    private final ObjectMapper objectMapper = new ObjectMapper();

    public JsonService() {
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    public static JsonService getInstance() {
        return ApplicationManager.getApplication().getService(JsonService.class);
    }

    public <T> T parse(String v, Class<T> clazz) {
        try {
            return objectMapper.readValue(v, clazz);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public <T> String write(T obj) {
        try {
            return objectMapper.writer().withDefaultPrettyPrinter().writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
