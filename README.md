# HibernateSQLAnalyzer
Its an Add-on created for <a href="https://github.com/javaquery/SQLAnalyzer" target="_blank">https://github.com/javaquery/SQLAnalyzer</a> to analyze Hibernate Criteria.

#Features
<table>
  <tr>
    <td>✔ SQLAnalyzer Features</td>
    <td>✔ Get Criteria Query</td>
  </tr>
  <tr>
    <td>✔ Get Criteria Query with real values</td>
  </tr>
</table>

#Source code
<pre>
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
criteria.add(Restrictions.eq("Email", "vicky.thakor@javaquery.com"));
List<User> listUser = criteria.list();

List<SQLPlan> sQLPlans = new HibernateSQLAnalyzer(objSession, null)
		.fromCriteria(criteria)
		.save("D:\\SQLAnalyzer\\MSSQL\\Hibernate", "prefix", "suffix")
		.generateReport();

for (SQLPlan sQLPlan : sQLPlans) {
	System.out.println(sQLPlan.getHTMLReport());
	Desktop.getDesktop().open(new File(sQLPlan.reportFiles().get(0)));
}
</pre>

#Sample Reports
<a href="https://javaquery.github.io/SQLAnalyzer/">https://javaquery.github.io/SQLAnalyzer/</a>
- <a href="http://javaquery.github.io/SQLAnalyzer/mssql">Microsoft SQL Server</a> 
- <a href="http://javaquery.github.io/SQLAnalyzer/mysql">MySQL</a>
- <a href="http://javaquery.github.io/SQLAnalyzer/postgresql">PostgreSQL</a>

#Warning
HibernateSQLAnalyzer is analysis tool and should be used at development phase. It'll cost a lot on Production environment so comment/delete HibernateSQLAnalyzer code before you deploy your code on Production environment. 
