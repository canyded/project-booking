package kz.sdu.booking.service;

import kz.sdu.booking.model.enums.Role;
import kz.sdu.booking.utils.Errors;
import kz.sdu.booking.handle.UserInputException;
import kz.sdu.booking.mapper.UserMapper;
import kz.sdu.booking.model.dto.UserDto;
import kz.sdu.booking.model.dto.UserEditRequestDto;
import kz.sdu.booking.model.entity.User;
import kz.sdu.booking.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Service
@AllArgsConstructor
public class UserService {
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    /**
     * Находит пользователя по идентификатору
     * <p/>
     * @param id Идентификатор пользователь
     * @return Информация о пользователе
     */
    public UserDto findUserById(final Long id) throws UserInputException {
        return convertAndFill(find(id));
    }

    public User find(final Long id) throws UserInputException {
        return userRepository.findById(id).orElseThrow(() -> new UserInputException(Errors.MSG_USER_IS_NULL));
    }

    /**
     * Обновляет пользователя по идентификатору
     * <p/>
     * @param id дентификатор пользователя
     * @param requestDto Изменяемые данные
     * @return Информация о пользователе
     * @throws UserInputException Если пользователь не найден
     */
    public UserDto updateUserById(final Long id, final UserEditRequestDto requestDto) throws UserInputException {
        final User user = userRepository.findById(id).orElseThrow(() -> new UserInputException(Errors.MSG_USER_IS_NULL));

        final String requestFirstName = requestDto.getFirstName();
        if (Objects.nonNull(requestFirstName)) {
            user.setFirstName(requestFirstName);
        }

        final String requestLastName = requestDto.getLastName();
        if (Objects.nonNull(requestLastName)) {
            user.setLastName(requestLastName);
        }

        final String requestPassword = requestDto.getPassword();
        final String password = user.getPassword();
        if (Objects.nonNull(requestPassword)) {
            if (!passwordEncoder.matches(requestPassword, password)) {
                user.setPassword(passwordEncoder.encode(requestPassword));
            }
        }

        userRepository.save(user);

        return convertAndFill(user);
    }

    /**
     * Помечаем пользователя как удаленного
     * <p/>
     * @param userId Идентификатор пользователя
     * @return Информация о пользователе
     * @throws UserInputException Если пользователь не найден
     */
    public UserDto deleteUser(final Long userId) throws UserInputException {
        final User user = userRepository.findById(userId).orElseThrow(() -> new UserInputException(Errors.MSG_USER_IS_NULL));
        user.setIsDeleted(Boolean.TRUE);

        userRepository.save(user);

        return convertAndFill(user);
    }

    /**
     * Возвращает список всех пользователей, если текущий пользователь — ADMIN.
     * @return список {@link UserDto}
     * @throws UserInputException если у пользователя нет прав
     */
    public List<UserDto> getAllUsers() throws UserInputException {
        if (!getAuthenticateUser().getRole().equals(Role.ADMIN)) {
            throw new UserInputException(Errors.MSG_ACCESS_DENIED);
        }

        final List<User> userList = userRepository.findAll();
        if (CollectionUtils.isEmpty(userList)) {
            return Collections.emptyList();
        }

        return convertAndFillList(userList);
    }

    /**
     * Конвертирует объект {@link User} в {@link UserDto}.
     * <p/>
     * @param user объект пользователя
     * @return DTO-представление пользователя или {@code null}, если входной объект равен {@code null}
     */
    public UserDto convertAndFill(final User user) {
        if (Objects.isNull(user)) {
            return null;
        }

        return UserMapper.INSTANCE.toDto(user);
    }

    /**
     * Конвертирует список пользователей {@link User} в список {@link UserDto}.
     * <p/>
     * @param userList список пользователей
     * @return список DTO-представлений пользователей; если входной список пустой — возвращается пустой список
     */
    public List<UserDto> convertAndFillList(final List<User> userList) {
        if (CollectionUtils.isEmpty(userList)) {
            return Collections.emptyList();
        }
        final List<UserDto> userDtoList = new ArrayList<>();

        for (final User user : userList) {
            userDtoList.add(convertAndFill(user));
        }

        return userDtoList;
    }

    /**
     * Получает текущего аутентифицированного пользователя из контекста безопасности.
     * @return объект {@link User}, представляющий текущего пользователя
     * @throws ClassCastException если объект principal не является экземпляром {@link User}
     */
    public User getAuthenticateUser() {
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		return (User) authentication.getPrincipal();
    }

    /**
     *
     * @return
     */
    public UserDto getMe() {
        return convertAndFill(getAuthenticateUser());
    }

}
