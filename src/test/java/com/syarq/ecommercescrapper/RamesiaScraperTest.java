package com.syarq.ecommercescrapper;

import com.syarq.ecommercescraper.RamesiaScraper;
import com.syarq.ecommercescraper.Scraper;
import com.syarq.ecommercescraper.ScraperProduct;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

public class RamesiaScraperTest {

    @Test
    public void scrapTest() {
        Scraper scraper = new RamesiaScraper();
        String url = "https://ramesia.com/product/mesin-cup-sealer-cs-727i/";
        ScraperProduct p = scraper.scrape(url);
        assertEquals(url, p.getUrl());
        assertEquals("Mesin Cup Sealer CS-727i", p.getName());
        assertEquals( 899000, p.getPrice(),0.1);
        assertNotNull(p.getPhotoUrl());
        assertNotNull(p.getDescription());
    }

    @Test
    public void searchTest() {
        Scraper scraper = new RamesiaScraper();
        String keyword = "mesin";
        List<ScraperProduct> products = scraper.search(keyword);
        assertFalse(products.isEmpty());
    }
}
