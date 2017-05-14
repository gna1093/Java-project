import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import java.sql.*;
import java.util.Random;

public class PasswordHelper implements ActionListener
{
	JFrame frame;
	JLabel question;
	JTextField answer;
	JButton submit; 
	JPanel panel;
	String ans,username;
	
	
	PasswordHelper(String username)
	{
		frame = new JFrame("Password Helper");
		submit = new JButton("submit");
		question = new JLabel("           ");
		panel = new JPanel();
		answer = new JTextField(10);
		this.username = username;
		setQuestion();
		
		panel.setLayout(new GridLayout(1,2,10,10));
		panel.add(question);
		panel.add(answer);
		
		frame.getContentPane().add(panel);
		frame.getContentPane().add(submit,BorderLayout.SOUTH);
		submit.addActionListener(this);
		
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(400,100);
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}
	
	private void setQuestion()
	{
		try
		{
			Class.forName("sun.jdbc.odbc.JdbcOdbcDriver");
			Connection c = DriverManager.getConnection("jdbc:odbc:VAULT");
			Statement s = c.createStatement();
			s.executeQuery("SELECT * FROM login WHERE username='"+username+"'");
			ResultSet r = s.getResultSet();
			r.next();
			question.setText(r.getString("question"));
			ans = new String(r.getString("answer"));
			s.close();
		}
		catch(Exception ex)
		{
			displayError("ERROR : Database Corrupt.");
			System.out.println(ex);
		}
	}
	
	private void displayError(String err)
	{
		JOptionPane.showMessageDialog(null,err,"ERROR",JOptionPane.ERROR_MESSAGE);
	}
	
	public void actionPerformed(ActionEvent e)
	{
		if(answer.getText().equals(ans))
		{
			Random r = new Random();
			String s = new String();
			for(int i=0;i<8;i++)
			{
				s = (r.nextInt(9)+1) + s;
			}
			try
			{
				Class.forName("sun.jdbc.odbc.JdbcOdbcDriver");
				Connection c = DriverManager.getConnection("jdbc:odbc:VAULT");
				Statement st = c.createStatement();
				st.execute("UPDATE login SET password='"+s+"' WHERE username='"+username+"'");
				st.close();
			}
			catch(Exception ex)
			{
				displayError("Sorry, Something went wrong!\nPlease Try Again.");
				System.out.println(ex);
				return;
			}
			JOptionPane.showMessageDialog(null,"New Password : "+s,"Password Changed !",JOptionPane.INFORMATION_MESSAGE);
			frame.hide();
			new VaultLogin();
		}
		else
		{
			displayError("Invalid Answer");
		}
	}
}