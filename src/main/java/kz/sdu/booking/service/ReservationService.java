package kz.sdu.booking.service;

import kz.sdu.booking.model.dto.UserDto;
import kz.sdu.booking.model.dto.UserStatisticsDto;
import kz.sdu.booking.utils.Errors;
import kz.sdu.booking.utils.ThrowIf;
import kz.sdu.booking.handle.UserInputException;
import kz.sdu.booking.mapper.ReservationMapper;
import kz.sdu.booking.model.dto.ReservationDto;
import kz.sdu.booking.model.dto.ReservationRequestDto;
import kz.sdu.booking.model.entity.Reservation;
import kz.sdu.booking.model.entity.Seat;
import kz.sdu.booking.model.entity.User;
import kz.sdu.booking.model.enums.ReservationStatus;
import kz.sdu.booking.model.enums.Role;
import kz.sdu.booking.repository.ReservationRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@AllArgsConstructor
public class ReservationService {
    private final ReservationRepository reservationRepository;
    private final UserService userService;
    private final SeatService seatService;

    /**
     *
     * @param request
     * @return
     * @throws UserInputException
     */
    public ReservationDto create(final ReservationRequestDto request) throws UserInputException {
        final User user = userService.getAuthenticateUser();
        if (user.getRole().equals(Role.ADMIN) || user.getRole().equals(Role.LIBRARIAN)) {
            return new ReservationDto();
        }

        final Seat seat = seatService.findById(request.getSeatId());
        ThrowIf.isTrue(reservationRepository.existsBySeatAndTime(seat, request.getStartTime(), request.getEndTime()),
                       Errors.MSG_SEAT_ALREADY_BOOKED);

        final Reservation reservation = reservationRepository.save(Reservation.newDraft(request, user, seat));

        return convertAndFill(reservation);
    }

    /**
     * Возвращает список всех бронирований пользователя по его идентификатору.
     *
     * @param userId идентификатор пользователя
     * @return список бронирований
     */
    public List<Reservation> findByUserId(final Long userId) throws UserInputException {
        ThrowIf.isNull(userId, Errors.MSG_USER_ID_REQUIRED);

        return reservationRepository.findByUserId(userId);
    }


    /**
     * Получает список бронирований по идентификатору пользователя или места.
     * <p/>
     * @param userId идентификатор пользователя
     * @param seatId идентификатор места
     * @return список объектов {@link ReservationDto}, представляющих бронирования
     */
    public List<ReservationDto> getReservationList(final Long userId, final Long seatId) {
        if (Objects.isNull(userId) && Objects.isNull(seatId)) {
            return Collections.emptyList();
        }

        final List<Reservation> reservations = (Objects.nonNull(userId))
                                               ? reservationRepository.findByUserId(userId)
                                               : reservationRepository.findBySeatId(seatId);

        return convertAndFillList(reservations);
    }

    /**
     * Получает детали бронирования по его идентификатору
     * <p/>
     * @param id идентификатор бронирования
     * @return объект {@link ReservationDto}, содержащий информацию о бронировании
     * @throws UserInputException если бронирование с указанным ID не найдено
     */
    public ReservationDto getReservationById(final Long id) throws UserInputException {
        final Reservation reservation = reservationRepository.findById(id)
                                                             .orElseThrow(() -> new UserInputException(String.format(Errors.MSG_RESERVATION_NOT_FOUND, id)));

        return convertAndFill(reservation);
    }


    /**
     * Конвертирует entity в dto
     * @param reservation объект резерва
     * @return Об
     */
    public ReservationDto convertAndFill(final Reservation reservation) {
        if (Objects.isNull(reservation)) {
            return null;
        }

        return ReservationMapper.INSTANCE.toDto(reservation);
    }

    public List<ReservationDto> convertAndFillList(final List<Reservation> reservationList) {
        if (CollectionUtils.isEmpty(reservationList)) {
            return null;
        }

        return ReservationMapper.INSTANCE.toDtoList(reservationList);
    }

    /**
     * Отменяет бронирование по его идентификатору (только для ADMIN)
     * <p>
     * Отмена доступна только пользователям с ролью {@code LIBRARIAN} или {@code ADMIN}.
     * Если пользователь не имеет достаточных прав или бронирование не найдено, выбрасывается исключение.
     * <p/>
     * @param id идентификатор бронирования, которое необходимо отменить
     * @return объект {@link ReservationDto}, представляющий обновленное бронирование со статусом {@code CANCELLED}
     * @throws UserInputException если бронирование не найдено или у пользователя недостаточно прав
     */
    public ReservationDto cancelReservationForAdmin(final Long id) throws UserInputException {
        // Получаем аутентифицированного пользователя
        final User user = userService.getAuthenticateUser();

        // Проверяем, является ли пользователь LIBRARIAN или ADMIN
        if (!(user.getRole().equals(Role.LIBRARIAN) || user.getRole().equals(Role.ADMIN))) {
            throw new UserInputException(Errors.MSG_ACCESS_DENIED);
        }

        // Ищем бронирование в базе
        final Reservation reservation =
            reservationRepository.findById(id)
                                 .orElseThrow(() -> new UserInputException(String.format(Errors.MSG_RESERVATION_NOT_FOUND, id)));

        // Меняем статус на "ОТМЕНЕНО"
        reservation.setStatus(ReservationStatus.CANCELLED);
        reservationRepository.save(reservation);

        return convertAndFill(reservation);
    }

