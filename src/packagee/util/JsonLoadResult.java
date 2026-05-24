package packagee.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class JsonLoadResult {

    private int totalRead;
    private int totalLoaded;
    private int administratorsLoaded;
    private int patientsLoaded;
    private int doctorsLoaded;
    private int skipped;
    private final List<String> errors;

    public JsonLoadResult() {
        this.errors = new ArrayList<>();
    }

    public void incrementTotalRead() {
        totalRead++;
    }

    public void incrementTotalLoaded() {
        totalLoaded++;
    }

    public void incrementAdministratorsLoaded() {
        administratorsLoaded++;
    }

    public void incrementPatientsLoaded() {
        patientsLoaded++;
    }

    public void incrementDoctorsLoaded() {
        doctorsLoaded++;
    }

    public void incrementSkipped() {
        skipped++;
    }

    public void addError(String error) {
        if (error != null && !error.trim().isEmpty()) {
            errors.add(error);
        }
    }

    public int getTotalRead() {
        return totalRead;
    }

    public int getTotalLoaded() {
        return totalLoaded;
    }

    public int getAdministratorsLoaded() {
        return administratorsLoaded;
    }

    public int getPatientsLoaded() {
        return patientsLoaded;
    }

    public int getDoctorsLoaded() {
        return doctorsLoaded;
    }

    public int getSkipped() {
        return skipped;
    }

    public List<String> getErrors() {
        return Collections.unmodifiableList(errors);
    }

    public boolean hasErrors() {
        return !errors.isEmpty();
    }

    public boolean isSuccessful() {
        return totalLoaded > 0 && errors.isEmpty();
    }

    @Override
    public String toString() {
        return "JsonLoadResult{" +
                "totalRead=" + totalRead +
                ", totalLoaded=" + totalLoaded +
                ", administratorsLoaded=" + administratorsLoaded +
                ", patientsLoaded=" + patientsLoaded +
                ", doctorsLoaded=" + doctorsLoaded +
                ", skipped=" + skipped +
                ", errors=" + errors.size() +
                '}';
    }
}
