package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.*;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.core.QueryParametersInterface;
import ru.practicum.shareit.item.dto.*;

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

    public ResponseEntity<Object> saveItem(long userId, ItemRequestDto dto) {
        return post("", userId, dto);
    }

    public ResponseEntity<Object> saveComment(long userId, long itemId, CommentRequestDto dto) {
        return post("/" + itemId + "/comment", userId, dto);
    }

    public ResponseEntity<Object> getItems(long userId, int from, int size) {
        Map<String, Object> parameters = Map.of(
            QueryParametersInterface.FROM, from,
            QueryParametersInterface.SIZE, size
        );

        return get("?from={from}&size={size}", userId, parameters);
    }

    public ResponseEntity<Object> getItemById(long userId, long itemId) {
        return get("/" + itemId, userId);
    }

    public ResponseEntity<Object> updateItem(long userId, long itemId, ItemRequestDto dto) {
        return patch("/" + itemId, userId, dto);
    }

    public ResponseEntity<Object> search(long userId, String text, int from, int size) {
        Map<String, Object> parameters = Map.of(
            QueryParametersInterface.TEXT, text,
            QueryParametersInterface.FROM, from,
            QueryParametersInterface.SIZE, size
        );

        return get("/search?text={text}&from={from}&size={size}", userId, parameters);
    }
}