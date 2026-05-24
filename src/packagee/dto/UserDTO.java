package packagee.dto;


public class UserDTO {

    private final long id;
    private final String username;
    private final String firstname;
    private final String lastname;
    private final String fullName;
    private final String role;

    public UserDTO(long id, String username, String firstname, String lastname, String role) {
        this.id = id;
        this.username = username;
        this.firstname = firstname;
        this.lastname = lastname;
        this.fullName = firstname + " " + lastname;
        this.role = role;
    }

    public long getId() {
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

    public String getFullName() {
        return fullName;
    }

    public String getRole() {
        return role;
    }
}

