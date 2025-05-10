package kz.sdu.booking.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import kz.sdu.booking.handle.UserInputException;
import kz.sdu.booking.model.dto.AuthenticationRequest;
import kz.sdu.booking.model.dto.AuthenticationResponse;
import kz.sdu.booking.model.dto.RefreshTokenRequest;
import kz.sdu.booking.model.dto.RegisterRequest;
import kz.sdu.booking.service.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController {

	private final AuthenticationService authenticationService;

	@PostMapping("/authentication")
	@Operation(
			summary = "Authenticate user and generate JWT token",
			description = "Authenticates a user using email and password and returns a JWT token.",
			requestBody = @RequestBody(
					description = "User credentials for authentication",
					required = true,
					content = @Content(
							schema = @Schema(implementation = AuthenticationRequest.class)
					)
			),
			responses = {
					@ApiResponse(
							responseCode = "200",
							description = "User authenticated successfully",
							content = @Content(
									schema = @Schema(implementation = AuthenticationResponse.class)
							)
					),
					@ApiResponse(
							responseCode = "401",
							description = "Unauthorized - Invalid credentials",
							content = @Content(mediaType = "text/plain")
					),
					@ApiResponse(
							responseCode = "500",
							description = "Internal Server Error"
					)
			}
	)
	public ResponseEntity<AuthenticationResponse> authentication(
			@org.springframework.web.bind.annotation.RequestBody final AuthenticationRequest authenticationRequest
	) {
		return ResponseEntity.ok(authenticationService.authentication(authenticationRequest));
	}

	@PostMapping("/refresh")
	@Operation(
			summary = "Refresh JWT token",
			description = "Refreshes the JWT token using a valid refresh token.",
			requestBody = @RequestBody(
					description = "Refresh token request",
					required = true,
					content = @Content(
							schema = @Schema(implementation = RefreshTokenRequest.class)
					)
			),
			responses = {
					@ApiResponse(
							responseCode = "200",
							description = "Token refreshed successfully",
							content = @Content(
									schema = @Schema(implementation = AuthenticationResponse.class)
							)
					),
					@ApiResponse(
							responseCode = "403",
							description = "Forbidden - Invalid refresh token",
							content = @Content(mediaType = "text/plain")
					),
					@ApiResponse(
							responseCode = "500",
							description = "Internal Server Error"
					)
			}
	)
	public ResponseEntity<AuthenticationResponse> refresh(@org.springframework.web.bind.annotation.RequestBody final RefreshTokenRequest refreshTokenRequest) {
		return ResponseEntity.ok(authenticationService.refreshToken(refreshTokenRequest));
	}


	@PostMapping("/register")
	@Operation(
			summary = "Register a new user",
			description = "Registers a new user in the system and returns an authentication token.",
			requestBody = @RequestBody(
					description = "User registration details",
					required = true,
					content = @Content(
							schema = @Schema(implementation = RegisterRequest.class)
					)
			),
			responses = {
					@ApiResponse(
							responseCode = "200",
							description = "User registered successfully",
							content = @Content(
									schema = @Schema(implementation = AuthenticationResponse.class)
							)
					),
					@ApiResponse(
							responseCode = "400",
							description = "Bad Request - User with this email already exists",
							content = @Content(mediaType = "text/plain")
					),
					@ApiResponse(
							responseCode = "500",
							description = "Internal Server Error"
					)
			}
	)
	public ResponseEntity<?> register(@org.springframework.web.bind.annotation.RequestBody final RegisterRequest registerRequest) {
		try {
			AuthenticationResponse response = authenticationService.register(registerRequest);
			return ResponseEntity.ok(response);
		} catch (IllegalStateException | UserInputException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User with this email already exists");
		}
	}
}
