package packagee.test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import packagee.app.ApplicationContext;
import packagee.dto.AppointmentDTO;
import packagee.request.AppointmentRequest;
import packagee.request.PatientRequest;
import packagee.response.Response;

public class PersistenceTestRunner {

    public static void main(String[] args) throws Exception {
        Path json = Paths.get("json", "users.json");
        Path backup = Paths.get("json", "users.persistence-test.bak");

        Files.copy(json, backup, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
        try {
            ApplicationContext firstRun = new ApplicationContext();
            firstRun.initialize();

            Response<?> patientResponse = firstRun.getPatientController().registerPatient(
                    new PatientRequest(
                            "909090909090",
                            "persist_patient",
                            "Paciente",
                            "Persistente",
                            "Pass@9999",
                            "Pass@9999",
                            "persistente@test.com",
                            "1999-09-09",
                            "true",
                            "3009090909",
                            "Direccion persistente"
                    )
            );
            assertSuccess("Registro paciente persistente", patientResponse);

            Response<AppointmentDTO> appointmentResponse = firstRun.getAppointmentController().requestAppointment(
                    new AppointmentRequest(
                            "909090909090",
                            "111234567890",
                            "CARDIOLOGY",
                            "2026-12-31",
                            "10:45",
                            "Prueba de persistencia",
                            "false"
                    )
            );
            assertSuccess("Registro cita persistente", appointmentResponse);
            String appointmentId = appointmentResponse.getData().getId();

            ApplicationContext secondRun = new ApplicationContext();
            secondRun.initialize();

            boolean patientLoaded = secondRun.getUserRepository().findPatientById(909090909090L).isPresent();
            boolean appointmentLoaded = secondRun.getAppointmentRepository().findById(appointmentId).isPresent();

            if (!patientLoaded) {
                throw new IllegalStateException("El paciente no se cargo desde el JSON despues de reiniciar.");
            }
            if (!appointmentLoaded) {
                throw new IllegalStateException("La cita no se cargo desde el JSON despues de reiniciar.");
            }

            System.out.println("PRUEBA DE PERSISTENCIA APROBADA");
            System.out.println("Paciente persistente cargado: SI");
            System.out.println("Cita persistente cargada: SI");
        } finally {
            Files.copy(backup, json, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
            Files.deleteIfExists(backup);
            System.out.println("JSON restaurado despues de la prueba.");
        }
    }

    private static void assertSuccess(String name, Response<?> response) {
        if (response == null || !response.isSuccess()) {
            throw new IllegalStateException(name + " fallo: " + (response == null ? "sin respuesta" : response.getMessage()));
        }
    }
}
