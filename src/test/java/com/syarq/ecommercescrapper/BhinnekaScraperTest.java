package com.syarq.ecommercescrapper;

import com.syarq.ecommercescraper.BhinnekaScraper;
import com.syarq.ecommercescraper.Scraper;
import com.syarq.ecommercescraper.ScraperProduct;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

public class BhinnekaScraperTest {

    @Test
    public void scrapTest() {
        Scraper scraper = new BhinnekaScraper();
        String url = "https://www.bhinneka.com/fjallraven-kanken-deep-red-random-blocked-sku3320816341";
        ScraperProduct p = scraper.scrap(url);
        assertEquals(url, p.getUrl());
        assertEquals("FJALLRAVEN Kanken Deep Red-Random Blocked", p.getName());
        assertEquals( 1399000, p.getPrice(),0.1);
        assertNotNull(p.getPhotoUrl());
        assertNotNull(p.getDescription());
    }

    @Test
    public void searchTest() {
        Scraper scraper = new BhinnekaScraper();
        String keyword = "notebook";
        List<ScraperProduct> products = scraper.search(keyword);
        assertFalse(products.isEmpty());
    }
}
