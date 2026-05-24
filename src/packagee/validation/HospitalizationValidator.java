package packagee.validation;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import packagee.model.enums.RoomType;
import packagee.request.HospitalizationRequest;

public class HospitalizationValidator {
     private static final String HOSPITALIZATION_ID_REGEX = "^H-\\d{12}-\\d{4}$";
    private static final DateTimeFormatter STRICT_DATE_FORMATTER =
            DateTimeFormatter.ofPattern("uuuu-MM-dd").withResolverStyle(ResolverStyle.STRICT);

    private final UserValidator userValidator;

    public HospitalizationValidator() {
        this.userValidator = new UserValidator();
    }

    public ValidationResult validateForRequest(HospitalizationRequest request) {
        ValidationResult result = validateRequestObject(request);
        if (!result.isValid()) {
            return result;
        }

        validatePatientId(request.getPatientId(), result);
        validateOptionalDoctorId(request.getDoctorId(), result);
        validateDate(request.getDate(), result);
        validateRoomType(request.getRoomType(), result);
        validateReason(request.getReason(), result);

        return result;
    }

    public ValidationResult validateHospitalizationId(String hospitalizationId) {
        ValidationResult result = ValidationResult.valid();
        if (isBlank(hospitalizationId)) {
            result.addError("El id de la hospitalizacion es obligatorio.");
            return result;
        }
        if (!hospitalizationId.matches(HOSPITALIZATION_ID_REGEX)) {
            result.addError("El id de la hospitalizacion debe seguir el formato H-{id_paciente}-NNNN.");
        }
        return result;
    }

    public ValidationResult validateForAction(String hospitalizationId) {
        return validateHospitalizationId(hospitalizationId);
    }

    private ValidationResult validateRequestObject(HospitalizationRequest request) {
        ValidationResult result = ValidationResult.valid();
        if (request == null) {
            result.addError("La solicitud de hospitalizacion no puede ser nula.");
        }
        return result;
    }

    public void validatePatientId(String patientId, ValidationResult result) {
        if (!userValidator.isValidIdFormat(patientId)) {
            result.addError("El id del paciente debe ser mayor que 0 y tener 12 digitos.");
        }
    }

    public void validateOptionalDoctorId(String doctorId, ValidationResult result) {
        if (!isBlank(doctorId) && !userValidator.isValidIdFormat(doctorId)) {
            result.addError("El id del doctor debe ser mayor que 0 y tener 12 digitos.");
        }
    }

    public void validateRequiredDoctorId(String doctorId, ValidationResult result) {
        if (isBlank(doctorId)) {
            result.addError("El id del doctor es obligatorio para esta operacion.");
            return;
        }
        if (!userValidator.isValidIdFormat(doctorId)) {
            result.addError("El id del doctor debe ser mayor que 0 y tener 12 digitos.");
        }
    }

    public void validateDate(String date, ValidationResult result) {
        if (isBlank(date)) {
            result.addError("La fecha de la hospitalizacion es obligatoria.");
            return;
        }
        if (!date.matches("\\d{4}-\\d{2}-\\d{2}")) {
            result.addError("La fecha de la hospitalizacion debe tener el formato AAAA-MM-DD.");
            return;
        }
        try {
            LocalDate.parse(date, STRICT_DATE_FORMATTER);
        } catch (DateTimeParseException ex) {
            result.addError("La fecha de la hospitalizacion no es valida.");
        }
    }

    public boolean isValidDate(String date) {
        if (isBlank(date) || !date.matches("\\d{4}-\\d{2}-\\d{2}")) {
            return false;
        }
        try {
            LocalDate.parse(date, STRICT_DATE_FORMATTER);
            return true;
        } catch (DateTimeParseException ex) {
            return false;
        }
    }

    public void validateRoomType(String roomType, ValidationResult result) {
        if (isBlank(roomType)) {
            result.addError("El tipo de habitacion es obligatorio.");
            return;
        }
        try {
            RoomType.valueOf(roomType.trim());
        } catch (IllegalArgumentException ex) {
            result.addError("El tipo de habitacion no es valido.");
        }
    }

    public boolean isValidRoomType(String roomType) {
        if (isBlank(roomType)) {
            return false;
        }
        try {
            RoomType.valueOf(roomType.trim());
            return true;
        } catch (IllegalArgumentException ex) {
            return false;
        }
    }

    public void validateReason(String reason, ValidationResult result) {
        if (isBlank(reason)) {
            result.addError("La razon de la hospitalizacion es obligatoria.");
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
