package com.foreach.demo.dfm.application.web;

import com.foreach.across.modules.bootstrapui.elements.BootstrapUiBuilders;
import com.foreach.across.modules.bootstrapui.elements.GlyphIcon;
import com.foreach.across.modules.bootstrapui.elements.TableViewElement;
import com.foreach.across.modules.bootstrapui.elements.builder.TableViewElementBuilder;
import com.foreach.across.modules.dynamicforms.domain.definition.DynamicDefinition;
import com.foreach.across.modules.dynamicforms.domain.definition.DynamicDefinitionRepository;
import com.foreach.across.modules.dynamicforms.domain.definition.QDynamicDefinition;
import com.foreach.across.modules.dynamicforms.domain.document.DynamicDocument;
import com.foreach.across.modules.dynamicforms.domain.document.DynamicDocumentRepository;
import com.foreach.across.modules.dynamicforms.domain.document.DynamicDocumentService;
import com.foreach.across.modules.entity.views.EntityViewElementBuilderHelper;
import com.foreach.across.modules.entity.views.bootstrapui.processors.element.EntityListActionsProcessor;
import com.foreach.across.modules.entity.views.bootstrapui.util.SortableTableBuilder;
import com.foreach.across.modules.entity.views.util.EntityViewElementUtils;
import com.foreach.across.modules.web.ui.ViewElementBuilderContext;
import com.foreach.across.modules.web.ui.ViewElementPostProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Collections;
import java.util.List;

/**
 * @author Marc & Steven
 * @since 1.0.0
 */
@Controller
public class TrainingsController
{
	@Autowired
	private DynamicDefinitionRepository dynamicDefinitionRepository;
	@Autowired
	private DynamicDocumentRepository documentRepository;
	@Autowired
	private EntityViewElementBuilderHelper builderHelper;
	@Autowired
	private DynamicDocumentService documentService;

	@GetMapping("/trainings")
	public String getTrainings( Model model ) {
		List<DynamicDefinition> dynamicDefinitions = dynamicDefinitionRepository.findAll(
				QDynamicDefinition.dynamicDefinition.key.equalsIgnoreCase( "training" ) );
		if ( dynamicDefinitions.size() > 0 ) {
			DynamicDefinition definition = dynamicDefinitions.get( 0 );
			List<DynamicDocument> items = documentRepository.findAllByType( definition );
			SortableTableBuilder sortableTableBuilder = builderHelper.createSortableTableBuilder( DynamicDocument.class );
			sortableTableBuilder.items( items )
			                    .tableOnly()
			                    .noSorting()
			                    .properties( "name" )
			                    .valueRowProcessor( new LinkToDocumentProcessor() );
			model.addAttribute( "dto", Collections.singletonMap( "definition", Collections.singletonMap( "name", "Trainingen" ) ) );
			model.addAttribute( "dynamicForm", sortableTableBuilder.build() );

		}
		return "th/demo/dynamicForm";
	}

	class LinkToDocumentProcessor implements ViewElementPostProcessor<TableViewElement.Row>
	{

		@Override
		public void postProcess( ViewElementBuilderContext viewElementBuilderContext, TableViewElement.Row row ) {
			DynamicDocument entity = EntityViewElementUtils.currentEntity( viewElementBuilderContext, DynamicDocument.class );
			TableViewElementBuilder.Cell cell = new TableViewElementBuilder.Cell()
					.name( EntityListActionsProcessor.CELL_NAME )
					.css( "row-actions" );
			cell.add( BootstrapUiBuilders.button()
			                             .link( UriComponentsBuilder.fromUriString( "/register" ).queryParam( "document", entity.getId() ).toUriString() )
			                             .iconOnly( BootstrapUiBuilders.glyphIcon( GlyphIcon.SEND ) )
			);
			row.addChild( cell.build( viewElementBuilderContext ) );
		}
	}
}
