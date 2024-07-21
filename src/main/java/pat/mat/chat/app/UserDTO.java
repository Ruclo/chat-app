package pat.mat.chat.app;

import jakarta.persistence.Column;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class UserDTO {

    @NotBlank
    @Size(min=3, max=16)
    private String username;

    @Column(nullable = false)
    @NotBlank
    @Size(min = 5)
    private String password;

    public @NotBlank @Size(min = 3, max = 16) String getUsername() {
        return username;
    }

    public void setUsername(@NotBlank @Size(min = 3, max = 16) String username) {
        this.username = username;
    }

    public @NotBlank @Size(min = 5) String getPassword() {
        return password;
    }

    public void setPassword(@NotBlank @Size(min = 5) String password) {
        this.password = password;
    }
}
