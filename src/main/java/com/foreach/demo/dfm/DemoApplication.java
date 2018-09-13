package com.foreach.demo.dfm;

import com.foreach.across.config.AcrossApplication;
import com.foreach.across.modules.adminweb.AdminWebModule;
import com.foreach.across.modules.dynamicforms.DynamicFormsModule;
import com.foreach.across.modules.entity.EntityModule;
import com.foreach.across.modules.hibernate.jpa.AcrossHibernateJpaModule;
import com.foreach.across.modules.user.UserModule;
import com.foreach.across.modules.web.AcrossWebModule;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.context.config.ConfigFileApplicationListener;

import java.util.Collections;

@AcrossApplication(
		modules = {
				AcrossWebModule.NAME,
				AdminWebModule.NAME,
				AcrossHibernateJpaModule.NAME,
				UserModule.NAME,
				EntityModule.NAME,
				DynamicFormsModule.NAME
		}
)
public class DemoApplication
{
	public static void main( String[] args ) {
		SpringApplication springApplication = new SpringApplication( DemoApplication.class );
		springApplication.setDefaultProperties(
				Collections.singletonMap( ConfigFileApplicationListener.CONFIG_LOCATION_PROPERTY, "${user.home}/dev-configs/demo-application.yml" ) );
		springApplication.run( args );
	}
}