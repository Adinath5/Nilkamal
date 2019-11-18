package com.atharvainfo.nilkamal.model;

public class accmodel {
    String glcode;
    String sledgername;
    String sladdress;
    String acname;
    String sltype;
    String amount;
    String taluka;
    String city;
    String contactperson;
    String phoneno;
    String emailid;
    String contactno;
    String statename;
    String panno;
    String aclimit;
    String routname;
    String dueperiod;
    String acstatus;
    String gsttinno;
    String clbal;
    String drcr1;

    public accmodel(String glcode,String sledgername,String sladdress, String acname, String sltype, String amount, String taluka,String city,
                    String contactperson, String phoneno, String emailid, String contactno, String statename, String panno, String aclimit,
                    String routname, String dueperiod, String acstatus, String gsttinno, String clbal, String drcr1){
        this.glcode = glcode;
        this.sledgername = sledgername;
        this.sladdress = sladdress;
        this.acname = acname;
        this.sltype = sltype;
        this.amount = amount;
        this.taluka = taluka;
        this.city = city;
        this.contactperson = contactperson;
        this.phoneno = phoneno;
        this.emailid = emailid;
        this.contactno = contactno;
        this.statename = statename;
        this.panno = panno;
        this.aclimit = aclimit;
        this.routname= routname;
        this.dueperiod = dueperiod;
        this.acstatus = acstatus;
        this.gsttinno= gsttinno;
        this.clbal = clbal;
        this.drcr1 = drcr1;
    }

    public String getAcname() {
        return acname;
    }

    public String getAmount() {
        return amount;
    }

    public String getGlcode() {
        return glcode;
    }

    public String getCity() {
        return city;
    }

    public String getSladdress() {
        return sladdress;
    }

    public String getContactperson() {
        return contactperson;
    }

    public String getSledgername() {
        return sledgername;
    }

    public String getSltype() {
        return sltype;
    }

    public String getTaluka() {
        return taluka;
    }

    public void setAcname(String acname) {
        this.acname = acname;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void setGlcode(String glcode) {
        this.glcode = glcode;
    }

    public String getContactno() {
        return contactno;
    }

    public void setSladdress(String sladdress) {
        this.sladdress = sladdress;
    }

    public String getEmailid() {
        return emailid;
    }

    public void setContactperson(String contactperson) {
        this.contactperson = contactperson;
    }

    public void setSledgername(String sledgername) {
        this.sledgername = sledgername;
    }

    public String getPhoneno() {
        return phoneno;
    }

    public void setEmailid(String emailid) {
        this.emailid = emailid;
    }

    public void setSltype(String sltype) {
        this.sltype = sltype;
    }

    public void setPhoneno(String phoneno) {
        this.phoneno = phoneno;
    }

    public void setTaluka(String taluka) {
        this.taluka = taluka;
    }

    public void setContactno(String contactno) {
        this.contactno = contactno;
    }

    public String getAclimit() {
        return aclimit;
    }

    public String getPanno() {
        return panno;
    }

    public String getAcstatus() {
        return acstatus;
    }

    public String getClbal() {
        return clbal;
    }

    public String getDrcr1() {
        return drcr1;
    }

    public String getDueperiod() {
        return dueperiod;
    }

    public String getStatename() {
        return statename;
    }

    public String getGsttinno() {
        return gsttinno;
    }

    public String getRoutname() {
        return routname;
    }

    public void setAclimit(String aclimit) {
        this.aclimit = aclimit;
    }

    public void setAcstatus(String acstatus) {
        this.acstatus = acstatus;
    }

    public void setClbal(String clbal) {
        this.clbal = clbal;
    }

    public void setDrcr1(String drcr1) {
        this.drcr1 = drcr1;
    }

    public void setDueperiod(String dueperiod) {
        this.dueperiod = dueperiod;
    }

    public void setGsttinno(String gsttinno) {
        this.gsttinno = gsttinno;
    }

    public void setPanno(String panno) {
        this.panno = panno;
    }

    public void setRoutname(String routname) {
        this.routname = routname;
    }

    public void setStatename(String statename) {
        this.statename = statename;
    }

}
