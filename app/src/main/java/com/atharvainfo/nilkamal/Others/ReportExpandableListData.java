package com.atharvainfo.nilkamal.Others;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ReportExpandableListData {


    public static HashMap<String, List<String>> getData() {
        HashMap<String, List<String>> expandableListDetail = new HashMap<String, List<String>>();

        List<String> Transaction = new ArrayList<String>();
        Transaction.add("Sales Report");
        Transaction.add("Purchase Report");
        Transaction.add("Cash Receipt Report");
        Transaction.add("Cash Payment Report");
        Transaction.add("Bank Cheque Report");
        Transaction.add("Day Book");
        Transaction.add("All Transactions");
        Transaction.add("Profit & Loss");
        Transaction.add("Balance Sheet");

        List<String> feedreport = new ArrayList<String>();
        feedreport.add("Feed Production");
        feedreport.add("Feed Supply To Farm");
        feedreport.add("Feed Transfer Farm To Farm");
        feedreport.add("Feed Balance Report");
        feedreport.add("Farmwise Feed Balance");
        feedreport.add("Farmwise Feed Requirement");
        feedreport.add("Farmwise Feed Consumption");


        List<String> birdreport = new ArrayList<String>();
        birdreport.add("Bird Supply Register");
        birdreport.add("Farm Wise Bird Balance");
        birdreport.add("Farm Wise Bird Mortality");

        List<String> partyreport = new ArrayList<String>();
        partyreport.add("Party Statement");
        partyreport.add("All Party Report");

        List<String> itemstockreport = new ArrayList<String>();
        itemstockreport.add("Stock Summary");
        itemstockreport.add("Product Report By Party");
        itemstockreport.add("Low Stock Summary");
        itemstockreport.add("Stock Detail Report");
        itemstockreport.add("Product Detail Report");

        List<String> farmreport = new ArrayList<String>();
        farmreport.add("Supervisor Daily Visit");
        farmreport.add("Farm History");




        expandableListDetail.put("Transaction", Transaction);
        expandableListDetail.put("Feed Report", feedreport);
        expandableListDetail.put("Bird Report", birdreport);
        expandableListDetail.put("Paty Report", partyreport);
        expandableListDetail.put("Stock Report", itemstockreport);
        expandableListDetail.put("Farm Report", farmreport);


        return expandableListDetail;
    }


}
