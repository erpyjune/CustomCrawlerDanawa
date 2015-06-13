package com.erpy.thumbnail;

import com.erpy.crawler.CrawlIO;
import com.erpy.crawler.CrawlSite;
import com.erpy.crawler.HttpRequestHeader;
import com.erpy.dao.*;
import com.erpy.utils.GlobalUtils;
import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.Arrays;
import java.util.Map;

/**
 * Created by baeonejune on 15. 6. 5..
 */
public class ThumbnailProc {

    private static Logger logger = Logger.getLogger("ThumbnailProc");
    /////////////////////////////////////////////////////////////////
    // 상품정보 url의 본문 정보에서 큰 이미지를 download 한다.
    public void thumbnailProcessing(ThumbnailProcData thumbnailProcData) throws Exception {
        ThumbnailDataService thumbnailDataService = new ThumbnailDataService();
        int returnCode, crawlErrorCount, imageSaveErrorCount;
        ThumbnailData thumbnailData = new ThumbnailData();
        ThumbnailData dbThumbnailData;
        GlobalUtils globalUtils = new GlobalUtils();
        Document doc, document;
        Elements elements, listE;
        CrawlSite crawlSite = new CrawlSite();
        CrawlIO crawlIO = new CrawlIO();
        SearchData searchData;
        String strItem;
        String key, imageFileName;
        boolean isCrawlDataError=false;


        ////////////////////////////////////////////////////////////////////////
        // image 저장할 디렉토리 체크. 없으면 생성.
        crawlIO.saveDirCheck(thumbnailProcData.getSavePathPrefix(), thumbnailProcData.getCpName());

        ////////////////////////////////////////////////////////////////////////
        // image를 수집하기 위한 기본 환경 셋팅.
        HttpRequestHeader httpRequestHeader =
                new HttpRequestHeader(thumbnailProcData.getHostDomain(), thumbnailProcData.getHostReferer());
        crawlSite.setRequestHeader(httpRequestHeader.getHttpRequestHeader());
        crawlSite.setConnectionTimeout(thumbnailProcData.getHtmlCrawlConnectionTimeout());
        crawlSite.setSocketTimeout(thumbnailProcData.getHtmlCrawlReadTimeout());
        crawlSite.setCrawlEncode(thumbnailProcData.getHtmlCrawlEncoding());

        ////////////////////////////////////////////////////////////////////////
        // search 테이블에서 cp에 해당되는 모든 데이터를 read.
        Map<String, SearchData> searchDataMap = globalUtils.getAllSearchDatasByCP(thumbnailProcData.getCpName());
        for (Map.Entry<String, SearchData> entry : searchDataMap.entrySet()) {
            key = entry.getKey();
            searchData = entry.getValue();

            crawlSite.setCrawlUrl(searchData.getContentUrl());

            // thumbnail 추출할 상품정보 html 수집.
            crawlErrorCount = 0;
            isCrawlDataError = false;
            for (;;) {
                try {
                    returnCode = crawlSite.HttpCrawlGetDataTimeout();
                    if (returnCode != 200 && returnCode != 201) {
                        logger.error(String.format(" [%d]데이터를 수집 못했음 - %s", returnCode, crawlSite.getCrawlUrl()));
                        isCrawlDataError = true;
                    } else {
                        break;
                    }
                } catch (Exception e) {
                    if (crawlErrorCount >= 3) {
                        isCrawlDataError = true;
                        break;
                    }
                    crawlErrorCount++;
                    logger.error(" 추출할 상품정보 html 수집중 Exception 발생 " + Arrays.toString(e.getStackTrace()));
                    logger.error(" 수집 에러 발생 URL : " + crawlSite.getCrawlUrl());
                }
            }

            if (isCrawlDataError) {
                continue;
            }

            // thumbnail 추출.
            if (thumbnailProcData.getParserType()==1) {
                doc = Jsoup.parse(crawlSite.getCrawlData());
                elements = doc.select(thumbnailProcData.getParserGroupSelect());
                for (Element element : elements) {
                    if (thumbnailProcData.getParserSkipPattern().length() > 0) {
                        if (!element.outerHtml().contains(thumbnailProcData.getParserSkipPattern()))
                            continue;
                    }
                    document = Jsoup.parse(element.outerHtml());
                    listE = document.select(thumbnailProcData.getParserDocumentSelect());
                    for (Element et : listE) {
                        strItem = et.attr("src");
                        if (thumbnailProcData.getReplacePatternFindData().length()>0 && strItem.contains(thumbnailProcData.getReplacePatternFindData())) {
                            searchData.setThumbUrlBig(thumbnailProcData.getPrefixHostThumbUrl() +
                                    strItem.replace(thumbnailProcData.getReplacePatternSource(), thumbnailProcData.getReplacePatternDest()));
                            break;
                        } else {
                            searchData.setThumbUrlBig(thumbnailProcData.getPrefixHostThumbUrl() + strItem);
                            break;
                        }
                    }
                    break;
                }
            } else {
                logger.error(" Parser Type is NULL !!");
                continue;
            }

            // 추출한 thumbnail을 thumb 테이블에 저장.
            imageSaveErrorCount = 0;
            for (;;) {
                try {
                    // thumb_url_big URL에서 파일이름을 추출.
                    imageFileName = globalUtils.splieImageFileName(searchData.getThumbUrlBig());
                    // 본문에서 big 이미지를 download 한다.
                    globalUtils.saveDiskImgage(
                            thumbnailProcData.getSavePathPrefix(),
                            thumbnailProcData.getCpName(), searchData.getThumbUrlBig(), imageFileName);

                    // 기존 thumbnail이 있는지 찾는다.
                    thumbnailData.setCpName(thumbnailProcData.getCpName());
                    thumbnailData.setProductId(searchData.getProductId());
                    thumbnailData.setBigThumbUrl(searchData.getThumbUrlBig());
                    dbThumbnailData = thumbnailDataService.getFindThumbnailData(thumbnailData);
                    if (dbThumbnailData==null || dbThumbnailData.getBigThumbUrl().trim().length()==0) {
                        thumbnailDataService.insertThumbnailData(thumbnailData);
                    } else {
                        if (thumbnailProcData.isAllDataCrawl()) {
                            thumbnailDataService.updateThumbnailData(thumbnailData);
                        }
                    }
                    break;
                } catch (Exception e) {
                    if (imageSaveErrorCount > 3) {
                        logger.error(" Failure Download image breaking over MAX COUNT retry!!");
                        break;
                    }
                    logger.error(String.format(" Download image (%s) failure (%s), Count(%d)",
                            searchData.getThumbUrlBig(), e.getStackTrace().toString(),imageSaveErrorCount));
                    imageSaveErrorCount++;
                    Thread.sleep(1100);
                }
            }
        }
    }
}
