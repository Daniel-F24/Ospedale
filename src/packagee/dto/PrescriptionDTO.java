package packagee.dto;


public class PrescriptionDTO {

    private final String appointmentId;
    private final String medicationName;
    private final String dose;
    private final String administrationRoute;
    private final String treatmentDuration;
    private final String frequency;
    private final String additionalInstructions;

    public PrescriptionDTO(String appointmentId, String medicationName, String dose,
                           String administrationRoute, String treatmentDuration,
                           String frequency, String additionalInstructions) {
        this.appointmentId = appointmentId;
        this.medicationName = medicationName;
        this.dose = dose;
        this.administrationRoute = administrationRoute;
        this.treatmentDuration = treatmentDuration;
        this.frequency = frequency;
        this.additionalInstructions = additionalInstructions;
    }

    public String getAppointmentId() {
        return appointmentId;
    }

    public String getMedicationName() {
        return medicationName;
    }

    public String getDose() {
        return dose;
    }

    public String getAdministrationRoute() {
        return administrationRoute;
    }

    public String getTreatmentDuration() {
        return treatmentDuration;
    }

    public String getFrequency() {
        return frequency;
    }

    public String getAdditionalInstructions() {
        return additionalInstructions;
    }
}
