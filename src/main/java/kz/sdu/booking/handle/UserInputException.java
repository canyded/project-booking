package kz.sdu.booking.handle;

public class UserInputException extends Exception {
    private final String detail;
    private final String inputName;

    public UserInputException(String message) {
        super(message);
        this.detail = "";
        this.inputName = "";
    }

    public UserInputException(String inputName, String message, String detail) {
        super(message);
        this.detail = detail;
        this.inputName = inputName;
    }

    public String getInputName() {
        return this.inputName;
    }

    public String getDetail() {
        return this.detail;
    }
}

