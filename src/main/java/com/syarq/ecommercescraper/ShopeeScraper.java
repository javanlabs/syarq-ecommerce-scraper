package com.syarq.ecommercescraper;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import id.co.javan.webscraper.PhantomJsClient;
import id.co.javan.webscraper.PropertiesResolutionStrategy;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

public class ShopeeScraper implements Scraper {
    private static final ObjectMapper mapper = new ObjectMapper();

    @Override
    public boolean shouldScrape(String host) {
        return host.contains("shopee");
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
            Elements scriptss = doc.select("script");
            for(int i=0; i<scriptss.size(); i++) {
                Element elm = scriptss.get(i);
            }

            Elements scripts = doc.select("script[type=application/ld+json]");
            JsonNode productJson = null;
            // search for @type = Product, in case they have different position index
            for(int i=0; i<scripts.size(); i++) {
                Element element = scripts.get(i);
                JsonNode elementJson = mapper.readTree(element.html());
                if(elementJson.get("@type").asText().equalsIgnoreCase("product")) {
                    productJson = elementJson;
                    break;
                }
            }
            if(productJson == null) throw  new Exception("Shopee has updated its page");
            product = new ScraperProduct(url,
                    productJson.get("name").asText(),
                    productJson.get("description").asText(),
                    productJson.get("offers").get("price").asDouble(),
                    productJson.get("image").asText());

        } catch (Exception e) {
            e.printStackTrace();
        }
        return product;
    }

    public List<ScraperProduct> search(String keyword){
        return search(keyword, 0);
    }

    @Override
    public List<ScraperProduct> search(String keyword, int limit) {
        List<ScraperProduct> products = new ArrayList<>();
        String url = "https://shopee.co.id/search/?keyword="+keyword;

        try{
            PhantomJsClient client = new PhantomJsClient(new PropertiesResolutionStrategy());
            String html = client.get(url);
            Document doc = Jsoup.parse(html);
            Elements scripts = doc.select("script[type$=application/ld+json]");

            for(int i = 0; i < scripts.size(); i++){
                Element element = scripts.get(i);
                JsonNode elementJson = mapper.readTree(element.html());
                if(!elementJson.get("@type").asText().equalsIgnoreCase("product")) {
                    continue;
                }

                ScraperProduct product = new ScraperProduct(
                        elementJson.get("url").asText(),
                        elementJson.get("name").asText(),
                        elementJson.get("description").asText(),
                        elementJson.get("offers").get("price").asDouble(),
                        elementJson.get("image").asText());
                products.add(product);
            }

        }catch (Exception e){
            e.printStackTrace();
        }
        return products;
    }
}
