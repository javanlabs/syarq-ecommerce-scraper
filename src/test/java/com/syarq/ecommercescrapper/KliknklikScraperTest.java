package com.syarq.ecommercescrapper;

import com.syarq.ecommercescraper.KliknklikScraper;
import com.syarq.ecommercescraper.Scraper;
import com.syarq.ecommercescraper.ScraperProduct;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

public class KliknklikScraperTest {

    @Test
    public void scrapTest() {
        Scraper scraper = new KliknklikScraper();
        String url = "https://kliknklik.com/action-camcorder/20291-xiaomi-yi-action-camera-basic-white.html";
        ScraperProduct p = scraper.scrape(url);
        assertEquals(url, p.getUrl());
        assertEquals("XIAOMI Yi Action Camera Basic - White", p.getName());
        assertEquals(1599000, p.getPrice(), 0.1);
        assertNotNull(p.getPhotoUrl());
        assertNotNull(p.getDescription());
    }

    @Test
    public void searchTest() {
        Scraper scraper = new KliknklikScraper();
        String keyword = "xiaomi";
        List<ScraperProduct> products = scraper.search(keyword);
        assertFalse(products.isEmpty());
    }
}
