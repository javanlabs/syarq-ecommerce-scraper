package com.syarq.ecommercescrapper;

import com.syarq.ecommercescraper.JdScraper;
import com.syarq.ecommercescraper.Scraper;
import com.syarq.ecommercescraper.ScraperProduct;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

public class JdScraperTest {

    @Test
    public void scrapTest() {
        Scraper scraper = new JdScraper();
        String url = "https://www.jd.id/product/shop-at-velvet-revel-in-nostalgia-rhode-white-all-size_10080903/100191629.html";
        ScraperProduct p = scraper.scrape(url);
        assertEquals(url, p.getUrl());
        assertEquals("JD.id SHOP AT VELVET Revel In Nostalgia Rhode - White [All Size]", p.getName());
        assertEquals( 275000, p.getPrice(),0.1);
        assertNotNull(p.getPhotoUrl());
        assertNotNull(p.getDescription());
    }

    @Test
    public void searchTest() {
        Scraper scraper = new JdScraper();
        String keyword = "asus";
        List<ScraperProduct> products = scraper.search(keyword);
        assertFalse(products.isEmpty());
    }
}
