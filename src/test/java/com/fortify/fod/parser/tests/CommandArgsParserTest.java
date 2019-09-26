package com.fortify.fod.parser.tests;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.beust.jcommander.JCommander;
import com.fortify.fod.fodapi.FodEnums;
import com.fortify.fod.parser.FortifyCommands;
import com.fortify.fod.parser.Proxy;
import org.junit.jupiter.api.Test;
import com.fortify.fod.parser.converters.BsiTokenConverter;
import com.fortify.fod.parser.BsiToken;

class CommandArgsParserTest {

    @Test
    void testParseCommandArgs() {
        final String[] args = new String[]{
                "-ac", "key", "secret",
                "-z", "C:\\_Payloads_and_FPRs\\Static\\Payloads\\java\\10JavaDefects.zip",
                "-ep", "SubscriptionOnly",
                "-rp", "NonRemediationScanOnly",
                "-pp", "DoNotStartScan",
                "-bsiUrl", "http://16.103.234.237/bsi2.aspx?tid=1&tc=Tenant1&pv=5765&payloadType=ANALYSIS_PAYLOAD&astid=55&ts=JAVA%2fJ2EE&ll=1.6",

        };

        FortifyCommands fc = new FortifyCommands();
        JCommander jc = new JCommander(fc);
        jc.parse(args);
        BsiToken bsiToken = new BsiTokenConverter().convert(fc.bsiToken);

        assertEquals(FodEnums.EntitlementPreferenceType.SubscriptionOnly, fc.entitlementPreference);
        assertEquals(1, bsiToken.getTenantId());
        assertEquals(5765, bsiToken.getProjectVersionId());
        assertEquals("http://16.103.234.237", bsiToken.getApiUri());

        assertEquals("Tenant1", bsiToken.getTenantCode());
        assertEquals(2, fc.apiCredentials.size());
        assertEquals("key", fc.apiCredentials.get(0));
        assertEquals("secret", fc.apiCredentials.get(1));
    }

    @Test
    void testValueOverrideArgsIncludeThirdPartyLibsFromCommandArg() {

        final String[] args = new String[]{
                "-ac", "key", "secret",
                "-z", "C:\\_Payloads_and_FPRs\\Static\\Payloads\\java\\10JavaDefects.zip",
                "-ep", "SubscriptionOnly",
                "-rp", "NonRemediationScanOnly",
                "-pp", "DoNotStartScan",
                "-bsiToken", "eyJ0ZW5hbnRJZCI6MSwidGVuYW50Q29kZSI6IlRlbmFudDEiLCJyZWxlYXNlSWQiOjMzMiwicGF5bG9hZFR5cGUiOiJBTkFMWVNJU19QQVlMT0FEIiwiYXNzZXNzbWVudFR5cGVJZCI6NSwidGVjaG5vbG9neVR5cGUiOiIuTkVUIiwidGVjaG5vbG9neVR5cGVJZCI6MSwidGVjaG5vbG9neVZlcnNpb24iOiI0LjciLCJ0ZWNobm9sb2d5VmVyc2lvbklkIjoxNiwiYXVkaXRQcmVmZXJlbmNlIjoiQXV0b21hdGVkIiwiYXVkaXRQcmVmZXJlbmNlSWQiOjIsImluY2x1ZGVUaGlyZFBhcnR5IjpmYWxzZSwiaW5jbHVkZU9wZW5Tb3VyY2VBbmFseXNpcyI6ZmFsc2UsInNjYW5QcmVmZXJlbmNlIjoiU3RhbmRhcmQiLCJzY2FuUHJlZmVyZW5jZUlkIjoxLCJwb3J0YWxVcmkiOiJodHRwOi8vZm9kLmxvY2FsaG9zdCIsImFwaVVyaSI6IiJ9",

        };

        FortifyCommands fc = new FortifyCommands();
        JCommander jc = new JCommander(fc);
        jc.parse(args);

    }

    @Test
    void testValueOverrideArgsIncludeThirdPartyLibsFromToken() {

        final String[] args = new String[]{
                "-ac", "key", "secret",
                "-z", "C:\\_Payloads_and_FPRs\\Static\\Payloads\\java\\10JavaDefects.zip",
                "-ep", "SubscriptionOnly",
                "-rp", "NonRemediationScanOnly",
                "-pp", "DoNotStartScan",
                "-bsiToken", "eyJ0ZW5hbnRJZCI6MSwidGVuYW50Q29kZSI6IlRlbmFudDEiLCJyZWxlYXNlSWQiOjMzMiwicGF5bG9hZFR5cGUiOiJBTkFMWVNJU19QQVlMT0FEIiwiYXNzZXNzbWVudFR5cGVJZCI6NSwidGVjaG5vbG9neVR5cGUiOiIuTkVUIiwidGVjaG5vbG9neVR5cGVJZCI6MSwidGVjaG5vbG9neVZlcnNpb24iOiI0LjciLCJ0ZWNobm9sb2d5VmVyc2lvbklkIjoxNiwiYXVkaXRQcmVmZXJlbmNlIjoiQXV0b21hdGVkIiwiYXVkaXRQcmVmZXJlbmNlSWQiOjIsImluY2x1ZGVUaGlyZFBhcnR5Ijp0cnVlLCJpbmNsdWRlT3BlblNvdXJjZUFuYWx5c2lzIjpmYWxzZSwic2NhblByZWZlcmVuY2UiOiJTdGFuZGFyZCIsInNjYW5QcmVmZXJlbmNlSWQiOjEsInBvcnRhbFVyaSI6Imh0dHA6Ly9mb2QubG9jYWxob3N0IiwiYXBpVXJpIjoiIn0"
        };

        FortifyCommands fc = new FortifyCommands();
        JCommander jc = new JCommander(fc);
        jc.parse(args);

    }

