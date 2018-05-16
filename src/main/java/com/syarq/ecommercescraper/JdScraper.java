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

public class JdScraper implements Scraper {
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

            Elements name = doc.select("#summary > h1");
            Elements description = doc.select("#p-detail-tab " +
                    "> div.bd.ui-switchable-panel-main " +
                    "> div.ui-switchable-panel.ui-switchable-panel-selected " +
                    "> div " +
                    "> div.item.mt20.description " +
                    "> div " +
                    "> p");

            Elements scripts = doc.select("script[type$=application/ld+json]");
            Element script = scripts.first();
            String offer = script.html();
            JsonNode productJson = mapper.readTree(offer);
            Double productPrice = 0.0;
            try {
                productPrice = productJson.get("offers").get("lowPrice").asDouble();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            product = new ScraperProduct(url,
                    name.text(),
                    description.html(),
                    productPrice,
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
        String url = "https://www.jd.id/search?keywords=" + keyword;
        try {
            PhantomJsClient client = new PhantomJsClient(new PropertiesResolutionStrategy());
            String html = client.get(url);
            Document doc = Jsoup.parse(html);
            Elements cards = doc.select("div.j-module > ul > li > div.jItem");
            ScraperProduct product;
            int max = limit < cards.size() && limit > 0 ? limit : cards.size();
            for(int i=0;i<max;i++){
                Element e = cards.get(i);
                Element name = e.select("div.jGoodsInfo > div.jDesc > a").first();
                Element image = e.select("div.jPic > a > img").first();
                Element price = e.select("div.jGoodsInfo > div.jPrice > p.jdPrice > span").first();
                Double productPrice = 0.0;
                try {
                    productPrice = Double.parseDouble(price.text().replaceAll(",", "").replace("Rp ", ""));
                } catch (Exception ex) {
                    System.out.println("Parse Price Error: " + ex.getMessage());
                }
                product = new ScraperProduct("https:" + name.attr("href"), name.html(), "-", productPrice, "https:" + image.attr("original"));
                products.add(product);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return products;
    }
}
