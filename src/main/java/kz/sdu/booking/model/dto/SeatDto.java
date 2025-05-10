package kz.sdu.booking.model.dto;

import kz.sdu.booking.model.enums.SeatStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SeatDto {

	private Integer id;
	private String number;
	private String location;
	private int floor;
	private SeatStatus status;
}
