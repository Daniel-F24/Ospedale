package packagee.repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import packagee.model.Appointment;
import packagee.model.enums.AppointmentStatus;


public class AppointmentRepository {

    private final DataStore dataStore;

    public AppointmentRepository(DataStore dataStore) {
        this.dataStore = dataStore;
    }

    public boolean save(Appointment appointment) {
        if (appointment == null || existsById(appointment.getId())) {
            return false;
        }
        return dataStore.getAppointments().add(appointment);
    }

    public boolean update(Appointment appointment) {
        return appointment != null && existsById(appointment.getId());
    }

    public Optional<Appointment> findById(String id) {
        if (id == null) {
            return Optional.empty();
        }
        return dataStore.getAppointments().stream()
                .filter(appointment -> id.equals(appointment.getId()))
                .findFirst();
    }

    public boolean existsById(String id) {
        return findById(id).isPresent();
    }

    public List<Appointment> findAll() {
        return sortedDescending(new ArrayList<>(dataStore.getAppointmentsReadOnly()));
    }

    public List<Appointment> findByPatientId(long patientId) {
        List<Appointment> result = new ArrayList<>();
        for (Appointment appointment : dataStore.getAppointments()) {
            if (appointment.getPatient() != null && appointment.getPatient().getId() == patientId) {
                result.add(appointment);
            }
        }
        return sortedDescending(result);
    }

    public List<Appointment> findByDoctorId(long doctorId) {
        List<Appointment> result = new ArrayList<>();
        for (Appointment appointment : dataStore.getAppointments()) {
            if (appointment.getDoctor() != null && appointment.getDoctor().getId() == doctorId) {
                result.add(appointment);
            }
        }
        return sortedDescending(result);
    }

    public List<Appointment> findPendingByDoctorId(long doctorId) {
        List<Appointment> result = new ArrayList<>();
        for (Appointment appointment : dataStore.getAppointments()) {
            if (appointment.getDoctor() != null
                    && appointment.getDoctor().getId() == doctorId
                    && appointment.getStatus() == AppointmentStatus.PENDING) {
                result.add(appointment);
            }
        }
        return sortedDescending(result);
    }

    public List<Appointment> findRequestedByDoctorId(long doctorId) {
        List<Appointment> result = new ArrayList<>();
        for (Appointment appointment : dataStore.getAppointments()) {
            if (appointment.getDoctor() != null
                    && appointment.getDoctor().getId() == doctorId
                    && appointment.getStatus() == AppointmentStatus.REQUESTED) {
                result.add(appointment);
            }
        }
        return sortedDescending(result);
    }

    public List<Appointment> findByPatientIdAndDate(long patientId, LocalDate date) {
        List<Appointment> result = new ArrayList<>();
        for (Appointment appointment : dataStore.getAppointments()) {
            if (appointment.getPatient() != null
                    && appointment.getPatient().getId() == patientId
                    && appointment.getDatetime() != null
                    && appointment.getDatetime().toLocalDate().equals(date)) {
                result.add(appointment);
            }
        }
        return sortedDescending(result);
    }

   
    public boolean existsActiveDoctorAppointmentAtDateTime(long doctorId, LocalDateTime dateTime) {
        if (dateTime == null) {
            return false;
        }
        return dataStore.getAppointments().stream()
                .anyMatch(appointment -> appointment.getDoctor() != null
                        && appointment.getDoctor().getId() == doctorId
                        && appointment.getDatetime() != null
                        && appointment.getDatetime().equals(dateTime)
                        && appointment.getStatus() != AppointmentStatus.CANCELED
                        && appointment.getStatus() != AppointmentStatus.COMPLETED);
    }

    public int countByPatientId(long patientId) {
        int count = 0;
        for (Appointment appointment : dataStore.getAppointments()) {
            if (appointment.getPatient() != null && appointment.getPatient().getId() == patientId) {
                count++;
            }
        }
        return count;
    }

    private List<Appointment> sortedDescending(List<Appointment> appointments) {
        appointments.sort(Comparator.comparing(Appointment::getDatetime,
                Comparator.nullsLast(Comparator.naturalOrder())).reversed());
        return appointments;
    }
}
