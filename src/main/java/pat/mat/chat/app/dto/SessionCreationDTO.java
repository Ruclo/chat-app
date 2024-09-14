package pat.mat.chat.app.dto;

import jakarta.validation.constraints.NotBlank;

public class SessionCreationDTO {
    @NotBlank
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
