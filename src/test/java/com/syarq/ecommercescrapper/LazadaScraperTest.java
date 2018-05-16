package com.syarq.ecommercescrapper;

import com.syarq.ecommercescraper.LazadaScraper;
import com.syarq.ecommercescraper.Scraper;
import com.syarq.ecommercescraper.ScraperProduct;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

public class LazadaScraperTest {

    @Test
    public void scrapTest() {
        Scraper scraper = new LazadaScraper();
        String url = "https://www.lazada.co.id/products/gazgas-hummer-pro-250-sepeda-motor-trail-i119081443-s124122239.html";
        ScraperProduct p = scraper.scrape(url);
        assertEquals(url, p.getUrl());
        assertEquals("Gazgas Hummer Pro 250 Sepeda Motor Trail", p.getName());
        assertEquals( 26000000, p.getPrice(),0.1);
        assertNotNull(p.getPhotoUrl());
        assertNotNull(p.getDescription());
    }

    @Test
    public void searchTest() {
        Scraper scraper = new LazadaScraper();
        String keyword = "asus";
        List<ScraperProduct> products = scraper.search(keyword);
        assertFalse(products.isEmpty());
    }
}
