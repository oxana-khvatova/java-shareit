package ru.practicum.shareit.item;

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
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import javax.validation.Valid;
import java.util.Map;

public class ItemClient extends BaseClient {
    private static final String API_PREFIX = "/items";

    @Autowired
    public ItemClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }

    public ResponseEntity<Object> add(@RequestHeader("X-Sharer-User-Id") long userId,
                                      @Valid @RequestBody ItemDto item) {
        return post("", userId, item);
    }

    public ResponseEntity<Object> update(@RequestHeader("X-Sharer-User-Id") long userId,
                                         @PathVariable long itemId,
                                         @Valid @RequestBody ItemDto item) {
        return patch("/" + itemId, userId, item);
    }

    public ResponseEntity<Object> getAll(@RequestHeader("X-Sharer-User-Id") long idUser,
                                         @RequestParam(required = false, defaultValue = "0") int from,
                                         @RequestParam(required = false, defaultValue = "20") int size) {
        Map<String, Object> parameters = Map.of(
                "from", from,
                "size", size
        );
        return get("", idUser, parameters);
    }

    public ResponseEntity<Object> getItem(@RequestHeader("X-Sharer-User-Id") long idUser,
                                          @PathVariable long itemId) {
        return get("/" + itemId, idUser);
    }

    public ResponseEntity<Object> getItemSearch(@RequestHeader("X-Sharer-User-Id") long idUser,
                                                @RequestParam(required = false) String text,
                                                @RequestParam(required = false, defaultValue = "0") int from,
                                                @RequestParam(required = false, defaultValue = "20") int size) {
        Map<String, Object> parameters = Map.of(
                "text", text,
                "from", from,
                "size", size
        );
        return get("/search", idUser, parameters);
    }

    public ResponseEntity<Object> addComment(@RequestHeader("X-Sharer-User-Id") long userId,
                                             @PathVariable long itemId,
                                             @Valid @RequestBody CommentDto comments) {
        return post("/" + itemId + "/comment", userId, comments);
    }
}
