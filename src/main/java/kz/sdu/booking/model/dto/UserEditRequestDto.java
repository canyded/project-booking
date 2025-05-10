package kz.sdu.booking.model.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UserEditRequestDto {
    private String firstName;
    private String lastName;
    private String password;
}
