package kz.sdu.booking.model.entity;

import jakarta.persistence.*;
import kz.sdu.booking.model.enums.Role;
import lombok.*;
import org.springframework.data.jpa.domain.AbstractAuditable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Entity
@Table(name = "t_users")
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class User extends AbstractAuditable<User, Long> implements UserDetails {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String firstName;

	private String lastName;

	@Column(unique = true, nullable = false)
	private String email;

	private String password;

	private Boolean isLocked;

	private Boolean isExpired;

	@Enumerated(EnumType.STRING)
	private Role role;

	@OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
	private List<Reservation> reservations;

	@Column(nullable = false)
	@Builder.Default
	private Boolean isDeleted = false;

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return List.of(new SimpleGrantedAuthority(role.name()));
	}

	@Override
	public String getUsername() {
		return email;
	}

	@Override
	public String getPassword() {
		return password;
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}
}
