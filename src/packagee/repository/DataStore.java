package packagee.repository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import packagee.model.Administrator;
import packagee.model.Appointment;
import packagee.model.Doctor;
import packagee.model.Hospitalization;
import packagee.model.Patient;
import packagee.model.User;


public class DataStore {

    private final List<User> users;
    private final List<Administrator> administrators;
    private final List<Patient> patients;
    private final List<Doctor> doctors;
    private final List<Appointment> appointments;
    private final List<Hospitalization> hospitalizations;

    public DataStore() {
        this.users = new ArrayList<>();
        this.administrators = new ArrayList<>();
        this.patients = new ArrayList<>();
        this.doctors = new ArrayList<>();
        this.appointments = new ArrayList<>();
        this.hospitalizations = new ArrayList<>();
    }

    public List<User> getUsers() {
        return users;
    }

    public List<Administrator> getAdministrators() {
        return administrators;
    }

    public List<Patient> getPatients() {
        return patients;
    }

    public List<Doctor> getDoctors() {
        return doctors;
    }

    public List<Appointment> getAppointments() {
        return appointments;
    }

    public List<Hospitalization> getHospitalizations() {
        return hospitalizations;
    }

    public List<User> getUsersReadOnly() {
        return Collections.unmodifiableList(users);
    }

    public List<Administrator> getAdministratorsReadOnly() {
        return Collections.unmodifiableList(administrators);
    }

    public List<Patient> getPatientsReadOnly() {
        return Collections.unmodifiableList(patients);
    }

    public List<Doctor> getDoctorsReadOnly() {
        return Collections.unmodifiableList(doctors);
    }

    public List<Appointment> getAppointmentsReadOnly() {
        return Collections.unmodifiableList(appointments);
    }

    public List<Hospitalization> getHospitalizationsReadOnly() {
        return Collections.unmodifiableList(hospitalizations);
    }

    public void clearAll() {
        users.clear();
        administrators.clear();
        patients.clear();
        doctors.clear();
        appointments.clear();
        hospitalizations.clear();
    }
}

