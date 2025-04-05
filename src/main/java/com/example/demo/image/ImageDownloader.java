package com.example.demo.image;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Component
public class ImageDownloader {

    private static final Logger logger = LoggerFactory.getLogger(ImageDownloader.class);

    public void downloadImage(String url, String fileName, String directory) {
        try {
            Path dirPath = Paths.get(directory);
            if (!Files.exists(dirPath)) {
                Files.createDirectories(dirPath);
            }

            Path filePath = dirPath.resolve(fileName + ".jpg");
            try (InputStream in = new URL(url).openStream()) {
                Files.copy(in, filePath, StandardCopyOption.REPLACE_EXISTING);
                logger.info("Image téléchargée : {}", filePath);
            }
        } catch (IOException e) {
            logger.error("Erreur lors du téléchargement de l'image {} : {}", url, e.getMessage());
        }
    }
}