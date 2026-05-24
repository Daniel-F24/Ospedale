package packagee.repository;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import packagee.model.Hospitalization;
import packagee.model.enums.HospitalizationStatus;


public class HospitalizationRepository {

    private final DataStore dataStore;

    public HospitalizationRepository(DataStore dataStore) {
        this.dataStore = dataStore;
    }

    public boolean save(Hospitalization hospitalization) {
        if (hospitalization == null || existsById(hospitalization.getId())) {
            return false;
        }
        return dataStore.getHospitalizations().add(hospitalization);
    }

    public boolean update(Hospitalization hospitalization) {
        return hospitalization != null && existsById(hospitalization.getId());
    }

    public Optional<Hospitalization> findById(String id) {
        if (id == null) {
            return Optional.empty();
        }
        return dataStore.getHospitalizations().stream()
                .filter(hospitalization -> id.equals(hospitalization.getId()))
                .findFirst();
    }

    public boolean existsById(String id) {
        return findById(id).isPresent();
    }

    public List<Hospitalization> findAll() {
        return sortedDescending(new ArrayList<>(dataStore.getHospitalizationsReadOnly()));
    }

    public List<Hospitalization> findByPatientId(long patientId) {
        List<Hospitalization> result = new ArrayList<>();
        for (Hospitalization hospitalization : dataStore.getHospitalizations()) {
            if (hospitalization.getPatient() != null && hospitalization.getPatient().getId() == patientId) {
                result.add(hospitalization);
            }
        }
        return sortedDescending(result);
    }

    public List<Hospitalization> findByDoctorId(long doctorId) {
        List<Hospitalization> result = new ArrayList<>();
        for (Hospitalization hospitalization : dataStore.getHospitalizations()) {
            if (hospitalization.getDoctor() != null && hospitalization.getDoctor().getId() == doctorId) {
                result.add(hospitalization);
            }
        }
        return sortedDescending(result);
    }

    public List<Hospitalization> findRequestedByDoctorId(long doctorId) {
        List<Hospitalization> result = new ArrayList<>();
        for (Hospitalization hospitalization : dataStore.getHospitalizations()) {
            boolean requested = hospitalization.getStatus() == HospitalizationStatus.REQUESTED;
            boolean assignedToDoctor = hospitalization.getDoctor() != null
                    && hospitalization.getDoctor().getId() == doctorId;
            boolean unassigned = hospitalization.getDoctor() == null;

            if (requested && (assignedToDoctor || unassigned)) {
                result.add(hospitalization);
            }
        }
        return sortedDescending(result);
    }

    public int countByPatientId(long patientId) {
        int count = 0;
        for (Hospitalization hospitalization : dataStore.getHospitalizations()) {
            if (hospitalization.getPatient() != null && hospitalization.getPatient().getId() == patientId) {
                count++;
            }
        }
        return count;
    }

    private List<Hospitalization> sortedDescending(List<Hospitalization> hospitalizations) {
        hospitalizations.sort(Comparator.comparing(Hospitalization::getDate,
                Comparator.nullsLast(Comparator.naturalOrder())).reversed());
        return hospitalizations;
    }
}

