package kz.sdu.booking.model.dto;

import lombok.Getter;

@Getter
public class RegisterRequest {
	private String firstName;
	private String lastName;
	private String email;
	private String userName;
	private String password;
}