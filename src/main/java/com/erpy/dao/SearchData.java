package com.erpy.dao;

/**
 * Created by baeonejune on 14. 12. 27..
 */
public class SearchData {
    Integer dataId=null;
    String productId=null;
    String productName=null;
    String brandName="";
    String contentUrl=null;
    String thumbUrl="";
    Integer orgPrice=0;
    Integer salePrice=0;
    Float salePer=0.0f;
    String cpName=null;
    String crawlKeyword=null;
    boolean bMan=false;
    boolean bWoman=false;
    String type;
    String dataStatus;


    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getBrandName() {
        return brandName;
    }

    public void setBrandName(String brandName) {
        this.brandName = brandName;
    }

    public String getContentUrl() {
        return contentUrl;
    }

    public void setContentUrl(String contentUrl) {
        this.contentUrl = contentUrl;
    }

    public String getThumbUrl() {
        return thumbUrl;
    }

    public void setThumbUrl(String thumbUrl) {
        this.thumbUrl = thumbUrl;
    }

    public Integer getOrgPrice() {
        return orgPrice;
    }

    public void setOrgPrice(Integer orgPrice) {
        this.orgPrice = orgPrice;
    }

    public Integer getSalePrice() {
        return salePrice;
    }

    public void setSalePrice(Integer salePrice) {
        this.salePrice = salePrice;
    }

    public Float getSalePer() {
        return salePer;
    }

    public void setSalePer(Float salePer) {
        this.salePer = salePer;
    }

    public String getCpName() {
        return cpName;
    }

    public void setCpName(String cpName) {
        this.cpName = cpName;
    }

    public Integer getDataId() {
        return dataId;
    }

    public void setDataId(Integer dataId) {
        this.dataId = dataId;
    }

    public String getCrawlKeyword() {
        return crawlKeyword;
    }

    public void setCrawlKeyword(String crawlKeyword) {
        this.crawlKeyword = crawlKeyword;
    }

    public boolean isbMan() {
        return bMan;
    }

    public void setbMan(boolean bMan) {
        this.bMan = bMan;
    }

    public boolean isbWoman() {
        return bWoman;
    }

    public void setbWoman(boolean bWoman) {
        this.bWoman = bWoman;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDataStatus() {
        return dataStatus;
    }

    public void setDataStatus(String dataStatus) {
        this.dataStatus = dataStatus;
    }
}
