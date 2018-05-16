package com.syarq.ecommercescraper;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import id.co.javan.webscraper.PhantomJsClient;
import id.co.javan.webscraper.PropertiesResolutionStrategy;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by idoej
 */
public class BukalapakScraper implements Scraper {
    private static final ObjectMapper mapper = new ObjectMapper();

    @Override
    public boolean shouldScrape(String host) {
        return host.contains("bukalapak.com");
    }

    @Override
    public ScraperProduct scrape(String url) {
        if (url.contains("//m.")) {
            url = url.replace("//m.", "//www.");
        }
        ScraperProduct product = null;
        try {
            PhantomJsClient client = new PhantomJsClient(new PropertiesResolutionStrategy());
            String html = client.get(url);
            Document doc = Jsoup.parse(html);

            Elements scripts = doc.select("script[type$=application/ld+json]");
            JsonNode productJson = mapper.readTree(scripts.get(1).html());
            JsonNode offerJson = productJson.get("offers");

            product = new ScraperProduct(url,
                    productJson.get("name").asText(),
                    productJson.get("description").asText(),
                    offerJson.get("price").asDouble(),
                    productJson.get("image").asText());

        } catch (Exception e) {
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
            PhantomJsClient client = new PhantomJsClient(new PropertiesResolutionStrategy());
            String html = client.get(url);
            Document doc = Jsoup.parse(html);
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
        catch (Exception e) {
            e.printStackTrace();
        }
        return products;
    }
}
