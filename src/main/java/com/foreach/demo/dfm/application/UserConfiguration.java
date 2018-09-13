package com.foreach.demo.dfm.application;

import com.foreach.across.modules.bootstrapui.elements.BootstrapUiElements;
import com.foreach.across.modules.entity.config.EntityConfigurer;
import com.foreach.across.modules.entity.config.builders.EntitiesConfigurationBuilder;
import com.foreach.across.modules.entity.views.ViewElementMode;
import com.foreach.across.modules.user.business.User;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UserConfiguration implements EntityConfigurer {
    @Override
    public void configure(EntitiesConfigurationBuilder entitiesConfigurationBuilder) {
        entitiesConfigurationBuilder.withType(User.class)
                .viewElementType(ViewElementMode.CONTROL.forMultiple(), BootstrapUiElements.SELECT);
    }
}
