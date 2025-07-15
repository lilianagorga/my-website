package dev.lilianagorga.mywebsite;

import dev.lilianagorga.mywebsite.config.MongoDBIpUpdater;
import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.web.client.RestTemplate;

public class UpdateIpLauncher {

    public static void main(String[] args) {
        Dotenv dotenv = Dotenv.configure().filename(".env.testing").load();

        MongoDBIpUpdater updater = new MongoDBIpUpdater(new RestTemplate());
        updater.setPublicKey(dotenv.get("MONGO_API_PUBLIC_KEY"));
        updater.setPrivateKey(dotenv.get("MONGO_API_PRIVATE_KEY"));
        updater.setProjectId(dotenv.get("MONGO_PROJECT_ID"));

        updater.updateIpAddress();
    }
}