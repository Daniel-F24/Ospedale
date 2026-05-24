package packagee.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import packagee.dto.PatientDTO;
import packagee.model.Patient;
import packagee.model.User;
import packagee.repository.UserRepository;
import packagee.observer.ModelEventPublisher;
import packagee.observer.ModelEventType;
import packagee.request.PatientRequest;
import packagee.response.Response;
import packagee.util.DateTimeUtil;
import packagee.util.Serializer;
import packagee.validation.PatientValidator;
import packagee.validation.ValidationResult;

public class PatientService {
     private final UserRepository userRepository;
    private final PatientValidator patientValidator;
    private final Serializer serializer;
    private final ModelEventPublisher modelEventPublisher;

    public PatientService(UserRepository userRepository, PatientValidator patientValidator,
                          Serializer serializer, ModelEventPublisher modelEventPublisher) {
        this.userRepository = userRepository;
        this.patientValidator = patientValidator;
        this.serializer = serializer;
        this.modelEventPublisher = modelEventPublisher;
    }

    public Response<PatientDTO> registerPatient(PatientRequest request) {
        ValidationResult validation = patientValidator.validateForCreate(request);
        if (!validation.isValid()) {
            return Response.badRequest(validation.getMessage());
        }

        long id = Long.parseLong(request.getId());
        if (userRepository.existsById(id)) {
            return Response.conflict("Ya existe un usuario con ese id.");
        }
        if (userRepository.existsByUsername(request.getUsername())) {
            return Response.conflict("Ya existe un usuario con ese nombre de usuario.");
        }

        Patient patient = buildPatient(request, request.getPassword());
        if (!userRepository.save(patient)) {
            return Response.error("No se pudo registrar el paciente.");
        }
        modelEventPublisher.notifyChange(ModelEventType.USERS_CHANGED, "PatientService", "Paciente registrado: " + patient.getId());
        return Response.created("Paciente registrado correctamente.", serializer.toPatientDTO(patient));
    }

    public Response<PatientDTO> updatePatient(PatientRequest request) {
        ValidationResult validation = patientValidator.validateForUpdate(request);
        if (!validation.isValid()) {
            return Response.badRequest(validation.getMessage());
        }

        long id = Long.parseLong(request.getId());
        Optional<Patient> currentPatientOptional = userRepository.findPatientById(id);
        if (!currentPatientOptional.isPresent()) {
            return Response.notFound("No existe un paciente con el id indicado.");
        }
        if (userRepository.existsByUsernameExcludingId(request.getUsername(), id)) {
            return Response.conflict("Ya existe otro usuario con ese nombre de usuario.");
        }

        Patient currentPatient = currentPatientOptional.get();
        String password = isBlank(request.getPassword()) ? currentPatient.getPassword() : request.getPassword();
        Patient updatedPatient = buildPatient(request, password);

        if (!userRepository.update(updatedPatient)) {
            return Response.error("No se pudo actualizar el paciente.");
        }
        modelEventPublisher.notifyChange(ModelEventType.USERS_CHANGED, "PatientService", "Paciente actualizado: " + id);
        return Response.ok("Paciente actualizado correctamente.",
                serializer.toPatientDTO(userRepository.findPatientById(id).orElse(currentPatient)));
    }

    public Response<PatientDTO> getPatientById(long patientId) {
        Optional<Patient> patient = userRepository.findPatientById(patientId);
        if (!patient.isPresent()) {
            return Response.notFound("No existe un paciente con el id indicado.");
        }
        return Response.ok("Paciente encontrado.", serializer.toPatientDTO(patient.get()));
    }

    public Response<List<PatientDTO>> getAllPatients() {
        return Response.ok("Pacientes consultados correctamente.",
                serializer.toPatientDTOList(userRepository.findAllPatients()));
    }

    public boolean isPatient(long id) {
        return userRepository.findPatientById(id).isPresent();
    }

    public Optional<Patient> findPatientModelById(long id) {
        return userRepository.findPatientById(id);
    }

    private Patient buildPatient(PatientRequest request, String password) {
        long id = Long.parseLong(request.getId());
        LocalDate birthdate = DateTimeUtil.parseDate(request.getBirthdate());
        boolean gender = parseGender(request.getGender());
        long phone = Long.parseLong(request.getPhone());

        return new Patient(
                id,
                request.getUsername().trim(),
                request.getFirstname().trim(),
                request.getLastname().trim(),
                password,
                request.getEmail().trim(),
                birthdate,
                gender,
                phone,
                request.getAddress().trim()
        );
    }

    private boolean parseGender(String gender) {
        if (gender == null) {
            return false;
        }
        String normalized = gender.trim().toLowerCase();
        return normalized.equals("true")
                || normalized.equals("male")
                || normalized.equals("masculino")
                || normalized.equals("m")
                || normalized.equals("1");
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
