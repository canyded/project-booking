package kz.sdu.booking.service.impl;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import kz.sdu.booking.service.JwtService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtServiceImpl implements JwtService {


	private final static String SECRET_KEY = "b99f64b9d889a38670fe7d8f25b4f2b2f1aaad37582c04cde93d7fa77cdedbe4";

	public String extractUserEmail(final String token) {
		return extractClaim(token, Claims::getSubject);
	}

	public <T> T extractClaim(final String token,final Function<Claims, T> claimsResolver) {
		final Claims claims = extractAllClaims(token);
		return claimsResolver.apply(claims);
	}

	public Claims extractAllClaims(final String token) {

		return Jwts
				.parser()
				.verifyWith(getSignKey())
				.build()
				.parseSignedClaims(token)
				.getPayload();
	}

	public SecretKey getSignKey() {
		byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);

		return Keys.hmacShaKeyFor(keyBytes);
	}


	public String generateToken(final UserDetails userDetails) {

		return Jwts
				.builder()
				.subject(userDetails.getUsername())
				.issuedAt(new Date(System.currentTimeMillis()))
				.expiration(new Date(System.currentTimeMillis() + 100000 * 60 * 24))
				.signWith(getSignKey(), SignatureAlgorithm.HS256)
				.compact();
	}

	public boolean isTokenValid(final String token, final UserDetails userDetails) {
		final String userName = extractUserEmail(token);

		return (userName.equals(userDetails.getUsername()) && !(isTokenExpired(token)));
	}

	private boolean isTokenExpired(final String token) {
		return extractExpiration(token).before(new Date());
	}

	public Date extractExpiration(final String token) {
		return extractClaim(token, Claims::getExpiration);
	}

	public String generateRefreshToken(final Map<String, Object> extraClaims, final UserDetails userDetails) {
		return Jwts
				.builder()
				.claims(extraClaims)
				.subject(userDetails.getUsername())
				.issuedAt(new Date(System.currentTimeMillis()))
				.expiration(new Date(System.currentTimeMillis() + 100000 * 60 * 100))
				.signWith(getSignKey(), SignatureAlgorithm.HS256)
				.compact();
	}

}
