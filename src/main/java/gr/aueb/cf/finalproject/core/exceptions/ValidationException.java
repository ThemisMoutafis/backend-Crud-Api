package gr.aueb.cf.finalproject.core.exceptions;

import lombok.Getter;
import org.springframework.validation.BindingResult;

@Getter
public class ValidationException extends AppGenericException {
    private final BindingResult bindingResult;

    public ValidationException(BindingResult bindingResult) {
        super("Validation error","validation failed");
        this.bindingResult = bindingResult;
    }

}
