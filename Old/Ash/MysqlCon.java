package com.srh_heidelberg.mealsanddeals;

import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.util.*;

import com.srh_heidelberg.mealsanddeals.AdminHomePage;

/*
 * All database interactions are taking place here
 */

	// Database connection information method
public class MysqlCon{
 
	private static final String URL = "jdbc:mysql://localhost:33061/mealsanddeals";
    private static final String USER = "root";
    private static final String PASSWORD = "Foodserviceapplication1@";
    private static final String DRIVER_CLASS = "com.mysql.cj.jdbc.Driver"; 
	
    static {
        try {
			Class.forName(DRIVER_CLASS);
		} catch (ClassNotFoundException ex) {
			ex.printStackTrace();
		}
    }

    	// Database Connection method
	public static Connection createConn() throws SQLException {
		return DriverManager.getConnection(URL, USER, PASSWORD);
	}
	
		// Database connection closing
	public static void closeConn(Connection conn) {
		if(conn!=null) {
			try {
				if(!conn.isClosed())
					conn.close();
			} catch (SQLException ex) {
				System.out.println("Connection is null!");
				ex.printStackTrace();
			}
		}
	}

		// Statement closing 
	public static void closePrStmt(PreparedStatement prStmt) {
		if(prStmt!=null) {
			try {
				if(!prStmt.isClosed())
					prStmt.close();
			} catch (SQLException ex) {
				System.out.println("PreparedStatement is null!");
				ex.printStackTrace();
			}
		}
	}

		// Result Set closing
	public static void closeRs(ResultSet rs) {
		if(rs!=null) {
			try {
				if(!rs.isClosed())
					rs.close();
			} catch (SQLException e) {
				System.out.println("ResultSet is null!");
				e.printStackTrace();
			}
		}
	}

		// Method for inserting Data to database which take parameters from respective classes and make the query 
	public static void insertToTable(String tablename , ArrayList<String> detail) throws NoSuchAlgorithmException {
		
		Connection conn = null;
		PreparedStatement prStmt = null;
		ResultSet rs = null;

		
		int i = 0;
		
		String query = "";
		
		while(i < detail.size()) {

			if(i < detail.size()-1) {

				query = query + "'" + detail.get(i) + "', ";

			} else if (i == detail.size() - 1) {

				query = query + "'" + detail.get(i) + "'";

			}

			i++;
		}
			try {
				conn = createConn();
				prStmt = conn.prepareStatement("insert into "+ tablename +" values ("+query+")");
				prStmt.executeUpdate();
				MealsandDeals.main(null);
				
			} catch (SQLException ex) {
				ex.printStackTrace();
				System.err.println(ex.getMessage());
			} finally {
				closeRs(rs);
				closePrStmt(prStmt);
				closeConn(conn);
			}

	}

