/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package packagee.validation;

/**
 *
 * @author danip
 */
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
public class ValidationResult {
    private final List<String> errors;

    public ValidationResult() {
        this.errors = new ArrayList<>();
    }

    public static ValidationResult valid() {
        return new ValidationResult();
    }

    public void addError(String error) {
        if (error != null && !error.trim().isEmpty()) {
            errors.add(error.trim());
        }
    }

    public boolean isValid() {
        return errors.isEmpty();
    }

    public List<String> getErrors() {
        return Collections.unmodifiableList(errors);
    }

    public String getMessage() {
        if (errors.isEmpty()) {
            return "Validacion exitosa.";
        }
        return String.join(" ", errors);
    }
}
