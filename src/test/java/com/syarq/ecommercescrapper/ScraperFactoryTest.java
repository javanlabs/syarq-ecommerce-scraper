package com.syarq.ecommercescrapper;

import com.syarq.ecommercescraper.*;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ScraperFactoryTest {

    @Test
    public void determineScraperTest() {
        Object[][] testData = {
                {"https://www.bhinneka.com/fjallraven-kanken-deep-red-random-blocked-sku3320816341",
                        BhinnekaScraper.class},
                {"https://www.blibli.com/p/ramadhan-fair-radysa-organizer-rak-sepatu-portable-abu-abu-6-susun/ps--RAO.12841.00666",
                        BlibliScraper.class},
                {"https://www.bukalapak.com/p/handphone/hp-smartphone/44j3iq-jual-iphone-6-16gb-gold-grey-garansi-1thn?from=old-popular-section-1",
                        BukalapakScraper.class},
                {"https://www.jd.id/product/shop-at-velvet-revel-in-nostalgia-rhode-white-all-size_10080903/100191629.html",
                        JdScraper.class},
                {"https://kliknklik.com/action-camcorder/20291-xiaomi-yi-action-camera-basic-white.html",
                        KliknklikScraper.class},
                {"https://www.lazada.co.id/products/gazgas-hummer-pro-250-sepeda-motor-trail-i119081443-s124122239.html",
                        LazadaScraper.class},
                {"https://ramesia.com/product/mesin-cup-sealer-cs-727i/",
                        RamesiaScraper.class},
                {"https://www.tokopedia.com/newrizkyapple/ready-macbook-12-mmgl2-rose-gold-dual-core-m3-ram-8gb-storage-256gb?src=topads",
                        TokopediaScraper.class}
        };

        for(Object[] testDatum: testData) {
            Scraper scraper = ScraperFactory.create((String)testDatum[0]);
            assertEquals(testDatum[1], scraper.getClass());
        }
    }

}
