/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sqlanalyzer.hibernate.core;

import com.sqlanalyzer.hibernate.core.parser.CriteriaQueryExpressionParser;
import com.sqlanalyzer.hibernate.core.parser.CriteriaQueryParser;
import com.sqlanalyzer.hibernate.core.parser.MSSQLCriteriaQueryParser;
import com.sqlanalyzer.hibernate.core.parser.MySQLCriteriaQueryParser;
import com.sqlanalyzer.hibernate.core.parser.OracleCriteriaQueryParser;
import com.sqlanalyzer.hibernate.core.parser.SubCriteriaQueryParser;
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
    private final List<CriteriaQueryParser> criteriaQueryParsers = new ArrayList<CriteriaQueryParser>();
    private String sqlQuery;

    public HibernateCriteria(SessionFactory sessionFactory, Criteria criteria, String dialect) {
        this.sessionFactory = sessionFactory;
        this.criteria = criteria;
        this.dialect = dialect;
        addDefaultCriteriaQueryParser();
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
                sqlQuery = (String) field.get(criteriaLoader);

                if (HibernateDialect.ORACLE_DIALECT.toString().equalsIgnoreCase(dialect)
                        || HibernateDialect.ORACLE_9_DIALECT.toString().equalsIgnoreCase(dialect)) {
                    if (criteriaImpl.getMaxResults() != null) {
                        sqlQuery = "SELECT * FROM (" + sqlQuery + ") WHERE ROWNUM <= ?";
                    }
                }
                return sqlQuery;
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
        /* Get SQL query */
        criteriaSQLQuery();
        try {
            /* Process SQL query for values. */
            HibernateCriteriaHolder criteriaHolder = buildHibernateCriteriaHolder();
            for (CriteriaQueryParser criteriaParser : criteriaQueryParsers) {
                criteriaParser.parse(criteriaHolder);
            }
            return criteriaHolder.getSqlQuery();
        } catch (Exception e) {
            throw new HibernateSQLAnalyzerException(Constants.CRITERIA_TO_VALUED_QUERY_ERROR, e);
        }
    }

    /**
     * Add your custom {@link CriteriaQueryParser}
     *
     * @param criteriaQueryParser
     */
    public void addCriteriaQueryParser(CriteriaQueryParser criteriaQueryParser) {
        criteriaQueryParsers.add(criteriaQueryParser);
    }

    /**
     * @since v2.2
     */
    private void addDefaultCriteriaQueryParser() {
        criteriaQueryParsers.add(new MSSQLCriteriaQueryParser());
        criteriaQueryParsers.add(new MySQLCriteriaQueryParser());
        criteriaQueryParsers.add(new OracleCriteriaQueryParser());
        criteriaQueryParsers.add(new CriteriaQueryExpressionParser());
        criteriaQueryParsers.add(new SubCriteriaQueryParser());
    }

    /**
     * @since v2.2
     * @return 
     */
    private HibernateCriteriaHolder buildHibernateCriteriaHolder() {
        CriteriaImpl criteriaImpl = (CriteriaImpl) criteria;
        String entityName = criteriaImpl.getEntityOrClassName();
        CriteriaQuery criteriaQuery = new CriteriaQueryTranslator((SessionFactoryImpl) sessionFactory, criteriaImpl, entityName, "this_");

        HibernateCriteriaHolder criteriaHolder = new HibernateCriteriaHolder();
        criteriaHolder.setCriteria(criteria);
        criteriaHolder.setCriteriaImpl(criteriaImpl);
        criteriaHolder.setCriteriaQuery(criteriaQuery);
        criteriaHolder.setDialect(dialect);
        criteriaHolder.setSqlQuery(sqlQuery);
        return criteriaHolder;
    }
}
