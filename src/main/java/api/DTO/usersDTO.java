package api.DTO;

import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.Size;

public class usersDTO {

    @NotBlank
    @Size(min = 5,max = 100)
    private String username;

    @NotBlank
    @Size(min = 4,max = 100)
    private String password;

    @NotBlank
    @Size(min = 5,max = 100)
    private String name;

    @NotBlank
    @Size(max = 50)
    @Email
    private String email;

    @NotBlank
    @Size(min = 2,max = 10)
    private String rolename;

    public usersDTO(){}

    public usersDTO(String username, String password, String name, String email, String rolename) {
        this.username = username;
        this.password = password;
        this.name = name;
        this.email = email;
        this.rolename = rolename;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRolename() {
        return rolename;
    }

    public void setRolename(String rolename) {
        this.rolename = rolename;
    }
}
