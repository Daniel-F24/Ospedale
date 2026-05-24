package packagee.request;

public class PatientRequest {

    private final String id;
    private final String username;
    private final String firstname;
    private final String lastname;
    private final String password;
    private final String passwordConfirmation;
    private final String email;
    private final String birthdate;
    private final String gender;
    private final String phone;
    private final String address;

    public PatientRequest(String id, String username, String firstname, String lastname,
                          String password, String passwordConfirmation, String email,
                          String birthdate, String gender, String phone, String address) {
        this.id = id;
        this.username = username;
        this.firstname = firstname;
        this.lastname = lastname;
        this.password = password;
        this.passwordConfirmation = passwordConfirmation;
        this.email = email;
        this.birthdate = birthdate;
        this.gender = gender;
        this.phone = phone;
        this.address = address;
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

    public String getEmail() {
        return email;
    }

    public String getBirthdate() {
        return birthdate;
    }

    public String getGender() {
        return gender;
    }

    public String getPhone() {
        return phone;
    }

    public String getAddress() {
        return address;
    }
}

