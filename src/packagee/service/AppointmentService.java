package packagee.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import packagee.dto.AppointmentDTO;
import packagee.dto.PrescriptionDTO;
import packagee.model.Appointment;
import packagee.model.Doctor;
import packagee.model.Patient;
import packagee.model.Prescription;
import packagee.model.enums.AppointmentStatus;
import packagee.model.enums.Specialty;
import packagee.repository.AppointmentRepository;
import packagee.observer.ModelEventPublisher;
import packagee.observer.ModelEventType;
import packagee.repository.UserRepository;
import packagee.request.AppointmentRequest;
import packagee.request.PrescriptionRequest;
import packagee.response.Response;
import packagee.util.DateTimeUtil;
import packagee.util.IdGenerator;
import packagee.util.Serializer;
import packagee.validation.AppointmentValidator;
import packagee.validation.PrescriptionValidator;
import packagee.validation.ValidationResult;

public class AppointmentService {
    private final AppointmentRepository appointmentRepository;
    private final UserRepository userRepository;
    private final AppointmentValidator appointmentValidator;
    private final PrescriptionValidator prescriptionValidator;
    private final IdGenerator idGenerator;
    private final Serializer serializer;
    private final ModelEventPublisher modelEventPublisher;

    public AppointmentService(AppointmentRepository appointmentRepository,
                              UserRepository userRepository,
                              AppointmentValidator appointmentValidator,
                              PrescriptionValidator prescriptionValidator,
                              IdGenerator idGenerator,
                              Serializer serializer,
                              ModelEventPublisher modelEventPublisher) {
        this.appointmentRepository = appointmentRepository;
        this.userRepository = userRepository;
        this.appointmentValidator = appointmentValidator;
        this.prescriptionValidator = prescriptionValidator;
        this.idGenerator = idGenerator;
        this.serializer = serializer;
        this.modelEventPublisher = modelEventPublisher;
    }

    public Response<AppointmentDTO> requestAppointment(AppointmentRequest request) {
        ValidationResult validation = appointmentValidator.validateForRequest(request);
        if (!validation.isValid()) {
            return Response.badRequest(validation.getMessage());
        }

        long patientId = Long.parseLong(request.getPatientId());
        Optional<Patient> patientOptional = userRepository.findPatientById(patientId);
        if (!patientOptional.isPresent()) {
            return Response.notFound("No existe un paciente con el id indicado.");
        }

        LocalDateTime dateTime = DateTimeUtil.parseDateTime(request.getDate(), request.getTime());
        Specialty specialty;
        Optional<Doctor> selectedDoctorOptional;

        if (!isBlank(request.getDoctorId())) {
            long doctorId = Long.parseLong(request.getDoctorId());
            selectedDoctorOptional = userRepository.findDoctorById(doctorId);
            if (!selectedDoctorOptional.isPresent()) {
                return Response.notFound("No existe un doctor con el id indicado.");
            }
            specialty = selectedDoctorOptional.get().getSpecialty();
            if (!isBlank(request.getSpecialty())
                    && Specialty.valueOf(request.getSpecialty().trim()) != specialty) {
                return Response.conflict("La especialidad de la cita debe coincidir con la especialidad del doctor.");
            }
        } else {
            specialty = Specialty.valueOf(request.getSpecialty().trim());
            selectedDoctorOptional = resolveDoctor(request.getDoctorId(), specialty, dateTime);
            if (!selectedDoctorOptional.isPresent()) {
                return Response.conflict("No hay doctor disponible para la especialidad y horario solicitados.");
            }
        }

        Doctor doctor = selectedDoctorOptional.get();
        if (isDoctorBusyAt(doctor.getId(), dateTime, null)) {
            return Response.conflict("El doctor no tiene disponibilidad en el horario solicitado.");
        }

        String appointmentId = idGenerator.nextAppointmentId(patientId);
        Appointment appointment = new Appointment(
                appointmentId,
                patientOptional.get(),
                doctor,
                specialty,
                dateTime,
                request.getReason().trim(),
                parseType(request.getType())
        );

        if (!appointmentRepository.save(appointment)) {
            return Response.error("No se pudo solicitar la cita.");
        }
        notifyAppointmentsChanged("Cita solicitada: " + appointment.getId());
        return Response.created("Cita solicitada correctamente.", serializer.toAppointmentDTO(appointment));
    }

