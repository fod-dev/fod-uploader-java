package com.fortify.fod.parser.legacy;

import com.fortify.fod.parser.FortifyParser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Code for supporting legacy arguments predicated on an "-l" tag.
 * Please note that this class is deprecated. Below is some info on it all
 * arg 0: username
 * arg 1: password
 * arg 2: url
 * arg 3: zip location
 * arg 4: proxy url
 * arg 5: proxy username
 * arg 6: proxy password
 * arg 7: nt workstation
 * arg 8: nt domain
 * need to parse urls like this:
 * http://www.fod.local/bsi2.aspx?tid=1&tc=tt0@qweqwe.com&pv=187&payloadType=ANALYSIS_PAYLOAD&astid=1&ts=JAVA/J2EE&ll=1.7
 */
public class LegacyParser {
    private Map<String, String> argsMap;

    public LegacyParser(String[] args) {
        argsMap = parseArgs(args);
    }

    private static Map<String, String> parseArgs(String[] args) {

        Map<String,String> result = new HashMap<>();
        result.put("username", args[0]);
        result.put("password", args[1]);
        result.put("bsiUrl", args[2]);
        result.put("zipLocation",args[3]);
        result.put("entitlementId", args[4]);
        result.put("entitlementFrequency", args[5]);
        processOptionalArgs(args,result);

        return result;
    }

    private static void processOptionalArgs(String[] args, Map<String, String> result)
    {
        ArrayList<String> unnamedArgs = removeNamedOptionalArgs(args,result);
        if(unnamedArgs.size() >= 7)
        {
            result.put("proxy",unnamedArgs.get(4));
            if(unnamedArgs.size() >= 9)
            {
                result.put("proxyUsername",unnamedArgs.get(5));
                result.put("proxyPassword",unnamedArgs.get(6));
                if(unnamedArgs.size() == 11)
                {
                    result.put("ntWorkStation",unnamedArgs.get(7));
                    result.put("ntDomain",unnamedArgs.get(8));
                }
            }
        }

    }

    private static ArrayList<String> removeNamedOptionalArgs(String[] args, Map<String,String> argMap )
    {
        ArrayList<String> result = new ArrayList<>();
        for(String arg : args)
        {
            if(arg.startsWith("-" + FortifyParser.POLLING_INTERVAL))
            {
                String[] split = arg.split(":");
                if(split.length == 2)
                {
                    argMap.put(FortifyParser.POLLING_INTERVAL, split[1]);
                }
            }
            else if(arg.startsWith("-" + FortifyParser.SCAN_PREFERENCE_ID))
            {
                String[] split = arg.split(":");
                if(split.length == 2)
                {
                    argMap.put(FortifyParser.SCAN_PREFERENCE_ID, split[1]);
                }
            }
            else if(arg.startsWith("-" + FortifyParser.AUDIT_PREFERENCE_ID))
            {
                String[] split = arg.split(":");
                if(split.length == 2)
                {
                    argMap.put(FortifyParser.AUDIT_PREFERENCE_ID, split[1]);
                }
            }
            else if(arg.startsWith("-" + FortifyParser.RUN_SONATYPE_SCAN))
            {
                String[] split = arg.split(":");
                if(split.length == 2)
                {
                    argMap.put(FortifyParser.RUN_SONATYPE_SCAN, split[1]);
                }
            }
            else if(arg.startsWith("-" + FortifyParser.EXCLUDE_THIRD_PARTY_LIBS))
            {
                String[] split = arg.split(":");
                if(split.length == 2) {
                    argMap.put(FortifyParser.EXCLUDE_THIRD_PARTY_LIBS, split[1]);
                }
            }
            else if(arg.startsWith("-" + FortifyParser.IS_REMEDIATION_SCAN))
            {
                String[] split = arg.split(":");
                if(split.length == 2)
                {
                    argMap.put(FortifyParser.IS_REMEDIATION_SCAN, split[1]);
                }
            }
            else  // unnamed argument
            {
                result.add(arg);
            }
        }
        return result;
    }

    public Map<String, String> getArgsMap() {
        return argsMap;
    }
}
