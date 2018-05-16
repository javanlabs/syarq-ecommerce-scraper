package com.syarq.ecommercescraper;

import java.net.URI;

public class ScraperFactory {
    public static final Scraper[] SCRAPERS = {
            new BukalapakScraper(),
            new KliknklikScraper(),
            new TokopediaScraper(),
            new JdScraper(),
            new LazadaScraper(),
            new BhinnekaScraper(),
            new BlibliScraper(),
            new RamesiaScraper(),
            new ShopeeScraper()
    };

    public static Scraper create(String url) {
        String host = null;
        try {
            URI uri = new URI(url.replaceAll("\\s", "%20"));
            host = uri.getHost();
        } catch (Exception e) {}
        
        if(host == null) return null;
        
        for(Scraper scraper: SCRAPERS) {
            if(scraper.shouldScrape(host)) return scraper;
        }
        return null;
    }

}
