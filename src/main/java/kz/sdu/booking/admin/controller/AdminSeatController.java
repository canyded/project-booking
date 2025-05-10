package kz.sdu.booking.admin.controller;

import kz.sdu.booking.handle.UserInputException;
import kz.sdu.booking.model.dto.SeatDto;
import kz.sdu.booking.service.SeatService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping(value = "/rest/sdu/booking/admin/seat", produces = APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class AdminSeatController {

    private final SeatService seatService;

    /**
     * Блокирует место по его идентификатору (только для ADMIN)
     * <p/>
     * @param id идентификатор места
     * @return обновленный объект {@link SeatDto}
     * @throws UserInputException если у пользователя нет прав или место не найдено
     */
    @PostMapping("/{id}/block")
    public SeatDto blockSeat(@PathVariable final Long id) throws UserInputException {
        return seatService.blockSeat(id);
    }

    /**
     * Разблокирует место по его идентификатору (только для ADMIN)
     * <p/>
     * @param id идентификатор места
     * @return обновленный объект {@link SeatDto}
     * @throws UserInputException если у пользователя нет прав или место не найдено
     */
    @PostMapping("/{id}/unblock")
    public SeatDto unblockSeat(@PathVariable Long id) throws UserInputException {
        return seatService.unblockSeat(id);
    }
}
