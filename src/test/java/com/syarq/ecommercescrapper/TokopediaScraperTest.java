package com.syarq.ecommercescrapper;

import com.syarq.ecommercescraper.Scraper;
import com.syarq.ecommercescraper.ScraperProduct;
import com.syarq.ecommercescraper.TokopediaScraper;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

public class TokopediaScraperTest {

    @Test
    public void scrapTest() {
        Scraper scraper = new TokopediaScraper();
        String url = "https://www.tokopedia.com/newrizkyapple/ready-macbook-12-mmgl2-rose-gold-dual-core-m3-ram-8gb-storage-256gb?src=topads";
        ScraperProduct p = scraper.scrap(url);
        assertEquals(url, p.getUrl());
        assertEquals("Ready Macbook 12\" MMGL2 Rose Gold Dual Core M3 RAM 8GB Storage 256GB", p.getName());
        assertEquals(15600000, p.getPrice(), 0.1);
        assertNotNull(p.getPhotoUrl());
        assertNotNull(p.getDescription());
    }

    @Test
    public void searchTest() {
        Scraper scraper = new TokopediaScraper();
        String keyword = "notebook";
        List<ScraperProduct> products = scraper.search(keyword);
        assertFalse(products.isEmpty());
    }
}
