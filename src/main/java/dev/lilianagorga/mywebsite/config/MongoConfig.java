package dev.lilianagorga.mywebsite.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.convert.DefaultMongoTypeMapper;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;
import org.springframework.data.mongodb.core.convert.MongoCustomConversions;
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;

@Configuration
public class MongoConfig {

  @Bean
  @SuppressWarnings("deprecation")
  public MappingMongoConverter mappingMongoConverter(
          MongoDatabaseFactory mongoDatabaseFactory,
          MongoMappingContext context,
          MongoCustomConversions conversions) {

    MappingMongoConverter converter = new MappingMongoConverter(mongoDatabaseFactory, context);
    converter.setCustomConversions(conversions);
    converter.setTypeMapper(new DefaultMongoTypeMapper(null));
    return converter;
  }

  @Bean
  public MongoTemplate mongoTemplate(MongoDatabaseFactory mongoDatabaseFactory, MappingMongoConverter converter) {
    return new MongoTemplate(mongoDatabaseFactory, converter);
  }
}