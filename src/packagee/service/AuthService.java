package packagee.service;

import java.util.Optional;
import packagee.dto.UserDTO;
import packagee.model.User;
import packagee.repository.UserRepository;
import packagee.request.LoginRequest;
import packagee.response.Response;
import packagee.util.Serializer;

public class AuthService {
     private final UserRepository userRepository;
    private final Serializer serializer;

    public AuthService(UserRepository userRepository, Serializer serializer) {
        this.userRepository = userRepository;
        this.serializer = serializer;
    }

    public Response<UserDTO> login(LoginRequest request) {
        if (request == null) {
            return Response.badRequest("La solicitud de login no puede ser nula.");
        }
        if (isBlank(request.getUsername()) || isBlank(request.getPassword())) {
            return Response.badRequest("Usuario y contrasena son obligatorios.");
        }

        Optional<User> userOptional = userRepository.findByUsername(request.getUsername().trim());
        if (!userOptional.isPresent()) {
            return Response.unauthorized("Usuario o contrasena incorrectos.");
        }

        User user = userOptional.get();
        if (!request.getPassword().equals(user.getPassword())) {
            return Response.unauthorized("Usuario o contrasena incorrectos.");
        }

        return Response.ok("Ingreso exitoso.", serializer.toUserDTO(user));
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
