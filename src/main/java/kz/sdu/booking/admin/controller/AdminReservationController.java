package kz.sdu.booking.admin.controller;

import kz.sdu.booking.handle.UserInputException;
import kz.sdu.booking.model.dto.ReservationDto;
import kz.sdu.booking.service.ReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/rest/sdu/booking/admin/reservations", produces = APPLICATION_JSON_VALUE)
public class AdminReservationController {

    private final ReservationService reservationService;

    /**
     * Отменяет бронирование по его идентификатору (только для ADMIN).
     * Доступно только для пользователей с ролью {@code LIBRARIAN} или {@code ADMIN}.
     * <p/>
     * @param id идентификатор бронирования
     * @return объект {@link ReservationDto}, представляющий обновленное бронирование
     * @throws UserInputException если бронирование не найдено или у пользователя недостаточно прав
     */
    @PostMapping("/{id}/cancel")
    public ReservationDto cancelReservation(@PathVariable final Long id) throws UserInputException {
        return reservationService.cancelReservationForAdmin(id);
    }
}
