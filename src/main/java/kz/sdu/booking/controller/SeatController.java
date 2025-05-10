package kz.sdu.booking.controller;

import kz.sdu.booking.handle.UserInputException;
import kz.sdu.booking.model.dto.ListResponse;
import kz.sdu.booking.model.dto.SeatDto;
import kz.sdu.booking.service.SeatService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping(value = "/rest/sdu/booking/seat", produces = APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class SeatController {
	private final SeatService seatService;

	/**
	 * Получить список всех мест
	 *
	 * @return
	 */
	@GetMapping("/all")
	public ListResponse<SeatDto> getAllSeats(@RequestParam(name = "floor", defaultValue = "0") int floor,
											 @RequestParam(name = "date") LocalDate date,
											 @RequestParam(name = "startTime") LocalDateTime startTime,
											 @RequestParam(name = "endTime") LocalDateTime endTime) {
		return seatService.getAllSeats(floor, date, startTime, endTime);
	}

	/**
	 * Получает информацию о конкретном месте по идентификатору
	 * <p/>
	 *
	 * @param id Идентификатор места
	 * @return {@link SeatDto} С данными о месте
	 * @throws UserInputException Если место не найдено
	 */
	@GetMapping("/{id}")
	public SeatDto getSeatById(@PathVariable("id") Long id) throws UserInputException {
		return seatService.getSeatById(id);
	}
}
