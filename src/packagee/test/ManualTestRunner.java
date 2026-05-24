package packagee.test;

import packagee.app.ApplicationContext;
import packagee.dto.AppointmentDTO;
import packagee.dto.HospitalizationDTO;
import packagee.dto.UserDTO;
import packagee.request.AppointmentRequest;
import packagee.request.HospitalizationRequest;
import packagee.request.LoginRequest;
import packagee.request.PrescriptionRequest;
import packagee.response.Response;

public class ManualTestRunner {

    private static int passed = 0;
    private static int failed = 0;

    public static void main(String[] args) {
        ApplicationContext context = new ApplicationContext();
        context.initialize();

        testLoginAdmin(context);
        testInvalidLogin(context);
        testAppointmentFlow(context);
        testHospitalizationFlow(context);
        testTableQueries(context);

        System.out.println("\n==============================");
        System.out.println("RESUMEN DE PRUEBAS MANUALES");
        System.out.println("Aprobadas: " + passed);
        System.out.println("Fallidas: " + failed);
        System.out.println("==============================");

        if (failed > 0) {
            throw new IllegalStateException("Existen pruebas manuales fallidas. Revise los mensajes anteriores.");
        }
    }

    private static void testLoginAdmin(ApplicationContext context) {
        Response<UserDTO> response = context.getAuthController()
                .login(new LoginRequest("admin_root", "Admin@1234"));
        assertSuccess("Login administrador valido", response);
        assertEquals("Rol de administrador", "ADMINISTRATOR", response.getData().getRole());
    }

    private static void testInvalidLogin(ApplicationContext context) {
        Response<UserDTO> response = context.getAuthController()
                .login(new LoginRequest("admin_root", "clave_incorrecta"));
        assertFailure("Login con clave incorrecta", response);
    }

    private static void testAppointmentFlow(ApplicationContext context) {
        AppointmentRequest request = new AppointmentRequest(
                "112234567890",
                "111234567890",
                "CARDIOLOGY",
                "2026-05-24",
                "08:00",
                "Dolor en el pecho",
                "false"
        );

        Response<AppointmentDTO> created = context.getAppointmentController().requestAppointment(request);
        assertSuccess("Solicitud de cita", created);
        String appointmentId = created.getData().getId();
        assertEquals("Estado inicial de cita", "REQUESTED", created.getData().getStatus());

        Response<AppointmentDTO> accepted = context.getAppointmentController().acceptAppointment(appointmentId);
        assertSuccess("Aceptacion de cita", accepted);
        assertEquals("Estado despues de aceptar", "PENDING", accepted.getData().getStatus());

        Response<?> prescription = context.getAppointmentController().prescribeMedication(
                new PrescriptionRequest(appointmentId, "Acetaminofen", "500", "Oral", "5", "8", "Tomar con agua")
        );
        assertSuccess("Prescripcion en cita PENDING", prescription);

        Response<AppointmentDTO> completed = context.getAppointmentController().completeAppointment(appointmentId);
        assertSuccess("Completar cita", completed);
        assertEquals("Estado despues de completar", "COMPLETED", completed.getData().getStatus());

        Response<AppointmentDTO> cancelCompleted = context.getAppointmentController().cancelAppointment(appointmentId);
        assertFailure("No cancelar cita COMPLETED", cancelCompleted);
    }

    private static void testHospitalizationFlow(ApplicationContext context) {
        HospitalizationRequest request = new HospitalizationRequest(
                "223345678901",
                "111234567890",
                "2026-05-25",
                "Observacion clinica por dolor toracico",
                "ICU",
                "Paciente estable al ingreso"
        );

        Response<HospitalizationDTO> created = context.getHospitalizationController().requestHospitalization(request);
        assertSuccess("Solicitud de hospitalizacion", created);
        String hospitalizationId = created.getData().getId();
        assertEquals("Estado inicial de hospitalizacion", "REQUESTED", created.getData().getStatus());

        Response<HospitalizationDTO> approved = context.getHospitalizationController()
                .approveHospitalization(hospitalizationId, "111234567890");
        assertSuccess("Aprobacion de hospitalizacion", approved);
        assertEquals("Estado despues de aprobar", "ONGOING", approved.getData().getStatus());
    }

    private static void testTableQueries(ApplicationContext context) {
        Response<?> patients = context.getTableController().getPatientsTableData();
        Response<?> doctors = context.getTableController().getDoctorsTableData();
        Response<?> appointments = context.getTableController().getAppointmentsTableData();
        Response<?> hospitalizations = context.getTableController().getHospitalizationsTableData();

        assertSuccess("Tabla de pacientes", patients);
        assertSuccess("Tabla de doctores", doctors);
        assertSuccess("Tabla de citas", appointments);
        assertSuccess("Tabla de hospitalizaciones", hospitalizations);
    }

    private static void assertSuccess(String name, Response<?> response) {
        if (response != null && response.isSuccess()) {
            passed++;
            System.out.println("[OK] " + name + " -> " + response.getMessage());
        } else {
            failed++;
            System.err.println("[FAIL] " + name + " -> " + messageOf(response));
        }
    }

    private static void assertFailure(String name, Response<?> response) {
        if (response != null && !response.isSuccess()) {
            passed++;
            System.out.println("[OK] " + name + " rechazo esperado -> " + response.getMessage());
        } else {
            failed++;
            System.err.println("[FAIL] " + name + " debia fallar y no fallo.");
        }
    }

    private static void assertEquals(String name, String expected, String actual) {
        if (expected.equals(actual)) {
            passed++;
            System.out.println("[OK] " + name + " = " + actual);
        } else {
            failed++;
            System.err.println("[FAIL] " + name + " esperado=" + expected + ", recibido=" + actual);
        }
    }

    private static String messageOf(Response<?> response) {
        return response == null ? "response null" : response.getMessage();
    }
}
