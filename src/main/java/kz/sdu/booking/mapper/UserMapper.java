package kz.sdu.booking.mapper;

import kz.sdu.booking.model.dto.UserDto;
import kz.sdu.booking.model.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.Optional;

@Mapper(componentModel = "spring")
public interface UserMapper {
	UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

	@Mapping(source = "id", target = "id")
	UserDto toDto(User user);

}
