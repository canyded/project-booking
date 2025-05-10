package kz.sdu.booking.repository;

import kz.sdu.booking.model.entity.Reservation;
import kz.sdu.booking.model.entity.Seat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {

	@Query("""
			    SELECT COUNT(r) > 0
			    FROM Reservation r
			    WHERE r.seat = :seat AND ((r.startTime <= :endTime AND r.endTime >= :startTime))
			""")
	boolean existsBySeatAndTime(@Param("seat") Seat seat,
								@Param("startTime") LocalDateTime startTime,
								@Param("endTime") LocalDateTime endTime);

	@Query("""
			    SELECT r
			    FROM Reservation r
			    WHERE r.seat.floor = :floor
			    AND (r.startTime < :endTime AND r.endTime > :startTime)
				AND r.date = :date
			""")
	List<Reservation> findAllByDateAndTime(@Param("date") LocalDate date,
										   @Param("startTime") LocalDateTime startTime,
										   @Param("endTime") LocalDateTime endTime,
										   @Param("floor") int floor);


	List<Reservation> findByUserId(Long userId);

	List<Reservation> findBySeatId(Long seatId);

	List<Reservation> findBySeatIdAndStartTimeBetween(Long seatId, LocalDateTime start, LocalDateTime end);
}
