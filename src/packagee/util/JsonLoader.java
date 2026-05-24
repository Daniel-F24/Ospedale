package packagee.util;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import packagee.model.Administrator;
import packagee.model.Appointment;
import packagee.model.Doctor;
import packagee.model.Hospitalization;
import packagee.model.Patient;
import packagee.model.Prescription;
import packagee.model.User;
import packagee.model.enums.AppointmentStatus;
import packagee.model.enums.HospitalizationStatus;
import packagee.model.enums.RoomType;
import packagee.model.enums.Specialty;
import packagee.observer.ModelEvent;
import packagee.observer.ModelObserver;
import packagee.repository.AppointmentRepository;
import packagee.repository.HospitalizationRepository;
import packagee.repository.UserRepository;


public class JsonLoader implements ModelObserver {

    private static final String DEFAULT_USERS_FILE = "json/users.json";

    private final UserRepository userRepository;
    private final AppointmentRepository appointmentRepository;
    private final HospitalizationRepository hospitalizationRepository;

    public JsonLoader(UserRepository userRepository) {
        this(userRepository, null, null);
    }

    public JsonLoader(UserRepository userRepository,
                      AppointmentRepository appointmentRepository,
                      HospitalizationRepository hospitalizationRepository) {
        this.userRepository = userRepository;
        this.appointmentRepository = appointmentRepository;
        this.hospitalizationRepository = hospitalizationRepository;
    }

    
    public JsonLoadResult loadDefaultUsers() {
        return loadUsersFromFile(resolveProjectPath(DEFAULT_USERS_FILE));
    }

    
    public JsonLoadResult loadUsersFromFile(Path filePath) {
        JsonLoadResult result = new JsonLoadResult();

        if (filePath == null) {
            result.addError("La ruta del archivo JSON no puede ser nula.");
            return result;
        }

        if (!Files.exists(filePath)) {
            result.addError("No se encontro el archivo JSON: " + filePath.toAbsolutePath());
            return result;
        }

        try {
            String content = new String(Files.readAllBytes(filePath), StandardCharsets.UTF_8);
            JSONObject root = new JSONObject(content);
            JSONArray users = root.optJSONArray("users");

            if (users == null) {
                result.addError("El archivo JSON no contiene el arreglo 'users'.");
                return result;
            }

            for (int i = 0; i < users.length(); i++) {
                result.incrementTotalRead();
                try {
                    JSONObject userJson = users.getJSONObject(i);
                    User user = buildUser(userJson);
                    saveUser(user, result);
                } catch (JSONException ex) {
                    result.incrementSkipped();
                    result.addError("Usuario en posicion " + i + " no cargado: " + ex.getMessage());
                } catch (IllegalArgumentException ex) {
                    result.incrementSkipped();
                    result.addError("Usuario en posicion " + i + " no cargado: " + ex.getMessage());
                }
            }

            loadAppointments(root.optJSONArray("appointments"), result);
            loadHospitalizations(root.optJSONArray("hospitalizations"), result);
        } catch (IOException ex) {
            result.addError("Error leyendo JSON: " + ex.getMessage());
        } catch (JSONException ex) {
            result.addError("Error leyendo JSON: " + ex.getMessage());
        }

        return result;
    }

    
    public boolean saveDefaultData() {
        return saveDataToFile(resolveProjectPath(DEFAULT_USERS_FILE));
    }

    public boolean saveDataToFile(Path filePath) {
        if (filePath == null) {
            return false;
        }

        try {
            Path parent = filePath.getParent();
            if (parent != null && !Files.exists(parent)) {
                Files.createDirectories(parent);
            }

            JSONObject root = new JSONObject();
            root.put("users", buildUsersJson());
            root.put("appointments", buildAppointmentsJson());
            root.put("hospitalizations", buildHospitalizationsJson());

            Files.write(filePath, root.toString(2).getBytes(StandardCharsets.UTF_8));
            return true;
        } catch (IOException ex) {
            System.err.println("No se pudo guardar el JSON: " + ex.getMessage());
            return false;
        } catch (JSONException ex) {
            System.err.println("No se pudo construir el JSON: " + ex.getMessage());
            return false;
        }
    }

    @Override
    public void onModelChanged(ModelEvent event) {
        boolean saved = saveDefaultData();
        if (!saved) {
            System.err.println("Advertencia: el cambio del modelo no pudo persistirse en json/users.json.");
        }
    }

