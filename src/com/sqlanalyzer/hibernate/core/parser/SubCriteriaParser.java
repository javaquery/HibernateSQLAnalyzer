/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sqlanalyzer.hibernate.core.parser;

import com.sqlanalyzer.hibernate.core.CriteriaHolder;
import com.sqlanalyzer.hibernate.util.CriteriaUtil;
import java.util.Iterator;
import org.hibernate.criterion.Criterion;
import org.hibernate.engine.spi.TypedValue;
import org.hibernate.internal.CriteriaImpl.Subcriteria;

/**
 * Parse criteria's expression (sub/inner - where condition).
 *
 * @git #6
 * @author vicky.thakor
 * @since v2.2
 */
public class SubCriteriaParser implements CriteriaParser {

    @Override
    public void parse(CriteriaHolder criteriaHolder) {
        Iterator<Subcriteria> iterator = criteriaHolder.getCriteriaImpl().iterateSubcriteria();
        while (iterator.hasNext()) {
            Subcriteria criterionEntry = iterator.next();
            Criterion criterion = criterionEntry.getWithClause();

            if (criterion != null) {
                TypedValue[] typedValues = criterion.getTypedValues(criteriaHolder.getCriteria(), criteriaHolder.getCriteriaQuery());

                String expression = criterion.toSqlString(criteriaHolder.getCriteria(), criteriaHolder.getCriteriaQuery());
                String expressionImpl = expression;
                for (TypedValue typedValue : typedValues) {
                    expressionImpl = CriteriaUtil.replaceParameterValue(typedValue, expressionImpl);
                }
                expression = CriteriaUtil.replaceSpecialChar(expression);
                String query = criteriaHolder.getSqlQuery().replaceFirst(expression, expressionImpl);
                criteriaHolder.setSqlQuery(query);
            }
        }
    }
}
