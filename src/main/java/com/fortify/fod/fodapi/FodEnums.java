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

    public enum AuditPreferenceTypes {
        Manual(1),
        Automated(2);

        private final int _val;

        AuditPreferenceTypes(int val) {
            this._val = val;
        }

        public int getValue() {
            return this._val;
        }

        public String toString() {
            switch (this._val) {
                case 1:
                    return "Manual";
                case 2:
                default:
                    return "Automated";
            }
        }

        public static AuditPreferenceTypes fromInt(int val) {
            switch (val) {

                case 1:
                    return Manual;
                case 2:
                default:
                    return Automated;
            }
        }
    }

    public enum EntitlementPreferenceType {
        SingleScanOnly(1),
        SubscriptionOnly(2),
        SingleScanFirstThenSubscription(3),
        SubscriptionFirstThenSingleScan(4) ;

        private final int _val;

        EntitlementPreferenceType(int val) {
            this._val = val;
        }
        public int getValue() {
            return this._val;
        }

        public String toString() {
            switch (this._val) {
                case 1:
                    return "SingleScanOnly";
                case 2:
                    return "SubscriptionOnly";
                case 3:
                    return "SingleScanFirstThenSubscription";
                case 4:
                default:
                    return "SubscriptionFirstThenSingleScan";
            }
        }

        public static EntitlementPreferenceType fromInt(int val) {
            switch (val) {
                case 1:
                    return SingleScanOnly;
                case 2:
                    return SubscriptionOnly;
                case 3:
                    return SingleScanFirstThenSubscription ;
                case 4:
                    return SubscriptionFirstThenSingleScan;
                default:
                    return null;
            }
        }
    }

    public enum InProgressScanActionType {
       DoNotStartScan(0),
       CancelScanInProgress(1),
       Queue(2);

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
                case 2:
                    return "Queue";
                case 0:
                default:
                    return "DoNotStartScan";
            }
        }

        public static InProgressScanActionType fromInt(int val) {
            switch (val) {
                case 2:
                    return Queue;
                case 1:
                    return CancelScanInProgress;
                case 0:
                default:
                    return DoNotStartScan;
            }
        }
    }

    public enum EntitlementFrequencyTypes {
        SingleScan(1),
        Subscription(2);

        private final int _val;

        EntitlementFrequencyTypes(int val) {
            this._val = val;
        }

        public int getValue() {
            return this._val;
        }

        public String toString() {
            switch (this._val) {
                case 1:
                    return "SingleScan";
                case 2:
                default:
                    return "Subscription";
            }
        }

        public static EntitlementFrequencyTypes fromInt(int val) {
            switch (val) {

                case 1:
                    return SingleScan;
                case 2:
                default:
                    return Subscription;
            }
        }
    }
}