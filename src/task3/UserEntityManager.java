package task3;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.neo4j.driver.*;
import org.neo4j.driver.v1.AuthTokens;
import org.neo4j.driver.v1.Driver;
import org.neo4j.driver.v1.GraphDatabase;
import org.neo4j.driver.v1.Session;
import org.neo4j.driver.v1.StatementResult;
import org.neo4j.driver.v1.Transaction;
import org.neo4j.driver.v1.TransactionWork;
import org.neo4j.driver.v1.Value;
import org.neo4j.driver.v1.types.Node;
import org.neo4j.driver.v1.util.Pair;


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
	    		session.readTransaction(new TransactionWork<User>() {
	    			@Override
	    			public User execute(Transaction tx) {
	    				List<Pair<String, Value>> list;
	    				list = new ArrayList<Pair<String, Value>>(matchUsernamePassword(tx, username, password));
	    				
	    				Iterator iterator = list.iterator();
	    			    while(iterator.hasNext()) {
	    			    	System.out.println(iterator.next());
	    			    }
	    				return user;
	    			}
	    		});
	    	}
	    	return user;
	    }
	    
	    private static List<Pair<String, Value >> matchUsernamePassword(Transaction tx, String user, String pwd) {
	    	Map<String, Object> params = new HashMap<>();
	           params.put("username", user);
	           params.put("password",pwd);
	           
	    	StatementResult result = tx.run("MATCH (ee:Users{username: $username, password: $password}) RETURN ee;",
	    			params);
	    	
	    	return result.single().fields();
	    }

}
