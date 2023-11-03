package ru.practicum.shareit.request;

import org.springframework.beans.factory.annotation.*;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.core.QueryParametersInterface;
import ru.practicum.shareit.request.dto.RequestDto;

import java.util.Map;

@Service
public class RequestClient extends BaseClient {
    private static final String API_PREFIX = "/requests";

    @Autowired
    public RequestClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
            builder
                .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                .build()
        );
    }

    public ResponseEntity<Object> save(long userId, RequestDto dto) {
        return post("", userId, dto);
    }

    public ResponseEntity<Object> findById(long userId, long requestId) {
        return get("/" + requestId, userId);
    }

    public ResponseEntity<Object> findAll(long userId) {
        return get("", userId);
    }

    public ResponseEntity<Object> findAllFromOtherUsers(long userId, int from, int size) {
        Map<String, Object> parameters = Map.of(
            QueryParametersInterface.FROM, from,
            QueryParametersInterface.SIZE, size
        );

        return get("/all?from={from}&size={size}", userId, parameters);
    }
}