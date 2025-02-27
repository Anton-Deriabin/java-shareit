package ru.practicum.shareit.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.user.dto.UserCreateDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserUpdateDto;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    private User user;
    private UserDto userDto;
    private UserCreateDto userCreateDto;
    private UserUpdateDto userUpdateDto;

    @BeforeEach
    void setUp() {
        user = new User(1L, "John Doe", "john.doe@example.com");
        userDto = new UserDto(1L, "John Doe", "john.doe@example.com");
        userCreateDto = new UserCreateDto();
        userCreateDto.setName("John Doe");
        userCreateDto.setEmail("john.doe@example.com");
        userUpdateDto = new UserUpdateDto();
        userUpdateDto.setId(1L);
        userUpdateDto.setName("John Doe Updated");
        userUpdateDto.setEmail("john.doe.updated@example.com");
    }

    @Test
    void testFindAllWhenUsersExistThenReturnUserDtos() {
        // Arrange
        List<User> users = List.of(user);
        List<UserDto> userDtos = List.of(userDto);
        when(userRepository.findAll()).thenReturn(users);

        // Act
        List<UserDto> result = userService.findAll();

        // Assert
        assertThat(result).isEqualTo(userDtos);
        verify(userRepository, times(1)).findAll();
    }

    @Test
    void testFindByIdWhenUserExistsThenReturnUserDto() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        // Act
        UserDto result = userService.findById(1L);

        // Assert
        assertThat(result).isEqualTo(userDto);
        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    void testCreateWhenUserIsCreatedThenReturnUserDto() {
        // Arrange
        when(userRepository.findByEmail(userCreateDto.getEmail())).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenReturn(user);

        // Act
        UserDto result = userService.create(userCreateDto);

        // Assert
        assertThat(result).isEqualTo(userDto);
        verify(userRepository, times(1)).findByEmail(userCreateDto.getEmail());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void testUpdateWhenUserIsUpdatedThenReturnUpdatedUserDto() {
        // Arrange
        User updatedUser = new User(1L, "John Doe Updated", "john.doe.updated@example.com");
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(updatedUser);

        // Act
        UserDto result = userService.update(userUpdateDto, 1L);

        // Assert
        assertThat(result.getName()).isEqualTo(userUpdateDto.getName());
        assertThat(result.getEmail()).isEqualTo(userUpdateDto.getEmail());
        verify(userRepository, times(1)).findById(1L);
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void testDeleteWhenUserIsDeletedThenReturnUserDto() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        // Act
        UserDto result = userService.delete(1L);

        // Assert
        assertThat(result).isEqualTo(userDto);
        verify(userRepository, times(1)).findById(1L);
        verify(userRepository, times(1)).delete(any(User.class));
    }
}
