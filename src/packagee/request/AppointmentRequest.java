package packagee.request;

public class AppointmentRequest {

    private final String patientId;
    private final String doctorId;
    private final String specialty;
    private final String date;
    private final String time;
    private final String reason;
    private final String type;

    public AppointmentRequest(String patientId, String doctorId, String specialty,
                              String date, String time, String reason, String type) {
        this.patientId = patientId;
        this.doctorId = doctorId;
        this.specialty = specialty;
        this.date = date;
        this.time = time;
        this.reason = reason;
        this.type = type;
    }

    public String getPatientId() {
        return patientId;
    }

    public String getDoctorId() {
        return doctorId;
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

    public String getReason() {
        return reason;
    }

    public String getType() {
        return type;
    }
}

