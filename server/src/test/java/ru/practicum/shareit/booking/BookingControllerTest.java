package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import ru.practicum.shareit.base.BaseWebMvcTest;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingRequestDto;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

public class BookingControllerTest extends BaseWebMvcTest {

    private BookingDto bookingDto;
    private BookingRequestDto bookingRequestDto;

    @BeforeEach
    public void setUp() {
        bookingDto = new BookingDto(1L, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2),
                Status.WAITING, null, null);
        bookingRequestDto = new BookingRequestDto(LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2),
                1L, 1L);
    }

    @Test
    public void testCreateBookingWhenServiceCreatesBookingThenReturnBooking() throws Exception {
        BDDMockito.given(bookingService.createBooking(Mockito.any(BookingRequestDto.class),
                Mockito.eq(1L))).willReturn(bookingDto);
        mockMvc.perform(MockMvcRequestBuilders.post("/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"start\":\"" + bookingRequestDto.getStart()
                                + "\",\"end\":\"" + bookingRequestDto.getEnd() + "\",\"itemId\":1,\"bookerId\":1}")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(bookingDto.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.status")
                        .value(bookingDto.getStatus().toString()));
    }

    @Test
    public void testApproveBookingWhenServiceApprovesBookingThenReturnBooking() throws Exception {
        BDDMockito.given(bookingService.approveBooking(Mockito.eq(1L), Mockito.eq(true),
                Mockito.eq(1L))).willReturn(bookingDto);
        mockMvc.perform(MockMvcRequestBuilders.patch("/bookings/{bookingId}", 1L)
                        .param("approved", "true")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(bookingDto.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.status")
                        .value(bookingDto.getStatus().toString()));
    }

    @Test
    public void testFindBookingWhenServiceReturnsBookingThenReturnBooking() throws Exception {
        BDDMockito.given(bookingService.findBooking(Mockito.eq(1L), Mockito.eq(1L))).willReturn(bookingDto);
        mockMvc.perform(MockMvcRequestBuilders.get("/bookings/{bookingId}", 1L)
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(bookingDto.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.status")
                        .value(bookingDto.getStatus().toString()));
    }

    @Test
    public void testFindBookerBookingsWhenServiceReturnsBookingsThenReturnListOfBookings() throws Exception {
        List<BookingDto> bookings = Arrays.asList(bookingDto);
        BDDMockito.given(bookingService.findBookerBookings(Mockito.any(BookingState.class),
                Mockito.eq(1L))).willReturn(bookings);
        mockMvc.perform(MockMvcRequestBuilders.get("/bookings")
                        .param("state", "ALL")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].id").value(bookingDto.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].status")
                        .value(bookingDto.getStatus().toString()));
    }

    @Test
    public void testFindOwnerBookingsWhenServiceReturnsBookingsThenReturnListOfBookings() throws Exception {
        List<BookingDto> bookings = Arrays.asList(bookingDto);
        BDDMockito.given(bookingService.findOwnerBookings(Mockito.any(BookingState.class),
                Mockito.eq(1L))).willReturn(bookings);
        mockMvc.perform(MockMvcRequestBuilders.get("/bookings/owner")
                        .param("state", "ALL")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].id").value(bookingDto.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].status")
                        .value(bookingDto.getStatus().toString()));
    }
}
