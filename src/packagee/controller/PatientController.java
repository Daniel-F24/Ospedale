package packagee.controller;

import java.util.List;
import packagee.dto.PatientDTO;
import packagee.request.PatientRequest;
import packagee.response.Response;
import packagee.service.PatientService;

public class PatientController {

    private final PatientService patientService;

    public PatientController(PatientService patientService) {
        this.patientService = patientService;
    }

    public Response<PatientDTO> registerPatient(PatientRequest request) {
        return patientService.registerPatient(request);
    }

    public Response<PatientDTO> updatePatient(PatientRequest request) {
        return patientService.updatePatient(request);
    }

    public Response<PatientDTO> getPatientInfo(long patientId) {
        return patientService.getPatientById(patientId);
    }

    public Response<List<PatientDTO>> getAllPatients() {
        return patientService.getAllPatients();
    }
}
