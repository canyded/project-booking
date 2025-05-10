package kz.sdu.booking.repository;

import kz.sdu.booking.model.entity.Seat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SeatRepository extends JpaRepository<Seat, Long> {

	List<Seat> findAllByFloor(int floor);
}
