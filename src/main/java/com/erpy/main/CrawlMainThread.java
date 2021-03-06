package com.erpy.main;

import com.erpy.crawler.CrawlIO;
import com.erpy.dao.CrawlData;
import com.erpy.dao.CrawlDataService;
import com.erpy.dao.Seed;
import com.erpy.dao.SeedService;
import com.erpy.parser.SB;
import com.erpy.utils.GlobalInfo;
import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by baeonejune on 15. 4. 13..
 */
public class CrawlMainThread extends Thread {
    private static Logger logger = Logger.getLogger(CrawlMainThread.class.getName());

    private String pageType;
    private int extractDataCount=0;
    private String crawlEncode;
    private String saveEncode;
    private String contentExtractCountPattern;
    private String extractType="html";

//    private String crawlUrl;
//    private String urlKeyword;
//    private String cpName;
    private Seed seed;
    private Map<String, CrawlData> allCrawlDatas;
    private Map<String, String> httpRequestHeader;


    public Map<String, String> getHttpRequestHeader() {
        return httpRequestHeader;
    }

    public void setHttpRequestHeader(Map<String, String> httpRequestHeader) {
        this.httpRequestHeader = httpRequestHeader;
    }

    public void setPageType(String pageType) {
        this.pageType = pageType;
    }

    public void setExtractDataCount(int extractDataCount) {
        this.extractDataCount = extractDataCount;
    }

    public void setCrawlEncode(String crawlEncode) {
        this.crawlEncode = crawlEncode;
    }

    public void setSaveEncode(String saveEncode) {
        this.saveEncode = saveEncode;
    }

    public void setContentExtractCountPattern(String contentExtractCountPattern) {
        this.contentExtractCountPattern = contentExtractCountPattern;
    }

    public Seed getSeed() {
        return seed;
    }

    public void setSeed(Seed seed) {
        this.seed = seed;
    }
    //    public void setCrawlUrl(String crawlUrl) {
//        this.crawlUrl = crawlUrl;
//    }
//
//    public void setUrlKeyword(String urlKeyword) {
//        this.urlKeyword = urlKeyword;
//    }
//
//    public String getCpName() {
//        return cpName;
//    }
//
//    public void setCpName(String cpName) {
//        this.cpName = cpName;
//    }

    public void setAllCrawlDatas(Map<String, CrawlData> allCrawlDatas) {
        this.allCrawlDatas = allCrawlDatas;
    }

    public void setExtractType(String extractType) {
        this.extractType = extractType;
    }


    public void run() {
        logger.info(String.format(" Thread run - %s", seed.getUrl()));

        CrawlIO crawlIO = new CrawlIO();
        crawlIO.setExtractType(extractType);
        crawlIO.setCrawlIO(pageType, extractDataCount, crawlEncode, saveEncode, contentExtractCountPattern);
        crawlIO.setHttpReqHeader(httpRequestHeader);

        try {
            if (seed.getCpName().equals(GlobalInfo.CP_GSDeal)) { // GSDeal
                crawlIO.crawlGSDeal(seed, allCrawlDatas);
            }
            else if (seed.getCpName().equals(GlobalInfo.CP_HappyVirusPost)) { // HappyVirus POST
                crawlIO.crawlHappyVirusPost(seed, allCrawlDatas);
            }
            else if (seed.getCpName().equals(GlobalInfo.CP_Totooutdoor)) { // HappyVirus POST
                crawlIO.crawlTotoOutdoor(seed, allCrawlDatas);
            }
            else if  (seed.getCpName().equals(GlobalInfo.CP_Timon)) { // Timon
                crawlIO.crawlOne(seed, allCrawlDatas);
                crawlIO.crawlTimon(seed, allCrawlDatas);
            }
            else if (seed.getCpName().equals(GlobalInfo.CP_WeMef) || seed.getCpName().equals(GlobalInfo.CP_HappyVirusFirst) ||
                    seed.getCpName().equals(GlobalInfo.CP_HappyDeals)) {
                crawlIO.crawlOne(seed, allCrawlDatas);
            }
            else {
                crawlIO.crawl(seed, allCrawlDatas);
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(String.format(" Running exception - %s", seed.getCpName()));
        }
    }
}
