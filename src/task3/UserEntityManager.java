package task3;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
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
	    					int id, credit;
	    					
	    				    if(record.get("id").isNull()) {
	    				    	id=0;
	    				    }else {
	    				    	id = record.get("id").asInt();
	    				    }
	    				    String username = record.get("username").asString();
	    				    String password = record.get("password").asString();
	    				    String surname = record.get("surname").asString();
	    				    String name = record.get("name").asString();
	    				    if(record.get("credit").isNull()) {
	    				    	credit=0;
	    				    }else {
	    				    	credit = record.get("credit").asInt();
	    				    }
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

	    //getting all the films
	    public static List<Film> getFilms(){
	    	List<Film> list;
	    	
	    	try(Session session = driver.session()){
	    		list = session.readTransaction(new TransactionWork<List<Film>>() {
	    			@Override
	    			public List<Film> execute(Transaction tx) {
	    				List<Film> films = new ArrayList<>();
	    				StatementResult res = matchFilms(tx);
	    				
	    				while(res.hasNext()) {
	    					
	    		    		int id = res.next().get("id").asInt();
	    					String title = res.next().get("title").asString();
	    					String genre = res.next().get("genre").asString();
	    					String plot = res.next().get("plot").asString();
	    					String year = res.next().get("year").asString();
	    					int price = res.next().get("weeklyPrice").asInt();
	    					String prod = res.next().get("production_company").asString();
	    					int budget = res.next().get("budget").asInt();
	    					
	    		    		films.add(new Film(id, title, genre, plot, year, price, prod, budget));
	    		    		
	    		    	}
	    				return films;
	    			}
	    		});
	    	}
	    	
	    	return list;
	    }
	    
	    
	    private static StatementResult matchFilms(Transaction tx){
	    	StatementResult result=tx.run("MATCH(ff:Movies) RETURN ff.id AS id, ff.title AS title,"
	    			+ "ff.genres AS genre, ff.overview AS plot, ff.release_date AS year,"
	    			+ "ff.weeklyPrice AS weeklyPrice, ff.production_company AS production_company,"
	    			+ "ff.budget AS budget;");

	    	
	    	return result;
	    }
	    
	    private static void createRent(Transaction tx, String username, String title) {
	    	Map<String, Object> params = new HashMap<>();
    		params.put("username", username);
    		params.put("title", title);
    		params.put("date", Local);
    		
	    	StatementResult result = tx.run("MATCH (ee:Users),(ff:Movies) "
	    			+ "WHERE ee.username=$username and ff.title=$title"
	    			+ "CREATE (ee)-[r:RENTS{date:$date}]->(ff) RETURN r");
	    	
	    	return;
	    }
	    
	    
	    
	    /*
	     * ADMIN CONTROLLER FUNCTIONS
	     */
	    //getting all the users
	    public static List<User> getUsers() {
	    	List<User> u;
	    	try(Session session = driver.session()){
	    		u = session.readTransaction(new TransactionWork<List<User>>() {
	    			@Override
	    			public List<User> execute(Transaction tx) {
	    				List<User> list = new ArrayList<>();
	    				
	    				StatementResult result = matchUsers(tx);
	    				
	    				
	    				while (result.hasNext()) {
	    					User user;
	    					Record record = result.next();
	   
	    				    int id = record.get("id").asInt();
	    				    String username = record.get("username").asString();
	    				    String password = record.get("password").asString();
	    				    String surname = record.get("surname").asString();
	    				    String name = record.get("name").asString();
	    				    int credit = record.get("credit").asInt();
	    			        String email = record.get("email").asString();
	    				        
	    			        user = new User(id, username, password, name, surname, email, credit);
	    			        list.add(user);
	    				}
	    				return list;
	    			}
	    		});
	    	}
	    	return u;
	    }
	    
	    private static StatementResult matchUsers(Transaction tx) {
	    	StatementResult result = tx.run("MATCH (ee:Users) WHERE ee.username <> \"admin\" "
	    			+ "RETURN ee.id AS id, ee.username AS username, ee.password AS password,"
	    			+ "ee.surname AS surname, ee.name AS name, ee.credit AS credit, ee.email AS email;");
	    	
	    	return result;
	    }
	    
	    
	    //getting all the rented films of a user
	    public static List<Rental> getRentals(User user){
	    	List<Record> list;
	    	List<Rental> rent = new ArrayList<>();
	    	
	    	try(Session session = driver.session()){
	    		list = session.readTransaction(new TransactionWork<List<Record>>() {
	    			@Override
	    			public List<Record> execute(Transaction tx) {	
	    				return matchUserFilms(tx, user);
	    			}
	    		});
	    		
	    		
	    		for(Record rec: list) {
					String username = rec.get("username").asString();
					int price = rec.get("price").asInt();
					String start_date = rec.get("start_date").asString();
					LocalDate stDate = LocalDate.parse(start_date);
					LocalDate eDate = stDate.plusDays(7);
					String title = rec.get("title").asString();
					
					rent.add(new Rental(username, title, stDate, eDate, price));
		    	} 
			}
	    	
	    	return rent;
	    } 
	    
	    private static List<Record> matchUserFilms(Transaction tx, User u){
	    	Map<String, Object> params = new HashMap<>();
    		params.put("username", u.getUsername());
    		
    		List<Record> result=tx.run("MATCH (ee:Users)-[r:RENTS]-(ff:Movies)WHERE ee.username =$username"
	    			+ " RETURN r.date AS start_date, ff.title AS title, ff.weeklyPrice AS price, ee.username AS username"
	    			, params).list();

	    	return result;
	    }
	    
	    
	    //deleting the user and all the rentals
	    public static void removeUser(User user) {
	    	
	    	try(Session session = driver.session()){
	    		session.writeTransaction(new TransactionWork<Void>() {
	    			@Override
	    			public Void execute(Transaction tx) {
	    				deleteUserMovies(tx, user);
	    				deleteUserRatings(tx, user);
	    				deleteUser(tx, user);
	    				return null;
	    			}
	    		});
	    	}
	    	
	    	return;
	    } 
	    
	    private static void deleteUserMovies(Transaction tx, User u) {
	    	Map<String, Object> params = new HashMap<>();
    		params.put("username", u.getUsername());
    		
	    	tx.run("MATCH (n { username: $username})-[r:RENTS]-() DELETE r", params);
	    	
	    	return;
	    }
	    
	    private static void deleteUserRatings(Transaction tx, User u) {
	    	Map<String, Object> params = new HashMap<>();
    		params.put("username", u.getUsername());
    		
	    	tx.run("MATCH (n { username: $username'})-[r:RATES]-() delete r", params);
	    	
	    	return;
	    }
	    
	    private static void deleteUser(Transaction tx, User u) {
	    	Map<String, Object> params = new HashMap<>();
    		params.put("username", u.getUsername());
    		
	    	tx.run("MATCH (n { username: $username'}) delete n", params);
	    	
	    	return;
	    }
	    
}
