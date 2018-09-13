package com.foreach.demo.dfm.application.web;

import com.foreach.across.modules.bootstrapui.elements.BootstrapUiBuilders;
import com.foreach.across.modules.bootstrapui.elements.Style;
import com.foreach.across.modules.bootstrapui.elements.builder.FormViewElementBuilder;
import com.foreach.across.modules.dynamicforms.domain.definition.DynamicDefinition;
import com.foreach.across.modules.dynamicforms.domain.definition.DynamicDefinitionRepository;
import com.foreach.across.modules.dynamicforms.domain.definition.QDynamicDefinition;
import com.foreach.across.modules.dynamicforms.domain.definition.document.DynamicDocumentDefinition;
import com.foreach.across.modules.dynamicforms.domain.document.DynamicDocument;
import com.foreach.across.modules.dynamicforms.domain.document.DynamicDocumentService;
import com.foreach.across.modules.dynamicforms.domain.document.DynamicDocumentWorkspace;
import com.foreach.across.modules.entity.registry.properties.EntityPropertySelector;
import com.foreach.across.modules.entity.views.EntityViewElementBuilderHelper;
import com.foreach.across.modules.entity.views.ViewElementMode;
import com.foreach.across.modules.entity.views.helpers.EntityViewElementBatch;
import com.foreach.across.modules.web.ui.DefaultViewElementBuilderContext;
import com.foreach.across.modules.web.ui.ViewElement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.math.MathContext;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Controller
public class RegistrationController
{
	@Autowired
	private DynamicDefinitionRepository dynamicDefinitionRepository;
	@Autowired
	private EntityViewElementBuilderHelper builderHelper;
	@Autowired
	private DynamicDocumentService documentService;

	@ModelAttribute("dto")
	DynamicDocumentWorkspace dynamicDocument( @RequestParam("document") DynamicDocument training ) {
		DynamicDocumentWorkspace dto = null;
		List<DynamicDefinition> dynamicDefinitions = dynamicDefinitionRepository.findAll(
				QDynamicDefinition.dynamicDefinition.key.equalsIgnoreCase( "registration" ) );
		if ( dynamicDefinitions.size() > 0 ) {
			DynamicDefinition dynamicDefinition = dynamicDefinitions.get( 0 );
			DynamicDocumentWorkspace trainingWorkspace = documentService.createDocumentWorkspace( training );
			LocalDate date = trainingWorkspace.getFieldValue( "date", LocalDate.class );
			long daysUntilTraining = LocalDate.now().until( date, ChronoUnit.DAYS );

			DynamicDocumentWorkspace workspace = documentService.createDocumentWorkspace( dynamicDefinition );
			workspace.setCalculatedFieldsEnabled( true );
			workspace.setDocumentName( UUID.randomUUID().toString() );
			workspace.setFieldValue( "training", training );
			workspace.setFieldValue( "status", "New" );
			workspace.setFieldValue( "reduction", getReduction( daysUntilTraining ) );
			workspace.getFieldValue( "priceToPay" );
			dto = workspace;
		}
		return dto;
	}

	private BigDecimal getReduction( long daysLeft ) {
		if ( daysLeft > 30 ) {
			return new BigDecimal( 0.2, new MathContext( 2 ) );
		}
		if ( daysLeft > 20 ) {
			return new BigDecimal( 0.15, new MathContext( 2 ) );
		}
		if ( daysLeft > 10 ) {
			return new BigDecimal( 0.1, new MathContext( 2 ) );
		}
		return new BigDecimal( 0 );
	}

	@GetMapping("/register")
	public String formGet( @ModelAttribute("dto") DynamicDocumentWorkspace dto, Model model ) {
		return renderForm( dto, model );
	}

	@PostMapping("/register")
	public String formPost( @ModelAttribute("dto") DynamicDocumentWorkspace dto, BindingResult bindingResult, Model model ) {
		dto.validate( bindingResult );
		if ( bindingResult.hasErrors() ) {
			return renderForm( dto, model );
		}
		dto.save();
		return "th/demo/thankyou";
	}

	private String renderForm( DynamicDocumentWorkspace dto, Model model ) {
		Collection<ViewElement> writeControls = getViewElementsForEntity( dto, ViewElementMode.FORM_WRITE, "training", "email", "reduction", "priceToPay" );

		FormViewElementBuilder formBuilder = BootstrapUiBuilders.form().post().noValidate().commandAttribute( "dto" )
		                                                        .addAll( writeControls );
		                                                       // .addAll( readControls );
		formBuilder.add( BootstrapUiBuilders.button().submit().style( Style.PRIMARY ).text( "Submit" ) );

		model.addAttribute( "dto", dto );
		model.addAttribute( "dynamicForm", formBuilder.build( new DefaultViewElementBuilderContext() ) );

		return "th/demo/dynamicForm";
	}

	@SuppressWarnings("unchecked")
	private Collection<ViewElement> getViewElementsForEntity( DynamicDocumentWorkspace dto, ViewElementMode viewElementMode, String... names ) {
		DynamicDocumentDefinition dynamicDocumentDefinition = dto.getFields().getDocumentDefinition();
		EntityViewElementBatch dtoBatch = builderHelper.createBatchForEntity( dto );

		dtoBatch.setViewElementMode( viewElementMode );
		dtoBatch.setEntity( dto.getFields() );
		dtoBatch.setPropertyRegistry( dynamicDocumentDefinition.getEntityPropertyRegistry() );

		Map<String,Object> builderHints = new HashMap<>(  );
		builderHints.put( "reduction", ViewElementMode.FORM_READ );
		builderHints.put( "training", ViewElementMode.FORM_READ );
		dtoBatch.setBuilderHints( builderHints );

		dtoBatch.setPropertySelector( EntityPropertySelector.of( names ) );

		return dtoBatch.build().values();
	}
}
