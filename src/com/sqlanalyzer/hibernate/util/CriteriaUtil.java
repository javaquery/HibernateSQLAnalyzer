/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sqlanalyzer.hibernate.util;

import java.sql.Timestamp;
import java.util.Date;
import org.hibernate.engine.spi.TypedValue;
import org.hibernate.type.BooleanType;
import org.hibernate.type.CustomType;
import org.hibernate.type.StringType;
import org.hibernate.type.TimestampType;

/**
 * Criteria utility class.
 *
 * @author vicky.thakor
 * @since v2.2
 */
public class CriteriaUtil {

    /**
     * Replace `?` with real value.
     *
     * @param typedValue
     * @param query
     * @return
     */
    public static String replaceParameterValue(TypedValue typedValue, String query) {
        if (typedValue != null && query != null) {
            Object value = typedValue.getValue();
            if (value == null) {
                // @git #5
                query = query.replace("?", String.valueOf(value));
            } else {
                if (typedValue.getType() instanceof StringType
                        || typedValue.getType() instanceof CustomType) {
                    String strValue = String.valueOf(value);
                    strValue = strValue.replace("'", "''");
                    query = query.replace("?", "\'" + strValue + "\'");
                } else if (typedValue.getType() instanceof TimestampType) {
                    Date date = (Date) value;
                    Timestamp timestamp = new Timestamp(date.getTime());
                    query = query.replace("?", "\'" + timestamp + "\'");
                } else if (typedValue.getType() instanceof BooleanType) {
                    int intValue = (Boolean) value ? 1 : 0;
                    query = query.replace("?", String.valueOf(intValue));
                } else {
                    query = query.replaceFirst("\\?", String.valueOf(value));
                }
            }
        }
        return query;
    }

    /**
     * Get String replaced with special character to use in
     * String.replaceFirst() as regular expression.
     *
     * @return
     */
    public static String replaceSpecialChar(String str) {
        str = str.replace("(", "\\(");
        str = str.replace(")", "\\)");
        str = str.replace("?", "\\?");
        str = str.replace(",", "\\,");
        return str;
    }
}
