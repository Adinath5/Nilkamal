package com.atharvainfo.nilkamal.model;

public class feedreqmodel {
    String prodno;
    String prodname;
    String prodqty;

    public feedreqmodel(String prodno, String prodname, String prodqty){
        this.prodno = prodno;
        this.prodname = prodname;
        this.prodqty = prodqty;

    }

    public String getProdname() {
        return prodname;
    }

    public String getProdno() {
        return prodno;
    }

    public String getProdqty() {
        return prodqty;
    }

    public void setProdname(String prodname) {
        this.prodname = prodname;
    }

    public void setProdno(String prodno) {
        this.prodno = prodno;
    }

    public void setProdqty(String prodqty) {
        this.prodqty = prodqty;
    }
}
