/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sqlanalyzer.hibernate.core;

import org.hibernate.Criteria;
import org.hibernate.criterion.CriteriaQuery;
import org.hibernate.internal.CriteriaImpl;

/**
 * @author vicky.thakor
 * @since v2.2
 */
public class HibernateCriteriaHolder {
    private Criteria criteria;
    private CriteriaImpl criteriaImpl;
    private CriteriaQuery criteriaQuery;
    private String sqlQuery;
    private String dialect;

    public Criteria getCriteria() {
        return criteria;
    }

    public void setCriteria(Criteria criteria) {
        this.criteria = criteria;
    }

    public CriteriaImpl getCriteriaImpl() {
        return criteriaImpl;
    }

    public void setCriteriaImpl(CriteriaImpl criteriaImpl) {
        this.criteriaImpl = criteriaImpl;
    }

    public CriteriaQuery getCriteriaQuery() {
        return criteriaQuery;
    }

    public void setCriteriaQuery(CriteriaQuery criteriaQuery) {
        this.criteriaQuery = criteriaQuery;
    }

    public String getSqlQuery() {
        return sqlQuery;
    }

    public void setSqlQuery(String sqlQuery) {
        this.sqlQuery = sqlQuery;
    }

    public String getDialect() {
        return dialect;
    }

    public void setDialect(String dialect) {
        this.dialect = dialect;
    }
    
    
}
