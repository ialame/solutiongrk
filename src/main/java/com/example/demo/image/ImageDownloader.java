package com.example.demo.image;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Component
public class ImageDownloader {

    private static final Logger logger = LoggerFactory.getLogger(ImageDownloader.class);
    private final WebClient webClient;

    @Autowired
    public ImageDownloader(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.build();
    }

    public String downloadImage(String imageUrl, String gameType, String cardId) throws IOException {
        String baseDir = "src/main/resources/static/images/" + gameType;
        Path directory = Paths.get(baseDir);
        if (!Files.exists(directory)) {
            Files.createDirectories(directory);
        }
        String filePath = baseDir + "/" + cardId + ".jpg";
        File imageFile = new File(filePath);

        if (!imageFile.exists()) {
            byte[] imageBytes = webClient.get()
                    .uri(imageUrl)
                    .retrieve()
                    .bodyToMono(byte[].class)
                    .block();
            if (imageBytes != null) {
                try (FileOutputStream fos = new FileOutputStream(imageFile)) {
                    fos.write(imageBytes);
                }
            }
        }
        String relativePath = "/images/" + gameType + "/" + cardId + ".jpg";
        logger.info("Image downloaded: {}", relativePath);
        return relativePath;
    }
}