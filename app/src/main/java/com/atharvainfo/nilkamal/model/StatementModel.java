package com.atharvainfo.nilkamal.model;

public class StatementModel {
    public String invoicedate,invoiceno, invoicetype, invdesc, invdramount,invcramount,invrnbal, invchallanno;

    public StatementModel(String invoicedate, String invoiceno, String invdesc, String invdramount, String invcramount, String invrnbal, String invoicetype, String invchallanno){
        this.invoicedate = invoicedate;
        this.invoiceno = invoiceno;
        this.invdesc = invdesc;
        this.invdramount = invdramount;
        this.invcramount = invcramount;
        this.invrnbal = invrnbal;
        this.invoicetype = invoicetype;
        this.invchallanno = invchallanno;
    }

    public String getInvoicedate() {
        return invoicedate;
    }

    public void setInvoicedate(String invoicedate) {
        this.invoicedate = invoicedate;
    }

    public String getInvoiceno() {
        return invoiceno;
    }

    public void setInvoiceno(String invoiceno) {
        this.invoiceno = invoiceno;
    }

    public String getInvdesc() {
        return invdesc;
    }

    public void setInvdesc(String invdesc) {
        this.invdesc = invdesc;
    }

    public String getInvdramount() {
        return invdramount;
    }

    public void setInvdramount(String invdramount) {
        this.invdramount = invdramount;
    }

    public String getInvcramount() {
        return invcramount;
    }

    public void setInvcramount(String invcramount) {
        this.invcramount = invcramount;
    }

    public String getInvrnbal() {
        return invrnbal;
    }

    public void setInvrnbal(String invrnbal) {
        this.invrnbal = invrnbal;
    }

    public String getInvoicetype() {
        return invoicetype;
    }

    public void setInvoicetype(String invoicetype) {
        this.invoicetype = invoicetype;
    }

    public String getInvchallanno() {
        return invchallanno;
    }

    public void setInvchallanno(String invchallanno) {
        this.invchallanno = invchallanno;
    }

}