	// Login method for fetching Data from database from user input 
	// Also checking validation of Food Service Agents
	// Method is general for all logins
	public static void login(String tablename , ArrayList<String> detail){

		Connection conn = null;
		PreparedStatement prStmt = null;
		ResultSet rs = null;
		String tablename1 = null;
		tablename1 = tablename.toLowerCase();
		String query = null;
		String username = null;

		if(tablename1.equals("admin")) 
		{
				query = "select * from "+tablename1+" where username='"+detail.get(0)+"' and password='"+detail.get(1)+"'";
				username = "username";
			} else if(tablename1.equals("fsa")) {
				query = "select * from "+tablename1+" where fsausername='"+detail.get(0)+"' and password='"+detail.get(1)+"'";
				
				username = "fsausername";
			} else if(tablename1.equals("customer")) {
				query = "select * from "+tablename1+" where customerusername='"+detail.get(0)+"' and password='"+detail.get(1)+"'";
				username = "customerusername";
		}

		try{  

			conn = createConn();
			prStmt = conn.prepareStatement(query);  
			rs = prStmt.executeQuery();

			if(rs.next()) {
				if(tablename1.equals("fsa")) 
				{		
					if(rs.getString("validation").equals("Active")) 
					{
						Customer loggedInFsa = new Customer();
						loggedInFsa.setName(rs.getString("name"));
						loggedInFsa.setSex(rs.getString("sex"));
						loggedInFsa.setBirthday(rs.getString("Birthday"));
						loggedInFsa.setAddress(rs.getString("address"));
						loggedInFsa.setCustomerusername(rs.getString("customerusername"));
						loggedInFsa.setEmail(rs.getString("email"));
						loggedInFsa.setNationality(rs.getString("nationality"));
						loggedInFsa.setPhonenumber(rs.getString("phonenumber"));
						//CustomerMain.main(loggedInFsa);
					} 
					else
					{
						System.out.println("Your Account is not Active yet..");
						ProfileUpdate.fsaprofileUpdate(rs.getString(username));
						MealsandDeals.main(null);
					}
				} 
				else if(tablename1.equals("customer")) 
				{
					Customer loggedInCustomer = new Customer();
					loggedInCustomer.setName(rs.getString("name"));
					loggedInCustomer.setSex(rs.getString("sex"));
					loggedInCustomer.setBirthday(rs.getString("Birthday"));
					loggedInCustomer.setAddress(rs.getString("address"));
					loggedInCustomer.setCustomerusername(rs.getString("customerusername"));
					loggedInCustomer.setEmail(rs.getString("email"));
					loggedInCustomer.setNationality(rs.getString("nationality"));
					loggedInCustomer.setPhonenumber(rs.getString("phonenumber"));
					CustomerMain.main(loggedInCustomer);
				} 
				else if(tablename1.equals("admin"))
				{
					
					AdminHomePage.homePage();
				}
			}
			else if(!rs.next()) 
			{
				System.out.print("Username or Password is not Correct");
				MealsandDeals.main(null);
			}
		}
		catch(Exception ex)
		{
			System.out.println(ex);
			ex.printStackTrace();
			System.err.println(ex.getMessage());
		} 
		finally 
		{
			closeRs(rs);
			closePrStmt(prStmt);
			closeConn(conn);
		}
	}
		// In order to keep the list of inactive Food Service Agents' Username for updating query
	public static ArrayList<String> userslistWithBothCertificates = new ArrayList<String>();

		// Method for fetching Inactive Food Service Agents with both certificate validated
	public static void inactiveUsers() {

		Connection conn = null;
		PreparedStatement prStmt = null;
		ResultSet rs = null;
		ArrayList<String> userlist = new ArrayList<String>();
		
		try{  

			conn = createConn();
			prStmt = conn.prepareStatement("select * from fsa where cookingcertificate='Yes' and businesscertificate='Yes' and validation='Inactive'");  
			rs = prStmt.executeQuery();
        	Scanner input = new Scanner(System.in);
			int j = 0;
			int i = 1;
			while(rs.next()) {
				while (i <= rs.getRow()) {
					System.out.println(rs.getRow() +" - "+rs.getString("fsausername")+"    Both Certificates Validated ["+rs.getRow()+"]");
					System.out.println("-----------------------------------------------------");
					userlist.add(rs.getString("fsausername"));
					j = rs.getRow();
					i++;
				}
			}
			userslistWithBothCertificates = userlist;
			userslistDelete = userlist;
			decisionMaker = "bothcertificate";
			int k = 0;
			if (k < j) {
				System.out.println("Back [0]");
	        	System.out.println("Select Food Service Agent's number to see options or Select 0 to go back...");
	        	String option = null;
	        	option = input.nextLine();
	        	int rowNumber = Integer.parseInt(option);
	        	if(rowNumber == 0) {
	            	AdminHomePage.homePage();
	        	} else if(rowNumber != 0) {
	            	adminDecisionValidation(rowNumber);
	        	}
			} else if(k == j) {
				System.out.println("There is no Inactive Food Service Agent!!! Press 0 to go to Admin Home Page...");
				System.out.println("Back [0]");
	        	String option = null;
	        	option = input.nextLine();
	        	int rowNumber = Integer.parseInt(option);
	        	if(rowNumber == 0) {
	            	AdminHomePage.homePage();
	        	} else if(rowNumber != 0) {
	        		adminDecisionValidation(rowNumber);
	        	}	
			}
        	input.close();
		}
		catch(Exception ex)
		{
			System.out.println("There is no Inactive Food Service Agent");
			System.out.println(ex);
			ex.printStackTrace();
			System.err.println(ex.getMessage());
		} 
		finally 
		{
			closeRs(rs);
			closePrStmt(prStmt);
			closeConn(conn);
			
		}
	}

