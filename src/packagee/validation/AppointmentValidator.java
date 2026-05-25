package packagee.validation;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import packagee.model.enums.Specialty;
import packagee.request.AppointmentRequest;

public class AppointmentValidator {
    private static final String APPOINTMENT_ID_REGEX = "^A-\\d{12}-\\d{4}$";
    private static final DateTimeFormatter STRICT_DATE_FORMATTER =
            DateTimeFormatter.ofPattern("uuuu-MM-dd").withResolverStyle(ResolverStyle.STRICT);
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    private final UserValidator userValidator;

    public AppointmentValidator() {
        this.userValidator = new UserValidator();
    }

    public ValidationResult validateForRequest(AppointmentRequest request) {
        ValidationResult result = validateRequestObject(request);
        if (!result.isValid()) {
            return result;
        }

        validatePatientId(request.getPatientId(), result);
        validateOptionalDoctorId(request.getDoctorId(), result);
        validateSpecialtyOrDoctor(request.getSpecialty(), request.getDoctorId(), result);
        validateDate(request.getDate(), result);
        validateTime(request.getTime(), result);
        validateReason(request.getReason(), result);

        return result;
    }

    public ValidationResult validateAppointmentId(String appointmentId) {
        ValidationResult result = ValidationResult.valid();
        if (isBlank(appointmentId)) {
            result.addError("El id de la cita es obligatorio.");
            return result;
        }
        if (!appointmentId.matches(APPOINTMENT_ID_REGEX)) {
            result.addError("El id de la cita debe seguir el formato A-{id_paciente}-NNNN.");
        }
        return result;
    }

    public ValidationResult validateForReschedule(String appointmentId, String newTime, String reason) {
        ValidationResult result = validateAppointmentId(appointmentId);
        validateTime(newTime, result);
        if (isBlank(reason)) {
            result.addError("La razon del reagendamiento es obligatoria.");
        }
        return result;
    }

    public ValidationResult validateForAction(String appointmentId) {
        return validateAppointmentId(appointmentId);
    }

    private ValidationResult validateRequestObject(AppointmentRequest request) {
        ValidationResult result = ValidationResult.valid();
        if (request == null) {
            result.addError("La solicitud de la cita no puede ser nula.");
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

    public void validateSpecialty(String specialty, ValidationResult result) {
        if (isBlank(specialty)) {
            result.addError("La especialidad de la cita es obligatoria.");
            return;
        }
        try {
            Specialty.valueOf(specialty.trim());
        } catch (IllegalArgumentException ex) {
            result.addError("La especialidad de la cita no es valida.");
        }
    }

    public void validateSpecialtyOrDoctor(String specialty, String doctorId, ValidationResult result) {
        if (isBlank(doctorId)) {
            validateSpecialty(specialty, result);
            return;
        }
        if (!isBlank(specialty)) {
            try {
                Specialty.valueOf(specialty.trim());
            } catch (IllegalArgumentException ex) {
                result.addError("La especialidad de la cita no es valida.");
            }
        }
    }

    public boolean isValidSpecialty(String specialty) {
        if (isBlank(specialty)) {
            return false;
        }
        try {
            Specialty.valueOf(specialty.trim());
            return true;
        } catch (IllegalArgumentException ex) {
            return false;
        }
    }

    public void validateDate(String date, ValidationResult result) {
        if (isBlank(date)) {
            result.addError("La fecha de la cita es obligatoria.");
            return;
        }
        if (!date.matches("\\d{4}-\\d{2}-\\d{2}")) {
            result.addError("La fecha de la cita debe tener el formato AAAA-MM-DD.");
            return;
        }
        try {
            LocalDate.parse(date, STRICT_DATE_FORMATTER);
        } catch (DateTimeParseException ex) {
            result.addError("La fecha de la cita no es valida.");
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

    public void validateTime(String time, ValidationResult result) {
        if (isBlank(time)) {
            result.addError("La hora de la cita es obligatoria.");
            return;
        }
        if (!time.matches("\\d{2}:\\d{2}")) {
            result.addError("La hora de la cita debe tener el formato hh:mm.");
            return;
        }
        try {
            LocalTime parsedTime = LocalTime.parse(time, TIME_FORMATTER);
            int minute = parsedTime.getMinute();
            if (!(minute == 0 || minute == 15 || minute == 30 || minute == 45)) {
                result.addError("Los minutos de la cita solo pueden ser 00, 15, 30 o 45.");
            }
        } catch (DateTimeParseException ex) {
            result.addError("La hora de la cita no es valida en formato de 24 horas.");
        }
    }

    public boolean isValidTime(String time) {
        if (isBlank(time) || !time.matches("\\d{2}:\\d{2}")) {
            return false;
        }
        try {
            LocalTime parsedTime = LocalTime.parse(time, TIME_FORMATTER);
            int minute = parsedTime.getMinute();
            return minute == 0 || minute == 15 || minute == 30 || minute == 45;
        } catch (DateTimeParseException ex) {
            return false;
        }
    }

    public void validateReason(String reason, ValidationResult result) {
        if (isBlank(reason)) {
            result.addError("La razon de la cita es obligatoria.");
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}

