package com.fortify.fod.parser;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

public class BsiUrl {
    private int tenantId;
    private String tenantCode;
    private int projectVersionId;
    private String payloadType;
    private int assessmentTypeId;
    private String technologyStack;
    private String languageLevel = "";
    private String endpoint;

    /**
     * Creates a bsi url object.
     * @param bsiUrl build server url
     */
    BsiUrl(String bsiUrl) {
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
                    tenantId = Integer.parseInt(param.getValue());
                    break;
                case "tc":
                    tenantCode = param.getValue();
                    break;
                case "pv":
                    projectVersionId = Integer.parseInt(param.getValue());
                    break;
                case "ts":
                    technologyStack = param.getValue();
                    break;
                case "ll":
                    languageLevel = param.getValue();
                    break;
                case "astid":
                    assessmentTypeId = Integer.parseInt(param.getValue());
                    break;
                case "payloadType":
                    payloadType = param.getValue();
                    break;
            }
        }
    }

    public int getTenantId() {
        return tenantId;
    }
    public boolean hasTenantId() {
        return tenantId != 0;
    }

    public String getTenantCode() {
        return tenantCode;
    }
    public boolean hasTenantCode() {
        return !tenantCode.isEmpty();
    }

    public int getProjectVersionId() {
        return projectVersionId;
    }
    public boolean hasProjectVersionId() {
        return projectVersionId != 0;
    }

    public String getPayloadType() {
        return payloadType;
    }
    public boolean hasPayloadType() {
        return !payloadType.isEmpty();
    }

    public int getAssessmentTypeId() {
        return assessmentTypeId;
    }
    public boolean hasAssessmentTypeId() {
        return assessmentTypeId != 0;
    }

    public String getTechnologyStack() {
        return technologyStack;
    }
    public boolean hasTechnologyStack() {
        return !technologyStack.isEmpty();
    }

    public String getLanguageLevel() {
        return languageLevel;
    }
    public boolean hasLanguageLevel() {
        return !languageLevel.isEmpty();
    }

    public String getEndpoint() {
        return endpoint;
    }
    public boolean hasEndpoint() {
        return !endpoint.isEmpty();
    }
}
