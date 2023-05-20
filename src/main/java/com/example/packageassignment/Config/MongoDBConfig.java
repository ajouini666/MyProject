package com.example.packageassignment.Config;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.AbstractMongoClientConfiguration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@Configuration
@EnableMongoRepositories(basePackages = "com.example.packageassignment.repository")
public class MongoDBConfig extends AbstractMongoClientConfiguration {

    @Override
    protected String getDatabaseName() {
        return "PackageManagmentSystem";
    }

    @Override
    @Bean
    public MongoClient mongoClient() {
        ConnectionString connectionString = new ConnectionString(
                "mongodb+srv://ajouini666:ZFrxnsnxjrDC9FC7@packagemanagment.yxdmssm.mongodb.net/?retryWrites=true&w=majority");

        MongoClientSettings settings = MongoClientSettings.builder()
                .applyConnectionString(connectionString)
                .build();
        return MongoClients.create(settings);
    }

    @Bean
    public MongoTemplate mongoTemplate() throws MongoDBConfigurationException {
        try {
            return new MongoTemplate(mongoClient(), getDatabaseName());
        } catch (Exception e) {
            throw new MongoDBConfigurationException("Error configuring MongoDB", e);
        }
    }

    public static class MongoDBConfigurationException extends Exception {
        public MongoDBConfigurationException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}