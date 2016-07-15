/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sqlanalyzer.hibernate.util;

/**
 * @author vicky.thakor
 * @date 5th July, 2016
 */
public enum HibernateDialect {
    SQL_SERVER_DIALECT("org.hibernate.dialect.SQLServerDialect"),
    MYSQL_DIALECT("org.hibernate.dialect.MySQLDialect"),
    MYSQL_INNODB_DIALECT("org.hibernate.dialect.MySQLInnoDBDialect"),
    MYSQL_MYISAM_DIALECT("org.hibernate.dialect.MySQLMyISAMDialect"),
    POSTGRE_SQL_DIALECT("org.hibernate.dialect.PostgreSQLDialect"),
    ORACLE_DIALECT("org.hibernate.dialect.OracleDialect"),
    ORACLE_9_DIALECT("org.hibernate.dialect.Oracle9Dialect");

    private final String dialect;
    private HibernateDialect(String dialect) {
        this.dialect = dialect;
    }

    @Override
    public String toString() {
        return this.dialect;
    }
}
