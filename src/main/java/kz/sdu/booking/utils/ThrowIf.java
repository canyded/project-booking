package kz.sdu.booking.utils;

import jakarta.validation.constraints.NotNull;
import jakarta.annotation.Nullable;
import kz.sdu.booking.handle.UserInputException;

import java.util.Objects;

public class ThrowIf {

    public static void isTrue(
        @Nullable final Boolean obj,
        @NotNull final String msg
    ) throws UserInputException {
        if (Boolean.TRUE.equals(obj)) {
            throw new UserInputException(msg);
        }
    }

    public static void isNull(
        @Nullable final Object obj,
        @NotNull final String msg
    ) throws UserInputException {
        if (Objects.isNull(obj)) {
            throw new UserInputException(msg);
        }
    }

}
