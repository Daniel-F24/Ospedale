package packagee.controller;

import java.util.List;
import packagee.dto.HospitalizationDTO;
import packagee.model.enums.RoomType;
import packagee.request.HospitalizationRequest;
import packagee.response.Response;
import packagee.service.HospitalizationService;

public class HospitalizationController {

    private final HospitalizationService hospitalizationService;

    public HospitalizationController(HospitalizationService hospitalizationService) {
        this.hospitalizationService = hospitalizationService;
    }

    public Response<HospitalizationDTO> requestHospitalization(HospitalizationRequest request) {
        return hospitalizationService.requestHospitalization(request);
    }

    public Response<HospitalizationDTO> approveHospitalization(String hospitalizationId) {
        return hospitalizationService.approveHospitalization(hospitalizationId);
    }

    public Response<HospitalizationDTO> approveHospitalization(String hospitalizationId, String doctorIdText) {
        return hospitalizationService.approveHospitalization(hospitalizationId, doctorIdText);
    }

    public Response<HospitalizationDTO> denyHospitalization(String hospitalizationId) {
        return hospitalizationService.denyHospitalization(hospitalizationId);
    }

    public Response<HospitalizationDTO> cancelHospitalization(String hospitalizationId) {
        return hospitalizationService.cancelHospitalization(hospitalizationId);
    }

    public Response<HospitalizationDTO> sendToHospitalizationFromAppointment(String appointmentId,
                                                                             String roomType,
                                                                             String reason,
                                                                             String observations) {
        return hospitalizationService.sendToHospitalizationFromAppointment(
                appointmentId, roomType, reason, observations
        );
    }

    /**
     * Sobrecarga util para vistas que ya capturan doctorId o fecha.
     * La regla del parcial indica que al enviar desde cita se usa el doctor y fecha de la cita.
     */
    public Response<HospitalizationDTO> sendToHospitalizationFromAppointment(String appointmentId,
                                                                             String doctorId,
                                                                             String date,
                                                                             String reason,
                                                                             String roomType,
                                                                             String observations) {
        return sendToHospitalizationFromAppointment(appointmentId, roomType, reason, observations);
    }

    public Response<List<HospitalizationDTO>> getAllHospitalizations() {
        return hospitalizationService.getAllHospitalizations();
    }

    public Response<List<HospitalizationDTO>> getPatientHospitalizations(long patientId) {
        return hospitalizationService.getPatientHospitalizations(patientId);
    }

    public Response<List<HospitalizationDTO>> getDoctorHospitalizations(long doctorId) {
        return hospitalizationService.getDoctorHospitalizations(doctorId);
    }

    public Response<List<HospitalizationDTO>> getRequestedHospitalizationsByDoctor(long doctorId) {
        return hospitalizationService.getRequestedHospitalizationsByDoctor(doctorId);
    }

    public RoomType[] getAvailableRoomTypes() {
        return RoomType.values();
    }
}