		// Updating Food Service Agent table in database for activating Food Service Agents
	public static void userActivation(int rowNumber) {

		Connection conn = null;
		PreparedStatement prStmt = null;
		ResultSet rs = null;
				
		try{  

			conn = createConn();
			prStmt = conn.prepareStatement("update fsa set validation = 'Active' where fsausername='"+userslistWithBothCertificates.get(rowNumber-1)+"'");  
			prStmt.executeUpdate();
			inactiveUsers();
		}
		catch(Exception ex)
		{
			System.out.println("There is no Inactive Food Service Agent");
			System.out.println(ex);
			ex.printStackTrace();
			System.err.println(ex.getMessage());
		} 
		finally 
		{
			closeRs(rs);
			closePrStmt(prStmt);
			closeConn(conn);
		}

	}
	
	public static void clear() {
		System.out.print("\033[H\033[2J");
		System.out.flush();
	}


public static void registeringCheck(String emailId){

	Connection conn = null;
	PreparedStatement prStmt = null;
	ResultSet rs = null;
	PreparedStatement prStmt1 = null;
	try{  

		conn = createConn();
		prStmt = conn.prepareStatement("select * from emailconfirmation where email='"+emailId+"'");  
		rs = prStmt.executeQuery();
		while(rs.next()) {
			System.out.println("You have Registered already with this E-Mail address");
			MealsandDeals.main(null);
		}

		if(!rs.next()) {
			prStmt1 = conn.prepareStatement("insert into emailconfirmation values ('"+emailId+"')");  
			prStmt1.executeUpdate();
    		EmailConfirmation.main(null, emailId);

			}
	}
	catch(Exception ex)
	{
//		System.out.println(ex);
//		ex.printStackTrace();
//		System.err.println(ex.getMessage());
	} 
	finally 
	{
		closePrStmt(prStmt1);
		closeRs(rs);
		closePrStmt(prStmt);
		closeConn(conn);
	}
}


public static String checkingDups(String tablename , String enteredValue , String columnLabel){

	Connection conn = null;
	PreparedStatement prStmt = null;
	ResultSet rs = null;
	PreparedStatement prStmt1 = null;
	try{  

		conn = createConn();
		prStmt = conn.prepareStatement("select * from "+tablename+" where "+columnLabel+"='"+enteredValue+"'");  
		rs = prStmt.executeQuery();
		while(rs.next()) {
			enteredValue = "";
		}

/*		if(!rs.next()) {
			enteredValue = "";
		}*/
	}
	catch(Exception ex)
	{
		System.out.println(ex);
		ex.printStackTrace();
		System.err.println(ex.getMessage());
	} 
	finally 
	{
		closePrStmt(prStmt1);
		closeRs(rs);
		closePrStmt(prStmt);
		closeConn(conn);
	}
	return enteredValue;
}

public static ArrayList<String> userslistWithoutCookingCert = new ArrayList<String>();
public static ArrayList<String> userslistWithoutBusinessCert = new ArrayList<String>();
public static ArrayList<String> userslistWithoutCertificate = new ArrayList<String>();
public static ArrayList<String> userslistDelete = new ArrayList<String>();
public static String decisionMaker = null;
public static String emailId = null;
public static void inactiveUsersWithoutCookingCert() {

	Connection conn = null;
	PreparedStatement prStmt = null;
	ResultSet rs = null;
	ArrayList<String> userlist = new ArrayList<String>();
	
	try{  

		conn = createConn();
		prStmt = conn.prepareStatement("select * from fsa where cookingcertificate='No' and businesscertificate='Yes' and validation='Inactive'");  
		rs = prStmt.executeQuery();
    	Scanner input = new Scanner(System.in);
		int j = 0;
		int i = 1;
		while(rs.next()) {
			while (i <= rs.getRow()) {
				System.out.println(rs.getRow() +" - "+rs.getString("fsausername")+"    Without Cooking Certificate ");
				System.out.println("-----------------------------------------------------");
				userlist.add(rs.getString("fsausername"));
				emailId = rs.getString("email");
				j = rs.getRow();
				i++;
			}
		}
		decisionMaker = "nocooking";
		userslistWithoutCookingCert = userlist;
		userslistDelete = userlist;
		int k = 0;
		if (k < j) {
			System.out.println("Back [0]");
        	System.out.println("Select Food Service Agent's number to see the option or Select 0 to go back...");
        	String option = null;
        	option = input.nextLine();
        	int rowNumber = Integer.parseInt(option);
        	if(rowNumber == 0) {
            	AdminHomePage.homePage();
        	} else if(rowNumber != 0) {
            	adminDecision(rowNumber);
        	}
		} else if(k == j) {
			System.out.println("There is no Inactive Food Service Agent Without Cooking Certificate !!! Press 0 to go to Admin Home Page...");
			System.out.println("Back [0]");
        	String option = null;
        	option = input.nextLine();
        	int rowNumber = Integer.parseInt(option);
        	if(rowNumber == 0) {
            	AdminHomePage.homePage();
        	} else if(rowNumber != 0) {
        		adminDecision(rowNumber);
        	}	
		}
    	input.close();
	}
	catch(Exception ex)
	{
		System.out.println("There is no Inactive Food Service Agent");
		System.out.println(ex);
		ex.printStackTrace();
		System.err.println(ex.getMessage());
	} 
	finally 
	{
		closeRs(rs);
		closePrStmt(prStmt);
		closeConn(conn);
		
	}
}

public static void inactiveUsersWithoutBusinessCert() {

	Connection conn = null;
	PreparedStatement prStmt = null;
	ResultSet rs = null;
	ArrayList<String> userlist = new ArrayList<String>();
	
	try{  

		conn = createConn();
		prStmt = conn.prepareStatement("select * from fsa where cookingcertificate='Yes' and businesscertificate='No' and validation='Inactive'");  
		rs = prStmt.executeQuery();
    	Scanner input = new Scanner(System.in);
		int j = 0;
		int i = 1;
		while(rs.next()) {
			while (i <= rs.getRow()) {
				System.out.println(rs.getRow() +" - "+rs.getString("fsausername")+"    Without Business Certificate ");
				System.out.println("-----------------------------------------------------");
				userlist.add(rs.getString("fsausername"));
				j = rs.getRow();
				emailId = rs.getString("email");
				i++;
			}
		}
		decisionMaker = "nobusiness";
		userslistWithoutBusinessCert = userlist;
		userslistDelete = userlist;
		int k = 0;
		if (k < j) {
			System.out.println("Back [0]");
        	System.out.println("Select Food Service Agent's number to see the option or Select 0 to go back...");
        	String option = null;
        	option = input.nextLine();
        	int rowNumber = Integer.parseInt(option);
        	if(rowNumber == 0) {
            	AdminHomePage.homePage();
        	} else if(rowNumber != 0) {
            	adminDecision(rowNumber);
        	}
		} else if(k == j) {
			System.out.println("There is no Inactive Food Service Agent Without Business Certificate !!! Press 0 to go to Admin Home Page...");
			System.out.println("Back [0]");
        	String option = null;
        	option = input.nextLine();
        	int rowNumber = Integer.parseInt(option);
        	if(rowNumber == 0) {
            	AdminHomePage.homePage();
        	} else if(rowNumber != 0) {
        		adminDecision(rowNumber);
        	}	
		}
    	input.close();
	}
	catch(Exception ex)
	{
		System.out.println("There is no Inactive Food Service Agent without Business Certificate");
		System.out.println(ex);
		ex.printStackTrace();
		System.err.println(ex.getMessage());
	} 
	finally 
	{
		closeRs(rs);
		closePrStmt(prStmt);
		closeConn(conn);
		
	}
}


public static void inactiveUsersWithoutCertificate() {

	Connection conn = null;
	PreparedStatement prStmt = null;
	ResultSet rs = null;
	ArrayList<String> userlist = new ArrayList<String>();
	
	try{  

		conn = createConn();
		prStmt = conn.prepareStatement("select * from fsa where cookingcertificate='No' and businesscertificate='No' and validation='Inactive'");  
		rs = prStmt.executeQuery();
    	Scanner input = new Scanner(System.in);
		int j = 0;
		int i = 1;
		while(rs.next()) {
			while (i <= rs.getRow()) {
				System.out.println(rs.getRow() +" - "+rs.getString("fsausername")+"    Without any Certificate ");
				System.out.println("-----------------------------------------------------");
				userlist.add(rs.getString("fsausername"));
				emailId = rs.getString("email");
				j = rs.getRow();
				i++;
			}
		}
		decisionMaker = "nocertificate";
		userslistWithoutCertificate = userlist;
		userslistDelete = userlist;
		int k = 0;
		if (k < j) {
			System.out.println("Back [0]");
        	System.out.println("Select Food Service Agent's number to see the option or Select 0 to go back...");
        	String option = null;
        	option = input.nextLine();
        	int rowNumber = Integer.parseInt(option);
        	if(rowNumber == 0) {
            	AdminHomePage.homePage();
        	} else if(rowNumber != 0) {
            	adminDecision(rowNumber);
        	}
		} else if(k == j) {
			System.out.println("There is no Inactive Food Service Agent Without Business Certificate !!! Press 0 to go to Admin Home Page...");
			System.out.println("Back [0]");
        	String option = null;
        	option = input.nextLine();
        	int rowNumber = Integer.parseInt(option);
        	if(rowNumber == 0) {
            	AdminHomePage.homePage();
        	} else if(rowNumber != 0) {
        		adminDecision(rowNumber);
        	}	
		}
    	input.close();
	}
	catch(Exception ex)
	{
		System.out.println("There is no Inactive Food Service Agent without Business Certificate");
		System.out.println(ex);
		ex.printStackTrace();
		System.err.println(ex.getMessage());
	} 
	finally 
	{
		closeRs(rs);
		closePrStmt(prStmt);
		closeConn(conn);
		
	}
}


public static void adminDecision(int rowNumber) {
	Scanner input = new Scanner(System.in);
	System.out.println("1 - Send Email reminder");
	System.out.println("2 - Delete Agent");
	System.out.println("3 - Back");
	String option = null;
	option = input.nextLine();
	switch (option) {

	case "1" : {
		EmailConfirmation.warningEmail(decisionMaker,emailId);
		break;
	}

	case "2" : {
		inactiveUserDelete(rowNumber);
		break;
	}

	case "3" : {
		if(decisionMaker.equals("nocooking")) {
			inactiveUsersWithoutCookingCert();
		}	
		else if(decisionMaker.equals("nobusiness")) {
			inactiveUsersWithoutBusinessCert();
		}	
		else if(decisionMaker.equals("bothcertificate")) {
			inactiveUsers();
		}	
		else if(decisionMaker.equals("nocertificate")) {
			inactiveUsersWithoutCertificate();
		}	
		break;
	}
	
	}
	input.close();
}

public static void adminDecisionValidation(int rowNumber) {
	Scanner input = new Scanner(System.in);
	System.out.println("1 - Activate Agent");
	System.out.println("2 - Delete Agent");
	System.out.println("3 - Back");
	String option = null;
	option = input.nextLine();
	switch (option) {

	case "1" : {
		userActivation(rowNumber);
		break;
	}

	case "2" : {
		inactiveUserDelete(rowNumber);
		break;
	}

	case "3" : {
		inactiveUsers();
		break;
	}
	
	}
	input.close();
}


private static void inactiveUserDelete(int rowNumber) {

	Connection conn = null;
	PreparedStatement prStmt = null;
	ResultSet rs = null;
			
	try{  

		conn = createConn();
		prStmt = conn.prepareStatement("delete from fsa where fsausername='"+userslistDelete.get(rowNumber-1)+"'");  
		prStmt.executeUpdate();
		if(decisionMaker.equals("nocooking")) {
			inactiveUsersWithoutCookingCert();
		}	
		else if(decisionMaker.equals("nobusiness")) {
			inactiveUsersWithoutBusinessCert();
		}	
		else if(decisionMaker.equals("bothcertificate")) {
			inactiveUsers();
		}	
		else if(decisionMaker.equals("nocertificate")) {
			inactiveUsersWithoutCertificate();
		}	
	}
	catch(Exception ex)
	{
		System.out.println("There is no Inactive Food Service Agent without Cooking Certificte");
		System.out.println(ex);
		ex.printStackTrace();
		System.err.println(ex.getMessage());
	} 
	finally 
	{
		closeRs(rs);
		closePrStmt(prStmt);
		closeConn(conn);
	}
}

public static String oldValueShow(String tablename , String enteredValue , String columnLabel , String checkingColumnLabel){

	Connection conn = null;
	PreparedStatement prStmt = null;
	ResultSet rs = null;
	PreparedStatement prStmt1 = null;
	try{  

		conn = createConn();
		prStmt = conn.prepareStatement("select * from "+tablename+" where "+checkingColumnLabel+"='"+enteredValue+"'");  
		rs = prStmt.executeQuery();
		while(rs.next()) {
			enteredValue = rs.getString(columnLabel);
			
		}

/*		if(!rs.next()) {
			enteredValue = "";
		}*/
	}
	catch(Exception ex)
	{
		System.out.println(ex);
		ex.printStackTrace();
		System.err.println(ex.getMessage());
	} 
	finally 
	{
		closePrStmt(prStmt1);
		closeRs(rs);
		closePrStmt(prStmt);
		closeConn(conn);
	}
	return enteredValue;
}

public static void updateTable(String tablename, ArrayList<String[]> detail, ArrayList<String[]> condition) throws SQLException {
	int i = 0;
	String query = "";
	Connection conn = null;
	PreparedStatement prStmt = null;

	while(i < detail.size()) {
		if(i < detail.size()-1) {
			query = query + detail.get(i)[0] + "='" + detail.get(i)[1] + "', ";
		} else if (i == detail.size() - 1) {
			query = query + detail.get(i)[0] + "='" + detail.get(i)[1] + "'";
		} 
		
		i++;
	}
	try {
		conn = createConn();
		prStmt = conn.prepareStatement("update "+ tablename + " set " + query +" where " + condition.get(0)[0] + "='" + condition.get(0)[1]+"'");
//		String updateq = "update "+ tablename + " set " + query +" where " + condition.get(0)[0] + "='" + condition.get(0)[1]+"'";
		prStmt.executeUpdate();
		System.out.println("update "+ tablename + " set " + query +" where " + condition.get(0)[0] + "='" + condition.get(0)[1]+"'");
/*		
		if(r==0)
			System.out.println("cannot update data");
		else
			System.out.println("successfully updated data");*/
			
	} catch (SQLException ex) {
		System.out.println(ex);
		ex.printStackTrace();
		System.err.println(ex.getMessage());
	}
	finally 
	{
		closePrStmt(prStmt);
		closeConn(conn);
	}
}
}


