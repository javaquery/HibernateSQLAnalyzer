/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sqlanalyzer.hibernate.core.parser;

import com.sqlanalyzer.hibernate.core.CriteriaHolder;
import com.sqlanalyzer.hibernate.util.HibernateDialect;

/**
 * Parse criteria for Oracle.
 *
 * @author vicky.thakor
 * @since v2.2
 */
public class OracleCriteriaParser implements CriteriaParser {

    @Override
    public void parse(CriteriaHolder criteriaHolder) {
        if (HibernateDialect.ORACLE_DIALECT.toString().equalsIgnoreCase(criteriaHolder.getDialect())
                || HibernateDialect.ORACLE_9_DIALECT.toString().equalsIgnoreCase(criteriaHolder.getDialect())) {
            if (criteriaHolder.getCriteriaImpl().getMaxResults() != null) {
                String query = criteriaHolder.getSqlQuery().replace("rownum <= ?", "rownum <= " + criteriaHolder.getCriteriaImpl().getMaxResults());
                criteriaHolder.setSqlQuery(query);
            }
        }
    }
}
