package com.fortify.fod.parser.tests;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.beust.jcommander.JCommander;
import com.fortify.fod.fodapi.FodEnums;
import com.fortify.fod.parser.FortifyCommands;
import org.junit.jupiter.api.Test;

class CommandArgsParserTest {

    @Test
    void parseCommandArgs1() {
        final String[] args = new String[]{
                "-ac", "key", "secret",
                "-z", "C:\\_Payloads_and_FPRs\\Static\\Payloads\\java\\10JavaDefects.zip",
                "-ep", "SingleScan",
                "-bsiUrl", "http://16.103.234.237/bsi2.aspx?tid=1&tc=Tenant1&pv=5765&payloadType=ANALYSIS_PAYLOAD&astid=55&ts=JAVA%2fJ2EE&ll=1.6",
                "-itp"
        };

        FortifyCommands fc = new FortifyCommands();
        JCommander jc = new JCommander(fc);
        jc.parse(args);

        assertEquals(true, fc.includeThirdPartyLibs);
        assertEquals(FodEnums.EntitlementPreferenceType.SingleScan, fc.entitlementPreference);
        assertEquals(1, fc.bsiToken.getTenantId());
        assertEquals(5765, fc.bsiToken.getProjectVersionId());
        assertEquals("http://16.103.234.237", fc.bsiToken.getApiUri());
        assertEquals("ANALYSIS_PAYLOAD", fc.bsiToken.getPayloadType());
        assertEquals("Tenant1", fc.bsiToken.getTenantCode());
        assertEquals(2, fc.apiCredentials.size());
        assertEquals("key", fc.apiCredentials.get(0));
        assertEquals("secret", fc.apiCredentials.get(1));
    }
}
