package packagee.util;

import java.util.ArrayList;
import java.util.List;
import packagee.dto.AppointmentDTO;
import packagee.dto.DoctorDTO;
import packagee.dto.HospitalizationDTO;
import packagee.dto.PatientDTO;
import packagee.dto.PrescriptionDTO;
import packagee.dto.UserDTO;
import packagee.model.Administrator;
import packagee.model.Appointment;
import packagee.model.Doctor;
import packagee.model.Hospitalization;
import packagee.model.Patient;
import packagee.model.Prescription;
import packagee.model.User;


public class Serializer {

    public UserDTO toUserDTO(User user) {
        if (user == null) {
            return null;
        }
        if (user instanceof Patient) {
            return toPatientDTO((Patient) user);
        }
        if (user instanceof Doctor) {
            return toDoctorDTO((Doctor) user);
        }
        String role = user instanceof Administrator ? "ADMINISTRATOR" : "USER";
        return new UserDTO(user.getId(), user.getUsername(), user.getFirstname(), user.getLastname(), role);
    }

    public PatientDTO toPatientDTO(Patient patient) {
        if (patient == null) {
            return null;
        }
        return new PatientDTO(
                patient.getId(),
                patient.getUsername(),
                patient.getFirstname(),
                patient.getLastname(),
                patient.getEmail(),
                DateTimeUtil.formatDate(patient.getBirthdate()),
                patient.isGender() ? "MALE" : "FEMALE",
                String.valueOf(patient.getPhone()),
                patient.getAddress()
        );
    }

    public DoctorDTO toDoctorDTO(Doctor doctor) {
        if (doctor == null) {
            return null;
        }
        return new DoctorDTO(
                doctor.getId(),
                doctor.getUsername(),
                doctor.getFirstname(),
                doctor.getLastname(),
                doctor.getSpecialty() == null ? "" : doctor.getSpecialty().name(),
                doctor.getLicenceNumber(),
                doctor.getAssignedOffice()
        );
    }

    public AppointmentDTO toAppointmentDTO(Appointment appointment) {
        if (appointment == null) {
            return null;
        }

        Patient patient = appointment.getPatient();
        Doctor doctor = appointment.getDoctor();

        return new AppointmentDTO(
                appointment.getId(),
                patient == null ? 0 : patient.getId(),
                patient == null ? "" : patient.getFullName(),
                doctor == null ? 0 : doctor.getId(),
                doctor == null ? "" : doctor.getFullName(),
                appointment.getSpecialty() == null ? "" : appointment.getSpecialty().name(),
                appointment.getDatetime() == null ? "" : DateTimeUtil.formatDate(appointment.getDatetime().toLocalDate()),
                appointment.getDatetime() == null ? "" : DateTimeUtil.formatTime(appointment.getDatetime().toLocalTime()),
                appointment.getReason(),
                appointment.isType() ? "HOSPITALIZATION" : "APPOINTMENT",
                appointment.getStatus() == null ? "" : appointment.getStatus().name(),
                appointment.getDiagnosis(),
                appointment.getObservations(),
                appointment.getRecommendedTreatment(),
                appointment.getFollowUp(),
                toPrescriptionDTOList(appointment.getPrescriptions())
        );
    }

    public HospitalizationDTO toHospitalizationDTO(Hospitalization hospitalization) {
        if (hospitalization == null) {
            return null;
        }

        Patient patient = hospitalization.getPatient();
        Doctor doctor = hospitalization.getDoctor();

        return new HospitalizationDTO(
                hospitalization.getId(),
                patient == null ? 0 : patient.getId(),
                patient == null ? "" : patient.getFullName(),
                doctor == null ? 0 : doctor.getId(),
                doctor == null ? "" : doctor.getFullName(),
                DateTimeUtil.formatDate(hospitalization.getDate()),
                hospitalization.getRoomType() == null ? "" : hospitalization.getRoomType().name(),
                hospitalization.getReason(),
                hospitalization.getObservations(),
                hospitalization.getStatus() == null ? "" : hospitalization.getStatus().name()
        );
    }

    public PrescriptionDTO toPrescriptionDTO(Prescription prescription) {
        if (prescription == null) {
            return null;
        }
        return new PrescriptionDTO(
                prescription.getAppointment() == null ? "" : prescription.getAppointment().getId(),
                prescription.getMedicationName(),
                String.valueOf(prescription.getDose()),
                prescription.getAdministrationRoute(),
                String.valueOf(prescription.getTreatmentDuration()),
                String.valueOf(prescription.getFrecuency()),
                prescription.getAdditionalInstructions()
        );
    }

    public List<UserDTO> toUserDTOList(List<? extends User> users) {
        List<UserDTO> result = new ArrayList<>();
        if (users == null) {
            return result;
        }
        for (User user : users) {
            result.add(toUserDTO(user));
        }
        return result;
    }

    public List<PatientDTO> toPatientDTOList(List<Patient> patients) {
        List<PatientDTO> result = new ArrayList<>();
        if (patients == null) {
            return result;
        }
        for (Patient patient : patients) {
            result.add(toPatientDTO(patient));
        }
        return result;
    }

    public List<DoctorDTO> toDoctorDTOList(List<Doctor> doctors) {
        List<DoctorDTO> result = new ArrayList<>();
        if (doctors == null) {
            return result;
        }
        for (Doctor doctor : doctors) {
            result.add(toDoctorDTO(doctor));
        }
        return result;
    }

    public List<AppointmentDTO> toAppointmentDTOList(List<Appointment> appointments) {
        List<AppointmentDTO> result = new ArrayList<>();
        if (appointments == null) {
            return result;
        }
        for (Appointment appointment : appointments) {
            result.add(toAppointmentDTO(appointment));
        }
        return result;
    }

    public List<HospitalizationDTO> toHospitalizationDTOList(List<Hospitalization> hospitalizations) {
        List<HospitalizationDTO> result = new ArrayList<>();
        if (hospitalizations == null) {
            return result;
        }
        for (Hospitalization hospitalization : hospitalizations) {
            result.add(toHospitalizationDTO(hospitalization));
        }
        return result;
    }

    public List<PrescriptionDTO> toPrescriptionDTOList(List<Prescription> prescriptions) {
        List<PrescriptionDTO> result = new ArrayList<>();
        if (prescriptions == null) {
            return result;
        }
        for (Prescription prescription : prescriptions) {
            result.add(toPrescriptionDTO(prescription));
        }
        return result;
    }
}


