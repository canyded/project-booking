package kz.sdu.booking.controller;

import kz.sdu.booking.handle.UserInputException;
import kz.sdu.booking.model.dto.ReservationDto;
import kz.sdu.booking.model.dto.ReservationRequestDto;
import kz.sdu.booking.model.dto.SeatDto;
import kz.sdu.booking.model.dto.UserDto;
import kz.sdu.booking.model.enums.ReservationStatus;
import kz.sdu.booking.model.enums.Role;
import kz.sdu.booking.model.enums.SeatStatus;
import kz.sdu.booking.service.ReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/rest/sdu/booking/reservations", produces = APPLICATION_JSON_VALUE)
public class ReservationController {
	private final ReservationService reservationService;

	/**
	 * Создает бронирование места для пользователя
	 * <p/>
	 * @param request объект {@link ReservationRequestDto}, содержащий данные для бронирования
	 * @return объект {@link ReservationDto}, содержащий информацию о созданном бронировании
	 * @throws UserInputException если переданы некорректные данные (например, место уже занято или время указано неверно).
	 */
	@PostMapping("/create")
	public ReservationDto createReservation(@RequestBody final ReservationRequestDto request) throws UserInputException {
		return reservationService.create(request);
	}

	/**
	 * Получает список бронирований по пользователю или месту.
	 * <p/>
	 * @param userId (необязательно) - ID пользователя для фильтрации
	 * @param seatId (необязательно) - ID места для фильтрации
	 * @return список бронирований в виде DTO
	 */
	@GetMapping
	public List<ReservationDto> getReservations(
		@RequestParam(required = false) final Long userId,
		@RequestParam(required = false) final Long seatId
	) {
		return reservationService.getReservationList(userId, seatId);
	}

//	@GetMapping
//	public List<ReservationDto> getReservations(
//		@RequestParam(required = false) final Long userId,
//		@RequestParam(required = false) final Long seatId
//	) {
//		List<ReservationDto> reservations = new ArrayList<>();
//
//		reservations.add(new ReservationDto(
//				1L,
//				new UserDto(1L, "John", "Doe", "john.doe@example.com", Role.STUDENT, false),
//				new SeatDto(1, "S-101", "Library A", 1, SeatStatus.AVAILABLE),
//				LocalDateTime.now(),
//				LocalDateTime.now().plusHours(2),
//				ReservationStatus.ACTIVE,
//				1,
//				LocalDate.now()
//		));
//
//		reservations.add(new ReservationDto(
//				2L,
//				new UserDto(2L, "Jane", "Smith", "jane.smith@example.com", Role.STUDENT, false),
//				new SeatDto(2, "S-102", "Library B", 2, SeatStatus.RESERVED),
//				LocalDateTime.now(),
//				LocalDateTime.now().plusHours(2),
//				ReservationStatus.CANCELLED,
//				2,
//				LocalDate.now()
//		));
//
//		reservations.add(new ReservationDto(
//				3L,
//				new UserDto(3L, "Alice", "Brown", "alice.brown@example.com", Role.STUDENT, false),
//				new SeatDto(3, "S-103", "Library C", 3, SeatStatus.OCCUPIED),
//				LocalDateTime.now(),
//				LocalDateTime.now().plusHours(2),
//				ReservationStatus.EXPIRED,
//				3,
//				LocalDate.now()
//		));
//
//		reservations.add(new ReservationDto(
//				4L,
//				new UserDto(4L, "Bob", "White", "bob.white@example.com", Role.STUDENT, false),
//				new SeatDto(4, "S-104", "Library D", 4, SeatStatus.BLOCKED),
//				LocalDateTime.now(),
//				LocalDateTime.now().plusHours(2),
//				ReservationStatus.RESERVED,
//				4,
//				LocalDate.now()
//		));
//
//		return reservations;
//	}

	/**
	 * Получает детали бронирования по его идентификатору.
	 * <p/>
	 * @param id идентификатор бронирования
	 * @return объект {@link ReservationDto}, содержащий детали бронирования
	 * @throws UserInputException если бронирование с таким ID не найдено
	 */
	@GetMapping("get/{id}")
	public ReservationDto getReservationById(@PathVariable final Long id) throws UserInputException {
		return reservationService.getReservationById(id);
	}

	/**
	 * Отменяет бронирование по его идентификатору
	 * <p/>
	 * @param id идентификатор бронирования
	 * @return объект {@link ReservationDto}, представляющий обновленное бронирование
	 * @throws UserInputException если бронирование не найдено или у пользователя недостаточно прав
	 */
	@PostMapping("/{id}/cancel")
	public ReservationDto cancelReservation(@PathVariable final Long id) throws UserInputException {
		return reservationService.cancelReservation(id);
	}

	/**
	 * Создаёт новое бронирование, повторяя последнее активное бронирование пользователя
	 * <p/>
	 * @param userId Идентификатор пользователя
	 * @return объект {@link ReservationDto}, представляющий новое бронирование
	 * @throws UserInputException если предыдущее бронирование не найдено или место недоступно
	 */
	@PostMapping("/reservations/repeat-last")
	public ReservationDto repeatLastBooking(
		@RequestParam final Long userId
	) throws UserInputException {
		return reservationService.repeatLastReservation(userId);
	}

}
