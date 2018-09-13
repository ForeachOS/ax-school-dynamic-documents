package com.foreach.demo.dfm.application.config;

import com.foreach.across.modules.dynamicforms.domain.definition.document.RawDocumentDefinition;
import com.foreach.across.modules.dynamicforms.domain.definition.types.DynamicTypeDefinition;
import com.foreach.across.modules.dynamicforms.domain.document.validation.DynamicDocumentFieldValidator;
import com.foreach.across.modules.dynamicforms.domain.document.validation.DynamicDocumentFieldValidatorBuilder;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @author Marc Vanbrabant
 * @since 1.0.0
 */
@Component
public class CustomEmailValidatorBuilder implements DynamicDocumentFieldValidatorBuilder {

    private static final CustomEmailValidator VALIDATOR = new CustomEmailValidator();

    @Override
    public boolean accepts(RawDocumentDefinition.AbstractField field,
                           DynamicTypeDefinition typeDefinition,
                           Map<String, Object> validatorSettings) {
        return validatorSettings.containsKey("email");
    }

    @Override
    public DynamicDocumentFieldValidator buildValidator(RawDocumentDefinition.AbstractField field,
                                                        DynamicTypeDefinition typeDefinition,
                                                        Map<String, Object> validatorSettings) {
        return VALIDATOR;
    }
}
