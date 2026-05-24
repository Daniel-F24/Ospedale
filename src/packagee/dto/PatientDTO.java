package packagee.dto;


public class PatientDTO extends UserDTO {

    private final String email;
    private final String birthdate;
    private final String gender;
    private final String phone;
    private final String address;

    public PatientDTO(long id, String username, String firstname, String lastname,
                      String email, String birthdate, String gender, String phone, String address) {
        super(id, username, firstname, lastname, "PATIENT");
        this.email = email;
        this.birthdate = birthdate;
        this.gender = gender;
        this.phone = phone;
        this.address = address;
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
