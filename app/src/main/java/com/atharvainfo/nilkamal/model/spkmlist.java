package com.atharvainfo.nilkamal.model;

public class spkmlist {
    String spname;
    String vdate;
    String spopkm;
    String spclkm;
    String op_image;
    String cl_image;

    public spkmlist(String spname, String vdate, String spopkm, String spclkm, String op_image, String cl_image){
        this.spname = spname;
        this.vdate = vdate;
        this.spopkm = spopkm;
        this.spclkm = spclkm;
        this.op_image= op_image;
        this.cl_image = cl_image;
    }

    public String getSpname() {
        return spname;
    }

    public String getVdate() {
        return vdate;
    }

    public String getSpopkm() {
        return spopkm;
    }

    public String getSpclkm() {
        return spclkm;
    }

    public String getOp_image() {
        return op_image;
    }

    public String getCl_image() {
        return cl_image;
    }

    public void setSpname(String spname) {
        this.spname = spname;
    }

    public void setVdate(String vdate) {
        this.vdate = vdate;
    }

    public void setSpopkm(String spopkm) {
        this.spopkm = spopkm;
    }

    public void setSpclkm(String spclkm) {
        this.spclkm = spclkm;
    }

    public void setCl_image(String cl_image) {
        this.cl_image = cl_image;
    }

    public void setOp_image(String op_image) {
        this.op_image = op_image;
    }
}
