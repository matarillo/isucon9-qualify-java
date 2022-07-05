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

    public static final Duration BumpChargeSeconds = Duration.ofSeconds(3);

    public static final int ItemsPerPage = 48;
    public static final int TransactionsPerPage = 10;
}
