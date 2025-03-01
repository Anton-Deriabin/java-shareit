package ru.practicum.shareit.client;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

public class BaseClient {
    // Поле для хранения экземпляра RestTemplate, используемого для выполнения HTTP-запросов
    protected final RestTemplate rest;

    // Конструктор, принимающий RestTemplate и инициализирующий поле rest
    public BaseClient(RestTemplate rest) {
        this.rest = rest;
    }

    // Метод для выполнения GET запроса без параметров и идентификатора пользователя
    protected ResponseEntity<Object> get(String path) {
        return get(path, null, null);
    }

    // Метод для выполнения GET запроса с идентификатором пользователя
    protected ResponseEntity<Object> get(String path, long userId) {
        return get(path, userId, null);
    }

    // Метод для выполнения GET запроса с идентификатором пользователя и параметрами запроса
    protected ResponseEntity<Object> get(String path, Long userId, @Nullable Map<String, Object> parameters) {
        return makeAndSendRequest(HttpMethod.GET, path, userId, parameters, null);
    }

    // Метод для выполнения POST запроса с телом запроса
    protected <T> ResponseEntity<Object> post(String path, T body) {
        return post(path, null, null, body);
    }

    // Метод для выполнения POST запроса с идентификатором пользователя и телом запроса
    protected <T> ResponseEntity<Object> post(String path, long userId, T body) {
        return post(path, userId, null, body);
    }

    // Метод для выполнения POST запроса с идентификатором пользователя, параметрами запроса и телом запроса
    protected <T> ResponseEntity<Object> post(String path, Long userId, @Nullable Map<String, Object> parameters,
                                              T body) {
        return makeAndSendRequest(HttpMethod.POST, path, userId, parameters, body);
    }

    // Метод для выполнения PUT запроса с идентификатором пользователя и телом запроса
    protected <T> ResponseEntity<Object> put(String path, long userId, T body) {
        return put(path, userId, null, body);
    }

    // Метод для выполнения PUT запроса с идентификатором пользователя, параметрами запроса и телом запроса
    protected <T> ResponseEntity<Object> put(String path, long userId, @Nullable Map<String, Object> parameters,
                                             T body) {
        return makeAndSendRequest(HttpMethod.PUT, path, userId, parameters, body);
    }

    // Метод для выполнения PATCH запроса с телом запроса
    protected <T> ResponseEntity<Object> patch(String path, T body) {
        return patch(path, null, null, body);
    }

    // Метод для выполнения PATCH запроса с идентификатором пользователя
    protected <T> ResponseEntity<Object> patch(String path, long userId) {
        return patch(path, userId, null, null);
    }

    // Метод для выполнения PATCH запроса с идентификатором пользователя и телом запроса
    protected <T> ResponseEntity<Object> patch(String path, long userId, T body) {
        return patch(path, userId, null, body);
    }

    // Метод для выполнения PATCH запроса с идентификатором пользователя, параметрами запроса и телом запроса
    protected <T> ResponseEntity<Object> patch(String path, Long userId, @Nullable Map<String, Object> parameters,
                                               T body) {
        return makeAndSendRequest(HttpMethod.PATCH, path, userId, parameters, body);
    }

    // Метод для выполнения DELETE запроса без параметров и идентификатора пользователя
    protected ResponseEntity<Object> delete(String path) {
        return delete(path, null, null);
    }

    // Метод для выполнения DELETE запроса с идентификатором пользователя
    protected ResponseEntity<Object> delete(String path, long userId) {
        return delete(path, userId, null);
    }

    // Метод для выполнения DELETE запроса с идентификатором пользователя и параметрами запроса
    protected ResponseEntity<Object> delete(String path, Long userId, @Nullable Map<String, Object> parameters) {
        return makeAndSendRequest(HttpMethod.DELETE, path, userId, parameters, null);
    }

    // Приватный метод для создания и отправки HTTP-запроса
    private <T> ResponseEntity<Object> makeAndSendRequest(HttpMethod method, String path, Long userId,
                                                          @Nullable Map<String, Object> parameters, @Nullable T body) {
        // Создание HttpEntity с телом запроса и заголовками
        HttpEntity<T> requestEntity = new HttpEntity<>(body, defaultHeaders(userId));

        ResponseEntity<Object> shareitServerResponse;
        try {
            // Выполнение запроса с параметрами или без них
            if (parameters != null) {
                shareitServerResponse = rest.exchange(path, method, requestEntity, Object.class, parameters);
            } else {
                shareitServerResponse = rest.exchange(path, method, requestEntity, Object.class);
            }
        } catch (HttpStatusCodeException e) {
            // Обработка исключений и возврат ответа с кодом ошибки
            return ResponseEntity.status(e.getStatusCode()).body(e.getResponseBodyAsByteArray());
        }
        // Подготовка и возврат ответа
        return prepareGatewayResponse(shareitServerResponse);
    }

    // Приватный метод для создания заголовков по умолчанию
    private HttpHeaders defaultHeaders(Long userId) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        // Добавление идентификатора пользователя в заголовок, если он передан
        if (userId != null) {
            headers.set("X-Sharer-User-Id", String.valueOf(userId));
        }
        return headers;
    }

    // Приватный метод для подготовки ответа
    private static ResponseEntity<Object> prepareGatewayResponse(ResponseEntity<Object> response) {
        // Если статус ответа успешный, возвращаем его как есть
        if (response.getStatusCode().is2xxSuccessful()) {
            return response;
        }

        ResponseEntity.BodyBuilder responseBuilder = ResponseEntity.status(response.getStatusCode());

        // Если ответ содержит тело, добавляем его в ответ
        if (response.hasBody()) {
            return responseBuilder.body(response.getBody());
        }

        // Возвращаем ответ без тела
        return responseBuilder.build();
    }
}