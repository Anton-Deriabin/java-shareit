package ru.practicum.shareit.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import ru.practicum.shareit.base.BaseWebMvcTest;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

public class ItemRequestControllerTest extends BaseWebMvcTest {

    private ItemRequestDto itemRequestDto;

    @BeforeEach
    public void setUp() {
        itemRequestDto = new ItemRequestDto(1L, "Описание запроса", LocalDateTime.now(), 1L, null);
    }

    @Test
    public void testCreateItemRequestWhenServiceCreatesRequestThenReturnRequest() throws Exception {
        BDDMockito.given(itemRequestService.createItemRequest(Mockito.any(ItemRequestCreateDto.class), Mockito.eq(1L)))
                .willReturn(itemRequestDto);
        mockMvc.perform(MockMvcRequestBuilders.post("/requests")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"description\":\"" + itemRequestDto.getDescription() + "\"}")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(itemRequestDto.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.description").value(itemRequestDto.getDescription()));
    }

    @Test
    public void testFindItemRequestWhenServiceReturnsRequestThenReturnRequest() throws Exception {
        BDDMockito.given(itemRequestService.findById(Mockito.eq(1L)))
                .willReturn(itemRequestDto);
        mockMvc.perform(MockMvcRequestBuilders.get("/requests/{requestId}", 1L)
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(itemRequestDto.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.description").value(itemRequestDto.getDescription()));
    }

    @Test
    public void testFindAllItemRequestsWhenServiceReturnsRequestsThenReturnListOfRequestsOfUser() throws Exception {
        List<ItemRequestDto> requests = Arrays.asList(itemRequestDto);
        BDDMockito.given(itemRequestService.findByRequestorId(Mockito.eq(1L))).willReturn(requests);
        mockMvc.perform(MockMvcRequestBuilders.get("/requests")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].id").value(itemRequestDto.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].description").value(itemRequestDto.getDescription()));
    }

    @Test
    public void testFindAllItemRequestsWhenServiceReturnsRequestsThenReturnListOfRequests() throws Exception {
        List<ItemRequestDto> requests = Arrays.asList(itemRequestDto);
        BDDMockito.given(itemRequestService.findAll()).willReturn(requests);
        mockMvc.perform(MockMvcRequestBuilders.get("/requests/all")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].id").value(itemRequestDto.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].description").value(itemRequestDto.getDescription()));
    }
}
