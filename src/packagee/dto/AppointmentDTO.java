package packagee.dto;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Datos serializados de una cita medica para las vistas y tablas.
 */
public class AppointmentDTO {

    private final String id;
    private final long patientId;
    private final String patientName;
    private final long doctorId;
    private final String doctorName;
    private final String specialty;
    private final String date;
    private final String time;
    private final String reason;
    private final String type;
    private final String status;
    private final String diagnosis;
    private final String observations;
    private final String recommendedTreatment;
    private final String followUp;
    private final List<PrescriptionDTO> prescriptions;

    public AppointmentDTO(String id, long patientId, String patientName, long doctorId, String doctorName,
                          String specialty, String date, String time, String reason, String type, String status,
                          String diagnosis, String observations, String recommendedTreatment, String followUp,
                          List<PrescriptionDTO> prescriptions) {
        this.id = id;
        this.patientId = patientId;
        this.patientName = patientName;
        this.doctorId = doctorId;
        this.doctorName = doctorName;
        this.specialty = specialty;
        this.date = date;
        this.time = time;
        this.reason = reason;
        this.type = type;
        this.status = status;
        this.diagnosis = diagnosis;
        this.observations = observations;
        this.recommendedTreatment = recommendedTreatment;
        this.followUp = followUp;
        this.prescriptions = prescriptions == null ? new ArrayList<>() : new ArrayList<>(prescriptions);
    }

    public String getId() {
        return id;
    }

    public long getPatientId() {
        return patientId;
    }

    public String getPatientName() {
        return patientName;
    }
    public String getReason() {
        return reason;
    }

    public String getType() {
        return type;
    }

    public String getStatus() {
        return status;
    }

    public String getDiagnosis() {
        return diagnosis;
    }

    public String getObservations() {
        return observations;
    }

    public long getDoctorId() {
        return doctorId;
    }

    public String getDoctorName() {
        return doctorName;
    }

    public String getSpecialty() {
        return specialty;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }


    public String getRecommendedTreatment() {
        return recommendedTreatment;
    }

    public String getFollowUp() {
        return followUp;
    }

    public List<PrescriptionDTO> getPrescriptions() {
        return Collections.unmodifiableList(prescriptions);
    }
}
