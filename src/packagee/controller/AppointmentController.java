package packagee.controller;

import java.util.List;
import packagee.dto.AppointmentDTO;
import packagee.dto.PrescriptionDTO;
import packagee.request.AppointmentRequest;
import packagee.request.PrescriptionRequest;
import packagee.response.Response;
import packagee.service.AppointmentService;

public class AppointmentController {
    private final AppointmentService appointmentService;

    public AppointmentController(AppointmentService appointmentService) {
        this.appointmentService = appointmentService;
    }

    public Response<AppointmentDTO> requestAppointment(AppointmentRequest request) {
        return appointmentService.requestAppointment(request);
    }

    public Response<AppointmentDTO> acceptAppointment(String appointmentId) {
        return appointmentService.acceptAppointment(appointmentId);
    }

    public Response<AppointmentDTO> completeAppointment(String appointmentId) {
        return appointmentService.completeAppointment(appointmentId);
    }

    public Response<AppointmentDTO> cancelAppointment(String appointmentId) {
        return appointmentService.cancelAppointment(appointmentId);
    }

    public Response<AppointmentDTO> rescheduleAppointment(String appointmentId, String newTime, String reason) {
        return appointmentService.rescheduleAppointment(appointmentId, newTime, reason);
    }

    public Response<PrescriptionDTO> prescribeMedication(PrescriptionRequest request) {
        return appointmentService.prescribeMedication(request);
    }

    public Response<List<AppointmentDTO>> getAllAppointments() {
        return appointmentService.getAllAppointments();
    }

    public Response<List<AppointmentDTO>> getPatientAppointments(long patientId) {
        return appointmentService.getPatientAppointments(patientId);
    }

    public Response<List<AppointmentDTO>> getPatientAppointments(String patientIdText) {
        if (patientIdText == null || patientIdText.trim().isEmpty()) {
            return Response.badRequest("Seleccione un paciente valido.");
        }
        try {
            return getPatientAppointments(Long.parseLong(patientIdText.trim()));
        } catch (NumberFormatException ex) {
            return Response.badRequest("El id del paciente debe ser numerico.");
        }
    }

    public Response<List<AppointmentDTO>> getDoctorAppointments(long doctorId, boolean onlyPending) {
        return appointmentService.getDoctorAppointments(doctorId, onlyPending);
    }

    public Response<List<AppointmentDTO>> getDoctorPendingAppointments(long doctorId) {
        return appointmentService.getDoctorAppointments(doctorId, true);
    }
}
