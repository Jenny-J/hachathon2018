package com.bc.rms.hackathon;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.support.SpringBootServletInitializer;


//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.ComponentScan;

/*import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.convert.DefaultDbRefResolver;
import org.springframework.data.mongodb.core.convert.DefaultMongoTypeMapper;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;*/


//@ComponentScan("com.bc.rms.hackathon")
@SpringBootApplication
public class SpringBootWebApplication extends SpringBootServletInitializer{
	
	/* @Bean
	  WebMvcConfigurerAdapter configurer () {
	        return new WebMvcConfigurerAdapter() {
	            @Override
	            public void addResourceHandlers (ResourceHandlerRegistry registry) {
	                registry.addResourceHandler("/pages/**").
	                          addResourceLocations("classpath:/resources/static");
	            }
	        };
	    }*/
	
	/*  @Bean
	    public MongoTemplate mongoTemplate(MongoDbFactory mongoDbFactory,
	                                       MongoMappingContext context) {

	        MappingMongoConverter converter =
	                new MappingMongoConverter(new DefaultDbRefResolver(mongoDbFactory), context);
	        converter.setTypeMapper(new DefaultMongoTypeMapper(null));

	        MongoTemplate mongoTemplate = new MongoTemplate(mongoDbFactory, converter);

	        return mongoTemplate;

	    }*/
	
	 @Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(SpringBootWebApplication.class);
	}
	
	
	public static void main(String[] args) throws Exception {
		
	/*	SpringApplication app =
                  new SpringApplication(SpringBootWebApplication.class);
        app.run(args);*/
		
		SpringApplication.run(SpringBootWebApplication.class, args);
	}


}
