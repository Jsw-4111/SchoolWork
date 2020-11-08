import java.sql.*;
import java.io.*;

class jdbcMySQL
{
	public static void main(String[] args) {
		Connection conn = null;
		try {
		    // db parameters
		    String url       = "jdbc:mysql://129.244.40.38:3306/user57";
		    String user      = "user57";
		    String password  = "Tuhurricane-iran";
		    // create a connection to the database
		    conn = DriverManager.getConnection(url, user, password);

		    // Substitute your userId and your password in the following statement
		    // conn = DriverManager.getConnection("jdbc:mysql://129.244.40.38:3306/user57","user57", "Tuhurricane-iran");
		    // more processing here
		    Statement stmt = conn.createStatement (); // Create a Statement

		    // Replace the following query text with the SQL query corresponding to the query in the assignment 
			String qry = 	"select ID from users where ID in " + 
							"(select ID1 " +
							"from friends, users b, users c " + 
							"where ID1 = b.ID AND ID2 = c.ID AND b.Gender != c.Gender)";

		    // All the records after executing "qry" are fetched a ResultSet rset.
		    ResultSet rset = stmt.executeQuery (qry);
		    
		    // Go through the records one at a time...
		    int i = 0;
		    while (rset.next ()){
		    	String row = rset.getString (1);
		    	System.out.println(" " + row);       
		    	i++;
		    }
			System.out.println("\nNumber of records fetched: " + i +" \n");
		} catch(SQLException e) {
		   	System.out.println(e.getMessage());
		} finally {
		 try{
		        if(conn != null)
					 conn.close();
		 }catch(SQLException ex){
		           System.out.println(ex.getMessage());
		 }
		}
		
	}
}
