package ru.practicum.shareit.booking;

import org.springframework.beans.factory.annotation.*;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.booking.dto.*;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.core.QueryParametersInterface;

import java.util.Map;

@Service
public class BookingClient extends BaseClient {
    private static final String API_PREFIX = "/bookings";

    @Autowired
    public BookingClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
            builder
                .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                .build()
        );
    }

    public ResponseEntity<Object> getBookings(long userId, BookingStatus state, Integer from, Integer size) {
        Map<String, Object> parameters = Map.of(
            QueryParametersInterface.STATE, state.name(),
            QueryParametersInterface.FROM, from,
            QueryParametersInterface.SIZE, size
        );

        return get("?state={state}&from={from}&size={size}", userId, parameters);
    }

    public ResponseEntity<Object> getBookingsForOwner(long userId, BookingStatus state, Integer from, Integer size) {
        Map<String, Object> parameters = Map.of(
            QueryParametersInterface.STATE, state.name(),
            QueryParametersInterface.FROM, from,
            QueryParametersInterface.SIZE, size
        );

        return get("/owner?state={state}&from={from}&size={size}", userId, parameters);
    }

    public ResponseEntity<Object> bookItem(long userId, ShortBookingRequestDto requestDto) {
        return post("", userId, requestDto);
    }

    public ResponseEntity<Object> approveBooking(long userId, long bookingId, boolean approved) {
        return patch("/" + bookingId + "?approved=" + approved, userId);
    }

    public ResponseEntity<Object> getBooking(long userId, Long bookingId) {
        return get("/" + bookingId, userId);
    }
}