/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sqlanalyzer.hibernate;

import com.sqlanalyzer.Configurator;
import com.sqlanalyzer.SQLAnalyzer;
import com.sqlanalyzer.database.service.DBService;
import com.sqlanalyzer.executionplans.SQLPlan;
import com.sqlanalyzer.hibernate.core.HibernateCriteria;
import com.sqlanalyzer.hibernate.util.HibernateDialect;
import java.util.List;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.internal.SessionImpl;

/**
 * Analyze Hibernate {@link Criteria} using {@link SQLAnalyzer}.
 * @author vicky.thakor
 * @date 5th July, 2016
 */
public class HibernateSQLAnalyzer extends SQLAnalyzer {

    private final Session session;
    private final SessionFactory sessionFactory;
    private String dialect;
    private Criteria criteria;
    private HibernateCriteria hibernateCriteria;

    public HibernateSQLAnalyzer(Session hibernateSession, Configurator configurator) {
        this.session = hibernateSession;
        this.sessionFactory = hibernateSession.getSessionFactory();
        initAnalyzer();
        usingConnection(((SessionImpl) session).connection());
        usingConfigurator(configurator);
    }
    
    /**
     * Initialize {@link DBService} based on database dialect.
     */
    private void initAnalyzer() {
        if (HibernateDialect.SQL_SERVER_DIALECT.toString().equalsIgnoreCase(getDialect())) {
            usingDBService(new MSSQLAnalyzerApi().getClass());
        } else if (HibernateDialect.MYSQL_DIALECT.toString().equalsIgnoreCase(getDialect())
                || HibernateDialect.MYSQL_INNODB_DIALECT.toString().equalsIgnoreCase(getDialect())
                || HibernateDialect.MYSQL_MYISAM_DIALECT.toString().equalsIgnoreCase(getDialect())) {
            usingDBService(new MySQLAnalyzerApi().getClass());
        } else if (HibernateDialect.POSTGRE_SQL_DIALECT.toString().equalsIgnoreCase(getDialect())) {
            usingDBService(new PostgreSQLAnalyzerApi().getClass());
        }
    }

    /**
     * Analyze {@link Criteria}.
     * @param criteria
     * @return 
     */
    public HibernateSQLAnalyzer fromCriteria(Criteria criteria) {
        this.criteria = criteria;
        this.hibernateCriteria = new HibernateCriteria(sessionFactory, criteria, getDialect());
        return this;
    }

    /**
     * Get database dialect.
     * @return 
     */
    public String getDialect() {
        if (dialect == null && session != null) {
            dialect = ((SessionFactoryImplementor) sessionFactory).getDialect().toString();
        }
        return dialect;
    }
    

    /**
     * Get SQLQuery from {@link Criteria}
     * @return 
     */
    public String criteriaSQLQuery() {
        return hibernateCriteria.criteriaSQLQuery();
    }

    /**
     * Get SQLQuery with real values from {@link Criteria}
     * @return 
     */
    public String criteriaValuedQuery() {
        return hibernateCriteria.criteriaValuedSQLQuery();
    }

    
    @Override
    public List<SQLPlan> generateReport() {
        if (HibernateDialect.SQL_SERVER_DIALECT.toString().equalsIgnoreCase(getDialect())) {
            fromQuery(criteriaSQLQuery());
        } else {
            fromQuery(criteriaValuedQuery());
        }
        
        return super.generateReport();
    }
}
