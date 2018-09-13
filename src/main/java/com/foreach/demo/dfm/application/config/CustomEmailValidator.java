package com.foreach.demo.dfm.application.config;

import com.foreach.across.modules.dynamicforms.domain.document.data.DynamicDocumentFieldValue;
import com.foreach.across.modules.dynamicforms.domain.document.validation.DynamicDocumentFieldValidator;
import lombok.RequiredArgsConstructor;
import org.hibernate.validator.internal.constraintvalidators.hv.EmailValidator;
import org.springframework.validation.Errors;

/**
 * @author Marc Vanbrabant
 * @since 1.0.0
 */
@RequiredArgsConstructor
public class CustomEmailValidator implements DynamicDocumentFieldValidator {

    private static final EmailValidator EMAIL_VALIDATOR = new EmailValidator();

    @Override
    public void validate(DynamicDocumentFieldValue field, Errors errors) {
        if (field.getFieldValue() instanceof CharSequence && !errors.hasFieldErrors(field.getFieldKey())) {
            if (!EMAIL_VALIDATOR.isValid((CharSequence) field.getFieldValue(), null)) {
                errors.rejectValue(field.getFieldKey(), "invalid-email");
            }
        }
    }
}
