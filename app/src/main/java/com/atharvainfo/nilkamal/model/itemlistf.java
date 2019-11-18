package com.atharvainfo.nilkamal.model;

public class itemlistf {
    String prodno;
    String prodname;
    String mfgcomp;
    String clqty;
    String prodtype;

    public itemlistf(String prodno, String prodname, String mfgcomp, String clqty, String prodtype){
        this.prodno = prodno;
        this.prodname = prodname;
        this.mfgcomp = mfgcomp;
        this.clqty = clqty;
        this.prodtype= prodtype;

    }

    public String getProdname() {
        return prodname;
    }

    public void setProdname(String prodname) {
        this.prodname = prodname;
    }

    public void setProdno(String prodno) {
        this.prodno = prodno;
    }

    public String getProdno() {
        return prodno;
    }

    public String getClqty() {
        return clqty;
    }

    public void setClqty(String clqty) {
        this.clqty = clqty;
    }

    public String getMfgcomp() {
        return mfgcomp;
    }

    public void setMfgcomp(String mfgcomp) {
        this.mfgcomp = mfgcomp;
    }

    public String getProdtype() {
        return prodtype;
    }

    public void setProdtype(String prodtype) {
        this.prodtype = prodtype;
    }

}
