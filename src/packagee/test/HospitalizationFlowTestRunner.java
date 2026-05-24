package packagee.test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import packagee.app.ApplicationContext;
import packagee.controller.AppointmentController;
import packagee.controller.HospitalizationController;
import packagee.dto.AppointmentDTO;
import packagee.dto.HospitalizationDTO;
import packagee.request.AppointmentRequest;
import packagee.request.HospitalizationRequest;
import packagee.response.Response;

public class HospitalizationFlowTestRunner {

    private static int passed = 0;
    private static int failed = 0;

    public static void main(String[] args) throws Exception {
        Path jsonPath = Paths.get("json", "users.json");
        byte[] backup = Files.readAllBytes(jsonPath);

        try {
            runHospitalizationFlowTest();
        } finally {
            Files.write(jsonPath, backup);
            System.out.println("JSON restaurado despues de la prueba de hospitalizaciones.");
        }

        System.out.println("==============================");
        System.out.println("RESUMEN HOSPITALIZACIONES");
        System.out.println("Aprobadas: " + passed);
        System.out.println("Fallidas: " + failed);
        System.out.println("==============================");

        if (failed > 0) {
            System.exit(1);
        }
    }

    private static void runHospitalizationFlowTest() {
        ApplicationContext context = new ApplicationContext();
        context.initialize();

        HospitalizationController hospitalizationController = context.getHospitalizationController();
        AppointmentController appointmentController = context.getAppointmentController();

        long patientId = 112234567890L;
        long doctorId = 111234567890L;

        HospitalizationRequest patientRequest = new HospitalizationRequest(
                String.valueOf(patientId),
                String.valueOf(doctorId),
                "2026-07-11",
                "Solicitud de hospitalizacion por paciente",
                "STANDARD",
                "Observacion inicial"
        );
        Response<HospitalizationDTO> requested = hospitalizationController.requestHospitalization(patientRequest);
        assertTrue("Paciente solicita hospitalizacion", requested.isSuccess());
        assertEquals("Hospitalizacion solicitada inicia REQUESTED", "REQUESTED", requested.getData().getStatus());
        assertEquals("Hospitalizacion queda asociada al doctor seleccionado", doctorId, requested.getData().getDoctorId());

        Response<HospitalizationDTO> approved = hospitalizationController.approveHospitalization(
                requested.getData().getId(),
                String.valueOf(doctorId)
        );
        assertTrue("Doctor aprueba hospitalizacion", approved.isSuccess());
        assertEquals("Hospitalizacion aprobada queda ONGOING", "ONGOING", approved.getData().getStatus());

        AppointmentRequest appointmentRequest = new AppointmentRequest(
                String.valueOf(patientId),
                String.valueOf(doctorId),
                "CARDIOLOGY",
                "2026-07-12",
                "10:00",
                "Cita previa a hospitalizacion directa",
                "false"
        );
        Response<AppointmentDTO> appointmentCreated = appointmentController.requestAppointment(appointmentRequest);
        assertTrue("Se crea cita para hospitalizacion directa", appointmentCreated.isSuccess());

        Response<AppointmentDTO> appointmentAccepted = appointmentController.acceptAppointment(appointmentCreated.getData().getId());
        assertTrue("Doctor acepta cita previa", appointmentAccepted.isSuccess());
        assertEquals("Cita aceptada queda PENDING", "PENDING", appointmentAccepted.getData().getStatus());

        Response<HospitalizationDTO> directHospitalization = hospitalizationController.sendToHospitalizationFromAppointment(
                appointmentCreated.getData().getId(),
                "STANDARD",
                "Hospitalizacion directa desde cita",
                "Paciente remitido desde consulta"
        );
        assertTrue("Doctor envia paciente a hospitalizacion desde cita", directHospitalization.isSuccess());
        assertEquals("Hospitalizacion directa queda ONGOING", "ONGOING", directHospitalization.getData().getStatus());

        Response<List<AppointmentDTO>> patientAppointments = appointmentController.getPatientAppointments(patientId);
        AppointmentDTO completedAppointment = null;
        for (AppointmentDTO appointment : patientAppointments.getData()) {
            if (appointmentCreated.getData().getId().equals(appointment.getId())) {
                completedAppointment = appointment;
                break;
            }
        }
        assertTrue("La cita directa existe luego de hospitalizar", completedAppointment != null);
        assertEquals("La cita queda COMPLETED al hospitalizar desde cita", "COMPLETED", completedAppointment.getStatus());

        HospitalizationRequest unassignedRequest = new HospitalizationRequest(
                String.valueOf(patientId),
                "",
                "2026-07-13",
                "Solicitud sin doctor seleccionado",
                "STANDARD",
                "Debe poder verla un doctor para aprobarla"
        );
        Response<HospitalizationDTO> unassigned = hospitalizationController.requestHospitalization(unassignedRequest);
        assertTrue("Paciente puede dejar hospitalizacion solicitada sin doctor asignado", unassigned.isSuccess());

        Response<List<HospitalizationDTO>> requestedByDoctor = hospitalizationController.getRequestedHospitalizationsByDoctor(doctorId);
        boolean visibleForDoctor = false;
        for (HospitalizationDTO hospitalization : requestedByDoctor.getData()) {
            if (unassigned.getData().getId().equals(hospitalization.getId())) {
                visibleForDoctor = true;
                break;
            }
        }
        assertTrue("Doctor puede ver solicitudes REQUESTED no asignadas", visibleForDoctor);

        Response<HospitalizationDTO> approvedUnassigned = hospitalizationController.approveHospitalization(
                unassigned.getData().getId(),
                String.valueOf(doctorId)
        );
        assertTrue("Doctor aprueba solicitud no asignada", approvedUnassigned.isSuccess());
        assertEquals("Solicitud no asignada queda asignada al doctor", doctorId, approvedUnassigned.getData().getDoctorId());
        assertEquals("Solicitud no asignada aprobada queda ONGOING", "ONGOING", approvedUnassigned.getData().getStatus());
    }

    private static void assertTrue(String label, boolean condition) {
        if (condition) {
            passed++;
            System.out.println("[OK] " + label);
        } else {
            failed++;
            System.out.println("[FAIL] " + label);
        }
    }

    private static void assertEquals(String label, Object expected, Object actual) {
        boolean ok = expected == null ? actual == null : expected.equals(actual);
        if (ok) {
            passed++;
            System.out.println("[OK] " + label + " = " + expected);
        } else {
            failed++;
            System.out.println("[FAIL] " + label + " esperado=" + expected + " actual=" + actual);
        }
    }
}
