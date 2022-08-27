package ru.practicum.shareit.requests;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.requests.dto.ItemRequestDto;

import javax.validation.Valid;
import java.util.Map;

public class RequestsClient extends BaseClient {

    private static final String API_PREFIX = "/requests";

    @Autowired
    public RequestsClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(builder.uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                .requestFactory(HttpComponentsClientHttpRequestFactory::new).build());
    }

    public ResponseEntity<Object> add(@RequestHeader("X-Sharer-User-Id") long userId,
                                      @Valid @RequestBody ItemRequestDto request) {
        return post("", userId, request);
    }

    public ResponseEntity<Object> getAllUserRequest(@RequestHeader("X-Sharer-User-Id") long userId) {
        return get("", userId);
    }

    public ResponseEntity<Object> getAllRequest(@RequestHeader("X-Sharer-User-Id") long userId,
                                                @RequestParam(required = false, defaultValue = "0") int from,
                                                @RequestParam(required = false, defaultValue = "20") int size) {
        Map<String, Object> parameters = Map.of(
                "from", from,
                "size", size
        );
        return get("/all", userId, parameters);
    }

    public ResponseEntity<Object> getRequest(@RequestHeader("X-Sharer-User-Id") long userId,
                                             @PathVariable @Valid Long requestId) {
        return get("/" + requestId, userId);
    }
}
