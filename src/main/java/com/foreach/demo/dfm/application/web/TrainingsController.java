package com.foreach.demo.dfm.application.web;

import com.foreach.across.modules.dynamicforms.domain.definition.DynamicDefinition;
import com.foreach.across.modules.dynamicforms.domain.definition.DynamicDefinitionId;
import com.foreach.across.modules.dynamicforms.domain.definition.DynamicDefinitionService;
import com.foreach.across.modules.dynamicforms.domain.document.CachingDynamicDocumentWorkspaceFactory;
import com.foreach.across.modules.dynamicforms.domain.document.DynamicDocumentRepository;
import com.foreach.across.modules.dynamicforms.domain.document.DynamicDocumentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.stream.Collectors;

/**
 * @author Marc & Steven
 * @since 1.0.0
 */
@Controller
@RequiredArgsConstructor
public class TrainingsController {
    private final DynamicDefinitionService definitionService;
    private final DynamicDocumentRepository documentRepository;
    private final DynamicDocumentService documentService;

    @GetMapping("/")
    public String getTrainings(Model model) {
        DynamicDefinition definition = definitionService.findDefinition(DynamicDefinitionId.fromString("trainings:training"));

        CachingDynamicDocumentWorkspaceFactory workspaceFactory = documentService.createCachingDocumentWorkspaceFactory();

        model.addAttribute("trainings",
                documentRepository.findAllByType(definition)
                        .stream()
                        .map(workspaceFactory::createDocumentWorkspace)
                        .collect(Collectors.toList())
        );

        return "th/demo/trainings";
    }
}
