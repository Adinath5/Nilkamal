package com.atharvainfo.nilkamal.Others;

public class myConfig {
    //AppVersion
    public static String APP_VERSION = "1.2";
    public static final String API_KEY = "teamatharvainfoisthebest";
    /* APP Setting */
    public static boolean IS_DEVELOPMENT = true; // set false, your app is production

    public static final String Base_url ="https://agrosoftprime.co.in/primebook/nilkamalpoultry/";
    public static final String Parent_Url ="https://agrosoftprime.co.in/primebook/nilkamalpoultry/";

    public static String URL_LOGIN=Base_url +"api.php?apicall=signup";
    public static String URL_BIOMETRICLOGIN=Base_url +"api.php?apicall=signupuserbiometric";
    public static String URL_GETUSERNAME=Base_url +"api.php?apicall=getUserNameFromMobile";
    public static String URL_REGISTERNEWUSER=Base_url +"api.php?apicall=registerNewUserPass";
    public static String URl_CONFIRMUSEROTP=Base_url +"api.php?apicall=confirmuserotp";
    public static String URl_SPFARMLIST=Base_url +"api.php?apicall=getOpenFarmList";
    public static String URl_FARMLISTSP=Base_url +"api.php?apicall=getSpFarmList";
    public static String URl_GETFINISHSTOCKPRODUCTLISTDATA=Base_url +"api.php?apicall=getFinishStockProductListData";
    public static String URl_FARMLISTALL=Base_url +"api.php?apicall=getAllFarmList";
    public static String URl_LAYERFARMLISTSP=Base_url +"api.php?apicall=getLayerFarmList";
    public static String URl_GETUSERDATA=Base_url +"api.php?apicall=GetUserDetail";
    public static String URl_NEWFARMENQ=Base_url +"api.php?apicall=saveNewFarm";
    public static String URl_FEEDPRODLIST=Base_url +"api.php?apicall=getFeedProduct";
    public static String URl_GETFARMVISITDATA=Base_url +"api.php?apicall=getSpFarmVisit";
    public static String URL_GETFARMBIRDSUPPLY = Base_url +"api.php?apicall=getSpFarmDetailone";
    public static String URL_GETFARMDETAILS = Base_url +"api.php?apicall=getSpFarmDetailtwo";
    public static String URL_SENDFARMNOTIFICATION = Base_url +"api.php?apicall=sendFireBaseMessageUser";
    public static String URL_GETUSERNOTIFICATION = Base_url +"api.php?apicall=getUserNotification";
    public static String URL_GETVISITENTRYNO = Base_url +"api.php?apicall=getSpDailyVisitEntryNo";
    public static String URL_GETLAYERFARMVISITENTRYNO = Base_url +"api.php?apicall=getLayerFConsDailyEntryNo";
    public static String URL_GETLAYERFARMDETAILDATA = Base_url +"api.php?apicall=getLayerFarmDetailData";
    public static String URL_GETFARMBALANCEFEED = Base_url +"api.php?apicall=getSpFarmBalanceFeed";
    public static String URL_GETFEEDMILLEFEESTOCK = Base_url +"api.php?apicall=getFeedMillFeedStock";
    public static String URL_GETFEEDPRODUCTLIST = Base_url +"api.php?apicall=getFeedProductNameList";
    public static String URL_GETFEEDPRODUCTIONPROCESS = Base_url +"api.php?apicall=getProductFormulaRawProduct";
    public static String URL_GETFEEDPRODUCTIONQUANTITY = Base_url +"api.php?apicall=getTempProductionTotalBag";
    public static String URL_SAVEFEEDPRODUCTIONENTRY = Base_url +"api.php?apicall=SaveFeedProductionEntry";
    public static String URL_CHECKFEEDENTRYESISTS = Base_url +"api.php?apicall=getSpFarmEntryExists";
    public static String URL_GETFEEDSTOCK = Base_url +"api.php?apicall=getSpFarmProductStock";
    public static String URL_GETFEEDMILLFEEDSTOCK = Base_url +"api.php?apicall=getFeedMillProductStock";
    public static String URL_SAVEFCENTRY = Base_url +"api.php?apicall=SaveFeedConsEntry";
    public static String URL_GETMORTREASON = Base_url +"api.php?apicall=getMortalityReason";
    public static String URL_GETFEEDCONENTRY= Base_url+"api.php?apicall=getFeedConsumptionEntry";
    public static String URL_GETLAYERFEEDCONENTRY= Base_url+"api.php?apicall=getLayerFeedConsumptionEntry";
    public static String URL_GETLAYEREGGSPRODENTRY= Base_url+"api.php?apicall=getLayerEggsProductionEntry";
    public static String URL_GETCHICKSMORTENTRY= Base_url+"api.php?apicall=getChiksMortaliltyEntry";
    public static String URL_GETLAYERBIRDSMORTENTRY= Base_url+"api.php?apicall=getLayerBirdMortaliltyEntry";
    public static String URL_SAVEMORTENTRY = Base_url +"api.php?apicall=SaveMortalityEntry";
    public static String URL_SAVEEGGSPRODENTRY = Base_url +"api.php?apicall=SaveLayerEggsProductionEntry";
    public static String URL_SAVEDAILYENTRYLAST = Base_url +"api.php?apicall=SaveDailyEntryLast";
    public static String URL_GETALLSPLIST = Base_url +"api.php?apicall=getAllSpList";
    public static String URL_GETUSERLOCATIONLIST = Base_url +"api.php?apicall=getSpLocationList";
    public static String URL_GETALLLEDGERLIST = Base_url +"api.php?apicall=getAllLedgerWithBalance";
    public static String URL_GETALLACCOUNTLIST = Base_url +"api.php?apicall=getAllAccountWithBalance";
    public static String URL_GETRCPYBALANCE = Base_url +"api.php?apicall=getReceivableBalanceParty";
    public static String URL_GETTRANSACTODAYS = Base_url +"api.php?apicall=getTodaysVoucherList";
    public static String URL_GETSALEPURCHASETOTALADMIN = Base_url +"api.php?apicall=getTotalSalePurchaseAdmin";

