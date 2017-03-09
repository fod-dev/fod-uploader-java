package com.fortify.fod.parser;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

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
    public Proxy(List<String> args) {
        try {
            final int URI_LOCATION = 0;
            final int USERNAME_LOCATION = 1;
            final int PASSWORD_LOCATION = 2;
            final int NTDOMAIN_LOCATION = 3;
            final int NTWORKSTATION_LOCATION = 4;

            proxyUri = args.size() > URI_LOCATION ? new URI(args.get(URI_LOCATION)) : null;
            username = args.size() > USERNAME_LOCATION && args.get(USERNAME_LOCATION) != null
                    ? args.get(USERNAME_LOCATION) : null;
            password = args.size() > PASSWORD_LOCATION && args.get(PASSWORD_LOCATION) != null
                    ? args.get(PASSWORD_LOCATION) : null;
            ntDomain = args.size() > NTDOMAIN_LOCATION && args.get(NTDOMAIN_LOCATION) != null
                    ? args.get(NTDOMAIN_LOCATION) : null;
            ntWorkstation = args.size() > NTWORKSTATION_LOCATION && args.get(NTWORKSTATION_LOCATION) != null
                    ? args.get(NTWORKSTATION_LOCATION) : null;

        } catch(URISyntaxException | ArrayIndexOutOfBoundsException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }

    public URI getProxyUri() {
        return proxyUri;
    }

    public String getUsername() {
        return username;
    }
    public boolean hasUsername() { return username != null && !username.isEmpty(); }

    public String getPassword() {
        return password;
    }
    public boolean hasPassword() { return password != null && !password.isEmpty(); }

    public String getNTDomain() {
        return ntDomain;
    }
    public boolean hasNTDomain() { return ntDomain != null && !ntDomain.isEmpty(); }

    public String getNTWorkstation() {
        return ntWorkstation;
    }
    public boolean hasNTWorkstation() { return ntWorkstation!= null && !ntWorkstation.isEmpty(); }
}
