package de.codecamps.jakdroid.auth;

public class AccountGeneral {
    public static final String ACCOUNT_TYPE = "de.codecamps.jak";
    public static final String ACCOUNT_NAME = "JAK Droid";

    public static final String AUTHTOKEN_TYPE_FULL_ACCESS = "Full Access";
    public static final String AUTHTOKEN_TYPE_FULL_ACCESS_LABEL = "Full Access to a JAK Account";

    public static final ServerAuthenticate sServerAuthenticate = new JAKDroidServerAuthenticate();
}
