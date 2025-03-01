package ru.practicum.shareit.base;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.annotation.MyWebMvcTest;
import ru.practicum.shareit.booking.BookingService;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.request.ItemRequestService;
import ru.practicum.shareit.user.UserService;

@MyWebMvcTest
public class BaseWebMvcTest {

    @Autowired
    protected MockMvc mockMvc;

    @MockBean
    protected ItemService itemService;

    @MockBean
    protected UserService userService;

    @MockBean
    protected BookingService bookingService;

    @MockBean
    protected ItemRequestService itemRequestService;
}
