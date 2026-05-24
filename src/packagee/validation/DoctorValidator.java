package packagee.validation;

import packagee.model.enums.Specialty;
import packagee.request.DoctorRequest;

public class DoctorValidator {
    private static final String LICENCE_REGEX = "^L-\\d{10} MTL$";
    private static final String OFFICE_REGEX = "^O-\\d{3}$";

    private final UserValidator userValidator;

    public DoctorValidator() {
        this.userValidator = new UserValidator();
    }

    public ValidationResult validateForCreate(DoctorRequest request) {
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

        validateDoctorSpecificData(request, result);
        return result;
    }

    public ValidationResult validateForUpdate(DoctorRequest request) {
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

        validateDoctorSpecificData(request, result);
        return result;
    }

    private ValidationResult validateRequestObject(DoctorRequest request) {
        ValidationResult result = ValidationResult.valid();
        if (request == null) {
            result.addError("La solicitud del doctor no puede ser nula.");
        }
        return result;
    }

    private void validateDoctorSpecificData(DoctorRequest request, ValidationResult result) {
        validateSpecialty(request.getSpecialty(), result);
        validateLicenceNumber(request.getLicenceNumber(), result);
        validateAssignedOffice(request.getAssignedOffice(), result);
    }

    public void validateSpecialty(String specialty, ValidationResult result) {
        if (isBlank(specialty)) {
            result.addError("La especialidad del doctor es obligatoria.");
            return;
        }
        try {
            Specialty.valueOf(specialty.trim());
        } catch (IllegalArgumentException ex) {
            result.addError("La especialidad del doctor no es valida.");
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

    public void validateLicenceNumber(String licenceNumber, ValidationResult result) {
        if (isBlank(licenceNumber)) {
            result.addError("El numero de licencia del doctor es obligatorio.");
            return;
        }
        if (!licenceNumber.matches(LICENCE_REGEX)) {
            result.addError("El numero de licencia debe seguir el formato L-XXXXXXXXXX MTL.");
        }
    }

    public boolean isValidLicenceNumber(String licenceNumber) {
        return !isBlank(licenceNumber) && licenceNumber.matches(LICENCE_REGEX);
    }

    public void validateAssignedOffice(String assignedOffice, ValidationResult result) {
        if (isBlank(assignedOffice)) {
            result.addError("La oficina asignada del doctor es obligatoria.");
            return;
        }
        if (!assignedOffice.matches(OFFICE_REGEX)) {
            result.addError("La oficina asignada debe seguir el formato O-XXX.");
        }
    }

    public boolean isValidAssignedOffice(String assignedOffice) {
        return !isBlank(assignedOffice) && assignedOffice.matches(OFFICE_REGEX);
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
