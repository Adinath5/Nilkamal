package com.atharvainfo.nilkamal.Adapter;

public class TranAdapter {
    public String invoicetype,invoiceno,ledgername,invoicedate,invoiceamount;

    public TranAdapter(String invoicetype, String invoiceno, String ledgername, String invoicedate, String invoiceamount){
        this.invoicetype = invoicetype;
        this.invoiceno = invoiceno;
        this.ledgername = ledgername;
        this.invoicedate = invoicedate;
        this.invoiceamount = invoiceamount;
    }

    public String getInvoicetype() {
        return invoicetype;
    }

    public void setInvoicetype(String invoicetype) {
        this.invoicetype = invoicetype;
    }

    public String getInvoiceno() {
        return invoiceno;
    }

    public void setInvoiceno(String invoiceno) {
        this.invoiceno = invoiceno;
    }

    public String getLedgername() {
        return ledgername;
    }

    public void setLedgername(String ledgername) {
        this.ledgername = ledgername;
    }

    public String getInvoicedate() {
        return invoicedate;
    }

    public void setInvoicedate(String invoicedate) {
        this.invoicedate = invoicedate;
    }

    public String getInvoiceamount() {
        return invoiceamount;
    }

    public void setInvoiceamount(String invoiceamount) {
        this.invoiceamount = invoiceamount;
    }
}