    @Test
    void testValueOverrideArgsDoNotIncludeThirdPartyLibs() {

        final String[] args = new String[]{
                "-ac", "key", "secret",
                "-z", "C:\\_Payloads_and_FPRs\\Static\\Payloads\\java\\10JavaDefects.zip",
                "-ep", "SubscriptionOnly",
                "-rp", "NonRemediationScanOnly",
                "-pp", "DoNotStartScan",
                "-bsiToken", "eyJ0ZW5hbnRJZCI6MSwidGVuYW50Q29kZSI6IlRlbmFudDEiLCJyZWxlYXNlSWQiOjMzMiwicGF5bG9hZFR5cGUiOiJBTkFMWVNJU19QQVlMT0FEIiwiYXNzZXNzbWVudFR5cGVJZCI6NSwidGVjaG5vbG9neVR5cGUiOiIuTkVUIiwidGVjaG5vbG9neVR5cGVJZCI6MSwidGVjaG5vbG9neVZlcnNpb24iOiI0LjciLCJ0ZWNobm9sb2d5VmVyc2lvbklkIjoxNiwiYXVkaXRQcmVmZXJlbmNlIjoiQXV0b21hdGVkIiwiYXVkaXRQcmVmZXJlbmNlSWQiOjIsImluY2x1ZGVUaGlyZFBhcnR5IjpmYWxzZSwiaW5jbHVkZU9wZW5Tb3VyY2VBbmFseXNpcyI6ZmFsc2UsInNjYW5QcmVmZXJlbmNlIjoiU3RhbmRhcmQiLCJzY2FuUHJlZmVyZW5jZUlkIjoxLCJwb3J0YWxVcmkiOiJodHRwOi8vZm9kLmxvY2FsaG9zdCIsImFwaVVyaSI6IiJ9"
        };

        FortifyCommands fc = new FortifyCommands();
        JCommander jc = new JCommander(fc);
        jc.parse(args);


    }

    @Test
    void testParseProxy() {
        final String[] args = new String[]{
                "-ac", "key", "secret",
                "-z", "C:\\_Payloads_and_FPRs\\Static\\Payloads\\java\\10JavaDefects.zip",
                "-ep", "SubscriptionOnly",
                "-rp", "NonRemediationScanOnly",
                "-pp", "DoNotStartScan",
                "-bsiToken", "eyJ0ZW5hbnRJZCI6MSwidGVuYW50Q29kZSI6IlRlbmFudDEiLCJyZWxlYXNlSWQiOjMzMiwicGF5bG9hZFR5cGUiOiJBTkFMWVNJU19QQVlMT0FEIiwiYXNzZXNzbWVudFR5cGVJZCI6NSwidGVjaG5vbG9neVR5cGUiOiIuTkVUIiwidGVjaG5vbG9neVR5cGVJZCI6MSwidGVjaG5vbG9neVZlcnNpb24iOiI0LjciLCJ0ZWNobm9sb2d5VmVyc2lvbklkIjoxNiwiYXVkaXRQcmVmZXJlbmNlIjoiQXV0b21hdGVkIiwiYXVkaXRQcmVmZXJlbmNlSWQiOjIsImluY2x1ZGVUaGlyZFBhcnR5IjpmYWxzZSwiaW5jbHVkZU9wZW5Tb3VyY2VBbmFseXNpcyI6ZmFsc2UsInNjYW5QcmVmZXJlbmNlIjoiU3RhbmRhcmQiLCJzY2FuUHJlZmVyZW5jZUlkIjoxLCJwb3J0YWxVcmkiOiJodHRwOi8vZm9kLmxvY2FsaG9zdCIsImFwaVVyaSI6IiJ9",
                "-P", "location", "username", "password"
        };

        FortifyCommands fc = new FortifyCommands();
        JCommander jc = new JCommander(fc);
        jc.parse(args);
        Proxy proxy = new Proxy(fc.proxy);

        //assertEquals("location", proxy.getProxyUri());
        assertEquals("username", proxy.getUsername());
        assertEquals("password", proxy.getPassword());
    }
}
