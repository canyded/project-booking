package kz.sdu.booking.service;

import io.jsonwebtoken.Claims;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Map;
import java.util.function.Function;

@Service
public interface JwtService {

	String generateToken(final UserDetails userDetails);

	String extractUserEmail(final String token);

	<T> T extractClaim(final String token, final Function<Claims, T> claimsResolver);

	Claims extractAllClaims(final String token);

	boolean isTokenValid(final String token, final UserDetails userDetails);

	String generateRefreshToken(final Map<String, Object> extraClaims, final UserDetails userDetails);

	Date extractExpiration(final String token);

}