package com.atharvainfo.nilkamal.model;

public class feeditemmodel {
    String prodno;
    String prodname;
    String pqty;
    String prate;
    String pamount;
    String pmfgcomp;
    String pcat;
    String ppack;
    String ptotalamt;

    public feeditemmodel(String prodno, String prodname, String pqty, String prate, String pamount, String pmfgcomp, String pcat,
                         String ppack, String ptotalamt) {
        this.prodno = prodno;
        this.prodname = prodname;
        this.pqty = pqty;
        this.prate = prate;
        this.pamount = pamount;
        this.pmfgcomp = pmfgcomp;
        this.pcat = pcat;
        this.ppack = ppack;
        this.ptotalamt = ptotalamt;
    }

    public String getPamount() {
        return pamount;
    }

    public String getPqty() {
        return pqty;
    }

    public String getProdname() {
        return prodname;
    }

    public String getPrate() {
        return prate;
    }

    public String getProdno() {
        return prodno;
    }

    public void setPamount(String pamount) {
        this.pamount = pamount;
    }

    public void setPqty(String pqty) {
        this.pqty = pqty;
    }

    public void setPrate(String prate) {
        this.prate = prate;
    }

    public String getPcat() {
        return pcat;
    }

    public void setProdname(String prodname) {
        this.prodname = prodname;
    }

    public String getPmfgcomp() {
        return pmfgcomp;
    }

    public void setProdno(String prodno) {
        this.prodno = prodno;
    }

    public void setPcat(String pcat) {
        this.pcat = pcat;
    }

    public String getPpack() {
        return ppack;
    }

    public void setPmfgcomp(String pmfgcomp) {
        this.pmfgcomp = pmfgcomp;
    }

    public String getPtotalamt() {
        return ptotalamt;
    }

    public void setPpack(String ppack) {
        this.ppack = ppack;
    }

    public void setPtotalamt(String ptotalamt) {
        this.ptotalamt = ptotalamt;
    }

}
