package packagee.service;

import java.util.List;
import java.util.Optional;
import packagee.dto.DoctorDTO;
import packagee.model.Doctor;
import packagee.model.enums.Specialty;
import packagee.observer.ModelEventPublisher;
import packagee.observer.ModelEventType;
import packagee.repository.UserRepository;
import packagee.request.DoctorRequest;
import packagee.response.Response;
import packagee.util.Serializer;
import packagee.validation.DoctorValidator;
import packagee.validation.ValidationResult;

public class DoctorService {
    private final UserRepository userRepository;
    private final DoctorValidator doctorValidator;
    private final Serializer serializer;
    private final ModelEventPublisher modelEventPublisher;

    public DoctorService(UserRepository userRepository, DoctorValidator doctorValidator,
                         Serializer serializer, ModelEventPublisher modelEventPublisher) {
        this.userRepository = userRepository;
        this.doctorValidator = doctorValidator;
        this.serializer = serializer;
        this.modelEventPublisher = modelEventPublisher;
    }

    public Response<DoctorDTO> registerDoctor(DoctorRequest request) {
        ValidationResult validation = doctorValidator.validateForCreate(request);
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

        Doctor doctor = buildDoctor(request, request.getPassword());
        if (!userRepository.save(doctor)) {
            return Response.error("No se pudo registrar el doctor.");
        }
        modelEventPublisher.notifyChange(ModelEventType.USERS_CHANGED, "DoctorService", "Doctor registrado: " + doctor.getId());
        return Response.created("Doctor registrado correctamente.", serializer.toDoctorDTO(doctor));
    }

    public Response<DoctorDTO> updateDoctor(DoctorRequest request) {
        ValidationResult validation = doctorValidator.validateForUpdate(request);
        if (!validation.isValid()) {
            return Response.badRequest(validation.getMessage());
        }

        long id = Long.parseLong(request.getId());
        Optional<Doctor> currentDoctorOptional = userRepository.findDoctorById(id);
        if (!currentDoctorOptional.isPresent()) {
            return Response.notFound("No existe un doctor con el id indicado.");
        }
        if (userRepository.existsByUsernameExcludingId(request.getUsername(), id)) {
            return Response.conflict("Ya existe otro usuario con ese nombre de usuario.");
        }

        Doctor currentDoctor = currentDoctorOptional.get();
        String password = isBlank(request.getPassword()) ? currentDoctor.getPassword() : request.getPassword();
        Doctor updatedDoctor = buildDoctor(request, password);

        if (!userRepository.update(updatedDoctor)) {
            return Response.error("No se pudo actualizar el doctor.");
        }
        modelEventPublisher.notifyChange(ModelEventType.USERS_CHANGED, "DoctorService", "Doctor actualizado: " + id);
        return Response.ok("Doctor actualizado correctamente.",
                serializer.toDoctorDTO(userRepository.findDoctorById(id).orElse(currentDoctor)));
    }

    public Response<DoctorDTO> getDoctorById(long doctorId) {
        Optional<Doctor> doctor = userRepository.findDoctorById(doctorId);
        if (!doctor.isPresent()) {
            return Response.notFound("No existe un doctor con el id indicado.");
        }
        return Response.ok("Doctor encontrado.", serializer.toDoctorDTO(doctor.get()));
    }

    public Response<List<DoctorDTO>> getAllDoctors() {
        return Response.ok("Doctores consultados correctamente.",
                serializer.toDoctorDTOList(userRepository.findAllDoctors()));
    }

    public Response<List<DoctorDTO>> getDoctorsBySpecialty(String specialtyText) {
        if (!doctorValidator.isValidSpecialty(specialtyText)) {
            return Response.badRequest("La especialidad indicada no es valida.");
        }
        Specialty specialty = Specialty.valueOf(specialtyText.trim());
        return Response.ok("Doctores por especialidad consultados correctamente.",
                serializer.toDoctorDTOList(userRepository.findDoctorsBySpecialty(specialty)));
    }

    public boolean isDoctor(long id) {
        return userRepository.findDoctorById(id).isPresent();
    }

    public Optional<Doctor> findDoctorModelById(long id) {
        return userRepository.findDoctorById(id);
    }

    private Doctor buildDoctor(DoctorRequest request, String password) {
        return new Doctor(
                Long.parseLong(request.getId()),
                request.getUsername().trim(),
                request.getFirstname().trim(),
                request.getLastname().trim(),
                password,
                Specialty.valueOf(request.getSpecialty().trim()),
                request.getLicenceNumber().trim(),
                request.getAssignedOffice().trim()
        );
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
