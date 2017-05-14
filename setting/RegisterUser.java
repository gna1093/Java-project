import java.awt.*;
import javax.swing.*;
import java.sql.*;
import java.awt.event.*;
import java.util.Random;

public class RegisterUser implements ActionListener
{
	String username,password,question,answer;
	JFrame frame;
	JPanel panel1,panel2;
	JLabel lab[],heading;
	JTextField u,q;
	JPasswordField p,a;
	JButton submit,clear;
	
	public RegisterUser()
	{
		panel1 = new JPanel();
		panel2 = new JPanel();
		submit = new JButton("Submit");
		clear = new JButton("Clear");
		lab = new JLabel[4];
		heading = new JLabel("Register New Vault",JLabel.CENTER);
		lab[0] = new JLabel("Username");
		lab[1] = new JLabel("Password");
		lab[2] = new JLabel("Secret Question");
		lab[3] = new JLabel("Secret Answer");
		u = new JTextField();
		q = new JTextField();
		p = new JPasswordField();
		a = new JPasswordField();
		frame = new JFrame();
		
		panel1.setLayout(new GridLayout(4,4,10,10));
		panel1.add(lab[0]);
		panel1.add(u);
		panel1.add(lab[1]);
		panel1.add(p);
		panel1.add(lab[2]);
		panel1.add(q);
		panel1.add(lab[3]);
		panel1.add(a);
		
		panel2.setLayout(new FlowLayout(FlowLayout.RIGHT));
		panel2.add(submit);
		panel2.add(clear);
		
		submit.addActionListener(this);
		clear.addActionListener(this);
		
		Font f1 = new Font("Calibri",Font.PLAIN,26);
		heading.setFont(f1);
		
		Font f2 = new Font("Consolas",Font.PLAIN,18);
		submit.setFont(f2);
		clear.setFont(f2);
		lab[0].setFont(f2);
		lab[1].setFont(f2);
		lab[2].setFont(f2);
		lab[3].setFont(f2);
		u.setFont(f2);
		p.setFont(f2);
		q.setFont(f2);
		a.setFont(f2);	
		
		//frame.getContentPane().add(menuBar);
		frame.getContentPane().add(heading,BorderLayout.NORTH);
		frame.getContentPane().add(panel1);
		frame.getContentPane().add(panel2,BorderLayout.SOUTH);
		frame.setSize(570,300);
		frame.setResizable(false);
		frame.setLocationRelativeTo(null);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}
	
	private int generateKey()
	{
		Random r = new Random();
		return 10+r.nextInt(90);
	}
	
	public void actionPerformed(ActionEvent event)
	{
		if(event.getSource() == submit)
		{
			if(u.getText().equals("") || p.getText().equals("") || q.getText().equals("") || a.getText().equals(""))
			{
				displayError("Please Enter Data in All fields first.");
				return;
			}
			else
			{
				int encKey = generateKey();
				username = u.getText();
				password = p.getText();
				question = q.getText();
				answer = a.getText();
				try
				{
					Class.forName("sun.jdbc.odbc.JdbcOdbcDriver");
					Connection c = DriverManager.getConnection("jdbc:odbc:VAULT");
					Statement s = c.createStatement();
					s.execute("INSERT INTO login ( username , password , question , answer , EncKey ) VALUES ( '"+username+"' , '"+password+"' , '"+question+"' , '"+answer+"' , '"+encKey+"' )");
					s.close();
					JOptionPane.showMessageDialog(null,username+", Your Account Has been created.","Account Created",JOptionPane.INFORMATION_MESSAGE);
					frame.hide();
					new VaultLogin();
				}
				catch(Exception ex)
				{
					displayError("ERROR : Cannot insert data into database");
					System.out.println(ex);
				}
			}
		}
		else if(event.getSource() == clear)
		{
			u.setText("");
			p.setText("");
			q.setText("");
			a.setText("");
			//displayError("Width : "+frame.getWidth()+"\tHeight : "+frame.getHeight());
		}
	}
	
	public static int getKey(String username,String password)
	{
		try
		{
			Class.forName("sun.jdbc.odbc.JdbcOdbcDriver");
			Connection c = DriverManager.getConnection("jdbc:odbc:VAULT");
			Statement s = c.createStatement();
			ResultSet r = s.executeQuery("SELECT * FROM LOGIN WHERE username = '"+username+"' AND password = '"+password+"'");
			if(r.next())
			{
				int key = Integer.parseInt(r.getString("EncKey"));
				System.out.println("Returning Encryption  Key : "+key);
				return key;
			}
			else
			{
				System.out.println("Encryption Key Not Found in Database for user : "+username);
			}
		}
		catch(Exception ex)
		{
			System.out.println("Error Retreiving Key : "+ex);
		}
		return 0;
	}
	
	public static String[] getPrefs(String username,String password)
	{
		String[] prefs = null;
		try
		{	
			Class.forName("sun.jdbc.odbc.JdbcOdbcDriver");
			Connection c = DriverManager.getConnection("jdbc:odbc:VAULT");
			Statement s = c.createStatement();
			ResultSet r = s.executeQuery("SELECT * FROM LOGIN WHERE username = '"+username+"' AND password = '"+password+"'");
			String id = new String();
			if(r.next())
			{
				id = r.getString("ID");
			}
			r = s.executeQuery("SELECT * FROM prefs WHERE ID = '"+id+"'");
			if(r.next())
			{
				prefs = new String[2];
				prefs[0] = new String(r.getString("font"));
				prefs[1] = new String(r.getString("size"));
				if(prefs[0] == null || prefs[1] == null || prefs[0].equals("") || prefs[1].equals(""))
				{
					System.out.println("No preferences found for user : "+username);
					return null;
				}
				else
					return prefs;
			}
			else
			{
				System.out.println("No preferences found for user : "+username);
					return null;
			}
		}
		catch(Exception e)
		{
			System.out.println("Error Reading Preferences : "+e);
		}
		return null;
	}
	
	public static boolean savePrefs(String u,String p,String font,String size)
	{
		System.out.println("Setting preferences , font : "+font+" , size : "+size);
		String id = new String("0");
		try
		{
			Class.forName("sun.jdbc.odbc.JdbcOdbcDriver");
			Connection c = DriverManager.getConnection("jdbc:odbc:VAULT");
			Statement s = c.createStatement();
			ResultSet rs = s.executeQuery("SELECT * FROM LOGIN where username= '"+u+"' AND password = '"+p+"' ");
			if(rs.next())
			{
				id = rs.getString("ID");
			}
			else
			{
				System.out.println("ERROR Saving Prefs for user : "+u+" : Retreiving ID");
				return false;
			}
			s.execute("INSERT INTO prefs ( font , size ) VALUES ( '"+font+"' , '"+size+"' ) WHERE id = '"+id+"';"); 
			s.close();
			c.close();
			return true;
		}
		catch(Exception e)
		{
			System.out.println("Error Saving Preferences for user "+u+" : "+e);
		}
		return false;
	}
	
	private void displayError(String err)
	{
		JOptionPane.showMessageDialog(null,err,"ERROR",JOptionPane.ERROR_MESSAGE);
	}
}