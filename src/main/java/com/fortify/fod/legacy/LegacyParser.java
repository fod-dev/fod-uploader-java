package com.fortify.fod.legacy;

import java.net.MalformedURLException;
import java.net.URL;
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
@Deprecated
class LegacyParser {
    private Map<String, String> argsMap;

    LegacyParser(String[] args) {
        argsMap = parseArgs(args);
    }

    private static Map<String, String> parseArgs(String[] args) {

        Map<String,String> result = new HashMap<>();
        result.put("username", args[0]);
        result.put("password", args[1]);
        String urlString = args[2];
        result.put("zipLocation",args[3]);
        processOptionalArgs(args,result);
        URL url;
        try {
            url = new URL(urlString);
            result.put("endpoint",url.getProtocol() + "://" + url.getAuthority());
            String query = url.getQuery();
            String[] queryNVPairs = query.split("&");
            Map<String,String> queryMap = new HashMap<>();
            for(String pair : queryNVPairs)
            {
                String[] nvPair = pair.split("=");
                if(nvPair.length == 2)
                {
                    queryMap.put(nvPair[0], nvPair[1]);
                }
                else
                {
                    queryMap.put(nvPair[0], "");
                }
            }
            result.put("releaseId", queryMap.get("pv"));
            result.put("technologyType", queryMap.get("ts"));
            result.put("assessmentTypeId", queryMap.get("astid"));
            result.put("tenantId", queryMap.get("tid"));
            result.put("tenantCode", queryMap.get("tc"));
            result.put("languageLevel", queryMap.get("ll"));
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return result;
    }

    private static void processOptionalArgs(String[] args, Map<String, String> result)
    {
        ArrayList<String> unnamedArgs = removeNamedOptionalArgs(args,result);
        if(unnamedArgs.size() >= 5)
        {
            result.put("proxy",unnamedArgs.get(4));
            if(unnamedArgs.size() >= 7)
            {
                result.put("proxyUsername",unnamedArgs.get(5));
                result.put("proxyPassword",unnamedArgs.get(6));
                if(unnamedArgs.size() == 9)
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
            if(arg.startsWith("-pollingInterval"))
            {
                String[] split = arg.split(":");
                if(split.length == 2)
                {
                    argMap.put("pollingInterval", split[1]);
                }
            }
            else if(arg.startsWith("-scanPreferenceId"))
            {
                String[] split = arg.split(":");
                if(split.length == 2)
                {
                    argMap.put("scanPreferenceId", split[1]);
                }
            }
            else if(arg.startsWith("-auditPreferenceId"))
            {
                String[] split = arg.split(":");
                if(split.length == 2)
                {
                    argMap.put("auditPreferenceId", split[1]);
                }
            }
            else if(arg.startsWith("-runSonatypeScan"))
            {
                String[] split = arg.split(":");
                if(split.length == 2)
                {
                    argMap.put("runSonatypeScan", split[1]);
                }
            }
            else  // unnamed argument
            {
                result.add(arg);
            }
        }
        return result;
    }

    @Deprecated
    Map<String, String> getArgsMap() {
        return argsMap;
    }
}
