/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sqlanalyzer.hibernate;

import com.sqlanalyzer.database.service.DBService;
import com.sqlanalyzer.database.service.MySQLServiceImpl;

/**
 * Default {@link DBService} used by {@link HibernateSQLAnalyzer}. <br/>
 * Note: Don't use it.
 * @author vicky.thakor
 * @date 10th July, 2016
 */
public class MySQLAnalyzerApi extends MySQLServiceImpl{

    @Override
    public String DatabaseDriver() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String DatabaseHost() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String DatabaseUsername() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String DatabasePassword() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
