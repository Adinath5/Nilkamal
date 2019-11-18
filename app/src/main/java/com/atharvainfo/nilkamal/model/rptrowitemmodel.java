package com.atharvainfo.nilkamal.model;

public class rptrowitemmodel implements Comparable<rptrowitemmodel>{
    private  boolean isSectionHeader;
    private String rptname;
    private String rptheadername;


    public rptrowitemmodel(String rptname, String rptheadername){
        this.rptname = rptname;
        this.rptheadername = rptheadername;
        isSectionHeader = false;
    }

    public String getRptname() {
        return rptname;
    }

    public void setRptname(String rptname) {
        this.rptname = rptname;
    }

    public boolean isSectionHeader() {
        return isSectionHeader;
    }

    public void setSectionHeader(boolean sectionHeader) {
        isSectionHeader = sectionHeader;
    }

    @Override
    public int compareTo(rptrowitemmodel o) {
        return 0;
    }

    public String getRptheadername() {
        return rptheadername;
    }

    public void setRptheadername(String rptheadername) {
        this.rptheadername = rptheadername;
    }
    public void setToSectionHeader(){
        isSectionHeader = true;
    }
}