    /**
     * Отменяет бронирование по его идентификатору
     * @param id идентификатор бронирования
     * @return объект {@link ReservationDto} с обновлённым статусом {@code CANCELLED}
     * @throws UserInputException если, бронирование не найдено, у пользователя недостаточно прав, бронирование уже было отменено ранее
     */
    public ReservationDto cancelReservation(final Long id) throws UserInputException {
        final User currentUser = userService.getAuthenticateUser();

        final Reservation reservation =
            reservationRepository.findById(id)
                                 .orElseThrow(() -> new UserInputException(String.format(Errors.MSG_RESERVATION_NOT_FOUND, id)));

        // Нельзя отменить уже отменённую бронь
        if (reservation.getStatus() == ReservationStatus.CANCELLED) {
            throw new UserInputException(Errors.MSG_RESERVATION_ALREADY_CANCELLED);
        }

        final boolean isAdminOrLibrarian = currentUser.getRole().equals(Role.ADMIN) || currentUser.getRole().equals(Role.LIBRARIAN);
        final boolean isOwner = reservation.getUser().getId().equals(currentUser.getId());

        if (!(isAdminOrLibrarian || isOwner)) {
            throw new UserInputException(Errors.MSG_ACCESS_DENIED);
        }

        reservation.setStatus(ReservationStatus.CANCELLED);
        reservationRepository.save(reservation);

        return convertAndFill(reservation);
    }

    /**
     *
     * @param userId
     * @return
     */
    /**
     * Повторяет последнее активное бронирование пользователя.
     *
     * @param userId идентификатор пользователя
     * @return новое бронирование в виде {@link ReservationDto}
     * @throws UserInputException если последнее бронирование не найдено или место занято
     */
    public ReservationDto repeatLastReservation(final Long userId) throws UserInputException {
        // 1. Получить пользователя
        final User user = userService.find(userId);

        // 2. Найти последнюю бронь пользователя
        final Optional<Reservation> lastReservationOpt =
            reservationRepository.findByUserId(userId)
                                 .stream()
                                 .filter(reservation -> reservation.getStatus() == ReservationStatus.ACTIVE
                                                        || reservation.getStatus() == ReservationStatus.RESERVED)
                                 .max(Comparator.comparing(Reservation::getStartTime));

        if (lastReservationOpt.isEmpty()) {
            throw new UserInputException("У вас нет прошлых бронирований для повтора.");
        }

        final Reservation lastReservation = lastReservationOpt.get();

        // 3. Проверить, свободно ли место на новое время
        final LocalDateTime newStartTime = LocalDateTime.now();
        final LocalDateTime newEndTime = newStartTime.plusHours(2);

        final Seat seat = lastReservation.getSeat();
        final boolean seatAlreadyBooked = reservationRepository.existsBySeatAndTime(seat, newStartTime, newEndTime);
        ThrowIf.isTrue(seatAlreadyBooked, Errors.MSG_SEAT_ALREADY_BOOKED);

        final ReservationRequestDto newRequest = new ReservationRequestDto(seat.getId(), newStartTime, newEndTime, LocalDate.now());

        final Reservation newReservation = Reservation.newDraft(newRequest, user, seat);

        // 5. Сохраняем новое бронирование
        reservationRepository.save(newReservation);

        // 6. Возвращаем результат
        return convertAndFill(newReservation);
    }

    /**
     * Возвращает статистику пользователя по его бронированиям.
     * <p/>
     * @param userId идентификатор пользователя
     * @return объект {@link UserStatisticsDto} со статистикой
     */
    public UserStatisticsDto getUserStatistics(final Long userId) throws UserInputException {
        // Получаем все бронирования пользователя
        final List<Reservation> reservations = findByUserId(userId);

        if (reservations.isEmpty()) {
            return new UserStatisticsDto(0, 0, 0, 0, 0);
        }

        int totalMinutes = 0;
        int bookingDaysInMonth = 0;
        int recordHours = 0;
        int recordDay = 0;

        final LocalDate currentMonth = LocalDate.now();

        for (final Reservation reservation : reservations) {
            if (reservation.getStatus() == ReservationStatus.ACTIVE || reservation.getStatus() == ReservationStatus.RESERVED) {
                // Считаем длительность
                if (reservation.getStartTime() != null && reservation.getEndTime() != null) {
                    int minutes = (int) java.time.Duration.between(reservation.getStartTime(), reservation.getEndTime()).toMinutes();
                    totalMinutes += minutes;

                    // Считаем рекорды
                    if (minutes > recordHours * 60) {
                        recordHours = minutes / 60; // сохраняем часы
                    }
                    if (minutes > recordDay * 60) {
                        recordDay = minutes / 60;   // по логике это тоже часы, но можно считать иначе
                    }
                }

                // Если бронирование в этом месяце
                if (Objects.nonNull(reservation.getDate()) && reservation.getDate().getMonth() == currentMonth.getMonth()) {
                    bookingDaysInMonth++;
                }
            }
        }

        int hoursInLibrary = totalMinutes / 60;
        int minutesInLibrary = totalMinutes % 60;

        return new UserStatisticsDto(hoursInLibrary, minutesInLibrary, bookingDaysInMonth, recordDay, recordHours);
    }

}
