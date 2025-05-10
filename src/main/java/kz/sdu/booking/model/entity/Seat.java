package kz.sdu.booking.model.entity;

import jakarta.persistence.*;
import kz.sdu.booking.model.enums.SeatStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.jpa.domain.AbstractAuditable;

import java.util.List;

@Entity
@Table(name = "t_seats")
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Seat extends AbstractAuditable<Seat, Long> {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String number;
	private String location;

	@Enumerated(value = EnumType.STRING)
	private SeatStatus status;

	private int floor;

	@OneToMany(mappedBy = "seat")
	private List<Reservation> reservations;

	@Builder.Default
	private boolean blocked = false;
}
