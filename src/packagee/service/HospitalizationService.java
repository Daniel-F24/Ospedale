package packagee.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import packagee.dto.HospitalizationDTO;
import packagee.model.Appointment;
import packagee.model.Doctor;
import packagee.model.Hospitalization;
import packagee.model.Patient;
import packagee.model.enums.AppointmentStatus;
import packagee.model.enums.HospitalizationStatus;
import packagee.model.enums.RoomType;
import packagee.repository.AppointmentRepository;
import packagee.observer.ModelEventPublisher;
import packagee.observer.ModelEventType;
import packagee.repository.HospitalizationRepository;
import packagee.repository.UserRepository;
import packagee.request.HospitalizationRequest;
import packagee.response.Response;
import packagee.util.DateTimeUtil;
import packagee.util.IdGenerator;
import packagee.util.Serializer;
import packagee.validation.HospitalizationValidator;
import packagee.validation.ValidationResult;

public class HospitalizationService {
      private final HospitalizationRepository hospitalizationRepository;
    private final AppointmentRepository appointmentRepository;
    private final UserRepository userRepository;
    private final HospitalizationValidator hospitalizationValidator;
    private final IdGenerator idGenerator;
    private final Serializer serializer;
    private final ModelEventPublisher modelEventPublisher;

    public HospitalizationService(HospitalizationRepository hospitalizationRepository,
                                  AppointmentRepository appointmentRepository,
                                  UserRepository userRepository,
                                  HospitalizationValidator hospitalizationValidator,
                                  IdGenerator idGenerator,
                                  Serializer serializer,
                                  ModelEventPublisher modelEventPublisher) {
        this.hospitalizationRepository = hospitalizationRepository;
        this.appointmentRepository = appointmentRepository;
        this.userRepository = userRepository;
        this.hospitalizationValidator = hospitalizationValidator;
        this.idGenerator = idGenerator;
        this.serializer = serializer;
        this.modelEventPublisher = modelEventPublisher;
    }

    public Response<HospitalizationDTO> requestHospitalization(HospitalizationRequest request) {
        ValidationResult validation = hospitalizationValidator.validateForRequest(request);
        if (!validation.isValid()) {
            return Response.badRequest(validation.getMessage());
        }

        long patientId = Long.parseLong(request.getPatientId());
        Optional<Patient> patientOptional = userRepository.findPatientById(patientId);
        if (!patientOptional.isPresent()) {
            return Response.notFound("No existe un paciente con el id indicado.");
        }

        Doctor doctor = null;
        if (!isBlank(request.getDoctorId())) {
            long doctorId = Long.parseLong(request.getDoctorId());
            Optional<Doctor> doctorOptional = userRepository.findDoctorById(doctorId);
            if (!doctorOptional.isPresent()) {
                return Response.notFound("No existe un doctor con el id indicado.");
            }
            doctor = doctorOptional.get();
        }

        String hospitalizationId = idGenerator.nextHospitalizationId(patientId);
        LocalDate date = DateTimeUtil.parseDate(request.getDate());
        RoomType roomType = RoomType.valueOf(request.getRoomType().trim());

        Hospitalization hospitalization = new Hospitalization(
                hospitalizationId,
                patientOptional.get(),
                doctor,
                date,
                request.getReason().trim(),
                roomType,
                request.getObservations()
        );

        if (!hospitalizationRepository.save(hospitalization)) {
            return Response.error("No se pudo solicitar la hospitalizacion.");
        }
        notifyHospitalizationsChanged("Hospitalizacion solicitada: " + hospitalization.getId());
        return Response.created("Hospitalizacion solicitada correctamente.",
                serializer.toHospitalizationDTO(hospitalization));
    }

    public Response<HospitalizationDTO> approveHospitalization(String hospitalizationId) {
        return approveHospitalization(hospitalizationId, null);
    }

    public Response<HospitalizationDTO> approveHospitalization(String hospitalizationId, String doctorIdText) {
        ValidationResult validation = hospitalizationValidator.validateForAction(hospitalizationId);
        if (!validation.isValid()) {
            return Response.badRequest(validation.getMessage());
        }

        Optional<Hospitalization> hospitalizationOptional = hospitalizationRepository.findById(hospitalizationId);
        if (!hospitalizationOptional.isPresent()) {
            return Response.notFound("No existe una hospitalizacion con el id indicado.");
        }

        Hospitalization hospitalization = hospitalizationOptional.get();
        if (hospitalization.getStatus() != HospitalizationStatus.REQUESTED) {
            return Response.conflict("Solo se pueden aprobar hospitalizaciones en estado REQUESTED.");
        }

        if (!isBlank(doctorIdText)) {
            long doctorId = Long.parseLong(doctorIdText);
            Optional<Doctor> doctorOptional = userRepository.findDoctorById(doctorId);
            if (!doctorOptional.isPresent()) {
                return Response.notFound("No existe un doctor con el id indicado.");
            }
            hospitalization.setDoctor(doctorOptional.get());
        }

        if (hospitalization.getDoctor() == null) {
            return Response.badRequest("La hospitalizacion debe tener un doctor asignado para ser aprobada.");
        }

        hospitalization.approve();
        hospitalizationRepository.update(hospitalization);
        notifyHospitalizationsChanged("Hospitalizacion aprobada: " + hospitalization.getId());
        return Response.ok("Hospitalizacion aprobada correctamente.",
                serializer.toHospitalizationDTO(hospitalization));
    }

