package com.koala.takeout.common;

public class Constants {
    public static final int ORDER_PENDING_PAY = 1;
    public static final int ORDER_PENDING_ACCEPT = 2;
    public static final int ORDER_DELIVERING = 3;
    public static final int ORDER_COMPLETED = 4;
    public static final int ORDER_CANCELLED = 5;
    public static final int ORDER_REFUNDED = 6;

    public static final int STATUS_ENABLE = 1;
    public static final int STATUS_DISABLE = 0;

    public static final String ROLE_ADMIN = "admin";
    public static final String ROLE_MERCHANT = "merchant";
    public static final String ROLE_USER = "user";
}
