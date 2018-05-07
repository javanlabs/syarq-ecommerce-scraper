package com.syarq.ecommercescraper;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by idoej
 */
public class BukalapakScraper implements Scraper {
    private static final ObjectMapper mapper = new ObjectMapper();

    @Override
    public ScraperProduct scrap(String url) {
        if (url.contains("//m.")) {
            url = url.replace("//m.", "//www.");
        }
        Document doc;
        ScraperProduct product = null;
        try {
            doc = Jsoup.connect(url).get();

            Elements scripts = doc.select("script[type$=application/ld+json]");
            JsonNode productJson = mapper.readTree(scripts.get(1).html());
            JsonNode offerJson = productJson.get("offers");

            product = new ScraperProduct(url,
                    productJson.get("name").asText(),
                    productJson.get("description").asText(),
                    offerJson.get("price").asDouble(),
                    productJson.get("image").asText());

        } catch (IOException e) {
            e.printStackTrace();
        }
        return product;
    }

    public List<ScraperProduct> search(String keyword){
        return search(keyword, 0);
    }

    public List<ScraperProduct> search(String keyword, int limit){
        List<ScraperProduct> products = new ArrayList<>();
        String url = "https://api.bukalapak.com/v2/products.json?per_page=10&keywords=" + keyword;
        try {
            Document doc = Jsoup.connect(url).ignoreContentType(true).get();
            JsonNode response = mapper.readTree(doc.text());
            JsonNode prds = response.get("products");
            int max = limit < prds.size() && limit > 0 ? limit : prds.size();
            for(int i=0;i<max;i++){
                JsonNode product = prds.get(i);
                JsonNode images = product.get("images");
                ScraperProduct data = new ScraperProduct(product.get("url").asText(),
                        product.get("name").asText(),
                        "",
                        product.get("price").asDouble(),
                        images.get(0).asText());
                products.add(data);
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return products;
    }

    public static void main(String[] args) {
        System.out.println("TESTING");
        Scraper crawler = new BukalapakScraper();
        List<ScraperProduct> products = crawler.search("xiaomi mi5");
        for(int i=0;i<products.size();i++){
            ScraperProduct p = products.get(i);
            System.out.println(p.getUrl());
            System.out.println(p.getName());
            System.out.println(p.getPrice());
            System.out.println(p.getPhotoUrl());
            System.out.println(p.getDescription());
        }
//        ScraperProduct p = crawler.crawl("https://www.bukalapak.com/p/handphone/hp-smartphone/44j3iq-jual-iphone-6-16gb-gold-grey-garansi-1thn?from=old-popular-section-1");
//        System.out.println(p.getUrl());
//        System.out.println(p.getName());
//        System.out.println(p.getPrice());
//        System.out.println(p.getPhotoUrl());
//        System.out.println(p.getDescription());
    }
}