    public Response<AppointmentDTO> acceptAppointment(String appointmentId) {
        ValidationResult validation = appointmentValidator.validateForAction(appointmentId);
        if (!validation.isValid()) {
            return Response.badRequest(validation.getMessage());
        }

        Optional<Appointment> appointmentOptional = appointmentRepository.findById(appointmentId);
        if (!appointmentOptional.isPresent()) {
            return Response.notFound("No existe una cita con el id indicado.");
        }

        Appointment appointment = appointmentOptional.get();
        if (appointment.getStatus() != AppointmentStatus.REQUESTED) {
            return Response.conflict("Solo se pueden aceptar citas en estado REQUESTED.");
        }

        appointment.accept();
        appointmentRepository.update(appointment);
        notifyAppointmentsChanged("Cita aceptada: " + appointment.getId());
        return Response.ok("Cita aceptada correctamente.", serializer.toAppointmentDTO(appointment));
    }

    public Response<AppointmentDTO> completeAppointment(String appointmentId) {
        ValidationResult validation = appointmentValidator.validateForAction(appointmentId);
        if (!validation.isValid()) {
            return Response.badRequest(validation.getMessage());
        }

        Optional<Appointment> appointmentOptional = appointmentRepository.findById(appointmentId);
        if (!appointmentOptional.isPresent()) {
            return Response.notFound("No existe una cita con el id indicado.");
        }

        Appointment appointment = appointmentOptional.get();
        if (appointment.getStatus() != AppointmentStatus.PENDING) {
            return Response.conflict("Solo se pueden completar citas en estado PENDING.");
        }

        appointment.complete();
        appointmentRepository.update(appointment);
        notifyAppointmentsChanged("Cita completada: " + appointment.getId());
        return Response.ok("Cita completada correctamente.", serializer.toAppointmentDTO(appointment));
    }

    public Response<AppointmentDTO> cancelAppointment(String appointmentId) {
        ValidationResult validation = appointmentValidator.validateForAction(appointmentId);
        if (!validation.isValid()) {
            return Response.badRequest(validation.getMessage());
        }

        Optional<Appointment> appointmentOptional = appointmentRepository.findById(appointmentId);
        if (!appointmentOptional.isPresent()) {
            return Response.notFound("No existe una cita con el id indicado.");
        }

        Appointment appointment = appointmentOptional.get();
        if (appointment.getStatus() == AppointmentStatus.COMPLETED) {
            return Response.conflict("No se puede cancelar una cita en estado COMPLETED.");
        }

        appointment.cancel();
        appointmentRepository.update(appointment);
        notifyAppointmentsChanged("Cita cancelada: " + appointment.getId());
        return Response.ok("Cita cancelada correctamente.", serializer.toAppointmentDTO(appointment));
    }

    public Response<AppointmentDTO> rescheduleAppointment(String appointmentId, String newTime, String reason) {
        ValidationResult validation = appointmentValidator.validateForReschedule(appointmentId, newTime, reason);
        if (!validation.isValid()) {
            return Response.badRequest(validation.getMessage());
        }

        Optional<Appointment> appointmentOptional = appointmentRepository.findById(appointmentId);
        if (!appointmentOptional.isPresent()) {
            return Response.notFound("No existe una cita con el id indicado.");
        }

        Appointment appointment = appointmentOptional.get();
        if (appointment.getStatus() == AppointmentStatus.COMPLETED
                || appointment.getStatus() == AppointmentStatus.CANCELED) {
            return Response.conflict("No se puede reagendar una cita completada o cancelada.");
        }

        LocalDateTime newDateTime = LocalDateTime.of(
                appointment.getDatetime().toLocalDate(),
                DateTimeUtil.parseTime(newTime)
        );

        if (appointment.getDoctor() != null
                && isDoctorBusyAt(appointment.getDoctor().getId(), newDateTime, appointment.getId())) {
            return Response.conflict("El doctor no tiene disponibilidad en la nueva hora solicitada.");
        }

        appointment.reschedule(newDateTime, reason.trim());
        appointmentRepository.update(appointment);
        notifyAppointmentsChanged("Cita reagendada: " + appointment.getId());
        return Response.ok("Cita reagendada correctamente.", serializer.toAppointmentDTO(appointment));
    }