    private User buildUser(JSONObject json) {
        String type = requiredString(json, "type").toLowerCase();
        long id = json.getLong("id");
        String username = requiredString(json, "username");
        String firstname = requiredString(json, "firstname");
        String lastname = requiredString(json, "lastname");
        String password = requiredString(json, "password");

        if ("admin".equals(type) || "administrator".equals(type)) {
            return new Administrator(id, username, firstname, lastname, password);
        }
        if ("patient".equals(type)) {
            return buildPatient(json, id, username, firstname, lastname, password);
        }
        if ("doctor".equals(type)) {
            return buildDoctor(json, id, username, firstname, lastname, password);
        }
        throw new IllegalArgumentException("Tipo de usuario no soportado: " + type);
    }

    private Patient buildPatient(JSONObject json, long id, String username, String firstname,
                                 String lastname, String password) {
        String email = requiredString(json, "email");
        LocalDate birthdate = LocalDate.parse(requiredString(json, "birthdate"));
        boolean gender = json.getBoolean("gender");
        long phone = json.getLong("phone");
        String address = requiredString(json, "address");

        return new Patient(id, username, firstname, lastname, password, email, birthdate, gender, phone, address);
    }

    private Doctor buildDoctor(JSONObject json, long id, String username, String firstname,
                               String lastname, String password) {
        Specialty specialty = parseSpecialty(requiredString(json, "specialty"));
        String licenceNumber = requiredString(json, "licenceNumber");
        String assignedOffice = requiredString(json, "assignedOffice");

        return new Doctor(id, username, firstname, lastname, password, specialty, licenceNumber, assignedOffice);
    }

    private void loadAppointments(JSONArray appointments, JsonLoadResult result) {
        if (appointments == null || appointmentRepository == null) {
            return;
        }

        for (int i = 0; i < appointments.length(); i++) {
            try {
                JSONObject json = appointments.getJSONObject(i);
                Appointment appointment = buildAppointment(json);
                if (appointment != null && appointmentRepository.save(appointment)) {
                    loadPrescriptions(json.optJSONArray("prescriptions"), appointment);
                }
            } catch (JSONException ex) {
                result.addError("Cita en posicion " + i + " no cargada: " + ex.getMessage());
            } catch (IllegalArgumentException ex) {
                result.addError("Cita en posicion " + i + " no cargada: " + ex.getMessage());
            }
        }
    }

    private Appointment buildAppointment(JSONObject json) {
        String id = requiredString(json, "id");
        long patientId = json.getLong("patientId");
        long doctorId = json.getLong("doctorId");
        Patient patient = userRepository.findPatientById(patientId)
                .orElseThrow(() -> new IllegalArgumentException("Paciente no encontrado para cita: " + patientId));
        Doctor doctor = userRepository.findDoctorById(doctorId)
                .orElseThrow(() -> new IllegalArgumentException("Doctor no encontrado para cita: " + doctorId));
        Specialty specialty = parseSpecialty(requiredString(json, "specialty"));
        LocalDateTime datetime = parseAppointmentDateTime(json);
        String reason = json.optString("reason", "");
        boolean type = json.optBoolean("type", false);

        Appointment appointment = new Appointment(id, patient, doctor, specialty, datetime, reason, type);
        String statusText = json.optString("status", AppointmentStatus.REQUESTED.name());
        appointment.setStatus(AppointmentStatus.valueOf(statusText));
        appointment.setDiagnosis(optionalString(json, "diagnosis"));
        appointment.setObservations(optionalString(json, "observations"));
        appointment.setRecommendedTreatment(optionalString(json, "recommendedTreatment"));
        appointment.setFollowUp(optionalString(json, "followUp"));
        return appointment;
    }

    private LocalDateTime parseAppointmentDateTime(JSONObject json) {
        if (json.has("datetime") && !json.isNull("datetime")) {
            return LocalDateTime.parse(requiredString(json, "datetime"));
        }
        String date = requiredString(json, "date");
        String time = requiredString(json, "time");
        return DateTimeUtil.parseDateTime(date, time);
    }

    private void loadPrescriptions(JSONArray prescriptions, Appointment appointment) {
        if (prescriptions == null || appointment == null) {
            return;
        }
        for (int i = 0; i < prescriptions.length(); i++) {
            JSONObject json = prescriptions.getJSONObject(i);
            new Prescription(
                    appointment,
                    requiredString(json, "medicationName"),
                    json.getDouble("dose"),
                    requiredString(json, "administrationRoute"),
                    json.getInt("treatmentDuration"),
                    json.optString("additionalInstructions", ""),
                    json.getInt("frequency")
            );
        }
    }

