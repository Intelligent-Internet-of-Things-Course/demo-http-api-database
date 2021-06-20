package it.unimore.dipi.iot.dt.api.persistence;

import java.util.List;
import com.zaxxer.hikari.HikariDataSource;
import org.javalite.activejdbc.Base;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class JdbcDataAccess {
	
	private static HikariDataSource ds = null;
	private static final Logger logger = LoggerFactory.getLogger(JdbcDataAccess.class);

	private String mysqlUrl;

	private String mysqlUsername;

	private String mysqlPassword;

	public JdbcDataAccess(String mysqlUrl, String mysqlUsername, String mysqlPassword) {
		this.mysqlUrl = mysqlUrl;
		this.mysqlUsername = mysqlUsername;
		this.mysqlPassword = mysqlPassword;
	}

	private synchronized void initPool(){

		if (ds != null)
			return;

		ds = new HikariDataSource();
		ds.setAutoCommit(true);
		ds.setJdbcUrl(this.mysqlUrl);
		ds.setUsername(this.mysqlUsername);
		ds.setPassword(this.mysqlPassword);
		ds.setIdleTimeout(5000);
		ds.setConnectionTimeout(5000);
		ds.setMaximumPoolSize(5);
	}

	private void initDatabase(){
		initPool();
		if (!Base.hasConnection()) Base.open(ds);
	}
	
	final static String join(List<Integer> list){
		StringBuilder sb = new StringBuilder();
		sb.append("(");
		for (int j=0; j < list.size(); j++){
			if (j > 0) sb.append(",");
			sb.append(list.get(j));
		}
		sb.append(")");
		return sb.toString();
	}
	
	public void onDBConnection(DBFunction function){
		initDatabase();
		try{
			function.doSomething();
		}catch(Exception e){
			throw new RuntimeException(e);
		}finally{ 
			try{
				if (Base.hasConnection()) Base.close();
				logger.info(
						"DB Connection closed[TOTAL:"+ds.getHikariPoolMXBean().getTotalConnections()
						+", FREE:"+ds.getHikariPoolMXBean().getThreadsAwaitingConnection()
						+", Active:"+ds.getHikariPoolMXBean().getActiveConnections()+"]");
			}catch(Exception e){
				/* silently ignore.. */
			}
		}
	}

	@SuppressWarnings("unchecked")
	public <T,E extends Throwable> T onDBConnection(DBCallable<T,E> callable) throws E{
		initDatabase();
		try{
			return callable.doSomething();
		}catch(Exception e){
			E cast = null;
			try{
				 cast = (E)e;
			}catch(Exception e2){
				throw new RuntimeException(e);
			}
			throw cast;
		}finally{
			try{
				if (Base.hasConnection()) Base.close();
				logger.info(
						"DB Connection closed[TOTAL:"+ds.getHikariPoolMXBean().getTotalConnections()
								+", FREE:"+ds.getHikariPoolMXBean().getThreadsAwaitingConnection()
								+", Active:"+ds.getHikariPoolMXBean().getActiveConnections()+"]");
			}catch(Exception e){
				/* silently ignore.. */
			}
		}
	}

	public static interface DBFunction{
		public void doSomething();
	}

	public static interface DBCallable<T,E extends Throwable>{
		public T doSomething() throws E;
	}

}
