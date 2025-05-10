package kz.sdu.booking.admin.controller;

import kz.sdu.booking.handle.UserInputException;
import kz.sdu.booking.model.dto.UserDto;
import kz.sdu.booking.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping(value = "/rest/sdu/booking/admin/user", produces = APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class AdminUserController {

	private final UserService userService;

	/**
	 * Получает список всех пользователей (доступно только ADMIN).
	 *
	 * @return список {@link UserDto}
	 * @throws UserInputException если у пользователя недостаточно прав
	 */
	@GetMapping
	public List<UserDto> getAllUsers() throws UserInputException {
		return userService.getAllUsers();
	}

	@GetMapping("/test")
	public String test() {
		return "userService.getAllUsers()";
	}

	@GetMapping("/test1")
	public String test1() {
		return "userService.getAllUsers()";
	}
}
