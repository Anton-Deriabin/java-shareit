package ru.practicum.shareit.base;

import org.springframework.beans.factory.annotation.Autowired;
import ru.practicum.shareit.annotation.MySpringBootTest;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingService;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.request.ItemRequestService;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.UserServiceImpl;

@MySpringBootTest
public class BaseSpringBootTest {

    @Autowired
    protected UserServiceImpl userService;

    @Autowired
    protected UserRepository userRepository;

    @Autowired
    protected ItemService itemService;

    @Autowired
    protected ItemRepository itemRepository;

    @Autowired
    protected BookingService bookingService;

    @Autowired
    protected BookingRepository bookingRepository;

    @Autowired
    protected ItemRequestService itemRequestService;
}
