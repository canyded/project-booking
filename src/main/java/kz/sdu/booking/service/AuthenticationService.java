package kz.sdu.booking.service;

import kz.sdu.booking.handle.UserInputException;
import kz.sdu.booking.model.dto.AuthenticationRequest;
import kz.sdu.booking.model.dto.AuthenticationResponse;
import kz.sdu.booking.model.dto.RefreshTokenRequest;
import kz.sdu.booking.model.dto.RegisterRequest;
import org.springframework.stereotype.Service;

@Service
public interface AuthenticationService {

	AuthenticationResponse register(final RegisterRequest registerRequest) throws UserInputException;

	AuthenticationResponse authentication(final AuthenticationRequest authenticationRequest);

	AuthenticationResponse refreshToken(final RefreshTokenRequest refreshTokenRequest);
}