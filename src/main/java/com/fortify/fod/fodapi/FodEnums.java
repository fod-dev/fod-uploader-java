package com.fortify.fod.fodapi;

public class FodEnums {

    public enum APILookupItemTypes {
        All,
        MobileScanPlatformTypes,
        MobileScanFrameworkTypes,
        MobileScanEnvironmentTypes,
        MobileScanRoleTypes,
        MobileScanExternalDeviceTypes,
        DynamicScanEnvironmentFacingTypes,
        DynamicScanAuthenticationTypes,
        TimeZones,
        RepeatScheduleTypes,
        GeoLocations,
        SDLCStatusTypes,
        DayOfWeekTypes,
        BusinessCriticalityTypes,
        ReportTemplateTypes,
        AnalysisStatusTypes,
        ScanStatusTypes,
        ReportFormats,
        Roles,
        ScanPreferenceTypes,
        AuditPreferenceTypes,
        EntitlementFrequencyTypes,
        ApplicationTypes,
        ScanTypes,
        AttributeTypes,
        AttributeDataTypes,
        MultiFactorAuthorizationTypes,
        ReportTypes,
        ReportStatusTypes,
        PassFailReasonTypes,
        DynamicScanWebServiceTypes
    }

    public enum RemediationScanPreferenceType {
        RemediationScanIfAvailable(0),
        RemediationScanOnly(1),
        NonRemediationScanOnly(2);

        private final int _val;

        RemediationScanPreferenceType(int val) {
            this._val = val;
        }

        public int getValue() {
            return this._val;
        }

        public String toString() {
            switch (this._val) {
                case 0:
                    return "RemediationScanIfAvailable";
                case 1:
                    return "RemediationScanOnly";
                case 2:
                default:
                    return "NonRemediationScanOnly";
            }
        }

        public static RemediationScanPreferenceType fromInt(int val) {
            switch (val) {

                case 1:
                    return RemediationScanOnly;
                case 0:
                    return RemediationScanIfAvailable;
                case 2:
                default:
                    return NonRemediationScanOnly;
            }
        }
    }

    public enum EntitlementPreferenceType {
        SingleScanOnly(0),
        SubscriptionOnly(1),
        SingleScanFirstThenSubscription(2),
        SubscriptionFirstThenSingleScan(3) ;

        private final int _val;

        EntitlementPreferenceType(int val) {
            this._val = val;
        }

        public int getValue() {
            return this._val;
        }

        public String toString() {
            switch (this._val) {
                case 3:
                    return "SubscriptionFirstThenSingleScan";
                case 2:
                    return "SingleScanFirstThenSubscription";
                case 1:
                    return "SubscriptionOnly";
                case 0:
                default:
                    return "SingleScanOnly";
            }
        }

        public static EntitlementPreferenceType fromInt(int val) {
            switch (val) {
                case 3:
                    return SubscriptionFirstThenSingleScan;
                case 2:
                    return SingleScanFirstThenSubscription ;
                case 1:
                    return SubscriptionOnly;
                case 0:
                    return SingleScanOnly;
                default:
                    return null;
            }
        }
    }

    public enum InProgressScanActionType {
        DoNotStartScan(0),
        CancelScanInProgress(1);

        private final int _val;

        InProgressScanActionType(int val) {
            this._val = val;
        }

        public int getValue() {
            return this._val;
        }

        public String toString() {
            switch (this._val) {
                 case 1:
                    return "CancelInProgressScan";
                case 0:
                default:
                    return "DoNotStartScan";
            }
        }

        public static InProgressScanActionType fromInt(int val) {
            switch (val) {
                case 1:
                    return CancelScanInProgress;
                case 0:
                    return DoNotStartScan;
                default:
                    return null;
            }
        }
    }
}
