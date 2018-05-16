package com.syarq.ecommercescraper;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import id.co.javan.webscraper.PhantomJsClient;
import id.co.javan.webscraper.PropertiesResolutionStrategy;
import org.jsoup.Jsoup;
import org.jsoup.nodes.DataNode;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class LazadaScraper implements Scraper {
    private static final ObjectMapper mapper = new ObjectMapper();

    @Override
    public boolean shouldScrape(String host) {
        return host.contains("lazada");
    }

    @Override
    public ScraperProduct scrape(String url) {
        ScraperProduct product = null;
        try {
            PhantomJsClient client = new PhantomJsClient(new PropertiesResolutionStrategy());
            String html = client.get(url);
            Document doc = Jsoup.parse(html);
            Elements name = doc.select("h1.pdp-product-title");
            Elements description = doc.select("div.pdp-product-desc");
            Elements price = doc.select("div.pdp-product-price > span");
            Elements photo_url = doc.select("div.item-gallery > div.gallery-preview-panel img");

            Double productPrice = 0.0;
            try {
                productPrice = Double.parseDouble(price.text().replaceAll("Rp", "").replaceAll("\\.", ""));
            } catch (Exception ex) {
                System.out.println("Parse Price Error: " + ex.getMessage());
            }
            product = new ScraperProduct(url,
                    name.text(),
                    description.html(),
                    productPrice,
                    "https:" + photo_url.attr("src"));

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
        String url = "https://www.lazada.co.id/catalog/?q=" + keyword;
        try {
            Document doc = Jsoup.connect(url).get();
            Elements cards = doc.getElementsByTag("script");
//            BufferedWriter writer =  new BufferedWriter(new FileWriter("output4.html"));
//            writer.write(cards.size());
//            writer.close();
            String result = null;
            boolean stop = false;
            for (Element element :cards ){
                for (DataNode node : element.dataNodes()) {
                    result = node.getWholeData();
                    if(result.contains("window.pageData")){
                        stop = true;
                        break;
                    }
                }
                if(stop) break;
            }
            JsonNode jsonObject = mapper.readTree(result.replace("window.pageData=", ""));
            jsonObject = jsonObject.get("mods");
            JsonNode prds = jsonObject.get("listItems");
            int max = limit < prds.size() && limit > 0 ? limit : prds.size();
            for(int i=0;i<max;i++){
                JsonNode product = prds.get(i);
                if(product.get("productUrl").asText() == null){
                    continue;
                }
                ScraperProduct data = new ScraperProduct("https:"+product.get("productUrl").asText(),
                        product.get("name").asText(),
                        "",
                        product.get("price").asDouble(),
                        product.get("image").asText());
                products.add(data);
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return products;
    }
}
