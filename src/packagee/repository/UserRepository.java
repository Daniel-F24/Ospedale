package packagee.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import packagee.model.Administrator;
import packagee.model.Doctor;
import packagee.model.Patient;
import packagee.model.User;
import packagee.model.enums.Specialty;


public class UserRepository {

    private final DataStore dataStore;

    public UserRepository(DataStore dataStore) {
        this.dataStore = dataStore;
    }

    public boolean save(User user) {
        if (user == null || existsById(user.getId()) || existsByUsername(user.getUsername())) {
            return false;
        }

        dataStore.getUsers().add(user);

        if (user instanceof Administrator) {
            dataStore.getAdministrators().add((Administrator) user);
        } else if (user instanceof Patient) {
            dataStore.getPatients().add((Patient) user);
        } else if (user instanceof Doctor) {
            dataStore.getDoctors().add((Doctor) user);
        }

        return true;
    }

   
    public boolean update(User updatedUser) {
        if (updatedUser == null) {
            return false;
        }

        Optional<User> currentUser = findById(updatedUser.getId());
        if (!currentUser.isPresent()) {
            return false;
        }

        User user = currentUser.get();
        user.setUsername(updatedUser.getUsername());
        user.setFirstname(updatedUser.getFirstname());
        user.setLastname(updatedUser.getLastname());
        user.setPassword(updatedUser.getPassword());

        if (user instanceof Patient && updatedUser instanceof Patient) {
            Patient patient = (Patient) user;
            Patient updatedPatient = (Patient) updatedUser;
            patient.setEmail(updatedPatient.getEmail());
            patient.setBirthdate(updatedPatient.getBirthdate());
            patient.setGender(updatedPatient.isGender());
            patient.setPhone(updatedPatient.getPhone());
            patient.setAddress(updatedPatient.getAddress());
            return true;
        }

        if (user instanceof Doctor && updatedUser instanceof Doctor) {
            Doctor doctor = (Doctor) user;
            Doctor updatedDoctor = (Doctor) updatedUser;
            doctor.setSpecialty(updatedDoctor.getSpecialty());
            doctor.setLicenceNumber(updatedDoctor.getLicenceNumber());
            doctor.setAssignedOffice(updatedDoctor.getAssignedOffice());
            return true;
        }

        return true;
    }

    public Optional<User> findById(long id) {
        return dataStore.getUsers().stream()
                .filter(user -> user.getId() == id)
                .findFirst();
    }

    public Optional<User> findByUsername(String username) {
        if (username == null) {
            return Optional.empty();
        }
        return dataStore.getUsers().stream()
                .filter(user -> username.equalsIgnoreCase(user.getUsername()))
                .findFirst();
    }

    public Optional<Patient> findPatientById(long id) {
        return dataStore.getPatients().stream()
                .filter(patient -> patient.getId() == id)
                .findFirst();
    }

    public Optional<Doctor> findDoctorById(long id) {
        return dataStore.getDoctors().stream()
                .filter(doctor -> doctor.getId() == id)
                .findFirst();
    }

    public Optional<Administrator> findAdministratorById(long id) {
        return dataStore.getAdministrators().stream()
                .filter(administrator -> administrator.getId() == id)
                .findFirst();
    }

    public boolean existsById(long id) {
        return findById(id).isPresent();
    }

    public boolean existsByUsername(String username) {
        return findByUsername(username).isPresent();
    }

    public boolean existsByUsernameExcludingId(String username, long excludedId) {
        if (username == null) {
            return false;
        }
        return dataStore.getUsers().stream()
                .anyMatch(user -> user.getId() != excludedId
                        && username.equalsIgnoreCase(user.getUsername()));
    }

    public List<User> findAllUsers() {
        return new ArrayList<>(dataStore.getUsersReadOnly());
    }

    public List<Patient> findAllPatients() {
        return new ArrayList<>(dataStore.getPatientsReadOnly());
    }

    public List<Doctor> findAllDoctors() {
        return new ArrayList<>(dataStore.getDoctorsReadOnly());
    }

    public List<Administrator> findAllAdministrators() {
        return new ArrayList<>(dataStore.getAdministratorsReadOnly());
    }

    public List<Doctor> findDoctorsBySpecialty(Specialty specialty) {
        List<Doctor> result = new ArrayList<>();
        for (Doctor doctor : dataStore.getDoctors()) {
            if (doctor.getSpecialty() == specialty) {
                result.add(doctor);
            }
        }
        return result;
    }
}


