package kz.sdu.booking.model.dto;

import kz.sdu.booking.model.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {

	private Long id;
	private String firstName;
	private String lastName;
	private String email;
	private Role role;
	private boolean isDeleted;
}
