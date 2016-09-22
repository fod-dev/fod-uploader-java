package com.fortify.fod.parser;

import java.net.URI;
import java.net.URISyntaxException;

public class Proxy {
    private URI proxyUri;
    private String username;
    private String password;
    private String ntDomain;
    private String ntWorkstation;

    /**
     * Creates a Proxy object from -proxy option
     * @param args array of up to 5 arguments for configuring proxy.
     */
    Proxy(String[] args) {
        try {
            final int URI_LOCATION = 0;
            final int USERNAME_LOCATION = 1;
            final int PASSWORD_LOCATION = 2;
            final int NTDOMAIN_LOCATION = 3;
            final int NTWORKSTATION_LOCATION = 4;

            proxyUri =      args.length > URI_LOCATION              ? new URI(args[URI_LOCATION])   : null;
            username =      args.length > USERNAME_LOCATION         ? args[USERNAME_LOCATION]       : null;
            password =      args.length > PASSWORD_LOCATION         ? args[PASSWORD_LOCATION]       : null;
            ntDomain =      args.length > NTDOMAIN_LOCATION         ? args[NTDOMAIN_LOCATION]       : null;
            ntWorkstation = args.length > NTWORKSTATION_LOCATION    ? args[NTWORKSTATION_LOCATION]  : null;

        } catch(URISyntaxException | ArrayIndexOutOfBoundsException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }

    public URI getProxyUri() {
        return proxyUri;
    }
    public boolean hasProxyUri() {
        return proxyUri != null;
    }

    public String getUsername() {
        return username;
    }
    public boolean hasUsername() {
        return !username.isEmpty();
    }

    public String getPassword() {
        return password;
    }
    public boolean hasPassword() {
        return !password.isEmpty();
    }

    public String getNTDomain() {
        return ntDomain;
    }
    public boolean hasNTDomain() {
        return !ntDomain.isEmpty();
    }

    public String getNTWorkstation() {
        return ntWorkstation;
    }
    public boolean hasNTWorkstation() {
        return !ntWorkstation.isEmpty();
    }
}
