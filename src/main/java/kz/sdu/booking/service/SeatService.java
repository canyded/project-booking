package kz.sdu.booking.service;

import kz.sdu.booking.handle.UserInputException;
import kz.sdu.booking.mapper.SeatMapper;
import kz.sdu.booking.model.dto.ListResponse;
import kz.sdu.booking.model.dto.SeatDto;
import kz.sdu.booking.model.dto.TimeSlotDto;
import kz.sdu.booking.model.entity.Reservation;
import kz.sdu.booking.model.entity.Seat;
import kz.sdu.booking.model.entity.User;
import kz.sdu.booking.model.enums.ReservationStatus;
import kz.sdu.booking.model.enums.Role;
import kz.sdu.booking.model.enums.SeatStatus;
import kz.sdu.booking.repository.ReservationRepository;
import kz.sdu.booking.repository.SeatRepository;
import kz.sdu.booking.utils.Errors;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

@Service
@AllArgsConstructor
public class SeatService {
	private final UserService userService;
	private final SeatRepository seatRepository;
	private final ReservationRepository reservationRepository;

	/**
	 * Получает список всех мест
	 *
	 * @return {@link ListResponse} с данными о местах и их количеством.
	 * Если список пуст, возвращает {@link ListResponse#empty()}.
	 */
	public ListResponse<SeatDto> getAllSeats(final int floor,
											 final LocalDate date,
											 final LocalDateTime startTime,
											 final LocalDateTime endTime) {
		final List<Seat> allSeatList = seatRepository.findAllByFloor(floor);
		final List<Reservation> reservations = reservationRepository.findAllByDateAndTime(date, startTime, endTime, floor);
		final List<Seat> seatList = allSeatList
				.stream()
				.peek(seat -> {
					boolean reserved = reservations.stream()
							.anyMatch(reservation -> Objects.requireNonNull(reservation.getSeat().getId()).equals(seat.getId()));
					seat.setStatus(reserved ? SeatStatus.RESERVED : seat.getStatus());
				})
				.toList();

		if (CollectionUtils.isEmpty(seatList)) {
			return ListResponse.empty();
		}
		final List<SeatDto> seatDtoList = SeatMapper.INSTANCE.toDtoList(seatList);

		return new ListResponse<>(seatDtoList, seatDtoList.size());
	}

	/**
	 * Получает информацию о конкретном месте по идентификатору
	 * <p/>
	 *
	 * @param id Идентификатор места
	 * @return {@link SeatDto} С данными о месте
	 * @throws UserInputException Если место не найдено
	 */
	public SeatDto getSeatById(final Long id) throws UserInputException {
		final Seat seat = findById(id);

		return convertAndFill(seat);
	}

	public Seat findById(final Long id) throws UserInputException {
		final Seat seat = seatRepository.findById(id)
				.orElseThrow(() -> new UserInputException(Errors.MSG_SEAT_NOT_FOUND
						+ " (ID: " + id + ")"));

		return seat;
	}

	/**
	 * Блокирует место (доступно только ADMIN).
	 * <p/>
	 *
	 * @param id идентификатор места
	 * @return обновленный объект {@link SeatDto}
	 * @throws UserInputException если у пользователя нет прав или место не найдено
	 */
	public SeatDto blockSeat(Long id) throws UserInputException {
		validateAdminAccess();

		final Seat seat =
				seatRepository.findById(id)
						.orElseThrow(() -> new UserInputException(String.format(Errors.MSG_SEAT_NOT_FOUND, id)));

		seat.setBlocked(true);
		seatRepository.save(seat);

		return convertAndFill(seat);
	}

	/**
	 * Разблокирует место (доступно только ADMIN).
	 * <p/>
	 *
	 * @param id идентификатор места
	 * @return обновленный объект {@link SeatDto}
	 * @throws UserInputException если у пользователя нет прав или место не найдено
	 */
	public SeatDto unblockSeat(final Long id) throws UserInputException {
		validateAdminAccess();

		final Seat seat =
				seatRepository.findById(id)
						.orElseThrow(() -> new UserInputException(String.format(Errors.MSG_SEAT_NOT_FOUND, id)));

		seat.setBlocked(false);
		seatRepository.save(seat);

		return convertAndFill(seat);
	}

	/**
	 * Возвращает список доступных временных интервалов (тайм-слотов) для бронирования места на указанную дату.
	 * <p>
	 * Доступное время рассчитывается между 9:00 и 18:00, исключая уже забронированные интервалы и короткие промежутки менее 30 минут.
	 * Если дата совпадает с текущим днём, исключаются уже прошедшие часы.
	 * Отменённые бронирования (со статусом {@code CANCELLED}) не учитываются.
	 * <p/>
	 *
	 * @param seatId идентификатор места
	 * @param date   дата, на которую нужно получить список доступных интервалов
	 * @return список доступных интервалов {@link TimeSlotDto}
	 * @throws UserInputException если указана прошедшая дата
	 */
	public List<TimeSlotDto> getAvailableTimeSlots(final Long seatId, final LocalDate date) throws UserInputException {
		if (date.isBefore(LocalDate.now())) {
			throw new UserInputException(Errors.MSG_PAST_DATE_NOT_ALLOWED);
		}

		final LocalDateTime dayStart = date.atTime(9, 0);
		final LocalDateTime dayEnd = date.atTime(18, 0);

		final List<Reservation> reservations = reservationRepository.findBySeatIdAndStartTimeBetween(seatId, dayStart, dayEnd);
		reservations.removeIf(r -> r.getStatus() == ReservationStatus.CANCELLED);
		reservations.sort(Comparator.comparing(Reservation::getStartTime));

		final List<TimeSlotDto> availableSlots = new ArrayList<>();
		LocalDateTime current = dayStart;

		// Исключаем прошедшее время, если сегодня
		if (date.equals(LocalDate.now())) {
			current = LocalDateTime.now().isAfter(current) ? LocalDateTime.now().withSecond(0).withNano(0) : current;
		}

		for (final Reservation reservation : reservations) {
			if (current.isBefore(reservation.getStartTime())) {
				final Duration gap = Duration.between(current, reservation.getStartTime());
				if (gap.toMinutes() >= 30) {
					availableSlots.add(new TimeSlotDto(current, reservation.getStartTime()));
				}
			}
			current = reservation.getEndTime().isAfter(current) ? reservation.getEndTime() : current;
		}

		final Duration lastGap = Duration.between(current, dayEnd);
		if (lastGap.toMinutes() >= 30) {
			availableSlots.add(new TimeSlotDto(current, dayEnd));
		}

		return availableSlots;
	}

	/**
	 * Проверяет, является ли пользователь ADMIN.
	 *
	 * @throws UserInputException если пользователь не ADMIN
	 */
	private void validateAdminAccess() throws UserInputException {
		final User user = userService.getAuthenticateUser();
		if (!user.getRole().equals(Role.ADMIN)) {
			throw new UserInputException(Errors.MSG_ACCESS_DENIED);
		}
	}

	private SeatDto convertAndFill(final Seat seat) {
		if (Objects.isNull(seat)) {
			return null;
		}

		return SeatMapper.INSTANCE.toDto(seat);
	}
}
