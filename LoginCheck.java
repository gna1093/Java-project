import java.sql.*;

public class LoginCheck
{
	static boolean check(String u,String p)
	{
		try
		{
			Class.forName("sun.jdbc.odbc.JdbcOdbcDriver");
			Connection c = DriverManager.getConnection("jdbc:odbc:VAULT");
			System.out.println("connection to DB successfully..!!!!");
			Statement s = c.createStatement();
			String code = "SELECT username,password FROM login";
			ResultSet rec = s.executeQuery(code);
			while(rec.next())
			{
				if(u.equals(rec.getString("username")) && p.equals(rec.getString("password")))
				return true;
			}
			return false;
		}
		catch(Exception e)
		{
			System.out.println(e);
		}
		return false;
	}
}