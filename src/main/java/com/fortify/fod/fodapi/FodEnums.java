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

    public enum ScanPreferenceType {
        Standard(1),
        Express(2);

        private final int _val;

        ScanPreferenceType(int val) {
            this._val = val;
        }

        public int getValue() {
            return this._val;
        }

        public String toString() {
            switch (this._val) {
                case 2:
                    return "Express";
                case 1:
                default:
                    return "Standard";
            }
        }

        public static ScanPreferenceType fromInt(int val) {
            switch (val) {
                case 2:
                    return Express;
                case 1:
                    return Standard;
                default:
                    return null;
            }
        }
    }

    public enum AuditPreferenceType {
        Manual(1),
        Automated(2);

        private final int _val;

        AuditPreferenceType(int val) {
            this._val = val;
        }

        public int getValue() {
            return this._val;
        }

        public String toString() {
            switch (this._val) {
                case 2:
                    return "Automated";
                case 1:
                default:
                    return "Manual";
            }
        }

        public static AuditPreferenceType fromInt(int val) {
            switch (val) {
                case 2:
                    return Automated;
                case 1:
                    return Manual;
                default:
                    return null;
            }
        }
    }

    public enum EntitlementPreferenceType {
        SingleScan(1),
        Subscription(2);

        private final int _val;

        EntitlementPreferenceType(int val) {
            this._val = val;
        }

        public int getValue() {
            return this._val;
        }

        public String toString() {
            switch (this._val) {
                case 2:
                    return "Subscription";
                case 1:
                default:
                    return "Single Scan";
            }
        }

        public static EntitlementPreferenceType fromInt(int val) {
            switch (val) {
                case 2:
                    return Subscription;
                case 1:
                    return SingleScan;
                default:
                    return null;
            }
        }
    }
}
