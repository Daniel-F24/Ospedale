package packagee.request;

public class HospitalizationRequest {

    private final String patientId;
    private final String doctorId;
    private final String date;
    private final String reason;
    private final String roomType;
    private final String observations;

    public HospitalizationRequest(String patientId, String doctorId, String date,
                                  String reason, String roomType, String observations) {
        this.patientId = patientId;
        this.doctorId = doctorId;
        this.date = date;
        this.reason = reason;
        this.roomType = roomType;
        this.observations = observations;
    }

    public String getPatientId() {
        return patientId;
    }

    public String getDoctorId() {
        return doctorId;
    }

    public String getDate() {
        return date;
    }

    public String getReason() {
        return reason;
    }

    public String getRoomType() {
        return roomType;
    }

    public String getObservations() {
        return observations;
    }
}


