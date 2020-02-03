package task3;

import org.neo4j.driver.AuthTokens;
import org.neo4j.driver.Driver;
import org.neo4j.driver.GraphDatabase;
import org.neo4j.driver.Session;
import org.neo4j.driver.Transaction;
import org.neo4j.driver.TransactionWork;
import org.neo4j.driver.*;


public class UserEntityManager {

	    // Driver objects are thread-safe and are typically made available application-wide.
	    private final Driver driver;

	    public UserEntityManager( String uri, String user, String password )
	    {
	        driver = GraphDatabase.driver( uri, AuthTokens.basic( user, password ) );
	    }

	    public void close() throws Exception
	    {
	        driver.close();
	    }

	    public void printGreeting( final String message )
	    {
	        try ( Session session = driver.session() )
	        {
	            String greeting = session.writeTransaction( new TransactionWork<String>()
	            {
	                @Override
	                public String execute( Transaction tx )
	                {
	                    StatementResult result = tx.run( "CREATE (a:Greeting) " +
	                                                     "SET a.message = $message " +
	                                                     "RETURN a.message + ', from node ' + id(a)",
	                            parameters( "message", message ) );
	                    return result.single().get( 0 ).asString();
	                }
	            } );
	            System.out.println( greeting );
	        }
	    }

	    public static void main( String... args ) throws Exception
	    {
	        UserEntityManager greeter = new UserEntityManager( "bolt://localhost:7687", "neo4j", "password" );
	        greeter.printGreeting( "hello, world" );
	    }
}
