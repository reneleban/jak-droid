package de.codecamps.jakdroid.auth;

public interface ServerAuthenticate {
    String userSignUp(final String name, final String pass, final String authTokenType) throws Exception;
    String userSignIn(final String name, final String pass, final String authTokenType) throws Exception;
}