    public Response<HospitalizationDTO> denyHospitalization(String hospitalizationId) {
        ValidationResult validation = hospitalizationValidator.validateForAction(hospitalizationId);
        if (!validation.isValid()) {
            return Response.badRequest(validation.getMessage());
        }

        Optional<Hospitalization> hospitalizationOptional = hospitalizationRepository.findById(hospitalizationId);
        if (!hospitalizationOptional.isPresent()) {
            return Response.notFound("No existe una hospitalizacion con el id indicado.");
        }

        Hospitalization hospitalization = hospitalizationOptional.get();
        if (hospitalization.getStatus() != HospitalizationStatus.REQUESTED) {
            return Response.conflict("Solo se pueden denegar hospitalizaciones en estado REQUESTED.");
        }

        hospitalization.deny();
        hospitalizationRepository.update(hospitalization);
        notifyHospitalizationsChanged("Hospitalizacion denegada: " + hospitalization.getId());
        return Response.ok("Hospitalizacion denegada correctamente.",
                serializer.toHospitalizationDTO(hospitalization));
    }


    public Response<HospitalizationDTO> cancelHospitalization(String hospitalizationId) {
        ValidationResult validation = hospitalizationValidator.validateForAction(hospitalizationId);
        if (!validation.isValid()) {
            return Response.badRequest(validation.getMessage());
        }

        Optional<Hospitalization> hospitalizationOptional = hospitalizationRepository.findById(hospitalizationId);
        if (!hospitalizationOptional.isPresent()) {
            return Response.notFound("No existe una hospitalizacion con el id indicado.");
        }

        Hospitalization hospitalization = hospitalizationOptional.get();
        if (hospitalization.getStatus() == HospitalizationStatus.CANCELED) {
            return Response.conflict("La hospitalizacion ya se encuentra cancelada.");
        }

        hospitalization.deny();
        hospitalizationRepository.update(hospitalization);
        notifyHospitalizationsChanged("Hospitalizacion cancelada: " + hospitalization.getId());
        return Response.ok("Hospitalizacion cancelada correctamente.",
                serializer.toHospitalizationDTO(hospitalization));
    }

    public Response<HospitalizationDTO> sendToHospitalizationFromAppointment(String appointmentId,
                                                                             String roomTypeText,
                                                                             String reason,
                                                                             String observations) {
        Optional<Appointment> appointmentOptional = appointmentRepository.findById(appointmentId);
        if (!appointmentOptional.isPresent()) {
            return Response.notFound("No existe una cita con el id indicado.");
        }

        Appointment appointment = appointmentOptional.get();
        if (appointment.getStatus() != AppointmentStatus.PENDING) {
            return Response.conflict("Solo se puede enviar a hospitalizacion desde una cita aceptada PENDING.");
        }
        if (appointment.getPatient() == null || appointment.getDoctor() == null) {
            return Response.conflict("La cita debe tener paciente y doctor asignados.");
        }
        if (!hospitalizationValidator.isValidRoomType(roomTypeText)) {
            return Response.badRequest("El tipo de habitacion no es valido.");
        }
        if (isBlank(reason)) {
            return Response.badRequest("La razon de la hospitalizacion es obligatoria.");
        }

        String hospitalizationId = idGenerator.nextHospitalizationId(appointment.getPatient().getId());
        Hospitalization hospitalization = new Hospitalization(
                hospitalizationId,
                appointment.getPatient(),
                appointment.getDoctor(),
                appointment.getDatetime().toLocalDate(),
                reason.trim(),
                RoomType.valueOf(roomTypeText.trim()),
                observations,
                HospitalizationStatus.ONGOING
        );

        appointment.complete();
        appointmentRepository.update(appointment);

        if (!hospitalizationRepository.save(hospitalization)) {
            return Response.error("No se pudo crear la hospitalizacion desde la cita.");
        }
        modelEventPublisher.notifyChange(ModelEventType.ALL_CHANGED, "HospitalizationService", "Paciente enviado a hospitalizacion desde cita: " + appointment.getId());
        return Response.created("Paciente enviado a hospitalizacion correctamente.",
                serializer.toHospitalizationDTO(hospitalization));
    }

    public Response<List<HospitalizationDTO>> getAllHospitalizations() {
        return Response.ok("Hospitalizaciones consultadas correctamente.",
                serializer.toHospitalizationDTOList(hospitalizationRepository.findAll()));
    }

    public Response<List<HospitalizationDTO>> getPatientHospitalizations(long patientId) {
        if (!userRepository.findPatientById(patientId).isPresent()) {
            return Response.notFound("No existe un paciente con el id indicado.");
        }
        return Response.ok("Hospitalizaciones del paciente consultadas correctamente.",
                serializer.toHospitalizationDTOList(hospitalizationRepository.findByPatientId(patientId)));
    }

    public Response<List<HospitalizationDTO>> getDoctorHospitalizations(long doctorId) {
        if (!userRepository.findDoctorById(doctorId).isPresent()) {
            return Response.notFound("No existe un doctor con el id indicado.");
        }
        return Response.ok("Hospitalizaciones del doctor consultadas correctamente.",
                serializer.toHospitalizationDTOList(hospitalizationRepository.findByDoctorId(doctorId)));
    }

    public Response<List<HospitalizationDTO>> getRequestedHospitalizationsByDoctor(long doctorId) {
        if (!userRepository.findDoctorById(doctorId).isPresent()) {
            return Response.notFound("No existe un doctor con el id indicado.");
        }
        return Response.ok("Solicitudes de hospitalizacion consultadas correctamente.",
                serializer.toHospitalizationDTOList(hospitalizationRepository.findRequestedByDoctorId(doctorId)));
    }

    public Optional<Hospitalization> findHospitalizationModelById(String hospitalizationId) {
        return hospitalizationRepository.findById(hospitalizationId);
    }

    private void notifyHospitalizationsChanged(String description) {
        modelEventPublisher.notifyChange(ModelEventType.HOSPITALIZATIONS_CHANGED, "HospitalizationService", description);
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
