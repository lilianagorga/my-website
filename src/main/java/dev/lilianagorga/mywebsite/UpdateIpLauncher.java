package dev.lilianagorga.mywebsite;

import dev.lilianagorga.mywebsite.config.MongoDBIpUpdater;
import org.springframework.web.client.RestTemplate;

import java.io.InputStream;
import java.util.Map;

import org.yaml.snakeyaml.Yaml;

public class UpdateIpLauncher {

    @SuppressWarnings("unchecked")
    public static void main(String[] args) {
        Yaml yaml = new Yaml();
        InputStream inputStream = UpdateIpLauncher.class
                .getClassLoader()
                .getResourceAsStream("application-test.yml");

        if (inputStream == null) {
            inputStream = UpdateIpLauncher.class
                    .getClassLoader()
                    .getResourceAsStream("application.yml");
        }

        if (inputStream == null) {
            throw new IllegalStateException("No application YAML config file found on classpath");
        }

        Map<String, Object> config = yaml.load(inputStream);
        Map<String, Object> mongo = (Map<String, Object>) config.get("mongo");
        Map<String, Object> api = (Map<String, Object>) mongo.get("api");
        Map<String, Object> publicMap = (Map<String, Object>) api.get("public");
        Map<String, Object> privateMap = (Map<String, Object>) api.get("private");
        Map<String, Object> project = (Map<String, Object>) mongo.get("project");

        MongoDBIpUpdater updater = new MongoDBIpUpdater(new RestTemplate());
        updater.setPublicKey((String) publicMap.get("key"));
        updater.setPrivateKey((String) privateMap.get("key"));
        updater.setProjectId((String) project.get("id"));

        updater.updateIpAddress();
    }
}