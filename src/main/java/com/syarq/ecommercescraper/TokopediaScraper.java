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
public class TokopediaScraper implements Scraper {
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
            Elements name = doc.select("h1.product-title > a");

            Elements description = doc.select("div.product-info-holder > p");

            Elements price = doc.select("div.product-pricetag > span:nth-child(2)");

            Elements photo_url = doc.select("div.product-imagebig > img");

            Double productPrice = 0.0;
            try {
                productPrice = Double.parseDouble(price.text().replaceAll("\\.", ""));
            } catch (Exception ex) {
                System.out.println("Parse Price Error: " + ex.getMessage());
            }
            product = new ScraperProduct(url,
                    name.text(),
                    description.html(),
                    productPrice,
                    photo_url.attr("src"));

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
        Document doc;

        String url = "https://ta.tokopedia.com/promo/v1.1/display/ads?user_id=0&ep=product&item=10&src=search&device=desktop&page=1&q=" + keyword;
        try {
            doc = Jsoup.connect(url).ignoreContentType(true).userAgent("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_9_2) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/33.0.1750.152 Safari/537.36").get();
            JsonNode docObj = mapper.readTree(doc.text());
            JsonNode datas = docObj.get("data");
            int max = limit < datas.size() && limit > 0 ? limit : datas.size();
            for(int i=0;i<max;i++){
                JsonNode data = datas.get(i);
                JsonNode prd = data.get("product");

                Double productPrice = 0.0;
                try {
                    productPrice = Double.parseDouble(prd.get("price_format").asText().replaceAll("\\.", "").replaceAll("Rp ",""));
                } catch (Exception ex) {
                    System.out.println("Parse Price Error: " + ex.getMessage());
                }
                ScraperProduct p =  new ScraperProduct(prd.get("uri").asText(),
                        prd.get("name").asText(),
                        "-",
                        productPrice,
                        prd.get("image").get("m_url").asText()
                );
                products.add(p);
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        return products;

    }

}
