import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import java.sql.*;

public class SettingsGUI extends JFrame implements ActionListener
{
	String username;
	JPasswordField org,new1,new2;
	JLabel curPass,newPass1,newPass2,heading;
	JButton save,close;
	JPanel panel1,panel2;
	Statement s;
	Connection c;
	
	public SettingsGUI(String username)
	{
		try
		{
			Class.forName("sun.jdbc.odbc.JdbcOdbcDriver");
			c = DriverManager.getConnection("jdbc:odbc:DiaryDB");
			s = c.createStatement();
		}
		catch(Exception e)
		{
			System.out.println(e);
			displayError("ERROR : SQL Connection");
		}
		this.username = username;
		curPass = new JLabel("Current Password : ");
		newPass1 = new JLabel("New Password : ");
		newPass2 = new JLabel("Re-enter Password : ");
		heading = new JLabel("Change Password : "+username);
		panel1 = new JPanel();
		panel2 = new JPanel();
		org = new JPasswordField(10);
		new1 = new JPasswordField(10);
		new2 = new JPasswordField(10);
		save = new JButton("Save");
		close = new JButton("Close");
		
		panel1.setLayout(new GridLayout(3,2,10,10));
		panel1.add(curPass);
		panel1.add(org);
		panel1.add(newPass1);
		panel1.add(new1);
		panel1.add(newPass2);
		panel1.add(new2);
		
		panel2.setLayout(new FlowLayout(FlowLayout.RIGHT));
		panel2.add(save);
		panel2.add(close);
		
		save.addActionListener(this);
		close.addActionListener(this);
		
		getContentPane().add(panel1,BorderLayout.CENTER);
		getContentPane().add(panel2,BorderLayout.SOUTH);
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(300,250);
		setLocationRelativeTo(null);
		setVisible(true);
	}
	
	public void actionPerformed(ActionEvent e)
	{
		if(e.getSource() == close)
		{
			hide();
			PersonalVaultGUI gui = new PersonalVaultGUI(username);
			return;
		}
		String orgPassword,pass1,pass2,original;		
		orgPassword = org.getText();
		pass1 = new1.getText();
		pass2 = new2.getText();
		if(orgPassword == null || orgPassword.equals(""))
		{
			displayError("Enter Password First .");
			org.requestFocus();
		}
		else if(pass1 == null || pass1.equals("") )
		{
			displayError("Enter New Password .");
			new1.requestFocus();
		}
		else if(pass2 == null || pass2.equals("") )
		{
			displayError("Enter New Re - Password .");
			new2.requestFocus();
		}
		else if(!pass1.equals(pass2))
		{
			displayError("Your Passwords did not match!");
			new1.requestFocus();
		}
		else
		{
			try
			{
				ResultSet r = s.executeQuery("SELECT username,password FROM login WHERE username='"+username+"'");
				while(r.next())
				{
					String u,p;
					u = r.getString("username");
					p = r.getString("password");
					if(u.equals(username) && p.equals(orgPassword));
					{
						s.execute("UPDATE login SET password='"+pass1+"' WHERE username='"+u+"'");
						JOptionPane.showMessageDialog(null,"Password Changed","Info",JOptionPane.INFORMATION_MESSAGE);
						return;
					}
				}
				displayError("Wrong Password Entered.");
			}
			catch(Exception ex)
			{
				System.out.println(ex);
				displayError("ERROR : IO Error");
			}
		}
	}
	
	private void displayError(String err)
	{
		JOptionPane.showMessageDialog(null,err,"ERROR",JOptionPane.ERROR_MESSAGE);
	}
}