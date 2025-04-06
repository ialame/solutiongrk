package com.example.demo.jsoup;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class YugiohTranslationScraper {
    private static final Logger logger = LoggerFactory.getLogger(YugiohTranslationScraper.class);
    private static final String KONAMI_BASE_URL = "https://www.db.yugioh-card.com/yugiohdb/card_search.action";

    public static String getFrenchTranslation(String cardId) {
        try {
            String url = KONAMI_BASE_URL + "?ope=2&cid=" + cardId + "&request_locale=fr";
            Document doc = Jsoup.connect(url)
                    .userAgent("Mozilla/5.0") // Pour éviter les blocages
                    .timeout(10000)
                    .get();

            // Le nom de la carte est dans l'élément avec la classe "card_name"
            String frenchName = doc.select(".card_name").text();
            if (frenchName.isEmpty()) {
                logger.warn("Aucune traduction française trouvée pour la carte ID {}", cardId);
                return null;
            }
            logger.info("Traduction française récupérée pour {} : {}", cardId, frenchName);
            return frenchName;
        } catch (IOException e) {
            logger.error("Erreur lors du scraping pour la carte ID {} : {}", cardId, e.getMessage());
            return null;
        }
    }

    public static void main(String[] args) {
        // Test avec "Blue-Eyes White Dragon" (cid=4007)
        String frenchName = getFrenchTranslation("4007");
        System.out.println("Nom français : " + frenchName);
    }
}