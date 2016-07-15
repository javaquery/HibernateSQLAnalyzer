/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sqlanalyzer.hibernate.core;

import com.sqlanalyzer.hibernate.exception.HibernateSQLAnalyzerException;
import com.sqlanalyzer.hibernate.util.Constants;
import com.sqlanalyzer.hibernate.util.HibernateDialect;
import java.lang.reflect.Field;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Iterator;
import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.CriteriaQuery;
import org.hibernate.criterion.Criterion;
import org.hibernate.engine.SessionFactoryImplementor;
import org.hibernate.engine.TypedValue;
import org.hibernate.impl.CriteriaImpl;
import org.hibernate.impl.CriteriaImpl.CriterionEntry;
import org.hibernate.impl.SessionFactoryImpl;
import org.hibernate.impl.SessionImpl;
import org.hibernate.loader.OuterJoinLoader;
import org.hibernate.loader.criteria.CriteriaLoader;
import org.hibernate.loader.criteria.CriteriaQueryTranslator;
import org.hibernate.persister.entity.OuterJoinLoadable;
import org.hibernate.type.BooleanType;
import org.hibernate.type.CustomType;
import org.hibernate.type.StringType;
import org.hibernate.type.TimestampType;

/**
 * Hibernate Criteria processor class. 
 * You can use it independently by calling constructor.
 * @author vicky.thakor
 * @date 8th July, 2016
 */
public class HibernateCriteria {

    private final SessionFactory sessionFactory;
    private final Criteria criteria;
    private final String dialect;

    public HibernateCriteria(SessionFactory sessionFactory, Criteria criteria, String dialect) {
        this.sessionFactory = sessionFactory;
        this.criteria = criteria;
        this.dialect = dialect;
    }

    /**
     * Convert {@link Criteria} to SQLQuery.
     * @return 
     */
    public String criteriaSQLQuery() {
        if (criteria == null) {
            throw new HibernateSQLAnalyzerException(Constants.NULL_CRITERIA, null);
        } else if (dialect == null) {
            throw new HibernateSQLAnalyzerException(Constants.NULL_DIALECT, null);
        } else {
            try {
                CriteriaImpl criteriaImpl = (CriteriaImpl) criteria;
                SessionImpl sessionImpl = (SessionImpl) criteriaImpl.getSession();
                SessionFactoryImplementor sessionFactoryImplementor = (SessionFactoryImplementor) sessionImpl.getSessionFactory();
                String[] implementors = sessionFactoryImplementor.getImplementors(criteriaImpl.getEntityOrClassName());
                CriteriaLoader criteriaLoader = new CriteriaLoader(
                        (OuterJoinLoadable) sessionFactoryImplementor.getEntityPersister(implementors[0]),
                        sessionFactoryImplementor,
                        criteriaImpl,
                        implementors[0],
                        sessionImpl.getLoadQueryInfluencers());
                Field field = OuterJoinLoader.class.getDeclaredField("sql");
                field.setAccessible(true);
                String query = (String) field.get(criteriaLoader);

                if (HibernateDialect.ORACLE_DIALECT.toString().equalsIgnoreCase(dialect)
                        || HibernateDialect.ORACLE_9_DIALECT.toString().equalsIgnoreCase(dialect)) {
                    if (criteriaImpl.getMaxResults() != null) {
                        query = "SELECT * FROM (" + query + ") WHERE ROWNUM <= ?";
                    }
                }
                return query;
            } catch (Exception e) {
                throw new HibernateSQLAnalyzerException(Constants.CRITERIA_TO_QUERY_ERROR, e);
            }
        }
    }

    /**
     * Convert {@link Criteria} to SQLQuery with real values.
     * @return 
     */
    public String criteriaValuedSQLQuery() {
        String query = criteriaSQLQuery();
        try {
            CriteriaImpl criteriaImpl = (CriteriaImpl) criteria;
            String entityName = criteriaImpl.getEntityOrClassName();
            CriteriaQuery criteriaQuery = new CriteriaQueryTranslator((SessionFactoryImpl) sessionFactory, criteriaImpl, entityName, "this_");

            if (criteriaImpl.getMaxResults() != null) {
                if (HibernateDialect.SQL_SERVER_DIALECT.toString().equalsIgnoreCase(dialect)) {
                    query = query.replace("select", "select top " + criteriaImpl.getMaxResults());
                } else if (HibernateDialect.MYSQL_DIALECT.toString().equalsIgnoreCase(dialect)
                        || HibernateDialect.MYSQL_INNODB_DIALECT.toString().equalsIgnoreCase(dialect)
                        || HibernateDialect.MYSQL_MYISAM_DIALECT.toString().equalsIgnoreCase(dialect)
                        || HibernateDialect.POSTGRE_SQL_DIALECT.toString().equalsIgnoreCase(dialect)) {
                    query = query + " limit " + criteriaImpl.getMaxResults();
                } else if (HibernateDialect.ORACLE_DIALECT.toString().equalsIgnoreCase(dialect)
                        || HibernateDialect.ORACLE_9_DIALECT.toString().equalsIgnoreCase(dialect)) {
                    query = query.replace("rownum <= ?", "rownum <= " + criteriaImpl.getMaxResults());
                }
            }

            /* Get all expression of Query(i.e `where` condition) */
            Iterator<CriterionEntry> iterator = criteriaImpl.iterateExpressionEntries();
            while (iterator.hasNext()) {
                CriterionEntry criterionEntry = iterator.next();
                Criterion criterion = criterionEntry.getCriterion();
                TypedValue[] typedValues = criterion.getTypedValues(criteria, criteriaQuery);

                String expression = criterion.toSqlString(criteria, criteriaQuery);
                String expressionImpl = expression;
                for (TypedValue typedValue : typedValues) {
                    expressionImpl = replaceParameterValue(typedValue, expressionImpl);
                }
                expression = replaceSpecialChar(expression);
                query = query.replaceFirst(expression, expressionImpl);
            }
        } catch (Exception e) {
            throw new HibernateSQLAnalyzerException(Constants.CRITERIA_TO_VALUED_QUERY_ERROR, e);
        }
        return query;
    }

    /**
     * Replace `?` with real value.
     *
     * @param typedValue
     * @param query
     * @return
     */
    private String replaceParameterValue(TypedValue typedValue, String query) {
        if (typedValue != null && query != null) {
            if (typedValue.getType() instanceof StringType
                    || typedValue.getType() instanceof CustomType) {
                String value = typedValue.getValue().toString();
                value = value.replace("'", "''");
                query = query.replace("?", "\'" + value + "\'");
            } else if (typedValue.getType() instanceof TimestampType) {
                Date date = (Date) typedValue.getValue();
                Timestamp timestamp = new Timestamp(date.getTime());
                query = query.replace("?", "\'" + timestamp + "\'");
            } else if (typedValue.getType() instanceof BooleanType) {
                int value = (Boolean) typedValue.getValue() ? 1 : 0;
                query = query.replace("?", String.valueOf(value));
            } else {
                query = query.replaceFirst("\\?", String.valueOf(typedValue.getValue()));
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
    private String replaceSpecialChar(String str) {
        str = str.replace("(", "\\(");
        str = str.replace(")", "\\)");
        str = str.replace("?", "\\?");
        str = str.replace(",", "\\,");
        return str;
    }
}
