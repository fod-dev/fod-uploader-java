package com.fortify.fod;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

public class BsiUrl {
    private String tenantId;
    private String tenantCode;
    private String projectVersionId;
    private String payloadType;
    private String assessmentTypeId;
    private String technologyStack;
    private String languageLevel;
    private String endpoint;

    //TODO: proxy (proxy, proxyUsername, proxyPassword, ntWorkStation, ntDomain)

    /**
     * Creates a bsi url object.
     * @param bsiUrl
     * @throws URISyntaxException
     */
    public BsiUrl(String bsiUrl) {
        try {
            URI uri = new URI(bsiUrl);

            endpoint = uri.getScheme() + "://" + uri.getAuthority();
            List<NameValuePair> params = URLEncodedUtils.parse(uri, "UTF-8");

            createBsiUrl(params);
        } catch(URISyntaxException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }

    /**
     * Map URL params to strongly-typed object.
     * Note: these param names could totally change in the future.
     * @param params list of params parsed from the given bsi url String
     */
    private void createBsiUrl(List<NameValuePair> params) {
        for(NameValuePair param : params) {
            switch(param.getName()) {
                case "tid":
                    tenantId = param.getValue();
                    break;
                case "tc":
                    tenantCode = param.getValue();
                    break;
                case "pv":
                    projectVersionId = param.getValue();
                    break;
                case "ts":
                    technologyStack = param.getValue();
                    break;
                case "ll":
                    languageLevel = param.getValue();
                    break;
                case "astid":
                    assessmentTypeId = param.getValue();
                    break;
                case "payloadType":
                    payloadType = param.getValue();
                    break;
            }
        }
    }

    public String getTenantId() {
        return tenantId;
    }

    public String getTenantCode() {
        return tenantCode;
    }

    public String getProjectVersionId() {
        return projectVersionId;
    }

    public String getPayloadType() {
        return payloadType;
    }

    public String getAssessmentTypeId() {
        return assessmentTypeId;
    }

    public String getTechnologyStack() {
        return technologyStack;
    }

    public String getLanguageLevel() {
        return languageLevel;
    }

    public String getEndpoint() {
        return endpoint;
    }
}
