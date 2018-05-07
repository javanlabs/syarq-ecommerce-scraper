package com.syarq.ecommercescraper;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by wisnov on 5/31/17.
 */
public class BlibliScraper implements Scraper {
    private static final ObjectMapper mapper = new ObjectMapper();

    @Override
    public ScraperProduct scrap(String url) {
        Document doc;
        ScraperProduct product = null;
        try {
            // parse url to get product SKU
            // example of URL:
            // - https://www.blibli.com/p/hiceh-sharp-sj-x185mg-fr-kulkas-flower-red-1-pintu-166-l/ps--BLE.15019.03472
            // - https://www.blibli.com/p/samsung-t5-portable-ssd-eksternal-hitam-1-tb/pc--MTA-1470620?ds=PRN-10729-00949-00001&list=
            // - https://www.blibli.com/oppo-a71-smartphone-gold-16gb-2gb-MYP.60024.00003.html
            // SKU = ps--BLE.15019.03472

            // if it contains .html, it is a slug url, we must follow redirect first
            if(url.contains(".html")) {
                Connection.Response response = Jsoup.connect(url).followRedirects(false).execute();
                if(response.hasHeader("location")) {
                    url = response.header("location");
                }
            }

            String[] urlSection = url.split("\\/");
            String sku = null;
            for(int i=urlSection.length-1; i>=0; i--) {
                if(urlSection[i].contains("--")) {
                    sku = urlSection[i];
                    if(sku.contains("?")) {
                        sku = sku.substring(0, sku.indexOf("?"));
                    }
                    break;
                }
            }
            if(sku == null) throw new Exception("Blibli has updated its page");

            String jsonUrl = "https://www.blibli.com/backend/product/products/"+sku+"/_summary";
            doc = Jsoup.connect(jsonUrl)
//                    .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:59.0) Gecko/20100101 Firefox/59.0")
                    .ignoreContentType(true)
                    .header("Referer", url)
                    .header("Accept", "application/json, text/plain, */*")
                    .get();
            JsonNode productJson = mapper.readTree(doc.text());
            JsonNode dataJson = productJson.get("data");

            String name = dataJson.get("name").asText();
            String description = dataJson.get("description").asText();
            String imgUrl = dataJson.get("images").get(0).get("thumbnail").asText();
            Double productPrice = 0.0;
            try {
                String priceUrl = "https://www.blibli.com/backend/product/products/"+sku+"/_info?defaultItemSku=";
                doc = Jsoup.connect(priceUrl)
//                    .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:59.0) Gecko/20100101 Firefox/59.0")
                        .ignoreContentType(true)
                        .header("Referer", url)
                        .header("Accept", "application/json, text/plain, */*")
                        .get();
                JsonNode priceJson = mapper.readTree(doc.text());
                productPrice = priceJson.get("data").get("price").get("offered").asDouble();
            } catch (Exception ex) {
                System.out.println("Parse Price Error: " + ex.getMessage());
            }
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
            Document doc = Jsoup.connect(url).get();
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
        catch (IOException e) {
            e.printStackTrace();
        }
        return products;
    }

}
