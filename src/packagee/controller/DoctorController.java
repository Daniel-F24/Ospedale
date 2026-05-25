package packagee.controller;

import java.util.List;
import packagee.dto.DoctorDTO;
import packagee.model.enums.Specialty;
import packagee.request.DoctorRequest;
import packagee.response.Response;
import packagee.service.DoctorService;

public class DoctorController {

    private final DoctorService doctorService;

    public DoctorController(DoctorService doctorService) {
        this.doctorService = doctorService;
    }

    public Response<DoctorDTO> registerDoctor(DoctorRequest request) {
        return doctorService.registerDoctor(request);
    }

    public Response<DoctorDTO> updateDoctor(DoctorRequest request) {
        return doctorService.updateDoctor(request);
    }

    public Response<DoctorDTO> getDoctorInfo(long doctorId) {
        return doctorService.getDoctorById(doctorId);
    }

    public Response<DoctorDTO> getDoctorInfo(String doctorIdText) {
        if (doctorIdText == null || doctorIdText.trim().isEmpty()) {
            return Response.badRequest("Seleccione un doctor valido.");
        }
        try {
            return getDoctorInfo(Long.parseLong(doctorIdText.trim()));
        } catch (NumberFormatException ex) {
            return Response.badRequest("El id del doctor debe ser numerico.");
        }
    }

    public Response<List<DoctorDTO>> getAllDoctors() {
        return doctorService.getAllDoctors();
    }

    public Response<List<DoctorDTO>> getDoctorsBySpecialty(String specialtyText) {
        return doctorService.getDoctorsBySpecialty(specialtyText);
    }

    public Specialty[] getAvailableSpecialties() {
        return Specialty.values();
    }
}

