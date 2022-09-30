package sqlancer.ydb;

import sqlancer.common.query.ExpectedErrors;

public class YdbCommon {

    public static void addCommonTypeCastErrors(ExpectedErrors errors) {
        errors.add("Mismatch type argument #1, source type: String?, target type: String");
        errors.add("S_ERROR");
    }

    public static void addCommonParseErrors(ExpectedErrors errors) {
        errors.add("Failed to parse string literal: Invalid octal value");
        errors.add("Unexpected character");
    }

}
