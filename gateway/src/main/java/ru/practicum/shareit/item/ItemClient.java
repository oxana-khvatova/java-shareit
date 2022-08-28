package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemForUpdate;

import javax.validation.Valid;
import java.util.Map;

@Service
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

    public ResponseEntity<Object> add(long userId,
                                      ItemDto item) {
        return post("", userId, item);
    }

    public ResponseEntity<Object> update(long userId,
                                         long itemId,
                                         ItemForUpdate item) {
        return patch("/" + itemId, userId, item);
    }

    public ResponseEntity<Object> getAll(long idUser,
                                         int from,
                                         int size) {
        Map<String, Object> parameters = Map.of(
                "from", from,
                "size", size
        );
        return get("", idUser, parameters);
    }

    public ResponseEntity<Object> getItem(long idUser,
                                          long itemId) {
        return get("/" + itemId, idUser);
    }

    public ResponseEntity<Object> getItemSearch(long idUser,
                                                String text,
                                                int from,
                                                int size) {
        Map<String, Object> parameters = Map.of(
                "text", text,
                "from", from,
                "size", size
        );
        return get("/search?text={text}&from={from}&size={size}", idUser, parameters);
    }

    public ResponseEntity<Object> addComment(long userId,
                                             long itemId,
                                             CommentDto comments) {
        return post("/" + itemId + "/comment", userId, comments);
    }
}
