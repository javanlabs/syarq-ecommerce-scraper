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

public class RamesiaScraper implements Scraper {
    private static final ObjectMapper mapper = new ObjectMapper();
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

            JsonNode rootJson = mapper.readTree(scripts.get(0).html());
            JsonNode graphJson = rootJson.get("graph");
            System.out.println(graphJson.toString());
            JsonNode productJson = null;
            for(int i=0; i<graphJson.size(); i++) {
                JsonNode json = graphJson.get(i);
                System.out.println(json.toString());
                if(json.get("type").asText().equalsIgnoreCase("product")) {
                    productJson = json;
                    break;
                }
            }

            if(productJson == null) throw new Exception("Ramesia has updated its page");

            product = new ScraperProduct(url,
                    productJson.get("name").asText(),
                    productJson.get("offers").get("description").asText(),
                    productJson.get("offers").get("price").asDouble(),
                    productJson.get("offers").get("image").asText());

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
        String baseUrl = "https://ramesia.com/";
        String url = baseUrl+"?s="+ keyword +"&post_type=product";
        try {
            PhantomJsClient client = new PhantomJsClient(new PropertiesResolutionStrategy());
            String html = client.get(url);
            Document doc = Jsoup.parse(html);
            Elements cards = doc.select("div.product-small.box");
            ScraperProduct product;
            int max = limit < cards.size() && limit > 0 ? limit : cards.size();
            for(int i=0;i<max;i++){
                Element e = cards.get(i);
                Elements name = e.select("div.box-text > div.title-wrapper > p.product-title > a");
                Elements image = e.select("div.box-image > div.image-fade_in_back > a > img");
                Elements price = e.select("div.box-text div.price-wrapper span.price span.amount");

                Double productPrice = 0.0;
                try {
                    productPrice = Double.parseDouble(price.last().text().replaceAll("\\.", "").replace(",00", "").replace("Rp", ""));
                } catch (Exception ex) {}

                if(productPrice > 0){
                    product = new ScraperProduct(name.attr("href"), name.html(), "-", productPrice, "https:"+image.attr("src"));
                    products.add(product);
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return products;
    }
}
