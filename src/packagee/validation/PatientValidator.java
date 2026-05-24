package packagee.validation;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import packagee.request.PatientRequest;

public class PatientValidator {
    private static final String PHONE_REGEX = "\\d{10}";
    private static final String EMAIL_REGEX = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.com$";
    private static final DateTimeFormatter STRICT_DATE_FORMATTER =
            DateTimeFormatter.ofPattern("uuuu-MM-dd").withResolverStyle(ResolverStyle.STRICT);

    private final UserValidator userValidator;

    public PatientValidator() {
        this.userValidator = new UserValidator();
    }

    public ValidationResult validateForCreate(PatientRequest request) {
        ValidationResult result = validateRequestObject(request);
        if (!result.isValid()) {
            return result;
        }

        merge(result, userValidator.validateUserData(
                request.getId(),
                request.getUsername(),
                request.getFirstname(),
                request.getLastname(),
                request.getPassword(),
                request.getPasswordConfirmation()
        ));

        validatePatientSpecificData(request, result);
        return result;
    }

    public ValidationResult validateForUpdate(PatientRequest request) {
        ValidationResult result = validateRequestObject(request);
        if (!result.isValid()) {
            return result;
        }

        merge(result, userValidator.validateUserUpdateData(
                request.getId(),
                request.getUsername(),
                request.getFirstname(),
                request.getLastname(),
                request.getPassword(),
                request.getPasswordConfirmation()
        ));

        validatePatientSpecificData(request, result);
        return result;
    }

    private ValidationResult validateRequestObject(PatientRequest request) {
        ValidationResult result = ValidationResult.valid();
        if (request == null) {
            result.addError("La solicitud del paciente no puede ser nula.");
        }
        return result;
    }

    private void validatePatientSpecificData(PatientRequest request, ValidationResult result) {
        validateEmail(request.getEmail(), result);
        validateBirthdate(request.getBirthdate(), result);
        validatePhone(request.getPhone(), result);
        userValidator.validateRequired(request.getGender(), "El genero del paciente es obligatorio.", result);
        userValidator.validateRequired(request.getAddress(), "La direccion del paciente es obligatoria.", result);
    }

    public void validateEmail(String email, ValidationResult result) {
        if (isBlank(email)) {
            result.addError("El email del paciente es obligatorio.");
            return;
        }
        if (!email.matches(EMAIL_REGEX)) {
            result.addError("El email debe seguir el formato XXXXX@XXXXX.com.");
        }
    }

    public boolean isValidEmail(String email) {
        return !isBlank(email) && email.matches(EMAIL_REGEX);
    }

    public void validateBirthdate(String birthdate, ValidationResult result) {
        if (isBlank(birthdate)) {
            result.addError("La fecha de nacimiento es obligatoria.");
            return;
        }
        if (!birthdate.matches("\\d{4}-\\d{2}-\\d{2}")) {
            result.addError("La fecha de nacimiento debe tener el formato AAAA-MM-DD.");
            return;
        }
        try {
            LocalDate.parse(birthdate, STRICT_DATE_FORMATTER);
        } catch (DateTimeParseException ex) {
            result.addError("La fecha de nacimiento no es valida.");
        }
    }

    public boolean isValidBirthdate(String birthdate) {
        if (isBlank(birthdate) || !birthdate.matches("\\d{4}-\\d{2}-\\d{2}")) {
            return false;
        }
        try {
            LocalDate.parse(birthdate, STRICT_DATE_FORMATTER);
            return true;
        } catch (DateTimeParseException ex) {
            return false;
        }
    }

    public void validatePhone(String phone, ValidationResult result) {
        if (isBlank(phone)) {
            result.addError("El telefono del paciente es obligatorio.");
            return;
        }
        if (!phone.matches(PHONE_REGEX)) {
            result.addError("El telefono del paciente debe tener exactamente 10 digitos.");
        }
    }

    public boolean isValidPhone(String phone) {
        return !isBlank(phone) && phone.matches(PHONE_REGEX);
    }

    private void merge(ValidationResult target, ValidationResult source) {
        for (String error : source.getErrors()) {
            target.addError(error);
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
