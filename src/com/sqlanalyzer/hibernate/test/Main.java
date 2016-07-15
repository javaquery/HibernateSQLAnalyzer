/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sqlanalyzer.hibernate.test;

import com.sqlanalyzer.executionplans.SQLPlan;
import com.sqlanalyzer.hibernate.HibernateSQLAnalyzer;
import com.sqlanalyzer.hibernate.test.bean.User;
import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.util.List;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.criterion.Restrictions;

/**
 *
 * @author Vicky
 */
public class Main {

    public static void main(String[] args) throws IOException {
        /* Create hibernate configuration. */
        Configuration objConfiguration = new Configuration();
        objConfiguration.configure("com\\sqlanalyzer\\hibernate\\test\\hibernate.cfg.xml");

        /* Open session and begin database transaction for database operation. */
        SessionFactory objSessionFactory = objConfiguration.buildSessionFactory();
        Session objSession = objSessionFactory.openSession();

        Criteria criteria = objSession.createCriteria(User.class);
        /*criteria.setProjection(Projections.projectionList().add(Projections.groupProperty("id")));*/
        criteria.createAlias("Messages", "Messages");
        criteria.createAlias("CreditCard", "CreditCard");

        /* Create object of Conjunction */
//        Conjunction objConjunction = Restrictions.conjunction();
        /* Add multiple condition separated by AND clause. */
//        objConjunction.add(Restrictions.eq("Messages.userID", 1));
//        objConjunction.add(Restrictions.eq("Messages.userID", 2));

        /* Attach Conjunction in Criteria */
        /*criteria.add(objConjunction);*/
        /*criteria.add(Restrictions.eq("Email", "vicky.thakor@javaquery.com"));
         criteria.addOrder(Order.asc("Username"));*/
        criteria.setMaxResults(10);
        criteria.add(Restrictions.between("id", 1, 2));
        List<User> listUser = criteria.list();

        List<SQLPlan> sQLPlans = new HibernateSQLAnalyzer(objSession, null)
                .fromCriteria(criteria)
                .save("D:\\SQLAnalyzer\\MSSQL\\Hibernate", "prefix", "suffix")
                .generateReport();
        
        for (SQLPlan sQLPlan : sQLPlans) {
            System.out.println(sQLPlan.getHTMLReport());
            Desktop.getDesktop().open(new File(sQLPlan.reportFiles().get(0)));
        }
    }
}
