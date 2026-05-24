package packagee.controller;

import java.util.ArrayList;
import java.util.List;
import packagee.dto.AppointmentDTO;
import packagee.dto.DoctorDTO;
import packagee.dto.HospitalizationDTO;
import packagee.dto.PatientDTO;
import packagee.response.Response;

public class TableController {

    private final PatientController patientController;
    private final DoctorController doctorController;
    private final AppointmentController appointmentController;
    private final HospitalizationController hospitalizationController;

    public TableController(PatientController patientController,
                           DoctorController doctorController,
                           AppointmentController appointmentController,
                           HospitalizationController hospitalizationController) {
        this.patientController = patientController;
        this.doctorController = doctorController;
        this.appointmentController = appointmentController;
        this.hospitalizationController = hospitalizationController;
    }

    public Response<List<Object[]>> getPatientsTableData() {
        Response<List<PatientDTO>> response = patientController.getAllPatients();
        if (!response.isSuccess()) {
            return Response.error(response.getMessage());
        }
        List<Object[]> rows = new ArrayList<>();
        for (PatientDTO patient : response.getData()) {
            rows.add(new Object[]{
                    patient.getId(),
                    patient.getUsername(),
                    patient.getFirstname(),
                    patient.getLastname(),
                    patient.getEmail(),
                    patient.getBirthdate(),
                    patient.getGender(),
                    patient.getPhone(),
                    patient.getAddress()
            });
        }
        return Response.ok("Datos de pacientes obtenidos correctamente.", rows);
    }

    public Response<List<Object[]>> getDoctorsTableData() {
        Response<List<DoctorDTO>> response = doctorController.getAllDoctors();
        if (!response.isSuccess()) {
            return Response.error(response.getMessage());
        }
        List<Object[]> rows = new ArrayList<>();
        for (DoctorDTO doctor : response.getData()) {
            rows.add(new Object[]{
                    doctor.getId(),
                    doctor.getUsername(),
                    doctor.getFirstname(),
                    doctor.getLastname(),
                    doctor.getSpecialty(),
                    doctor.getLicenceNumber(),
                    doctor.getAssignedOffice()
            });
        }
        return Response.ok("Datos de doctores obtenidos correctamente.", rows);
    }

    public Response<List<Object[]>> getAppointmentsTableData() {
        return appointmentRowsFromResponse(appointmentController.getAllAppointments());
    }

    public Response<List<Object[]>> getPatientAppointmentsTableData(long patientId) {
        return appointmentRowsFromResponse(appointmentController.getPatientAppointments(patientId));
    }

    public Response<List<Object[]>> getDoctorAppointmentsTableData(long doctorId, boolean onlyPending) {
        return appointmentRowsFromResponse(appointmentController.getDoctorAppointments(doctorId, onlyPending));
    }

    public Response<List<Object[]>> getHospitalizationsTableData() {
        return hospitalizationRowsFromResponse(hospitalizationController.getAllHospitalizations());
    }

    public Response<List<Object[]>> getPatientHospitalizationsTableData(long patientId) {
        return hospitalizationRowsFromResponse(hospitalizationController.getPatientHospitalizations(patientId));
    }

    public Response<List<Object[]>> getDoctorHospitalizationsTableData(long doctorId) {
        return hospitalizationRowsFromResponse(hospitalizationController.getDoctorHospitalizations(doctorId));
    }

    private Response<List<Object[]>> appointmentRowsFromResponse(Response<List<AppointmentDTO>> response) {
        if (!response.isSuccess()) {
            return Response.error(response.getMessage());
        }
        List<Object[]> rows = new ArrayList<>();
        for (AppointmentDTO appointment : response.getData()) {
            rows.add(new Object[]{
                    appointment.getId(),
                    appointment.getPatientId(),
                    appointment.getPatientName(),
                    appointment.getDoctorId(),
                    appointment.getDoctorName(),
                    appointment.getSpecialty(),
                    appointment.getDate(),
                    appointment.getTime(),
                    appointment.getReason(),
                    appointment.getType(),
                    appointment.getStatus()
            });
        }
        return Response.ok("Datos de citas obtenidos correctamente.", rows);
    }

    private Response<List<Object[]>> hospitalizationRowsFromResponse(Response<List<HospitalizationDTO>> response) {
        if (!response.isSuccess()) {
            return Response.error(response.getMessage());
        }
        List<Object[]> rows = new ArrayList<>();
        for (HospitalizationDTO hospitalization : response.getData()) {
            rows.add(new Object[]{
                    hospitalization.getId(),
                    hospitalization.getPatientId(),
                    hospitalization.getPatientName(),
                    hospitalization.getDoctorId(),
                    hospitalization.getDoctorName(),
                    hospitalization.getDate(),
                    hospitalization.getRoomType(),
                    hospitalization.getReason(),
                    hospitalization.getObservations(),
                    hospitalization.getStatus()
            });
        }
        return Response.ok("Datos de hospitalizaciones obtenidos correctamente.", rows);
    }
}

