package packagee.dto;

/**
 * Datos serializados del doctor para formularios, comboBox y tablas.
 */
public class DoctorDTO extends UserDTO {

    private final String specialty;
    private final String licenceNumber;
    private final String assignedOffice;

    public DoctorDTO(long id, String username, String firstname, String lastname,
                     String specialty, String licenceNumber, String assignedOffice) {
        super(id, username, firstname, lastname, "DOCTOR");
        this.specialty = specialty;
        this.licenceNumber = licenceNumber;
        this.assignedOffice = assignedOffice;
    }

    public String getSpecialty() {
        return specialty;
    }

    public String getLicenceNumber() {
        return licenceNumber;
    }

    public String getAssignedOffice() {
        return assignedOffice;
    }
}
