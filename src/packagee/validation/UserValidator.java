package packagee.validation;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author danip
 */
public class UserValidator {
     private static final String USER_ID_REGEX = "\\d{12}";

    public ValidationResult validateUserData(String id, String username, String firstname,
                                             String lastname, String password,
                                             String passwordConfirmation) {
        ValidationResult result = ValidationResult.valid();

        validateId(id, result);
        validateRequired(username, "El nombre de usuario es obligatorio.", result);
        validateRequired(firstname, "El nombre es obligatorio.", result);
        validateRequired(lastname, "El apellido es obligatorio.", result);
        validatePassword(password, passwordConfirmation, result);

        return result;
    }

    public ValidationResult validateUserUpdateData(String id, String username, String firstname,
                                                   String lastname, String password,
                                                   String passwordConfirmation) {
        ValidationResult result = ValidationResult.valid();

        validateId(id, result);
        validateRequired(username, "El nombre de usuario es obligatorio.", result);
        validateRequired(firstname, "El nombre es obligatorio.", result);
        validateRequired(lastname, "El apellido es obligatorio.", result);

        if (!isBlank(password) || !isBlank(passwordConfirmation)) {
            validatePassword(password, passwordConfirmation, result);
        }

        return result;
    }

    public void validateId(String id, ValidationResult result) {
        if (isBlank(id)) {
            result.addError("El id del usuario es obligatorio.");
            return;
        }

        if (!id.matches(USER_ID_REGEX)) {
            result.addError("El id debe tener exactamente 12 digitos numericos.");
            return;
        }

        try {
            long parsedId = Long.parseLong(id);
            if (parsedId <= 0) {
                result.addError("El id debe ser mayor que 0.");
            }
        } catch (NumberFormatException ex) {
            result.addError("El id debe ser numerico.");
        }
    }

    public boolean isValidIdFormat(String id) {
        if (isBlank(id) || !id.matches(USER_ID_REGEX)) {
            return false;
        }
        try {
            return Long.parseLong(id) > 0;
        } catch (NumberFormatException ex) {
            return false;
        }
    }

    protected void validatePassword(String password, String passwordConfirmation, ValidationResult result) {
        validateRequired(password, "La contrasena es obligatoria.", result);
        validateRequired(passwordConfirmation, "La confirmacion de la contrasena es obligatoria.", result);

        if (!isBlank(password) && !isBlank(passwordConfirmation)
                && !password.equals(passwordConfirmation)) {
            result.addError("La contrasena y su confirmacion deben coincidir.");
        }
    }

    protected void validateRequired(String value, String message, ValidationResult result) {
        if (isBlank(value)) {
            result.addError(message);
        }
    }

    protected boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
