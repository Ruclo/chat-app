package pat.mat.chat.app.dto;

import pat.mat.chat.app.model.User;

public class UserDTO {

    private String username;

    private String pfpUrl;

    public UserDTO() {

    }

    public UserDTO(String username, String pfpUrl) {
        this.username = username;
        this.pfpUrl = pfpUrl;
    }

    public UserDTO(User user) {
        this.username = user.getUsername();
        this.pfpUrl = user.getPfpUrl();
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPfpUrl() {
        return pfpUrl;
    }

    public void setPfpUrl(String pfpUrl) {
        this.pfpUrl = pfpUrl;
    }
}
