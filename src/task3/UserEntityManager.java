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
import org.neo4j.driver.v1.Record;
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
	    
	    
	    /* 
	     * LOGIN FUNCTIONS
	     */
	    
	    //login to the system
	    public static User login(String username, String password) {
	    	User user = null;
	    	try(Session session = driver.session()){
	    		
	    		user = session.readTransaction(new TransactionWork<User>() {
	    			@Override
	    			public User execute(Transaction tx) {
	    				User u = null;
	    				
	    				StatementResult result = matchUsernamePassword(tx, username, password);
	    				
	    				
	    				while (result.hasNext()) {
	    					Record record = result.next();
	    				      
	    				    int id = record.get("id").asInt();
	    				    String username = record.get("username").asString();
	    				    String password = record.get("password").asString();
	    				    String surname = record.get("surname").asString();
	    				    String name = record.get("name").asString();
	    				    int credit = record.get("credit").asInt();
	    			        String email = record.get("email").asString();
	    				        
	    			        u = new User(id, username, password, name, surname, email, credit);
	    				}
	    				return u;
	    			}
	    		});
	    	}
	    	return user;
	    }
	    
	    //finding the node with username and password
	    private static StatementResult matchUsernamePassword(Transaction tx, String user, String pwd) {
	    	Map<String, Object> params = new HashMap<>();
	           params.put("username", user);
	           params.put("password",pwd);
	           
	    	StatementResult result = tx.run("MATCH (ee:Users{username: $username, password: $password}) "
	    			+ "RETURN ee.id AS id, ee.username AS username, ee.password AS password,"
	    			+ "ee.surname AS surname, ee.name AS name, ee.credit AS credit, ee.email AS email;",
	    			params);
	    	
	    	return result;
	    }
	    
	    
	    
	    /*
	     * SIGN UP FUNCTIONS
	     */
	    
	    //finding if the user is already signed up
	    public static boolean findIfNotExist(User new_user) {
	    	boolean exists;
	    	try(Session session = driver.session()){
	    		exists = session.readTransaction(new TransactionWork<Boolean>() {
	    			@Override
	    			public Boolean execute(Transaction tx) {
	    				Boolean sign=false;
	    				
	    				StatementResult result = matchUsernamePassword(tx, new_user.getUsername(), new_user.getPassword());
	    				if(result.hasNext()) {
	    					return sign;
	    				}else {
	    					sign=true;
	    					return sign;
	    				}
	    			}
	    		});
	    	}
	    	
	    	return exists;
	    } 
	    
	    
	    //inserting a new user
	    public static void insertUser(User u) {
	    	try(Session session = driver.session()){
	    		session.writeTransaction(new TransactionWork<Void>() {
	    			@Override
	    			public Void execute(Transaction tx) {
	    				
	    				createUserNode(tx, u);
	    				return null;
	    			}
	    		});
	    	}
	    }
	    
	    //creating a new user node
	    private static void createUserNode(Transaction tx, User user) {
	    	Map<String, Object> params = new HashMap<>();
	    		params.put("id", user.getIdUser());
	    		params.put("name", user.getName());
	    		params.put("surname", user.getSurname());
	    		params.put("username", user.getUsername());
	    		params.put("password", user.getPassword());
	    		params.put("credit", user.getCredit());
	    		params.put("email", user.getEmail());
	           
	    	tx.run("CREATE(a:Users{id: $id, name: $name, surname: $surname,"
	    			+ "username: $username, password: $password, credit: $credit,"
	    			+ "email: $email})", params);
	    	
	    }
	    
	    
	    /*
	     * USER CONTROLLER FUNCTIONS
	     */
	    
	    public static List<Film> getFilms(){
	    	List<Film> list;
	    	
	    	try(Session session = driver.session()){
	    		list = session.readTransaction(new TransactionWork<List<Film>>() {
	    			@Override
	    			public List<Film> execute(Transaction tx) {
	    				List<Film> films;
	    				
	    				StatementResult result = matchFilms(tx);
	    				
	    				while(result.hasNext()) {
	    					Record record = result.next();
	    					
	    					
	    				}
	    			}
	    		});
	    	}
	    	
	    	return list;
	    }
	    
	    
	    private static StatementResult matchFilms(Transaction tx){
	    	StatementResult result=tx.run("MATCH(ff:Movies) RETURN ff;");
	    	return result;
	    }
}
