package com.foreach.demo.dfm.application.installers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.foreach.across.core.annotations.Installer;
import com.foreach.across.core.annotations.InstallerMethod;
import com.foreach.across.core.installers.InstallerPhase;
import com.foreach.across.modules.dynamicforms.domain.dataset.DynamicDefinitionDataSet;
import com.foreach.across.modules.dynamicforms.domain.dataset.DynamicDefinitionDataSetRepository;
import com.foreach.across.modules.dynamicforms.domain.definition.*;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.transaction.annotation.Transactional;
import org.yaml.snakeyaml.Yaml;

import java.util.Optional;
import java.util.function.Consumer;

/**
 * @author Marc Vanbrabant
 * @since 0.0.1
 */
@RequiredArgsConstructor
@Installer(name = "definition-installer", description = "Creates the initial document definitions", phase = InstallerPhase.AfterContextBootstrap, version = 1)
@Slf4j
@Order(1000)
public class DefinitionInstaller {
    public final static String DEMO_DATASET = "Trainings";
    public final static String REGISTRATION_DEFINITION_NAME = "Registration";
    public final static String TRAINING_DEFINITION_NAME = "Training";

    @Value("classpath:demo-dfm/registration.yml")
    private Resource registrationDefinition;

    @Value("classpath:demo-dfm/training.yml")
    private Resource trainingDefinition;

    @Value("classpath:demo-dfm/registration-listView.yml")
    private Resource registrationListViewDefinition;

    @Value("classpath:demo-dfm/training-listView.yml")
    private Resource trainingListViewDefinition;

    private final DynamicDefinitionDataSetRepository dataSetRepository;
    private final DynamicDefinitionRepository definitionRepository;
    private final DynamicDefinitionVersionRepository definitionVersionRepository;
    private final DynamicDefinitionTypeRepository definitionTypeRepository;
    private final RawDefinitionService rawDefinitionService;
    private final ObjectMapper objectMapper;

    @Transactional
    @InstallerMethod
    public void createDefinitions() {
        DynamicDefinitionType documentType = definitionTypeRepository.findByName(DynamicDefinitionTypes.DOCUMENT);
        DynamicDefinitionType viewType = definitionTypeRepository.findByName(DynamicDefinitionTypes.VIEW);
        if (documentType != null && viewType != null) {
            DynamicDefinitionDataSet dataSet = getOrCreateDataSet(DEMO_DATASET.toLowerCase());
            createDefinition(dataSet, documentType, REGISTRATION_DEFINITION_NAME, "registration",
                    registrationDefinition, null,
                    dynamicDefinitionVersion -> createDefinition(dataSet, viewType, "listView",
                            "registration-listView", registrationListViewDefinition, dynamicDefinitionVersion.getDefinition(), null));

            createDefinition(dataSet, documentType, "Training", "training", trainingDefinition, null,
                    dynamicDefinitionVersion -> createDefinition(dataSet, viewType, "listView",
                            "training-listView", trainingListViewDefinition, dynamicDefinitionVersion.getDefinition(), null));
        }
    }

    private DynamicDefinitionDataSet getOrCreateDataSet(String key) {
        DynamicDefinitionDataSet dataSet = dataSetRepository.findOneByName(DefinitionInstaller.DEMO_DATASET);
        if (dataSet == null) {
            dataSet = new DynamicDefinitionDataSet();
            dataSet.setName(DefinitionInstaller.DEMO_DATASET);
            dataSet.setKey(key);
            dataSetRepository.save(dataSet);
        }
        return dataSet;
    }

    private void createDefinition(DynamicDefinitionDataSet dataSet,
                                  DynamicDefinitionType type,
                                  String name,
                                  String key,
                                  Resource documentDefinition,
                                  DynamicDefinition parent,
                                  Consumer<DynamicDefinitionVersion> consumer
    ) {
        DynamicDefinition definition = definitionRepository.findOneByDataSetAndParentAndName(dataSet, parent, name);
        if (definition == null) {
            definition = definitionRepository.save(DynamicDefinition.builder()
                    .dataSet(dataSet)
                    .type(type)
                    .name(name)
                    .key(key)
                    .parent(parent)
                    .build());
        }

        DynamicDefinitionVersion dynamicDefinitionVersion = definitionVersionRepository.findByDefinitionAndVersionEquals(definition, "1");
        if (dynamicDefinitionVersion == null) {
            dynamicDefinitionVersion = definitionVersionRepository.save(DynamicDefinitionVersion.builder()
                    .definition(definition)
                    .version("1")
                    .definitionContent(rawDefinitionService.writeDefinition(readDefinition(documentDefinition, type.getName())))
                    .published(true)
                    .build()
            );
        }

        if (consumer != null) {
            consumer.accept(dynamicDefinitionVersion);
        }
    }

    @SneakyThrows
    private RawDefinition readDefinition(Resource resource, String typeName) {
        Yaml yaml = new Yaml();
        Object raw = yaml.load(resource.getInputStream());
        String convertedJson = objectMapper.writeValueAsString(raw);

        return rawDefinitionService.readRawDefinition(convertedJson, typeName);
    }
}
