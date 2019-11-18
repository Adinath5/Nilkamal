package com.atharvainfo.nilkamal.model;

public class pitemmodel {
    String prodno;
    String prodname;
    String weight;
    String quantity;
    String netquantity;
    String rateinctax;
    String amountinctax;
    String rateexetax;
    String amountexctax;
    String mfgcomp;
    String prodtype;
    String dcno;
    String taxperc;
    String taxamount;
    String mrp;
    String gname;
    String packing;
    public pitemmodel(String prodno, String prodname, String weight, String quantity, String netquantity, String rateinctax, String amountinctax, String rateexetax,
                      String amountexctax, String mfgcomp, String prodtype, String dcno, String taxperc, String taxamount, String mrp, String gname, String packing){
        this.prodno = prodno;
        this.prodname = prodname;
        this.weight = weight;
        this.quantity = quantity;
        this.netquantity = netquantity;
        this.rateinctax= rateinctax;
        this.amountinctax = amountinctax;
        this.rateexetax = rateexetax;
        this.amountexctax = amountexctax;
        this.mfgcomp = mfgcomp;
        this.prodtype = prodtype;
        this.dcno = dcno;
        this.taxperc = taxperc;
        this.taxamount = taxamount;
        this.mrp = mrp;
        this.gname = gname;
        this.packing = packing;
    }

    public void setProdno(String prodno) {
        this.prodno = prodno;
    }

    public void setProdname(String prodname) {
        this.prodname = prodname;
    }

    public String getProdno() {
        return prodno;
    }

    public String getProdname() {
        return prodname;
    }

    public String getNetquantity() {
        return netquantity;
    }

    public String getQuantity() {
        return quantity;
    }

    public String getWeight() {
        return weight;
    }

    public String getAmountinctax() {
        return amountinctax;
    }

    public String getRateinctax() {
        return rateinctax;
    }

    public void setNetquantity(String netquantity) {
        this.netquantity = netquantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getRateexetax() {
        return rateexetax;
    }

    public void setRateinctax(String rateinctax) {
        this.rateinctax = rateinctax;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public void setAmountinctax(String amountinctax) {
        this.amountinctax = amountinctax;
    }

    public void setRateexetax(String rateexetax) {
        this.rateexetax = rateexetax;
    }

    public String getAmountexctax() {
        return amountexctax;
    }

    public String getDcno() {
        return dcno;
    }

    public void setAmountexctax(String amountexctax) {
        this.amountexctax = amountexctax;
    }

    public String getGname() {
        return gname;
    }

    public String getMfgcomp() {
        return mfgcomp;
    }

    public String getMrp() {
        return mrp;
    }

    public String getPacking() {
        return packing;
    }

    public void setMfgcomp(String mfgcomp) {
        this.mfgcomp = mfgcomp;
    }

    public String getProdtype() {
        return prodtype;
    }

    public String getTaxamount() {
        return taxamount;
    }

    public String getTaxperc() {
        return taxperc;
    }

    public void setDcno(String dcno) {
        this.dcno = dcno;
    }

    public void setGname(String gname) {
        this.gname = gname;
    }

    public void setProdtype(String prodtype) {
        this.prodtype = prodtype;
    }

    public void setMrp(String mrp) {
        this.mrp = mrp;
    }

    public void setPacking(String packing) {
        this.packing = packing;
    }

    public void setTaxamount(String taxamount) {
        this.taxamount = taxamount;
    }

    public void setTaxperc(String taxperc) {
        this.taxperc = taxperc;
    }

}
