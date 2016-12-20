/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sqlanalyzer.hibernate.core;

import com.sqlanalyzer.hibernate.core.parser.CriteriaExpressionParser;
import com.sqlanalyzer.hibernate.core.parser.CriteriaParser;
import com.sqlanalyzer.hibernate.core.parser.MSSQLCriteriaParser;
import com.sqlanalyzer.hibernate.core.parser.MySQLCriteriaParser;
import com.sqlanalyzer.hibernate.core.parser.OracleCriteriaParser;
import com.sqlanalyzer.hibernate.core.parser.SubCriteriaParser;
import com.sqlanalyzer.hibernate.exception.HibernateSQLAnalyzerException;
import com.sqlanalyzer.hibernate.util.Constants;
import com.sqlanalyzer.hibernate.util.HibernateDialect;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.CriteriaQuery;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.internal.CriteriaImpl;
import org.hibernate.internal.SessionFactoryImpl;
import org.hibernate.internal.SessionImpl;
import org.hibernate.loader.OuterJoinLoader;
import org.hibernate.loader.criteria.CriteriaLoader;
import org.hibernate.loader.criteria.CriteriaQueryTranslator;
import org.hibernate.persister.entity.OuterJoinLoadable;

/**
 * Hibernate Criteria processor class. You can use it independently by calling
 * constructor.
 *
 * @author vicky.thakor
 * @date 8th July, 2016
 */
public class HibernateCriteria {

    private final SessionFactory sessionFactory;
    private final Criteria criteria;
    private final String dialect;
    private final List<CriteriaParser> criteriaParsers = new ArrayList<CriteriaParser>();

    public HibernateCriteria(SessionFactory sessionFactory, Criteria criteria, String dialect) {
        this.sessionFactory = sessionFactory;
        this.criteria = criteria;
        this.dialect = dialect;
    }

    /**
     * Convert {@link Criteria} to SQLQuery.
     *
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
     *
     * @return
     */
    public String criteriaValuedSQLQuery() {
        String query = criteriaSQLQuery();
        try {
            CriteriaImpl criteriaImpl = (CriteriaImpl) criteria;
            String entityName = criteriaImpl.getEntityOrClassName();
            CriteriaQuery criteriaQuery = new CriteriaQueryTranslator((SessionFactoryImpl) sessionFactory, criteriaImpl, entityName, "this_");

            CriteriaHolder criteriaHolder = new CriteriaHolder();
            criteriaHolder.setCriteria(criteria);
            criteriaHolder.setCriteriaImpl(criteriaImpl);
            criteriaHolder.setCriteriaQuery(criteriaQuery);
            criteriaHolder.setDialect(dialect);
            criteriaHolder.setSqlQuery(query);

            criteriaParsers.add(new MSSQLCriteriaParser());
            criteriaParsers.add(new MySQLCriteriaParser());
            criteriaParsers.add(new OracleCriteriaParser());
            criteriaParsers.add(new CriteriaExpressionParser());
            criteriaParsers.add(new SubCriteriaParser());
            for (CriteriaParser criteriaParser : criteriaParsers) {
                criteriaParser.parse(criteriaHolder);
            }
            return criteriaHolder.getSqlQuery();
        } catch (Exception e) {
            throw new HibernateSQLAnalyzerException(Constants.CRITERIA_TO_VALUED_QUERY_ERROR, e);
        }
    }
    
    /**
     * Add your custom {@link CriteriaParser}
     * @param criteriaParser 
     */
    public void addCriteriaParser(CriteriaParser criteriaParser){
        criteriaParsers.add(criteriaParser);
    }
}
