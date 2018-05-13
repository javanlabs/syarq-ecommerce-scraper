package com.syarq.ecommercescraper;

import id.co.javan.webscraper.PhantomJsClient;
import id.co.javan.webscraper.PropertiesResolutionStrategy;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by idoej
 */
public class KliknklikScraper implements Scraper {

    @Override
    public ScraperProduct scrap(String url) {
        if (url.contains("//m.")) {
            url = url.replace("//m.", "//www.");
        }

        ScraperProduct product = null;
        try {
            PhantomJsClient client = new PhantomJsClient(new PropertiesResolutionStrategy());
            String html = client.get(url);
            Document doc = Jsoup.parse(html);
            Elements name = doc.select("#judul-produk");
            Elements description = doc.select("#producttab-description");
            Elements specification = doc.select("#producttab-datasheet");

            Elements price = doc.select("#our_price_display");

            Elements photo_url = doc.select("#view_full_size > a.jqzoom.img-responsive");

            Double productPrice = 0.0;
            try {
                productPrice = Double.parseDouble(price.text().replaceAll("\\.", "").replace("Rp ", ""));
            } catch (Exception ex) {
                System.out.println("Parse Price Error: " + ex.getMessage());
            }
            String productName = name.text();

            product = new ScraperProduct(url,
                    productName,
                    description.html() + specification.html(),
                    productPrice,
                    photo_url.attr("href"));

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
        String url = "http://kliknklik.com/search?controller=search&orderby=position&orderway=desc&submit_search=&search_query=" + keyword;
        try {
            PhantomJsClient client = new PhantomJsClient(new PropertiesResolutionStrategy());
            String html = client.get(url);
            Document doc = Jsoup.parse(html);
            Elements cards = doc.select("div.product-container");
            ScraperProduct product;
            int max = limit < cards.size() && limit > 0 ? limit : cards.size();
            for(int i=0;i<max;i++){
                Element e = cards.get(i);
                Elements name = e.select("h4.name > a");
                Elements image = e.select("a.img.product_img_link > img");
                Elements price = e.select("div.product-price.new-price");
                if(price == null || price.isEmpty()){
                    price = e.select("div.product-price");
                }
                Double productPrice = 0.0;
                try {
                    productPrice = Double.parseDouble(price.text().replaceAll("\\.", "").replace("Rp ", ""));
                } catch (Exception ex) {
                    System.out.println("Parse Price Error: " + ex.getMessage());
                }
                product = new ScraperProduct(name.attr("href"), name.html(), "-", productPrice, image.attr("src"));
                products.add(product);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return products;
    }

}