    public Response<PrescriptionDTO> prescribeMedication(PrescriptionRequest request) {
        ValidationResult validation = prescriptionValidator.validateForCreate(request);
        if (!validation.isValid()) {
            return Response.badRequest(validation.getMessage());
        }

        Optional<Appointment> appointmentOptional = appointmentRepository.findById(request.getAppointmentId());
        if (!appointmentOptional.isPresent()) {
            return Response.notFound("No existe una cita con el id indicado.");
        }

        Appointment appointment = appointmentOptional.get();
        if (appointment.getStatus() != AppointmentStatus.PENDING) {
            return Response.conflict("Solo se pueden prescribir medicamentos en citas aceptadas PENDING.");
        }

        double dose;
        int duration;
        int frequency;
        try {
            dose = Double.parseDouble(request.getDose());
            duration = Integer.parseInt(request.getTreatmentDuration());
            frequency = Integer.parseInt(request.getFrequency());
        } catch (NumberFormatException ex) {
            return Response.badRequest("Dosis, duracion y frecuencia deben ser valores numericos.");
        }

        if (dose <= 0 || duration <= 0 || frequency <= 0) {
            return Response.badRequest("Dosis, duracion y frecuencia deben ser mayores que 0.");
        }

        Prescription prescription = new Prescription(
                appointment,
                request.getMedicationName().trim(),
                dose,
                request.getAdministrationRoute().trim(),
                duration,
                request.getAdditionalInstructions(),
                frequency
        );
        appointmentRepository.update(appointment);
        notifyAppointmentsChanged("Medicamento prescrito en cita: " + appointment.getId());
        return Response.created("Medicamento prescrito correctamente.", serializer.toPrescriptionDTO(prescription));
    }

    public Response<List<AppointmentDTO>> getAllAppointments() {
        return Response.ok("Citas consultadas correctamente.",
                serializer.toAppointmentDTOList(appointmentRepository.findAll()));
    }

    public Response<List<AppointmentDTO>> getPatientAppointments(long patientId) {
        if (!userRepository.findPatientById(patientId).isPresent()) {
            return Response.notFound("No existe un paciente con el id indicado.");
        }
        return Response.ok("Citas del paciente consultadas correctamente.",
                serializer.toAppointmentDTOList(appointmentRepository.findByPatientId(patientId)));
    }

    public Response<List<AppointmentDTO>> getDoctorAppointments(long doctorId, boolean onlyPending) {
        if (!userRepository.findDoctorById(doctorId).isPresent()) {
            return Response.notFound("No existe un doctor con el id indicado.");
        }
        List<Appointment> appointments = onlyPending
                ? appointmentRepository.findPendingByDoctorId(doctorId)
                : appointmentRepository.findByDoctorId(doctorId);
        return Response.ok("Citas del doctor consultadas correctamente.",
                serializer.toAppointmentDTOList(appointments));
    }

    public Optional<Appointment> findAppointmentModelById(String appointmentId) {
        return appointmentRepository.findById(appointmentId);
    }

    private Optional<Doctor> resolveDoctor(String doctorIdText, Specialty specialty, LocalDateTime dateTime) {
        if (!isBlank(doctorIdText)) {
            long doctorId = Long.parseLong(doctorIdText);
            return userRepository.findDoctorById(doctorId);
        }

        for (Doctor doctor : userRepository.findDoctorsBySpecialty(specialty)) {
            if (!isDoctorBusyAt(doctor.getId(), dateTime, null)) {
                return Optional.of(doctor);
            }
        }
        return Optional.empty();
    }

    private boolean isDoctorBusyAt(long doctorId, LocalDateTime dateTime, String appointmentIdToIgnore) {
        for (Appointment appointment : appointmentRepository.findByDoctorId(doctorId)) {
            if (appointmentIdToIgnore != null && appointmentIdToIgnore.equals(appointment.getId())) {
                continue;
            }
            if (appointment.getDatetime() != null
                    && appointment.getDatetime().equals(dateTime)
                    && appointment.getStatus() != AppointmentStatus.CANCELED
                    && appointment.getStatus() != AppointmentStatus.COMPLETED) {
                return true;
            }
        }
        return false;
    }

    private boolean parseType(String type) {
        if (type == null) {
            return false;
        }
        String normalized = type.trim().toLowerCase();
        return normalized.equals("true")
                || normalized.equals("hospitalization")
                || normalized.equals("hospitalizacion")
                || normalized.equals("1")
                || normalized.equals("yes")
                || normalized.equals("si");
    }

    private void notifyAppointmentsChanged(String description) {
        modelEventPublisher.notifyChange(ModelEventType.APPOINTMENTS_CHANGED, "AppointmentService", description);
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
    
}

