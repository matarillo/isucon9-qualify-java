package isucon9.qualify;

import java.time.Duration;

public class Const {
    public static final String DefaultPaymentServiceURL = "http://localhost:5555";
    public static final String DefaultShipmentServiceURL = "http://localhost:7000";

    public static final int ItemMinPrice = 100;
    public static final int ItemMaxPrice = 1000000;
    public static final String ItemPriceErrMsg = "商品価格は100ｲｽｺｲﾝ以上、1,000,000ｲｽｺｲﾝ以下にしてください";

    public static final String ItemStatusOnSale = "on_sale";
    public static final String ItemStatusTrading = "trading";
    public static final String ItemStatusSoldOut = "sold_out";
    public static final String ItemStatusStop = "stop";
    public static final String ItemStatusCancel = "cancel";

    public static final String PaymentServiceIsucariApiKey = "a15400e46c83635eb181-946abb51ff26a868317c";
    public static final String PaymentServiceIsucariShopId = "11";

    public static final String TransactionEvidenceStatusWaitShipping = "wait_shipping";
    public static final String TransactionEvidenceStatusWaitDone = "wait_done";
    public static final String TransactionEvidenceStatusDone = "done";

    public static final String ShippingsStatusInitial = "initial";
    public static final String ShippingsStatusWaitPickup = "wait_pickup";
    public static final String ShippingsStatusShipping = "shipping";
    public static final String ShippingsStatusDone = "done";

    public static final Duration BumpChargeSeconds = Duration.ofSeconds(3);

    public static final int ItemsPerPage = 48;
    public static final int TransactionsPerPage = 10;

    public static final int BcryptCost = 10;
}
