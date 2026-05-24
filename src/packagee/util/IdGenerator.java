package packagee.util;

import packagee.repository.AppointmentRepository;
import packagee.repository.HospitalizationRepository;


public class IdGenerator {

    private final AppointmentRepository appointmentRepository;
    private final HospitalizationRepository hospitalizationRepository;

    public IdGenerator(AppointmentRepository appointmentRepository,
                       HospitalizationRepository hospitalizationRepository) {
        this.appointmentRepository = appointmentRepository;
        this.hospitalizationRepository = hospitalizationRepository;
    }

    public String nextAppointmentId(long patientId) {
        int sequence = appointmentRepository.countByPatientId(patientId);
        String candidate;
        do {
            candidate = "A-" + patientId + "-" + String.format("%04d", sequence);
            sequence++;
        } while (appointmentRepository.existsById(candidate));
        return candidate;
    }

    public String nextHospitalizationId(long patientId) {
        int sequence = hospitalizationRepository.countByPatientId(patientId);
        String candidate;
        do {
            candidate = "H-" + patientId + "-" + String.format("%04d", sequence);
            sequence++;
        } while (hospitalizationRepository.existsById(candidate));
        return candidate;
    }
}

