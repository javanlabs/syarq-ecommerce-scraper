package com.syarq.ecommercescraper;

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
public class BhinnekaScraper implements Scraper {
    @Override
    public ScraperProduct scrap(String url) {
        if (url.contains("/mob_products/")) {
            url = url.replace("/mob_products/", "/products/");
        }
        Document doc;
        ScraperProduct product = null;
        try {
            doc = Jsoup.connect(url).userAgent("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_9_2) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/33.0.1750.152 Safari/537.36")
                    .get();
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
        String url = "https://www.bhinneka.com/search.aspx?Search=" + keyword;
        String baseUrl = "https://www.bhinneka.com";
        try {
            Document doc = Jsoup.connect(url)
                    .get();
//            System.out.println(doc.toString());
            Element card = doc.select("div#productList.productListGrid").first();
            if(card != null && card.children() != null) {
                ScraperProduct product;
                int added = 0;
                int max = limit < card.children().size() && limit > 0 ? limit : card.children().size();
                for (int i = 0; i < max; i++) {
                    Element e = card.children().get(i);
                    Elements urlProd = e.select("a.productItem.clickable");
                    Elements name = e.select("div.productItemThumbnail > img");
                    Elements price = e.select("div#specialPrice");
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
                    product = new ScraperProduct(urlProduct, name.attr("title"),
                            "-", productPrice, "https:" + name.attr("src"));
                    products.add(product);
                    added++;
                }
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return products;
    }

    public static void main(String[] args) {
        System.out.println("TESTING");
        Scraper crawler = new BhinnekaScraper();
//        ScraperProduct p = crawler.crawl("https://www.bhinneka.com/products/sku09116551/xiaomi_mi_band_2_with_oled_display__merchant_.aspx");
//        System.out.println(p.getUrl());
//        System.out.println(p.getName());
//        System.out.println(p.getPrice());
//        System.out.println(p.getPhotoUrl());
//        System.out.println(p.getDescription());
        List<ScraperProduct> products = crawler.search("xiaomi", 10);
        for(ScraperProduct product: products) {
            System.out.println(product.getUrl());
            System.out.println(product.getName());
            System.out.println(product.getPrice());
            System.out.println(product.getPhotoUrl());
            System.out.println(product.getDescription());
        }
    }
}
