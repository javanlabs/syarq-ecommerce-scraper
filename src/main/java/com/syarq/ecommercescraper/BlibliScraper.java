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

/**
 * Created by idoej
 */
public class BlibliScraper implements Scraper {
    private static final ObjectMapper mapper = new ObjectMapper();

    @Override
    public boolean shouldScrape(String host) {
        return host.contains("blibli");
    }

    @Override
    public ScraperProduct scrape(String url) {
        ScraperProduct product = null;
        try {
            // parse url to get product SKU
            // example of URL:
            // - https://www.blibli.com/p/hiceh-sharp-sj-x185mg-fr-kulkas-flower-red-1-pintu-166-l/ps--BLE.15019.03472
            // - https://www.blibli.com/p/samsung-t5-portable-ssd-eksternal-hitam-1-tb/pc--MTA-1470620?ds=PRN-10729-00949-00001&list=
            // - https://www.blibli.com/oppo-a71-smartphone-gold-16gb-2gb-MYP.60024.00003.html
            // SKU = ps--BLE.15019.03472

            PhantomJsClient client = new PhantomJsClient(new PropertiesResolutionStrategy());
            String html = client.get(url);
            Document doc = Jsoup.parse(html);
            Elements scripts = doc.select("script[type$=application/ld+json]");
            JsonNode productJson = mapper.readTree(scripts.get(0).html());

            String name = productJson.get("name").asText();
            String description = productJson.get("description").asText();
            String imgUrl = productJson.get("image").asText();
            Double productPrice = productJson.get("offers").get("price").asDouble();
            product = new ScraperProduct(url, name, description, productPrice, imgUrl);

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
        String parameter = keyword.replaceAll(" ", "-");
        String url = "https://www.blibli.com/jual/" + parameter + "?" + keyword;
        try {
            PhantomJsClient client = new PhantomJsClient(new PropertiesResolutionStrategy());
            String html = client.get(url);
            Document doc = Jsoup.parse(html);
            Elements cards = doc.select("a.single-product");
            ScraperProduct product;
            int max = limit < cards.size() && limit > 0 ? limit : cards.size();
            for(int i=0;i<max;i++){
                Element card = cards.get(i);
                String imgUrl = card.select("div.product-image img").get(0).attr("data-original");
                String name = card.select("div.product-title").get(0).attr("title");
                String href = card.attr("href");
                String priceText = card.select("span.new-price-text").get(0).text().substring(3);
                if(priceText.contains("-")) {
                    priceText = priceText.substring(0, priceText.indexOf("-")).trim();
                }
                Double price = Double.parseDouble(priceText.replaceAll(",", ""));
                product = new ScraperProduct(href, name, "-", price, imgUrl);
                products.add(product);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return products;
    }

}
