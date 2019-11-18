package com.atharvainfo.nilkamal.model;

public class spvmodellist {
    private String spname, spvdate,spopkm, spclkm;
    private int opimage;
    private int climage;

    public spvmodellist(String spname, String spvdate, String spopkm, String spclkm, int op_image, int cl_image){
        this.spname = spname;
        this.spvdate = spvdate;
        this.spopkm = spopkm;
        this.spclkm = spclkm;
        this.opimage = op_image;
        this.climage = cl_image;

    }

    public String getSpname() {
        return spname;
    }

    public void setSpname(String spname) {
        this.spname = spname;
    }


    public String getSpopkm() {
        return spopkm;
    }

    public String getSpclkm() {
        return spclkm;
    }

    public int getOp_image() {
        return opimage;
    }

    public int getCl_image() {
        return climage;
    }


}