    public static String URl_FARMLISTMANAGER=Base_url +"api.php?apicall=getManagerFarmList";
    public static String URl_FARMLISTADMIN=Base_url +"api.php?apicall=getFarmListAll";

    public static String URl_FARMMORTLISTADMIN=Base_url +"api.php?apicall=getFarmMortalityListH";
    public static String URl_FARMFEEDCONSADMIN=Base_url +"api.php?apicall=getFarmFeedConsListH";
    public static String URl_FARMVISITLISTADMIN=Base_url +"api.php?apicall=AdminReportSpDailyVisitList";
    public static String URl_SPVISITLISTADMIN=Base_url +"api.php?apicall=AdminReportSpDailyVisit";

    public static String URl_ADMINGETCASHBAL=Base_url +"api.php?apicall=AdminReportCashInHand";
    public static String URl_SALESINVOICEPRINT=Base_url +"api.php?apicall=getSalesInvoiceData";
    public static String URl_GETACCOUNTLEDGERDETAIL=Base_url +"api.php?apicall=getAccountLedgerDetail";
    public static String URl_GETACCOUNTDETAILLEDGER=Base_url +"api.php?apicall=getAccountDetailLedger";
    public static String URl_GETACCOUNTSTATEMENT=Base_url +"api.php?apicall=getLedgerStatement";
    public static String URl_GETBRANCHDATALIST=Base_url +"api.php?apicall=getBranchData";
    public static String URl_SAVECASHPAYMENTENTRY=Base_url +"api.php?apicall=SaveCashPaymentVoucherEntry";
    public static String URl_SAVECASHRECEIPTENTRY=Base_url +"api.php?apicall=SaveCashReceiptVoucherEntry";
    public static String URl_GETSALESREGISTERLIST=Base_url +"api.php?apicall=getSalesRegisterData";
    public static String URl_GETPURCHASEREGISTERLIST=Base_url +"api.php?apicall=getPurchaseRegisterData";
    public static String URl_GETRCPAYLEDGERLIST=Base_url +"api.php?apicall=getReceivablePayableLedgerList";
    public static String URl_GETCASHRECEIPTVOUCHERDATA=Base_url +"api.php?apicall=getCashReceiptVoucher";
    public static String URl_GETCASHPAYMENTVOUCHERDATA=Base_url +"api.php?apicall=getCashPaymentVoucher";
    public static String URl_GETSPFEEDPHOTOFARM=Base_url +"api.php?apicall=getFeedPhotoSupervisor";
    public static String URl_GETSPMORTPHOTOFARM=Base_url +"api.php?apicall=getMortPhotoSupervisor";
    public static String URl_GETSPOPKMPHOTOFARM=Base_url +"api.php?apicall=getOpKmPhotoSupervisor";
    public static String URl_GETSPCLKMPHOTOFARM=Base_url +"api.php?apicall=getClKmPhotoSupervisor";
    public static String URl_GETPRODUCTLISTALL=Base_url +"api.php?apicall=getProductMastData";

