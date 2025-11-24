package net.microcontroller.backend.security.response;

import java.util.List;

public class UserInfoResponse {

    private String jwtToken;
    private String username;
    private String userEmail;
    private List<String> roles;

    public UserInfoResponse(String userEmail, List<String> roles, String jwtToken) {

        this.userEmail = userEmail;
        this.roles = roles;
        this.jwtToken = jwtToken;
    }

    public UserInfoResponse(String username, List<String> roles) {

        this.username = username;
        this.roles = roles;
    }

    public UserInfoResponse() {

    }

    public String getUserEmail() { return userEmail; }

    public void setUserEmail(String userEmail) { this.userEmail = userEmail; }

    public String getJwtToken() {
        return jwtToken;
    }

    public void setJwtToken(String jwtToken) {
        this.jwtToken = jwtToken;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public List<String> getRoles() {
        return roles;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }
}


