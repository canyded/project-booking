package kz.sdu.booking.mapper;

import kz.sdu.booking.model.dto.SeatDto;
import kz.sdu.booking.model.entity.Seat;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface SeatMapper {
    SeatMapper INSTANCE = Mappers.getMapper(SeatMapper.class);

    SeatDto toDto(Seat seat);
    List<SeatDto> toDtoList(List<Seat> seats);
}
