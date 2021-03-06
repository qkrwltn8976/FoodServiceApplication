package mealsanddeals;

import java.util.ArrayList;
import java.util.Scanner;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

//send notification 
public class SendNotification {
	MysqlCon Sql = new MysqlCon();
	Notification newNotification = new Notification();
	String tablename = "notification";
	ArrayList<String> sendqueryarray = new  ArrayList<String>();
	
	//enter content 
	public void enterSendContent(String sendername) {
		Scanner scanner = new Scanner(System.in);
		SimpleDateFormat transFormat = new SimpleDateFormat("yyyy-MM-dd");
		
		//user input 
		System.out.println("Enter Message\n");
		System.out.println("Receiver Name:");
		//checking the receiver name in table 
		String receivername = scanner.nextLine();
		String checkingcustomername = "customer";
		String checkingfsaname = "fsa";
		String enteredValue = receivername;
		String customercolumnLabel = "customerusername";
		String fsacolumnLabel = "fsausername";
		if(!Sql.checkingDups(checkingcustomername, enteredValue , customercolumnLabel).equals(enteredValue) || 
			!Sql.checkingDups(checkingcustomername, enteredValue , customercolumnLabel).equals(enteredValue)) {
				newNotification.setReceiverusername(receivername);
		}else {
		    System.out.println("\nYour Receiver name is not available.. \n");
		}
		System.out.println("Subject:");
		newNotification.setSubject(scanner.nextLine());
		System.out.println("Content:");
		newNotification.setContent(scanner.nextLine());
		
		long time = System.currentTimeMillis(); 
		DateFormat dayTime = new SimpleDateFormat("yyyy-MM-dd");
		
		//system input auto 
		newNotification.setSenderusername("AA");
		newNotification.setStatus("Unread");
				
		//make query and insert into DB
		sendqueryarray.add("0");
		sendqueryarray.add(newNotification.getSenderusername());
		sendqueryarray.add(newNotification.getReceiverusername());
		sendqueryarray.add(newNotification.getSubject());
		sendqueryarray.add(newNotification.getContent());
		sendqueryarray.add(dayTime.format(new Date(time)));
		sendqueryarray.add(newNotification.getStatus());
				
		try {
			Sql.insertToTable(tablename,sendqueryarray);
			System.out.println("Success! ");
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
				
		scanner.close();
				
	}

}
