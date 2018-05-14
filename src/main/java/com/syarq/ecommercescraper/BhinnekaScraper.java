package com.syarq.ecommercescraper;

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
public class BhinnekaScraper implements Scraper {
    @Override
    public ScraperProduct scrape(String url) {
//        if (url.contains("/mob_products/")) {
//            url = url.replace("/mob_products/", "/products/");
//        }
//        Document doc;
        ScraperProduct product = null;
        try {
            PhantomJsClient client = new PhantomJsClient(new PropertiesResolutionStrategy());
            String html = client.get(url);
            Document doc = Jsoup.parse(html);
            Elements name = doc.select("h1");
            Elements metas = doc.select("meta");
            Double productPrice = null;

            String imageUrl = "";
            String description = "";
            try {
                for(int i=0;i<metas.size();i++){
                    Element meta = metas.get(i);
                    if(meta.attr("name").equalsIgnoreCase("twitter:data2")){
                        productPrice = Double.parseDouble(meta.attr("content").replaceAll("Rp ", "").replaceAll("\\,", ""));
                    }

                    if(meta.attr("property").equalsIgnoreCase("og:image")){
                        imageUrl = meta.attr("content");
                    }

                    if(meta.attr("itemprop").equalsIgnoreCase("description")){
                        description  = meta.attr("content");
                    }

                }
            } catch (Exception ex) {
                ex.printStackTrace();
                System.out.println("Parse Price Error: " + ex.getMessage());
            }
            product = new ScraperProduct(url,
                    name.text(),
                    description,
                    productPrice,
                    imageUrl);

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
        String url = "https://www.bhinneka.com/search.aspx?Search=" + keyword;
        String baseUrl = "https://www.bhinneka.com";
        try {
            PhantomJsClient client = new PhantomJsClient(new PropertiesResolutionStrategy());
            String html = client.get(url);
            Document doc = Jsoup.parse(html);
            Element card = doc.select("ul.prod-result-grid").first();
            if(card != null && card.children() != null) {
                ScraperProduct product;
                int added = 0;
                int max = limit < card.children().size() && limit > 0 ? limit : card.children().size();
                for (int i = 0; i < max; i++) {
                    Element e = card.children().get(i);
                    Elements urlProd = e.select("a.prod-itm-link");
                    Elements image = urlProd.select("img");
                    Elements name = urlProd.select("span.prod-itm-fullname");
                    Elements price = e.select("span.prod-itm-price-grid");
                    Double productPrice = 0.0;
                    if (urlProd.attr("href") == null || urlProd.attr("href").isEmpty()) {
                        continue;
                    }
                    try {
                        productPrice = Double.parseDouble(price.text().replaceAll(",", "").replace("Rp ", ""));
                    } catch (Exception ex) {
                        System.out.println("Parse Price Error: " + ex.getMessage());
                        continue;
                    }
                    String urlProduct = baseUrl + urlProd.attr("href");
                    if (urlProduct.contains("/mob_products/")) {
                        urlProduct = urlProduct.replace("/mob_products/", "/products/");
                    }
                    product = new ScraperProduct(urlProduct, name.text(),
                            "-", productPrice, "https:" + image.attr("src"));
                    products.add(product);
                    added++;
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return products;
    }
}
