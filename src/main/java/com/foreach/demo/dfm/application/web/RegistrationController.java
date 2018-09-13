package com.foreach.demo.dfm.application.web;

import com.foreach.across.modules.dynamicforms.domain.definition.DynamicDefinition;
import com.foreach.across.modules.dynamicforms.domain.definition.DynamicDefinitionId;
import com.foreach.across.modules.dynamicforms.domain.definition.DynamicDefinitionService;
import com.foreach.across.modules.dynamicforms.domain.document.DynamicDocument;
import com.foreach.across.modules.dynamicforms.domain.document.DynamicDocumentService;
import com.foreach.across.modules.dynamicforms.domain.document.DynamicDocumentWorkspace;
import com.foreach.across.modules.dynamicforms.domain.document.data.DynamicDocumentData;
import com.foreach.across.modules.entity.registry.properties.EntityPropertySelector;
import com.foreach.across.modules.entity.views.EntityViewElementBuilderHelper;
import com.foreach.across.modules.entity.views.ViewElementMode;
import com.foreach.across.modules.entity.views.helpers.EntityViewElementBatch;
import com.foreach.across.modules.web.ui.elements.ContainerViewElement;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Controller
@RequestMapping("/register")
@RequiredArgsConstructor
public class RegistrationController {
    private final DynamicDocumentService documentService;
    private final DynamicDefinitionService definitionService;
    private final EntityViewElementBuilderHelper builderHelper;

    @ModelAttribute("document")
    DynamicDocumentWorkspace createDocument(@RequestParam DynamicDocument training) {
        DynamicDefinition definition = definitionService.findDefinition(DynamicDefinitionId.fromString("trainings:registration"));
        DynamicDocumentWorkspace document = documentService.createDocumentWorkspace(definition);
        document.setDocumentName(UUID.randomUUID().toString());
        document.setFieldValue("training", training);
        document.setFieldValue("status", "requested");
        document.updateCalculatedFields();

        return document;
    }

    @GetMapping
    public String showForm(@ModelAttribute("document") DynamicDocumentWorkspace document, Model model) {
        ContainerViewElement container = new ContainerViewElement();

        DynamicDocumentData fields = document.getFields();

        EntityViewElementBatch<Object> batch = builderHelper.createBatchForEntity(document);
        batch.setPropertyRegistry(fields.getDocumentDefinition().getEntityPropertyRegistry());
        batch.setEntity(fields);
        batch.setViewElementMode(ViewElementMode.FORM_WRITE);
        batch.setPropertySelector(EntityPropertySelector.of("*", "~status"));

        Map<String, Object> builderHints = new HashMap<>();
        builderHints.put("training", ViewElementMode.FORM_READ);
        batch.setBuilderHints(builderHints);

        batch.build().values().forEach(container::addChild);

        model.addAttribute("formContent", container);

        return "th/demo/register";
    }

    @PostMapping
    public String saveRegistration(@Valid @ModelAttribute("document") DynamicDocumentWorkspace document, BindingResult errors, Model model) {
        document.validate(errors);
        if (!errors.hasErrors()) {
            document.save();
        } else {
            return showForm(document, model);
        }

        return "th/demo/register";
    }
}
