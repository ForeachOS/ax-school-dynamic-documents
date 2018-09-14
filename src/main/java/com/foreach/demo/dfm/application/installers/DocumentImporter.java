package com.foreach.demo.dfm.application.installers;

import com.foreach.across.core.annotations.Installer;
import com.foreach.across.core.annotations.InstallerMethod;
import com.foreach.across.core.installers.InstallerPhase;
import com.foreach.across.modules.dynamicforms.domain.definition.DynamicDefinition;
import com.foreach.across.modules.dynamicforms.domain.definition.DynamicDefinitionId;
import com.foreach.across.modules.dynamicforms.domain.definition.DynamicDefinitionService;
import com.foreach.across.modules.dynamicforms.domain.document.DynamicDocumentService;
import com.foreach.across.modules.dynamicforms.domain.document.DynamicDocumentWorkspace;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.Resource;
import org.yaml.snakeyaml.Yaml;

import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Installer(name = "document-importer", description = "Creates the initial documents", phase = InstallerPhase.AfterContextBootstrap, version = 1)
@Slf4j
@Order(2000)
public class DocumentImporter
{
	private final DynamicDocumentService documentService;
	private final DynamicDefinitionService definitionService;

	@Value("classpath:demo-dfm/training-samples.yml")
	private Resource trainings;

	@InstallerMethod
	@SneakyThrows
	@SuppressWarnings("unchecked")
	public void importDocuments() {
		DynamicDefinition definition = definitionService.findDefinition( DynamicDefinitionId.fromString( "trainings:training" ) );
		Yaml yaml = new Yaml();
		List<Map<String, Object>> raw = yaml.loadAs( trainings.getInputStream(), List.class );
		raw.forEach( training -> {
			DynamicDocumentWorkspace workspace = documentService.createDocumentWorkspace( definition );
			workspace.setDocumentName( (String) training.get( "documentName" ) );
			workspace.importFields( (Map<String, Object>) training.get( "fields" ) );
			workspace.save();
		} );
	}
}