package task3;


import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
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
	    private static Driver driver;
	    //private static UserEntityManager userEntityManager;
	    
	    public static void start(){	
	    	//Constructing the Driver instance on startup
	    	driver = GraphDatabase.driver( "bolt://localhost:7687", AuthTokens.basic( "neo4j", "fede95" ) );
	    }


	    public static void close(){
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
	    				//System.out.println(res.list().size());
	    				int i  = 0;
	    				Record rec;
	    				while(res.hasNext()) {
	    					rec = res.next();
	    					
	    		    		int id = rec.get("id").asInt();
	    					String title = rec.get("title").asString();
	    					String genre = rec.get("genre").asString();
	    					String plot = rec.get("plot").asString();
	    					String year = rec.get("year").asString();
	    					int price = rec.get("weeklyPrice").asInt();
	    					String production_companies = rec.get("production_companies").asString();
	    					int budget = rec.get("budget").asInt();
	    					int revenue = rec.get("revenue").asInt();
	    					String production_country = rec.get("production_countries").asString();
	    					String language = rec.get("original_language").asString();
	    					int runtime = rec.get("duration").asInt();
	    					int vote_count = rec.get("vote_count").asInt();
	    					double vote_avg = Double.parseDouble(rec.get("vote_avg").asString());
	    					
	    		    		films.add(new Film(id, title, genre, plot, year, price, production_companies, 
	    		    				budget, revenue, production_country, language, runtime, vote_count, vote_avg));
	    		    		
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
	    			+ "ff.weeklyPrice AS weeklyPrice, ff.production_companies AS production_companies,"
	    			+ "ff.budget AS budget, ff.revenue AS revenue, ff.production_countries AS production_countries,"
	    			+ "ff.original_language AS language, ff.runtime AS duration, ff.vote_count AS vote_count,"
	    			+ "ff.vote_average as vote_avg");

	    	
	    	return result;
	    }
	    
	    //search if the user has already rated the film
	    public static boolean searchRating(User user, Film f) {
	    	boolean found;
	    	try(Session session = driver.session()){
	    		found = session.readTransaction(new TransactionWork<Boolean>() {
	    			@Override
	    			public Boolean execute(Transaction tx) {
	    				StatementResult res = matchRating(tx, user.getUsername(), f.getTitle());
	    				
	    				
	    				if(res.hasNext()) {
	    					return true;
	    				}else {
	    					return false;
	    				}
	    				
	    			}
	    		});
	    	}
	    	return found;
	    }
	    
	    private static StatementResult matchRating(Transaction tx, String username, String title) {
	    	Map<String, Object> params = new HashMap<>();
    		params.put("username", username);
    		params.put("title", title);
    		
    		StatementResult result = tx.run("MATCH(ee:Users)-[r:RATES]->(ff:Movies) "
    				+ "where ee.username=$username and ff.title=$title "
    				+ "return r.vote", params);
	    	
	    	return result;
	    }
	    
	    
	    //inserting a new rating for the user
	    public static void insertRating(Rating r) {
	    	
	    	try(Session session = driver.session()){
	    		session.writeTransaction(new TransactionWork<Void>() {
	    			@Override
	    			public Void execute(Transaction tx) {
	    				LocalDate localDate = r.getDate();
	    				Date date = Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());  
	    				int time = (int) (date.getTime()/1000);
	    				createRating(tx, r.getUser().getUsername(), r.getFilm().getTitle(), r.getVote(), time);
	    				return null;
	    			}
	    		});
	    	}
	    	
	    	return;
	    } 
	    
	    private static void createRating(Transaction tx, String username, String title, int vote, int ts) {
	    	Map<String, Object> params = new HashMap<>();
    		params.put("username", username);
    		params.put("title", title);
    		params.put("ts", ts);
    		params.put("vote", vote);
    		
	    	tx.run("MATCH (ee:Users),(ff:Movies)\r\n" + 
	    			"WHERE ee.username=$username and ff.title=$title\r\n" + 
	    			"CREATE (ee)-[r:RATES{vote:$vote, timestamp:$ts}]->(ff)", params);
	    	
	    	return;
	    }
	    
	    //updating rate's info for the movie
	    public static List<String> updateMovieRate(Film f, int rate) {
	    	List<String> res = new ArrayList<String>();
	    	int[]count= {0};
	    	int[] avg= {0};
	    	
	    	try(Session session = driver.session()){
	    		count[0] = session.readTransaction(new TransactionWork<Integer>() {
	    			@Override
	    			public Integer execute(Transaction tx) {
	    				int match;
	    				Record result = matchMovieCount(tx, f.getTitle());
	    				
	    				match = result.get("vote_count").asInt();
	    				
	    				return match;
	    			}
	    		});
	    		
	    		avg[0] = session.readTransaction(new TransactionWork<Integer>() {
	    			@Override
	    			public Integer execute(Transaction tx) {
	    				String match;
	    				Record result = matchMovieAvg(tx, f.getTitle());
	    				
	    				match = result.get("vote_average").asString();
	    				
	    				return Integer.parseInt(match);
	    			}
	    		});
	    	}
	    	
	    	count[0] = count[0]+1;
			avg[0] = ((avg[0]+rate)/count[0]);
			
			res.add(Integer.toString(count[0]));
			res.add(Double.toString(avg[0]));
			
			
			String average = Integer.toString(avg[0]);
	
	    	try(Session session = driver.session()){
	    		session.writeTransaction(new TransactionWork<Void>() {
	    			@Override
	    			public Void execute(Transaction tx) {
	    				
	    				updateMovieRating(tx, f.getTitle(), average, count[0]);
	    				return null;
	    			}
	    		});
	    	}
	    	
	    	return res;
	    } 
	    
	    private static Record matchMovieCount(Transaction tx, String title) {
	    	Map<String, Object> params = new HashMap<>();
    		params.put("title", title);
    		
	    	StatementResult res = tx.run("MATCH(ff:Movies) " + 
	    			"WHERE ff.title = $title " + 
	    			"RETURN ff.vote_count as vote_count", params);
	    	
	    	return res.single();
	    }
	    
	    private static Record matchMovieAvg(Transaction tx, String title) {
	    	Map<String, Object> params = new HashMap<>();
    		params.put("title", title);
    		
	    	StatementResult res = tx.run("MATCH(ff:Movies) " + 
	    			"WHERE ff.title=$title " + 
	    			"RETURN ff.vote_average as vote_average", params);
	    	
	    	return res.single();
	    }
	    
	    private static Void updateMovieRating(Transaction tx, String title, String avg, int count) {
	    	Map<String, Object> params = new HashMap<>();
    		params.put("title", title);
    		params.put("avg", avg);
    		params.put("count", Integer.toString(count));
    		
	    	tx.run("MATCH(ff:Movies) " + 
	    			"WHERE  ff.title=$title " + 
	    			"SET ff.vote_count=$count , ff.vote_average=$avg", params);
	    	
	    	return null;
	    }
	    
	    //inserting a new rental for the user
	    public static void insertRental(Rental rent) {
	    	
	    	try(Session session = driver.session()){
	    		session.writeTransaction(new TransactionWork<Void>() {
	    			@Override
	    			public Void execute(Transaction tx) {
	    				String date = rent.getStartDate().toString();
	    				createRent(tx, rent.getUser(), rent.getTitle(), date);
	    				return null;
	    			}
	    		});
	    	}
	    	
	    	return;
	    } 
	    
	    private static void createRent(Transaction tx, String username, String title, String date) {
	    	Map<String, Object> params = new HashMap<>();
    		params.put("username", username);
    		params.put("title", title);
    		params.put("date", date);
    		
	    	tx.run("MATCH (ee:Users),(ff:Movies) "
	    			+ " WHERE ee.username=$username and ff.title=$title"
	    			+ " CREATE(ee)-[r:RENTS{date:$date}]->(ff) RETURN r", params);
	    	
	    	return;
	    }
	    
	  //getting all the users
	    public static List<User> getFollowed(User user) {
	    	List<User> list = new ArrayList<>();
	    	List<Record> record;
	    	try(Session session = driver.session()){
	    		
	    		record = session.readTransaction(new TransactionWork<List<Record>>() {
	    			@Override
	    			public List<Record> execute(Transaction tx) {
	    				List<Record> res = matchFollowed(tx, user);
	    				
	    				return res;
	    			}
	    		});
	    		
	    		for(Record rec: record) {
    				User new_user;
    				
    				int id = rec.get("id").asInt();
    				String username = rec.get("username").asString();
    				String password = rec.get("password").asString();
    				String surname = rec.get("surname").asString();
    				String name = rec.get("name").asString();
    				int credit = rec.get("credit").asInt();
    			    String email = rec.get("email").asString();
    				        
    			    new_user = new User(id, username, password, name, surname, email, credit);
    			    list.add(new_user);
    				
	    		}
	    	}
	    	return list;
	    }
	    
	    private static List<Record> matchFollowed(Transaction tx, User u) {
	    	Map<String, Object> params = new HashMap<>();
    		params.put("username", u.getUsername());
    		
	    	List<Record> result = tx.run("MATCH (ee:Users)-[r:FOLLOWS]->(friends) "
	    			+ "WHERE ee.username =$username return friends.id AS id, "
	    			+ "friends.username AS username, friends.password AS password,"
	    			+ "friends.surname AS surname, friends.name AS name,"
	    			+ "friends.credit AS credit, friends.email AS email", params).list();
	    	
	    	return result;
	    }
	    
	    //getting all the users
	    public static List<User> getUsersToFollow(User user) {
	    	List<User> list = new ArrayList<>();
	    	List<Record> record;
	    	try(Session session = driver.session()){
	    		
	    		record = session.readTransaction(new TransactionWork<List<Record>>() {
	    			@Override
	    			public List<Record> execute(Transaction tx) {
	    				List<Record> res = matchUsersToFollow(tx, user);
	    				
	    				return res;
	    			}
	    		});
	    		
	    		for(Record rec: record) {
    				User new_user;
    				
    				int id = rec.get("id").asInt();
    				String username = rec.get("username").asString();
    				String password = rec.get("password").asString();
    				String surname = rec.get("surname").asString();
    				String name = rec.get("name").asString();
    				int credit = rec.get("credit").asInt();
    			    String email = rec.get("email").asString();
    				        
    			    new_user = new User(id, username, password, name, surname, email, credit);
    			    list.add(new_user);
    				
	    		}
	    	}
	    	return list;
	    }
	    
	    private static List<Record> matchUsersToFollow(Transaction tx, User u) {
	    	Map<String, Object> params = new HashMap<>();
    		params.put("username", u.getUsername());
    		
    		List<Record> result = tx.run("MATCH (ee:Users) WHERE ee.username <> $username AND ee.username <> 'admin' "
	    			+ "RETURN ee.id AS id, ee.username AS username, ee.password AS password,"
	    			+ "ee.surname AS surname, ee.name AS name, ee.credit AS credit, ee.email AS email;", params).list();
	    	
	    	return result;
	    }
	    
	    public static void followUser(User current_user, User followed) {
	    	try(Session session = driver.session()){
	    		session.writeTransaction(new TransactionWork<Void>() {
	    			@Override
	    			public Void execute(Transaction tx) {
	    				createNewFollows(tx, current_user, followed);
	    				return null;
	    			}
	    		});
	    	}
	    	
	    	return;
	    }
	    
	    private static void createNewFollows(Transaction tx, User u1, User u2) {
	    	Map<String, Object> params = new HashMap<>();
    		params.put("username_u1", u1.getUsername());
    		params.put("username_u2", u2.getUsername());
    		
    		tx.run("MATCH (u1:Users),(u2:Users) "
	    			+ " WHERE u1.username=$username_u1 and u2.username=$username_u2"
	    			+ " CREATE(u1)-[f:FOLLOWS]->(u2) RETURN f", params);
	    	
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
	    				deleteUserFollow(tx, user);
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

	    private static void deleteUserFollow(Transaction tx, User u) {
	    	Map<String, Object> params = new HashMap<>();
    		params.put("username", u.getUsername());
    		
	    	tx.run("MATCH (n {username: $username'})-[:FOLLOWS]-() delete n", params);
	    	
	    	return;
	    }
	    
	    private static void deleteUser(Transaction tx, User u) {
	    	Map<String, Object> params = new HashMap<>();
    		params.put("username", u.getUsername());
    		
	    	tx.run("MATCH (n { username: $username'}) delete n", params);
	    	
	    	return;
	    }

	    
	    public static List<Film> getTopRentedFilms(){
	    	List<Record> list;
	    	List<Film> films = new ArrayList<>();
	    	
	    	try(Session session = driver.session()){
	    		list = session.readTransaction(new TransactionWork<List<Record>>() {
	    			@Override
	    			public List<Record> execute(Transaction tx) {	
	    				return topRentedFilms(tx);
	    			}
	    		});
	    		
	    		for(Record res: list) {
	    	  		int id = res.get("id").asInt();
					String title = res.get("title").asString();
					String genre = res.get("genre").asString();
					String plot = res.get("plot").asString();
					String year = res.get("year").asString();
					int price = res.get("weeklyPrice").asInt();
					String prod = res.get("production_company").asString();
					int budget = res.get("budget").asInt();
					double vote_avg = Double.parseDouble(res.get("vote_avg").asString());
		    		films.add(new Film(id, title, genre, plot, year, price, prod, budget, vote_avg));
		    		
		    	} 
			}

	    	return films;
	    } 
	    
	    private static List<Record> topRentedFilms(Transaction tx){
    		
    		List<Record> result=tx.run("MATCH (ff:Movies)-[r:RENTS]-()" + 
    				"return ff.id AS id, ff.title AS title," + 
    				"ff.genres AS genre, ff.overview AS plot, ff.release_date AS year," + 
    				"ff.weeklyPrice AS weeklyPrice, ff.production_company AS production_company," + 
    				"ff.budget AS budget, ff.vote_average as vote_avg, count(r) as conta " + 
    				"ORDER BY conta " + 
    				"DESC LIMIT 10").list();

	    	return result;
	    }
	    
	    
	    public static List<Film> getTopRatedFilms(){
	    	List<Record> list;
	    	List<Film> films = new ArrayList<>();
	    	
	    	try(Session session = driver.session()){
	    		list = session.readTransaction(new TransactionWork<List<Record>>() {
	    			@Override
	    			public List<Record> execute(Transaction tx) {	
	    				return topRatedFilms(tx);
	    			}
	    		});
	    		
	    		for(Record res: list) {
	    	  		int id = res.get("id").asInt();
					String title = res.get("title").asString();
					String genre = res.get("genre").asString();
					String plot = res.get("plot").asString();
					String year = res.get("year").asString();
					int price = res.get("weeklyPrice").asInt();
					String prod = res.get("production_company").asString();
					int budget = res.get("budget").asInt();
					System.out.println(res.get("vote_avg"));
					double vote_avg = Double.parseDouble(res.get("vote_avg").asString());
		    		films.add(new Film(id, title, genre, plot, year, price, prod, budget, vote_avg));
		    		
		    	} 
			}
	    	return films;
	    } 
	    
	    private static List<Record> topRatedFilms(Transaction tx){
    		
    		List<Record> result=tx.run("MATCH (ff:Movies)-[r:RATES]-()" + 
    				"return ff.id AS id, ff.title AS title," + 
    				"ff.genres AS genre, ff.overview AS plot, ff.release_date AS year," + 
    				"ff.weeklyPrice AS weeklyPrice, ff.production_company AS production_company," + 
    				"ff.budget AS budget, ff.vote_average as vote_avg, sum(r.vote) as rate " + 
    				"ORDER BY rate " + 
    				"DESC LIMIT 10").list();

	    	return result;
	    }
	    
	    //QUERY PER PRENDERE I FILM DI CHI FOLLOWA L'USER, CAMBIANDO LA FRECCIA SI PRENDE I FILM DI CHI è FOLLOWATO DALL'USER
	    //match (u:Users{username: 'adam'})<-[f:FOLLOWS]-(u2:Users)-[r:RENTS]-(m:Movies)
	    //return distinct(m.title)
	    public static List<Film> getFollowingFilms(){
	    	List<Record> list;
	    	List<Film> films = new ArrayList<>();
	    	
	    	try(Session session = driver.session()){
	    		list = session.readTransaction(new TransactionWork<List<Record>>() {
	    			@Override
	    			public List<Record> execute(Transaction tx) {	
	    				return followingFilms(tx);
	    			}
	    		});
	    		
	    		for(Record res: list) {
	    	  		int id = res.get("id").asInt();
					String title = res.get("title").asString();
					String genre = res.get("genre").asString();
					String plot = res.get("plot").asString();
					String year = res.get("year").asString();
					int price = res.get("weeklyPrice").asInt();
					String prod = res.get("production_company").asString();
					int budget = res.get("budget").asInt();
					double vote_avg = Double.parseDouble(res.get("vote_avg").asString());
		    		films.add(new Film(id, title, genre, plot, year, price, prod, budget, vote_avg));
		    		
		    	} 
			}

	    	return films;
	    } 
	    
	    private static List<Record> followingFilms(Transaction tx){
    		List<Record> result=tx.run("MATCH (u:Users{username: 'adam'})<-[t:FOLLOWS]-(u2:Users)-[r:RENTS]-(movie)"+
    				"return distinct movie.id as id, movie.title as title,"+
    				"movie.production_companies as production_company, movie.budget as budget,"+
    				"movie.release_date as year, movie.overview as plot, movie.genres as genre,"+
    				"movie.weeklyPrice as weeklyPrice, movie.vote_average as vote_avg").list();

	    	return result;
	    }
	    
	    
}
