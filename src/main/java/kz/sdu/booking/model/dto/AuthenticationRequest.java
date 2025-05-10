package kz.sdu.booking.model.dto;

import lombok.Getter;

@Getter
public class AuthenticationRequest {
	private String email;
	private String password;
}