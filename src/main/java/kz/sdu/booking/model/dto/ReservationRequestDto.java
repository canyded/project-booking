package kz.sdu.booking.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class ReservationRequestDto {
    private Long seatId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private LocalDate date;
}
