package kz.sdu.booking.service.impl;

import kz.sdu.booking.mapper.UserMapper;
import kz.sdu.booking.model.dto.*;
import kz.sdu.booking.utils.Errors;
import kz.sdu.booking.handle.UserInputException;
import kz.sdu.booking.model.entity.User;
import kz.sdu.booking.model.enums.Role;
import kz.sdu.booking.service.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {

	private final kz.sdu.booking.repository.UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final kz.sdu.booking.service.JwtService jwtService;
	private final AuthenticationManager authenticationManager;
	private final UserMapper userMapper;

	public AuthenticationResponse register(final RegisterRequest registerRequest) throws UserInputException {
		final Optional<User> existingUserOptional = userRepository.findByEmail(registerRequest.getEmail());

		if (existingUserOptional.isPresent()) {
			throw new UserInputException(Errors.MSG_USER_ALREADY_EXISTS);
		}

		final User user = User.builder()
							  .firstName(registerRequest.getFirstName())
							  .lastName(registerRequest.getLastName())
							  .email(registerRequest.getEmail())
							  .password(passwordEncoder.encode(registerRequest.getPassword()))
							  .role(Role.STUDENT)
							  .isExpired(false)
							  .isLocked(false)
							  .build();

		userRepository.save(user);

		final String accessToken = jwtService.generateToken(user);
		final String refreshToken = jwtService.generateRefreshToken(new HashMap<>(), user);
		final UserDto userDto = userMapper.toDto(user);

		return AuthenticationResponse.builder()
									 .accessToken(accessToken)
									 .refreshToken(refreshToken)
									 .user(userDto)
									 .build();

	}

	@Override
	public AuthenticationResponse authentication(final AuthenticationRequest authenticationRequest) {
		authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
			authenticationRequest.getEmail(),
			authenticationRequest.getPassword())
		);

		final User user = userRepository.findByEmail(authenticationRequest.getEmail()).orElseThrow(
			() -> new UsernameNotFoundException(Errors.MSG_USER_IS_NULL)
		);

		final String accessToken = jwtService.generateToken(user);
		final String refreshToken = jwtService.generateRefreshToken(new HashMap<>(), user);
		final UserDto userDto = userMapper.toDto(user);

		return AuthenticationResponse.builder()
									 .accessToken(accessToken)
									 .refreshToken(refreshToken)
									 .user(userDto)
									 .build();

	}

	public AuthenticationResponse refreshToken(final RefreshTokenRequest refreshTokenRequest) {
		final String userEmail = jwtService.extractUserEmail(refreshTokenRequest.getRefreshToken());
		final User user = userRepository.findByEmail(userEmail).orElseThrow();
		if (jwtService.isTokenValid(refreshTokenRequest.getRefreshToken(), user)) {
			final String accessToken = jwtService.generateToken(user);
			final String refreshToken = refreshTokenRequest.getRefreshToken();
			final UserDto userDto = userMapper.toDto(user);


			return AuthenticationResponse.builder()
										 .accessToken(accessToken)
										 .refreshToken(refreshToken)
										 .user(userDto)
										 .build();
		}

		return null;
	}
}