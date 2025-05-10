package kz.sdu.booking.model.dto;

import kz.sdu.booking.model.enums.ReservationStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReservationDto {

	private Long id;
	private UserDto user;
	private SeatDto seat;
	private LocalDateTime startTime;
	private LocalDateTime endTime;
	private ReservationStatus status;
	private Integer floor;
	private LocalDate date;
}
