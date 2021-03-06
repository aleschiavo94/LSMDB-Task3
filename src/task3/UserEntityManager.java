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
	    private static int count = 0;
	    
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
	    				Record rec;
	    				while(res.hasNext()) {
	    					rec = res.next();
	    					
	    		    		//int id = rec.get("id").asInt();
	    					String title = rec.get("title").asString();
	    					String genre = getGenreByMovie(title);
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
	    					
	    		    		films.add(new Film(title, genre, plot, year, price, production_companies, 
	    		    				budget, revenue, production_country, language, runtime, vote_count, vote_avg));
	    		    		
	    		    	}
	    				return films;
	    			}
	    		});
	    	}
	    	
	    	return list;
	    }
	    
	    
	    private static StatementResult matchFilms(Transaction tx){
	    	StatementResult result=tx.run("MATCH(ff:Movies) RETURN ff.title AS title,"
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
    				+ "WHERE ee.username=$username AND ff.title=$title "
    				+ "RETURN r.vote", params);
	    	
	    	return result;
	    }
	    
	    //verify if user have already vote the film
	    public static List<String> getAllRatingByFilm(Film f) {
	    	List<String> list = new ArrayList<String>();
	    	List<Record> record;
	    	
	    	try(Session session = driver.session()){
	    		record = session.readTransaction(new TransactionWork<List<Record>>() {
	    			@Override
	    			public List<Record> execute(Transaction tx) {
	    				List<Record> res = matchRatingByFilm(tx, f.getTitle());
	    				
	    				return res;
	    			}
	    		});
	    		
	    		for(Record rec: record) {    				
    				int vote = rec.get("vote").asInt();
    				String username = rec.get("username").asString();
    				
    				String votes = username +", "+ vote;
    				
    			    list.add(votes);
    				
	    		}
	    	}
	    	return list;
	    }
	    
	    private static List<Record> matchRatingByFilm(Transaction tx, String title) {
	    	Map<String, Object> params = new HashMap<>();
    		params.put("title", title);
    		
    		List<Record> result = tx.run("MATCH (u:Users)-[r:RATES]->(m:Movies) "
    				+ "WHERE m.title=$title "
    				+ "RETURN r.vote as vote, u.username as username", params).list();
	    	
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
	    
	    private static void createRating(Transaction tx, String username, String title, double vote, int ts) {
	    	Map<String, Object> params = new HashMap<>();
    		params.put("username", username);
    		params.put("title", title);
    		params.put("ts", ts);
    		params.put("vote", vote);
    		
	    	tx.run("MATCH (ee:Users),(ff:Movies) " + 
	    			"WHERE ee.username=$username and ff.title=$title " + 
	    			"CREATE (ee)-[r:RATES{vote:$vote, timestamp:$ts}]->(ff)", params);
	    	
	    	return;
	    }
	    
	    //updating rate's info for the movie
	    public static List<String> updateMovieRate(Film f, double rate) {
	    	List<String> res = new ArrayList<String>();
	    	int c = 0;
	    	
	    	double avg= 0.0;
	    		    	
	    	try(Session session = driver.session()){
	    		c = session.readTransaction(new TransactionWork<Integer>() {
	    			@Override
	    			public Integer execute(Transaction tx) {
	    				int match;
	    				Record result = matchMovieCount(tx, f.getTitle());
	    				
	    				match = result.get("vote_count").asInt();
	    				
	    				return match;
	    			}
	    		});
	    		
	    		avg = session.readTransaction(new TransactionWork<Double>() {
	    			@Override
	    			public Double execute(Transaction tx) {
	    				String match;
	    				Record result = matchMovieAvg(tx, f.getTitle());
	    				
	    				match = result.get("vote_average").asString();
	    				
	    				return Double.parseDouble(match);
	    			}
	    		});
	    	}
	    	
	    	int old_count = c;
	    	count = c+1;
			avg = Math.round((((avg*old_count)+rate)/count)*100.0)/100.0;
			String average = Double.toString(avg);
			
		   	try(Session session = driver.session()){
	    		session.writeTransaction(new TransactionWork<Void>() {
	    			@Override
	    			public Void execute(Transaction tx) {
	    				
	    				updateMovieRating(tx, f.getTitle(), average, count);
	    				return null;
	    			}
	    		});
	    	}
		   	res.add(Integer.toString(count));
			res.add(Double.toString(avg));
							   			   	
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
    		params.put("count", count);
    		
	    	tx.run("MATCH(ff:Movies) " + 
	    			"WHERE  ff.title=$title " + 
	    			"SET ff.vote_count=$count, ff.vote_average=$avg", params);
	    	
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
	    			+ "WHERE ee.username=$username and ff.title=$title "
	    			+ "CREATE(ee)-[r:RENTS{date:$date}]->(ff) RETURN r", params);
	    	
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
	    
	    //getting the users followed
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
	    
	    //add new followed person
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
	    			+ "WHERE u1.username=$username_u1 and u2.username=$username_u2 "
	    			+ "CREATE(u1)-[f:FOLLOWS]->(u2) RETURN f", params);
	    	
	    	return;
	    }
	    
	    //update user info
	    public static void updateUserInfo(User user) {	    	
	    	try(Session session = driver.session()){
	    		session.writeTransaction(new TransactionWork<Void>() {
	    			@Override
	    			public Void execute(Transaction tx) {
	    				updateUserNode(tx, user);
	    				return null;
	    			}
	    		});
	    	}	    	
	    	return;
	    } 
	    
    	private static void updateUserNode(Transaction tx, User u) {
 	    	Map<String, Object> params = new HashMap<>();
     		params.put("username", u.getUsername());
     		params.put("password", u.getPassword());
     		params.put("credit", u.getCredit());
     		params.put("email", u.getEmail());
     		
 	    	tx.run("MATCH (n:Users ) WHERE n.username = $username " +
 	    			"SET n.email=$email, n.credit=$credit, n.password=$password", params);
 	    	
 	    	return;
 	    }
    	 
    	//update credit when rent film
    	public static void updateUserCredit(User u, int new_credit) {
 	    	String username = u.getUsername();
 	    	try(Session session = driver.session()){
 	    		session.writeTransaction(new TransactionWork<Void>() {
 	    			@Override
 	    			public Void execute(Transaction tx) {
 	    				updateCredit(tx, username, new_credit);
 	    				return null;
 	    			}
 	    		});
 	    	}
 	    	
 	    	return;
 	    }
 	    
 	    private static void updateCredit(Transaction tx, String username, int new_credit){
 	    	Map<String, Object> params = new HashMap<>();
      		params.put("username",username);
      		params.put("new_credit",new_credit);
      		
     		tx.run("MATCH (u:Users) "+
     				"WHERE u.username = $username "
     				+ "SET u.credit = $new_credit", params);
     		
 	    	return;
 	    }
	    
 	    //query to find the most rented movie
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
	    	  		String title = res.get("title").asString();
					String genre = getGenreByMovie(title);
					String plot = res.get("plot").asString();
					String year = res.get("year").asString();
					int price = res.get("weeklyPrice").asInt();
					String production_companies = res.get("production_companies").asString();
					int budget = res.get("budget").asInt();
					int revenue = res.get("revenue").asInt();
					String production_country = res.get("production_countries").asString();
					String language = res.get("original_language").asString();
					int runtime = res.get("duration").asInt();
					int vote_count = res.get("vote_count").asInt();
					double vote_avg = Double.parseDouble(res.get("vote_avg").asString());
					
					films.add(new Film(title, genre, plot, year, price, production_companies, 
		    				budget, revenue, production_country, language, runtime, vote_count, vote_avg));
		    	} 
			}
	    	return films;
	    } 
	    
	    private static List<Record> topRentedFilms(Transaction tx){
    		
    		List<Record> result=tx.run("MATCH (ff:Movies)-[r:RENTS]-() " + 
    				"RETURN ff.title AS title, ff.overview AS plot, ff.release_date AS year," + 
    				"ff.weeklyPrice AS weeklyPrice, ff.production_companies AS production_companies," + 
    				"ff.budget AS budget, ff.revenue AS revenue, ff.production_countries AS production_countries," + 
    				"ff.original_language AS language, ff.runtime AS duration, ff.vote_count AS vote_count," + 
    				"ff.vote_average as vote_avg, count(r) as conta " + 
    				"ORDER BY conta " + 
    				"DESC LIMIT 10").list();

	    	return result;
	    }
	    
	    //query to find the top rated movie
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
	    			String title = res.get("title").asString();
					String genre = getGenreByMovie(title);
					String plot = res.get("plot").asString();
					String year = res.get("year").asString();
					int price = res.get("weeklyPrice").asInt();
					String production_companies = res.get("production_companies").asString();
					int budget = res.get("budget").asInt();
					int revenue = res.get("revenue").asInt();
					String production_country = res.get("production_countries").asString();
					String language = res.get("original_language").asString();
					int runtime = res.get("duration").asInt();
					int vote_count = res.get("vote_count").asInt();
					double vote_avg = Double.parseDouble(res.get("vote_avg").asString());
					
					films.add(new Film(title, genre, plot, year, price, production_companies, 
		    				budget, revenue, production_country, language, runtime, vote_count, vote_avg));
		    		
		    	} 
			}
	    	return films;
	    } 
	    
	    private static List<Record> topRatedFilms(Transaction tx){
    		List<Record> result=tx.run("MATCH (ff:Movies)-[r:RATES]-() " + 
    				"RETURN ff.title AS title, ff.overview AS plot, ff.release_date AS year," + 
    				"ff.weeklyPrice AS weeklyPrice, ff.production_companies AS production_companies," + 
    				"ff.budget AS budget, ff.revenue AS revenue, ff.production_countries AS production_countries," + 
    				"ff.original_language AS language, ff.runtime AS duration, ff.vote_count AS vote_count," + 
    				"ff.vote_average as vote_avg, sum(r.vote) as rate " + 
    				"ORDER BY rate " + 
    				"DESC LIMIT 10").list();

	    	return result;
	    }
	    
	    //query to find film that followed user have rented
	    public static List<Film> getFollowingFilms(User u){
	    	List<Record> list;
	    	List<Film> films = new ArrayList<>();
	    	
	    	try(Session session = driver.session()){
	    		list = session.readTransaction(new TransactionWork<List<Record>>() {
	    			@Override
	    			public List<Record> execute(Transaction tx) {	
	    				return followingFilms(tx, u);
	    			}
	    		});
	    		
	    		for(Record res: list) {
	    			String title = res.get("title").asString();
					String genre = getGenreByMovie(title);
					String plot = res.get("plot").asString();
					String year = res.get("year").asString();
					int price = res.get("weeklyPrice").asInt();
					String production_companies = res.get("production_companies").asString();
					int budget = res.get("budget").asInt();
					int revenue = res.get("revenue").asInt();
					String production_country = res.get("production_countries").asString();
					String language = res.get("original_language").asString();
					int runtime = res.get("duration").asInt();
					int vote_count = res.get("vote_count").asInt();
					double vote_avg = Double.parseDouble(res.get("vote_avg").asString());
					
					films.add(new Film(title, genre, plot, year, price, production_companies, 
		    				budget, revenue, production_country, language, runtime, vote_count, vote_avg));
		    		
		    	} 
			}

	    	return films;
	    } 
	    
	    private static List<Record> followingFilms(Transaction tx, User u){
	    	Map<String, Object> params = new HashMap<>();
    		params.put("username", u.getUsername());
    	
    		List<Record> result=tx.run("MATCH (u:Users)-[t:FOLLOWS]->(u2:Users)-[r:RENTS]-(movie) " + 
    				"WHERE u.username = $username " + 
    				"RETURN DISTINCT movie.id as id, movie.title as title, movie.revenue AS revenue," + 
    				"movie.production_countries AS production_countries, movie.production_companies as production_company," +
    				"movie.budget as budget, movie.original_language AS original_language, movie.runtime AS duration," + 
    				"movie.vote_count AS vote_count, movie.release_date as year, movie.overview as plot," + 
    				"movie.weeklyPrice as weeklyPrice, movie.vote_average as vote_avg", params).list();

	    	return result;
	    }
	    
	    //retrieving all the rentals
	    public static List<Rental> getRentals(){
	    	List<Record> list;
	    	List<Rental> rent = new ArrayList<>();
	    	
	    	try(Session session = driver.session()){
	    		list = session.readTransaction(new TransactionWork<List<Record>>() {
	    			@Override
	    			public List<Record> execute(Transaction tx) {	
	    				return matchUserFilms(tx);
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
	    
	    private static List<Record> matchUserFilms(Transaction tx){
    		List<Record> result=tx.run("MATCH (ee:Users)-[r:RENTS]-(ff:Movies)"
	    			+ " RETURN r.date AS start_date, ff.title AS title, ff.weeklyPrice AS price, ee.username AS username"
	    			).list();

	    	return result;
	    }
	    
	    //insert new film
	    public static void insertFilm(Film f) {
	    	try(Session session = driver.session()){
	    		session.writeTransaction(new TransactionWork<Void>() {
	    			@Override
	    			public Void execute(Transaction tx) {
	    				createFilm(tx, f);
	    				createGenreRelation(tx, f);
	    				return null;
	    			}
	    		});
	    	}
	    	
	    	return;
	    }
	    
	    //query to create new film
	    private static void createFilm(Transaction tx, Film f) {
	    	Map<String, Object> params = new HashMap<>();
     		params.put("title", f.getTitle());
     		params.put("genre", f.getGenre());
     		params.put("plot", f.getPlot());
     		params.put("release_date", f.getReleaseDate());
     		params.put("weekly_price", f.getWeeklyPrice());
     		params.put("production_company",f.getProductionCompany());
     		params.put("budget", f.getBudget());
     		params.put("revenue", f.getRevenue());
     		params.put("production_country", f.getProductionCountry());
     		params.put("language", f.getLanguage());
     		params.put("runtime", f.getRuntime());
     		params.put("vote_count", f.getVoteCount());
     		params.put("vote_avg", Double.toString(f.getVoteAvg()));
     		
 	    	tx.run("CREATE(a:Movies{title:$title, genres:$genre, plot:$plot,"
 	    			+ "release_date:$release_date, weeklyPrice: $weekly_price,"
 	    			+ "production_companies:$production_company,"
 	    			+ "budget: $budget, revenue:$revenue, "
 	    			+ "production_countries: $production_country,"
 	    			+ "language: $language, runtime:$runtime,"
 	    			+ "vote_count: $vote_count, vote_average: $vote_avg})", params);
 	    	
 	    	return;
	    }
	    
	    private static void createGenreRelation(Transaction tx, Film f) {
	    	Map<String, Object> params = new HashMap<>();
     		params.put("title", f.getTitle());
     		params.put("genre", f.getGenre());
     		
 	    	tx.run("MATCH (m:Movies), (g:Genre) " + 
 	    			"WHERE m.title = $title " + 
 	    			"AND m.genres CONTAINS g.type " + 
 	    			"CREATE (m)-[:IS]->(g)", params);
 	    	
 	    	return;
	    }
	    
	    //query to find the genre by movie title
	    public static String getGenreByMovie(String title) {
	    	List<Record> list;
	    	String genres = "";
	    	
	    	try(Session session = driver.session()){
	    		list = session.readTransaction(new TransactionWork<List<Record>>() {
	    			@Override
	    			public List<Record> execute(Transaction tx) {	
	    				return getFilmGenre(tx, title);
	    			}
	    		});
	    		for(Record res: list) {
	    			genres += res.get("type").asString() + ",";
		    	}
	    		if(genres.length() > 0)
	    			genres = genres.substring(0, genres.length()-1);
			}

	    	return genres;
	    }
	    
	    private static List<Record> getFilmGenre(Transaction tx, String title){
	    	Map<String, Object> params = new HashMap<>();
     		params.put("title", title);
     		List<Record> result = tx.run("MATCH (m:Movies)-[:IS]->(g:Genre)" + 
     				"WHERE m.title = $title " + 
     				"RETURN g.type AS type", params).list();
  
	    	return result;
	    }
	    
	    
	    //query to find film that have the same genre as the last rented movie
	    public static List<Film> getSuggestedGenreFilms(User u){
	    	List<Record> list;
	    	List<Film> films = new ArrayList<>();
	    	
	    	try(Session session = driver.session()){
	    		list = session.readTransaction(new TransactionWork<List<Record>>() {
	    			@Override
	    			public List<Record> execute(Transaction tx) {	
	    				return getLatestGenreRented(tx, u);
	    			}
	    		});
	    		
	    		for(Record res: list) {
	    			String title = res.get("title").asString();
					String genre = getGenreByMovie(title);
					String plot = res.get("plot").asString();
					String year = res.get("year").asString();
					int price = res.get("weeklyPrice").asInt();
					String production_companies = res.get("production_companies").asString();
					int budget = res.get("budget").asInt();
					int revenue = res.get("revenue").asInt();
					String production_country = res.get("production_countries").asString();
					String language = res.get("original_language").asString();
					int runtime = res.get("duration").asInt();
					int vote_count = res.get("vote_count").asInt();
					double vote_avg = Double.parseDouble(res.get("vote_avg").asString());
					
					films.add(new Film(title, genre, plot, year, price, production_companies, 
		    				budget, revenue, production_country, language, runtime, vote_count, vote_avg));
		    		
		    	} 
			}

	    	return films;
	    } 
	
	    private static List<Record> getLatestGenreRented(Transaction tx, User u){
	    	Map<String, Object> params = new HashMap<>();
     		params.put("username",u.getUsername());
     		List<Record> result = tx.run("MATCH (mm:Movies)-[:IS]->(gg:Genre)<-[:IS]-(m:Movies)<-[r:RENTS]-(u:Users) " + 
     				"WHERE u.username = $username  AND mm.title <> m.title " + 
     				"RETURN mm.title AS title, mm.production_countries AS production_countries, mm.production_companies AS production_company," + 
     				"mm.budget AS budget, mm.original_language AS original_language, mm.runtime AS duration," + 
     				"mm.vote_count AS vote_count, mm.revenue AS revenue," + 
     				"mm.release_date AS year, mm.overview AS plot," + 
     				"mm.weeklyPrice AS weeklyPrice, mm.vote_average AS vote_avg " + 
     				"ORDER BY r.date DESC LIMIT 10", params).list();
  
	    	return result;
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
	    	StatementResult result = tx.run("MATCH (ee:Users) WHERE ee.username <> 'admin' "
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
	    				deleteUserDetach(tx, user);
	    				return null;
	    			}
	    		});
	    	}
	    	
	    	return;
	    } 
	    
	    private static void deleteUserDetach(Transaction tx, User u) {
	    	Map<String, Object> params = new HashMap<>();
    		params.put("username", u.getUsername());
	    	tx.run("MATCH (n:Users ) WHERE n.username = $username DETACH DELETE n", params);
	    	
	    	return;
	    }
	    	    
}
