package packagee.dto;


public class HospitalizationDTO {

    private final String id;
    private final long patientId;
    private final String patientName;
    private final long doctorId;
    private final String doctorName;
    private final String date;
    private final String roomType;
    private final String reason;
    private final String observations;
    private final String status;

    public HospitalizationDTO(String id, long patientId, String patientName, long doctorId, String doctorName,
                              String date, String roomType, String reason, String observations, String status) {
        this.id = id;
        this.patientId = patientId;
        this.patientName = patientName;
        this.doctorId = doctorId;
        this.doctorName = doctorName;
        this.date = date;
        this.roomType = roomType;
        this.reason = reason;
        this.observations = observations;
        this.status = status;
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

    public long getDoctorId() {
        return doctorId;
    }

    public String getDoctorName() {
        return doctorName;
    }

    public String getDate() {
        return date;
    }

    public String getRoomType() {
        return roomType;
    }

    public String getReason() {
        return reason;
    }

    public String getObservations() {
        return observations;
    }

    public String getStatus() {
        return status;
    }
}
