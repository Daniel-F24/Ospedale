/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package packagee.validation;

import packagee.request.PrescriptionRequest;

public class PrescriptionValidator {
     private final AppointmentValidator appointmentValidator;

    public PrescriptionValidator() {
        this.appointmentValidator = new AppointmentValidator();
    }

    public ValidationResult validateForCreate(PrescriptionRequest request) {
        ValidationResult result = ValidationResult.valid();
        if (request == null) {
            result.addError("La solicitud de prescripcion no puede ser nula.");
            return result;
        }

        merge(result, appointmentValidator.validateAppointmentId(request.getAppointmentId()));
        validateRequired(request.getMedicationName(), "El nombre del medicamento es obligatorio.", result);
        validateRequired(request.getDose(), "La dosis es obligatoria.", result);
        validateRequired(request.getAdministrationRoute(), "La via de administracion es obligatoria.", result);
        validateRequired(request.getTreatmentDuration(), "La duracion del tratamiento es obligatoria.", result);
        validateRequired(request.getFrequency(), "La frecuencia es obligatoria.", result);

        return result;
    }

    private void validateRequired(String value, String message, ValidationResult result) {
        if (value == null || value.trim().isEmpty()) {
            result.addError(message);
        }
    }

    private void merge(ValidationResult target, ValidationResult source) {
        for (String error : source.getErrors()) {
            target.addError(error);
        }
    }
}