    private void loadHospitalizations(JSONArray hospitalizations, JsonLoadResult result) {
        if (hospitalizations == null || hospitalizationRepository == null) {
            return;
        }

        for (int i = 0; i < hospitalizations.length(); i++) {
            try {
                JSONObject json = hospitalizations.getJSONObject(i);
                Hospitalization hospitalization = buildHospitalization(json);
                if (hospitalization != null) {
                    hospitalizationRepository.save(hospitalization);
                }
            } catch (JSONException ex) {
                result.addError("Hospitalizacion en posicion " + i + " no cargada: " + ex.getMessage());
            } catch (IllegalArgumentException ex) {
                result.addError("Hospitalizacion en posicion " + i + " no cargada: " + ex.getMessage());
            }
        }
    }

    private Hospitalization buildHospitalization(JSONObject json) {
        String id = requiredString(json, "id");
        long patientId = json.getLong("patientId");
        Patient patient = userRepository.findPatientById(patientId)
                .orElseThrow(() -> new IllegalArgumentException("Paciente no encontrado para hospitalizacion: " + patientId));

        Doctor doctor = null;
        if (json.has("doctorId") && !json.isNull("doctorId")) {
            long doctorId = json.getLong("doctorId");
            doctor = userRepository.findDoctorById(doctorId)
                    .orElseThrow(() -> new IllegalArgumentException("Doctor no encontrado para hospitalizacion: " + doctorId));
        }

        LocalDate date = LocalDate.parse(requiredString(json, "date"));
        RoomType roomType = RoomType.valueOf(requiredString(json, "roomType"));
        String statusText = json.optString("status", HospitalizationStatus.REQUESTED.name());

        return new Hospitalization(
                id,
                patient,
                doctor,
                date,
                json.optString("reason", ""),
                roomType,
                json.optString("observations", ""),
                HospitalizationStatus.valueOf(statusText)
        );
    }

    private JSONArray buildUsersJson() {
        JSONArray users = new JSONArray();
        if (userRepository == null) {
            return users;
        }

        List<User> allUsers = userRepository.findAllUsers();
        for (User user : allUsers) {
            JSONObject json = new JSONObject();
            if (user instanceof Administrator) {
                json.put("type", "admin");
            } else if (user instanceof Patient) {
                json.put("type", "patient");
            } else if (user instanceof Doctor) {
                json.put("type", "doctor");
            } else {
                json.put("type", "user");
            }
            json.put("id", user.getId());
            json.put("username", user.getUsername());
            json.put("firstname", user.getFirstname());
            json.put("lastname", user.getLastname());
            json.put("password", user.getPassword());

            if (user instanceof Patient) {
                Patient patient = (Patient) user;
                json.put("email", patient.getEmail());
                json.put("birthdate", patient.getBirthdate() == null ? "" : patient.getBirthdate().toString());
                json.put("gender", patient.isGender());
                json.put("phone", patient.getPhone());
                json.put("address", patient.getAddress());
            } else if (user instanceof Doctor) {
                Doctor doctor = (Doctor) user;
                json.put("specialty", doctor.getSpecialty() == null ? "" : doctor.getSpecialty().name());
                json.put("licenceNumber", doctor.getLicenceNumber());
                json.put("assignedOffice", doctor.getAssignedOffice());
            }
            users.put(json);
        }
        return users;
    }

    private JSONArray buildAppointmentsJson() {
        JSONArray appointments = new JSONArray();
        if (appointmentRepository == null) {
            return appointments;
        }

        for (Appointment appointment : appointmentRepository.findAll()) {
            JSONObject json = new JSONObject();
            json.put("id", appointment.getId());
            if (appointment.getPatient() != null) {
                json.put("patientId", appointment.getPatient().getId());
            }
            if (appointment.getDoctor() != null) {
                json.put("doctorId", appointment.getDoctor().getId());
            }
            json.put("specialty", appointment.getSpecialty() == null ? "" : appointment.getSpecialty().name());
            if (appointment.getDatetime() != null) {
                json.put("datetime", appointment.getDatetime().toString());
                json.put("date", appointment.getDatetime().toLocalDate().toString());
                json.put("time", appointment.getDatetime().toLocalTime().toString());
            }
            json.put("reason", appointment.getReason());
            json.put("type", appointment.isType());
            json.put("status", appointment.getStatus() == null ? AppointmentStatus.REQUESTED.name() : appointment.getStatus().name());
            putOptional(json, "diagnosis", appointment.getDiagnosis());
            putOptional(json, "observations", appointment.getObservations());
            putOptional(json, "recommendedTreatment", appointment.getRecommendedTreatment());
            putOptional(json, "followUp", appointment.getFollowUp());
            json.put("prescriptions", buildPrescriptionsJson(appointment));
            appointments.put(json);
        }
        return appointments;
    }

