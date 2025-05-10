package kz.sdu.booking.controller;

import kz.sdu.booking.handle.UserInputException;
import kz.sdu.booking.model.dto.UserDto;
import kz.sdu.booking.model.dto.UserEditRequestDto;
import kz.sdu.booking.model.dto.UserStatisticsDto;
import kz.sdu.booking.service.ReservationService;
import kz.sdu.booking.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping(value = "/rest/sdu/booking/user", produces = APPLICATION_JSON_VALUE)
@AllArgsConstructor
public class UserController {
    private final UserService userService;
    private final ReservationService reservationService;

    /**
     * Находит пользователя по идентификатору
     * <p/>
     * @param id Идентификатор пользователя
     * @return Информация о пользователе
     * @throws UserInputException Если пользователь не найден
     */
    @GetMapping("/{id}")
    public UserDto getUserById(@PathVariable("id") final Long id) throws UserInputException {
        return userService.findUserById(id);
    }

    /**
     * Обновляет пользователя по идентификатору
     * <p/>
     * @param id Идентификатор пользователя
     * @param requestDto Изменяемые данные
     * @return Информация о пользователе
     * @throws UserInputException Если пользователь не найден
     */
    @PostMapping("/update/{id}")
    public UserDto updateUserById(
        @PathVariable("id") final Long id,
        @RequestBody final UserEditRequestDto requestDto
    ) throws UserInputException {
        return userService.updateUserById(id, requestDto);
    }

    /**
     * Рест для удаления пользователя
     * @param id Идентификатор пользователя
     * @return Информация о пользователе
     * @throws UserInputException Если пользователь не найден
     */
    @PostMapping("/delete/{id}")
    public UserDto delete(@PathVariable("id") final Long id) throws UserInputException {
        return userService.deleteUser(id);
    }

    @GetMapping("/me")
    public UserDto getMe() {
        return userService.getMe();
    }

    /**
     * Получает статистику по пользователю:
     * сколько часов он находился в библиотеке, сколько бронирований за месяц,
     * и его рекорды по длительности бронирования.
     * <p/>
     * @param userId идентификатор пользователя
     * @return объект {@link UserStatisticsDto} со статистикой пользователя
     */
    @GetMapping("/statistics")
    public UserStatisticsDto getUserStatistics(
        @RequestParam final Long userId
    ) throws UserInputException {
        return reservationService.getUserStatistics(userId);
    }
}
