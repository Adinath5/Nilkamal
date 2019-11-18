package com.atharvainfo.nilkamal.model;

public class productlist {
    private String prodname;

    public productlist(String prodname){
        this.prodname = prodname;
    }

    public String getProdname(){
        return prodname;
    }

    public void setProdname(String prodname) {
        this.prodname = prodname;
    }
}