    private JSONArray buildPrescriptionsJson(Appointment appointment) {
        JSONArray prescriptions = new JSONArray();
        if (appointment == null) {
            return prescriptions;
        }

        for (Prescription prescription : appointment.getPrescriptionsReadOnly()) {
            JSONObject json = new JSONObject();
            json.put("medicationName", prescription.getMedicationName());
            json.put("dose", prescription.getDose());
            json.put("administrationRoute", prescription.getAdministrationRoute());
            json.put("treatmentDuration", prescription.getTreatmentDuration());
            json.put("additionalInstructions", prescription.getAdditionalInstructions());
            json.put("frequency", prescription.getFrecuency());
            prescriptions.put(json);
        }
        return prescriptions;
    }

    private JSONArray buildHospitalizationsJson() {
        JSONArray hospitalizations = new JSONArray();
        if (hospitalizationRepository == null) {
            return hospitalizations;
        }

        for (Hospitalization hospitalization : hospitalizationRepository.findAll()) {
            JSONObject json = new JSONObject();
            json.put("id", hospitalization.getId());
            if (hospitalization.getPatient() != null) {
                json.put("patientId", hospitalization.getPatient().getId());
            }
            if (hospitalization.getDoctor() != null) {
                json.put("doctorId", hospitalization.getDoctor().getId());
            }
            json.put("date", hospitalization.getDate() == null ? "" : hospitalization.getDate().toString());
            json.put("reason", hospitalization.getReason());
            json.put("roomType", hospitalization.getRoomType() == null ? "" : hospitalization.getRoomType().name());
            json.put("observations", hospitalization.getObservations());
            json.put("status", hospitalization.getStatus() == null
                    ? HospitalizationStatus.REQUESTED.name()
                    : hospitalization.getStatus().name());
            hospitalizations.put(json);
        }
        return hospitalizations;
    }

    private void saveUser(User user, JsonLoadResult result) {
        boolean saved = userRepository.save(user);

        if (!saved) {
            result.incrementSkipped();
            result.addError("Usuario no guardado por id o username duplicado: " + user.getUsername());
            return;
        }

        result.incrementTotalLoaded();

        if (user instanceof Administrator) {
            result.incrementAdministratorsLoaded();
        } else if (user instanceof Patient) {
            result.incrementPatientsLoaded();
        } else if (user instanceof Doctor) {
            result.incrementDoctorsLoaded();
        }
    }

    private String requiredString(JSONObject json, String key) {
        if (!json.has(key) || json.isNull(key)) {
            throw new IllegalArgumentException("Falta el campo obligatorio '" + key + "'.");
        }
        String value = json.getString(key);
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("El campo '" + key + "' no puede estar vacio.");
        }
        return value.trim();
    }

    private String optionalString(JSONObject json, String key) {
        if (!json.has(key) || json.isNull(key)) {
            return null;
        }
        return json.optString(key, null);
    }

    private void putOptional(JSONObject json, String key, String value) {
        if (value != null) {
            json.put(key, value);
        }
    }

   
    private Specialty parseSpecialty(String value) {
        String normalized = value.trim().toUpperCase();

        if ("ORTHOPEDICS".equals(normalized)) {
            normalized = "TRAUMATOLOGY_ORTHOPEDICS";
        }

        if ("GENERAL".equals(normalized) || "GENERAL_MEDICINE".equals(normalized)) {
            normalized = "GENERAL_MEDICINE";
        }

        if ("GYNECOLOGY".equals(normalized) || "GYNECOLOGY_OBSTETRICS".equals(normalized)) {
            normalized = "GYNECOLOGY_OBSTETRICS";
        }

        try {
            return Specialty.valueOf(normalized);
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException("Especialidad no valida en JSON: " + value);
        }
    }

    private Path resolveProjectPath(String relativePath) {
        Path workingDirectory = Paths.get(System.getProperty("user.dir"));
        Path directPath = workingDirectory.resolve(relativePath);

        if (Files.exists(directPath)) {
            return directPath;
        }

        Path parentPath = workingDirectory.getParent() == null
                ? directPath
                : workingDirectory.getParent().resolve(relativePath);

        if (Files.exists(parentPath)) {
            return parentPath;
        }

        return directPath;
    }
}


