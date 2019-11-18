package com.atharvainfo.nilkamal.model;

public class farmlistmodel {
    String farmcode;
    String farmname;
    String farmaddress;
    String farmbatch;
    String placeqty;
    String feedsqty;
    String feedcqty;
    String bmortqty;
    String bsalqty;

    public  farmlistmodel(String farmcode, String farmname, String farmaddress, String farmbatch, String placeqty, String feedsqty, String feedcqty,
                          String bmortqty, String bsalqty){
        this.farmcode = farmcode;
        this.farmname = farmname;
        this.farmaddress = farmaddress;
        this.farmbatch = farmbatch;
        this.placeqty = placeqty;
        this.feedsqty = feedsqty;
        this.feedcqty = feedcqty;
        this.bmortqty = bmortqty;
        this.bsalqty = bsalqty;
    }

    public String getFarmaddress() {
        return farmaddress;
    }

    public String getFarmbatch() {
        return farmbatch;
    }

    public String getFarmcode() {
        return farmcode;
    }

    public String getFarmname() {
        return farmname;
    }

    public void setFarmaddress(String farmaddress) {
        this.farmaddress = farmaddress;
    }

    public void setFarmbatch(String farmbatch) {
        this.farmbatch = farmbatch;
    }

    public void setFarmcode(String farmcode) {
        this.farmcode = farmcode;
    }

    public void setFarmname(String farmname) {
        this.farmname = farmname;
    }

    public String getPlaceqty() {
        return placeqty;
    }

    public void setPlaceqty(String placeqty) {
        this.placeqty = placeqty;
    }

    public String getBmortqty() {
        return bmortqty;
    }

    public String getBsalqty() {
        return bsalqty;
    }

    public String getFeedcqty() {
        return feedcqty;
    }

    public String getFeedsqty() {
        return feedsqty;
    }

    public void setBmortqty(String bmortqty) {
        this.bmortqty = bmortqty;
    }

    public void setBsalqty(String bsalqty) {
        this.bsalqty = bsalqty;
    }

    public void setFeedcqty(String feedcqty) {
        this.feedcqty = feedcqty;
    }

    public void setFeedsqty(String feedsqty) {
        this.feedsqty = feedsqty;
    }

}
