import java.awt.*;
import javax.swing.*;
import java.io.*;
import javax.swing.event.DocumentListener;
import javax.swing.event.DocumentEvent;
import java.awt.event.*;
import java.util.Calendar;
import java.text.DateFormat;
import java.sql.*;

public class PersonalVaultGUI implements ActionListener,ItemListener,DocumentListener
{
	JFrame frame;
	JPanel panel1,panel2,panel3,panel4;
	Choice font,size;
	JTextArea diary;
	JLabel statusLabel;
	JButton save,logout,settings;
	String filename,password;
	
	boolean edited,saved;
	
	public PersonalVaultGUI(String filename,String password)
	{
		this.filename = filename;
		this.password = password;
		panel1 = new JPanel();
		panel2 = new JPanel();
		panel3 = new JPanel();
		//panel4 = new JPanel();
		font = new Choice();
		size = new Choice();
		diary = new JTextArea();
		statusLabel = new JLabel("                                ");
		save = new JButton("Save");
		settings = new JButton("Settings");
		logout = new JButton("Logout");
		frame = new JFrame("Personal Vault : "+filename);
		
		JLabel f,s;
		JPanel fp = new JPanel(),dp = new JPanel();
		f = new JLabel("Font : ",JLabel.RIGHT);
		s = new JLabel("Size : ",JLabel.RIGHT);
		font.add("Consolas");
		font.add("Calibri");
		font.add("Constantia");
		font.add("Corbel");
		font.add("Courier");
		font.add("Comic Sans MS");
		font.addItemListener(this);
		fp.setLayout(new GridLayout(1,4,5,5	));
		fp.add(f);
		fp.add(font);
		fp.add(s);
		fp.add(size);
		dp.setLayout(new GridLayout(1,2,5,5));
		//dp.add(d);
		for(int i=0;i<20;i+=2)
			size.add((14+i)+"");
		panel1.setLayout(new GridLayout(2,1,10,10));
		panel1.add(dp);
		panel1.add(fp);
		
		panel2.setLayout(new BorderLayout());
		panel2.add(diary);
		
		panel3.setLayout(new FlowLayout(FlowLayout.RIGHT));
		panel3.add(statusLabel);
		panel3.add(save);
		panel3.add(settings);
		panel3.add(logout);
		
		save.addActionListener(this);
		logout.addActionListener(this);
		settings.addActionListener(this);
		
		font.addItemListener(this);
		size.addItemListener(this);
		
		diary.getDocument().addDocumentListener(this);
		diary.setText(readData().toString());
		
		readPrefs();
		
		frame.getContentPane().add(panel1,BorderLayout.NORTH);
		frame.getContentPane().add(panel2);
		frame.getContentPane().add(panel3,BorderLayout.SOUTH);
		frame.setLocationRelativeTo(null);
		frame.setSize(500,500);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}
	
	public void actionPerformed(ActionEvent e)
	{
		if(e.getSource() == save)
		{
			saveData();
			showStatus("Content Saved..");
			saved = true;
			edited = false;
		}
		else if(e.getSource() == logout)
		{
			if(edited && !saved)
			{
				int option = JOptionPane.showConfirmDialog(null,"Are you Sure ?","Message",JOptionPane.YES_NO_OPTION);
				if(option == JOptionPane.OK_OPTION)
				{
					frame.hide();
					//savePrefs();
					VaultLogin l = new VaultLogin();
				}
				else
					return;
			}
			else if(!edited)
			{
				frame.hide();
				//savePrefs();
				VaultLogin l = new VaultLogin();
			}
		}
		else if(e.getSource() == settings)
		{
			frame.hide();
			SettingsGUI sg = new SettingsGUI(filename,password);
			showStatus("Settings .. opening");
		}
	}
	
	private void showStatus(String msg)
	{
		statusLabel.setText(msg);
	}
	
	public void insertUpdate(DocumentEvent e)
	{
		setEdited();
	}
	
	public void changedUpdate(DocumentEvent e)
	{
		setEdited();
	}
	
	public void removeUpdate(DocumentEvent e)
	{
		setEdited();
	}
	
	private void setEdited()
	{
		edited = true;
		saved  = false;
	}
	
	public void itemStateChanged(ItemEvent e)
	{
		setEdited();
		Font f = new Font(font.getSelectedItem(),Font.PLAIN,Integer.parseInt(size.getSelectedItem()));
		diary.setFont(f);
	}
	
	private void saveData()
	{
		try
		{
			BufferedWriter bw = new BufferedWriter(new FileWriter(new File(filename+".dia")));
			bw.write(encrypt(diary.getText()));
			boolean x = RegisterUser.savePrefs(filename,password,font.getSelectedItem(),size.getSelectedItem());
			if(x)
			{
				showStatus("Preferences Saved.");
			}
			else
			{
				showStatus("Preferences could not be saved.");
			}
			bw.flush();
			bw.close();
		}
		catch(Exception ex)
		{
			System.out.println("Data Save Failed : "+ex);
		}
	}
	
	private StringBuffer readData()
	{
		try
		{
			BufferedReader br = new BufferedReader(new FileReader(new File(filename+".dia")));
			String temp;
			int key = RegisterUser.getKey(filename,password);
			StringBuffer sb = new StringBuffer();
			sb.ensureCapacity(1024);
			while( (temp = br.readLine()) != null)
			{
				for(int i=0;i<temp.length();i++)
				{
					sb.append((char)(temp.charAt(i)^key));
				}
				sb.append("\n");
			}
			return sb;
		}
		catch(FileNotFoundException e)
		{
			try
			{
				FileWriter fw = new FileWriter(new File(filename+".dia"));
				frame.hide();
				PersonalVaultGUI gui = new PersonalVaultGUI(filename,password);
				return new StringBuffer("");
			}
			catch(IOException ex)
			{
				System.out.println(ex);
			}
		}
		catch(Exception e)
		{
			System.out.println(e);
		}
		return null;
	}
	
	private String encrypt(String data)
	{
		int key = RegisterUser.getKey(filename,password);
		StringBuffer sb  = new StringBuffer();
		sb.ensureCapacity(1024);
		for(int i=0;i<data.length();i++)
		{
			sb.append((char)(data.charAt(i)^key));
		}
		System.out.println("Data Encrypted.");
		return sb.toString();
	}
	
	private void readPrefs()
	{
		String[] prefs = RegisterUser.getPrefs(filename,password);
		if(prefs!=null)
		{
			font.select(prefs[0]);
			size.select(prefs[1]);
			Font f = new Font(prefs[0],Font.PLAIN,Integer.parseInt(prefs[1]));
			diary.setFont(f);
		}
		else
			setDefaultPrefs();
	}
	
	private void setDefaultPrefs()
	{
		System.out.println("Setting Default Preferences");
		Font f = new Font(font.getSelectedItem(),Font.PLAIN,Integer.parseInt(size.getSelectedItem()));
		diary.setFont(f);
	}
	
	private void displayError(String err)
	{
		JOptionPane.showMessageDialog(null,err,"ERROR",JOptionPane.ERROR_MESSAGE);
	}
}