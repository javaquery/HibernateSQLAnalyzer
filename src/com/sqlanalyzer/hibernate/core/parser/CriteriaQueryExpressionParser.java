/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sqlanalyzer.hibernate.core.parser;

import com.sqlanalyzer.hibernate.core.HibernateCriteriaHolder;
import com.sqlanalyzer.hibernate.util.CriteriaUtil;
import java.util.Iterator;
import org.hibernate.criterion.Criterion;
import org.hibernate.engine.spi.TypedValue;
import org.hibernate.internal.CriteriaImpl;

/**
 * Parse criteria's expression (where condition).
 *
 * @author vicky.thakor
 * @since v2.2
 */
public class CriteriaQueryExpressionParser implements CriteriaQueryParser {

    @Override
    public void parse(HibernateCriteriaHolder criteriaHolder) {
        /* Get all expression of Query(i.e `where` condition) */
        Iterator<CriteriaImpl.CriterionEntry> iterator = criteriaHolder.getCriteriaImpl().iterateExpressionEntries();
        while (iterator.hasNext()) {
            CriteriaImpl.CriterionEntry criterionEntry = iterator.next();
            Criterion criterion = criterionEntry.getCriterion();
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
