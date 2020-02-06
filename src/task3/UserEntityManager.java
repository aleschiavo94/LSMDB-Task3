package task3;

import java.util.HashMap;
import java.util.Map;

import org.neo4j.driver.*;
import org.neo4j.driver.v1.AuthTokens;
import org.neo4j.driver.v1.Driver;
import org.neo4j.driver.v1.GraphDatabase;
import org.neo4j.driver.v1.Session;
import org.neo4j.driver.v1.StatementResult;
import org.neo4j.driver.v1.Transaction;
import org.neo4j.driver.v1.TransactionWork;


public class UserEntityManager {

	    // Driver objects are thread-safe and are typically made available application-wide.
	    private final static Driver driver;
	    private static UserEntityManager userEntityManager;
	    
	    static {	
	    	//Constructing the Driver instance on startup
	    	driver = GraphDatabase.driver( "bolt://localhost:7687", AuthTokens.basic( "neo4j", "fede95" ) );
	    }


	    public void close() throws Exception{
	        driver.close();
	    }
	    
	    //LOGIN FUNCTIONS
	    public static User login(String username, String password) {
	    	User user = null;
	    	
	    	try(Session session = driver.session()){
	    		session.readTransaction(new TransactionWork<Long>() {
	    			@Override
	    			public Long execute(Transaction tx) {
	    				return matchUsernamePassword(tx, username, password);
	    			}
	    		});
	    	}
	    	return user;
	    }
	    
	    private static long matchUsernamePassword(Transaction tx, String user, String pwd) {
	    	Map<String, Object> params = new HashMap<>();
	           params.put("username", user);
	           params.put("password",pwd);
	           
	    	StatementResult result = tx.run("MATCH (ee:Users{username: $username, password: $password}) RETURN ee;",
	    			params);
	    	
	    	return result.single().get(0).asLong();
	    }

}
