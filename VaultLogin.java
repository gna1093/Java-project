import java.awt.*;
import javax.swing.*;
import java.awt.event.*;

public class VaultLogin implements ActionListener
{
	JFrame frame;
	JPanel panel1,panel2,panel3;
	JTextField username;
	JPasswordField password;
	JLabel lab1,lab2,heading;
	JButton login,register,forgot;
	
	VaultLogin()
	{
		frame = new JFrame("Vault Login");
		panel1 = new JPanel();
		panel2 = new JPanel();
		panel3 = new JPanel();
		username = new JTextField(10);
		password = new JPasswordField(10);
		lab1 = new JLabel("Username");
		lab2 = new JLabel("Password");
		heading = new JLabel("Vault Login");
		login = new JButton("login");
		forgot = new JButton("forgot password ?");
		register = new JButton("register new vault");
		
		login.addActionListener(this);
		register.addActionListener(this);
		forgot.addActionListener(this);
		
		initialize();
	}
	
	public void initialize()
	{
		Font f1 = new Font("Consolas",Font.PLAIN,18);
		login.setFont(f1);
		forgot.setFont(f1);
		register.setFont(f1);
		username.setFont(f1);
		password.setFont(f1);
		lab1.setFont(f1);
		lab2.setFont(f1);
		
		Font f2 = new Font("Calibri",Font.PLAIN,25);
		heading.setFont(f2);
		
		panel1.setLayout(new GridLayout(2,2,10,10));
		panel1.add(lab1);
		panel1.add(username);
		panel1.add(lab2);
		panel1.add(password);
		
		panel2.setLayout(new FlowLayout());
		panel2.add(login);
		panel2.add(register);
		panel2.add(forgot);
		
		panel3.setLayout(new FlowLayout(FlowLayout.CENTER));
		panel3.add(heading);
		
		frame.setLayout(new FlowLayout());
		frame.getContentPane().add(panel3);
		frame.getContentPane().add(panel1);
		frame.getContentPane().add(panel2);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLocationRelativeTo(null);
		frame.setSize(500,169);
		frame.setResizable(false);	
		frame.setVisible(true);
	}
	
	public void actionPerformed(ActionEvent e)
	{
		if(e.getSource() == login)
		{
			if(LoginCheck.checkCredentials(username.getText(),password.getText()))
			{
				frame.hide();
				System.out.println("Logged in as : "+username.getText());
				JOptionPane.showMessageDialog(null,"You are now logged in","Message",JOptionPane.INFORMATION_MESSAGE);
				PersonalVaultGUI gui = new PersonalVaultGUI(username.getText(),password.getText());
			}
			else
			{
				System.out.println("Invalid Username/Password");
				displayError("Invalid Credentials");
			}
		}
		else if(e.getSource() == register)
		{
			frame.hide();
			RegisterUser r = new RegisterUser();
		}
		else
		{
			String u = username.getText();
			if(u == null || u.equals(""))
			{
				displayError("Enter your Username in the username field first...");
			}
			else
			{
				frame.hide();
				new PasswordHelper(u);
			}
		}
	}
	
	private void displayError(String err)
	{
		JOptionPane.showMessageDialog(null,err,"ERROR",JOptionPane.ERROR_MESSAGE);
	}
}