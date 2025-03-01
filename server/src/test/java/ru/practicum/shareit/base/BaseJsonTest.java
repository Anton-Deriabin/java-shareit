package ru.practicum.shareit.base;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import ru.practicum.shareit.annotation.MyJsonTest;

@MyJsonTest
public class BaseJsonTest {

    @Autowired
    protected ObjectMapper objectMapper;
}