    public static String URL_GETLEDGERNAMELIST=Base_url +"api.php?apicall=getLedgerWithBalance";
    public static String URL_PRODUCTNAMELIST=Base_url +"api.php?apicall=getProductNameList";
    public static String URL_GETPRODUCTITEMDETAILS=Base_url +"api.php?apicall=getProductItemDetails";
    public static String URL_GETSALEVOUCHERNODETAILS=Base_url +"api.php?apicall=getSaleVoucherNoDetails";
    public static String URL_MODIFYSALEINVOICEPRODUCT=Base_url +"api.php?apicall=ModifySaleInvoiceProduct";
    public static String URL_MODIFYSALEINVOICE=Base_url +"api.php?apicall=ModifySaveSaleInvoice";
    public static String URL_REMOVESALEINVOICEPRODUCT=Base_url +"api.php?apicall=RemoveSalesProduct";
    public static String URL_REMOVESALERETURNINVOICEPRODUCT=Base_url +"api.php?apicall=RemoveSaleReurnProduct";
    public static String URL_SAVESALEINVOICE=Base_url +"api.php?apicall=SaveSaleInvoiceMast";
    public static String URL_SAVESALEINVOICEPRODUCT=Base_url +"api.php?apicall=SaveSaleInvoiceProduct";
    public static String URL_GETLEDGERACCOUNTDETAIL=Base_url +"api.php?apicall=getAccountLedgerDetail";
    public static String URL_ROUTENAMELIST=Base_url +"api.php?apicall=getRouteNameList";
    public static String URL_SAVEPURCHASEINVOICEPRODUCT=Base_url +"api.php?apicall=SavePurchaseInvoiceProduct";
    public static String URL_SAVEPURCHASEINVOICE=Base_url +"api.php?apicall=SavePurchaseInvoiceMast";
    public static String URL_REMOVEPURCHASEINVOICEPRODUCT=Base_url +"api.php?apicall=RemovePurchaseProduct";
    public static String URL_SAVENEWPARTY=Base_url +"api.php?apicall=SaveLedgerName";
    public static String URL_CHECKACCOUNTEXIST=Base_url +"api.php?apicall=checkAccountExist";
    public static String URL_RPTCASHBOOKVOUCHERADMIN=Base_url +"api.php?apicall=getCashBookVoucherAdmin";
    public static String URL_ADMINFEEDCONSUMTIONRE=Base_url +"api.php?apicall=getDailyFeedConsumtionReport";
    public static String URL_GETALLFARMLIST=Base_url +"api.php?apicall=getFarmName";
    public static String URL_GETFARMDETAILSUPLCLR=Base_url +"api.php?apicall=getFarmNameDetailsData";
    public static String URL_GETSAVEFEEDSUPPLYENTRY=Base_url +"api.php?apicall=SaveFeedSupplyVoucherEntry";
    public static String URL_GETSAVEFEEDREQVOUCHER=Base_url +"api.php?apicall=savefeedrequirementvoucher";
    public static final String TOPIC_GLOBAL = "global";

    // broadcast receiver intent filters
    public static final String REGISTRATION_COMPLETE = "registrationComplete";
    public static final String PUSH_NOTIFICATION = "pushNotification";

    // id to handle the notification in the notification tray
    public static final int NOTIFICATION_ID = 100;
    public static final int NOTIFICATION_ID_BIG_IMAGE = 101;

    public static final String SHARED_PREF = "ah_firebase";

}
