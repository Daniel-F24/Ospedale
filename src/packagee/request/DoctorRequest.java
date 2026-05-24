package packagee.request;

public class DoctorRequest {

    private final String id;
    private final String username;
    private final String firstname;
    private final String lastname;
    private final String password;
    private final String passwordConfirmation;
    private final String specialty;
    private final String licenceNumber;
    private final String assignedOffice;

    public DoctorRequest(String id, String username, String firstname, String lastname,
                         String password, String passwordConfirmation, String specialty,
                         String licenceNumber, String assignedOffice) {
        this.id = id;
        this.username = username;
        this.firstname = firstname;
        this.lastname = lastname;
        this.password = password;
        this.passwordConfirmation = passwordConfirmation;
        this.specialty = specialty;
        this.licenceNumber = licenceNumber;
        this.assignedOffice = assignedOffice;
    }

    public String getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getFirstname() {
        return firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public String getPassword() {
        return password;
    }

    public String getPasswordConfirmation() {
        return passwordConfirmation;
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

