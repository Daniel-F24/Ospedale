package packagee.controller;

import packagee.dto.UserDTO;
import packagee.request.LoginRequest;
import packagee.response.Response;
import packagee.service.AuthService;

public class AuthController {

    private final AuthService authService;
    private UserDTO currentUser;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    public Response<UserDTO> login(LoginRequest request) {
        Response<UserDTO> response = authService.login(request);
        if (response.isSuccess()) {
            currentUser = response.getData();
        }
        return response;
    }

    public Response<String> logout() {
        currentUser = null;
        return Response.ok("Sesion cerrada correctamente.", "LOGOUT");
    }

    public UserDTO getCurrentUser() {
        return currentUser;
    }

    public boolean isLoggedIn() {
        return currentUser != null;
    }

    public boolean isAdminSession() {
        return currentUser != null && "ADMINISTRATOR".equalsIgnoreCase(currentUser.getRole());
    }
}

