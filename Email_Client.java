// Index No: 200590J
// OOP Project

//imported libraries
import java.util.Scanner;
import java.util.ArrayList;
import java.util.Date;
import java.util.Calendar;
import java.io.Serializable;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import javax.mail.internet.AddressException;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.Arrays;
import java.util.List;

/*******************************************************************************************/
/**
 * @author 200590J
 * Email Client - OOP Principles 1
 */
public class Email_Client {

      public static void main(String[] args) {
    	  
        // This handles user requests
    	EmailMachine emailMachine = EmailMachine.getInstance();
    	  
    	/*
    	 *  This is Machine will run until it is closed
    	 *  Initialize the variable open to True
    	 *  this maintains the state of the machine, whether it is open or closed
    	*/
    	boolean open = true;
    	  
    	Scanner scanner = new Scanner(System.in);

    	while (open) {

            try {
            	// Get the user input as integers from these options
            	System.out.println("\nSelect one of these options: \n"
                        + "1 - Adding a new recipient\n"
                        + "2 - Sending an email\n"
                        + "3 - Printing out all the recipients who have birthdays\n"
                        + "4 - Printing out details of all the emails sent\n"
                        + "5 - Printing out the number of recipient objects in the application"
                        + "-1 - Shut Down\n");
            	
            	System.out.print("Enter Option: ");
            	String curIn = scanner.nextLine();
                
    			int option = Integer.parseInt(curIn);

                System.out.println();
            	
            	switch(option){
	           	  	case -1:
	           	  		// This will turn off the machine
		           		emailMachine.shutDown();
		           		open = false;
		           		break;
	                case 1:
	                	System.out.println("Enter the details of the recipient to add in the following format.");
	                	System.out.println("Input Format=> Official: nimal,nimal@gmail.com,ceo");
	                	System.out.print("Recipient Details: ");
	              	    String line = scanner.nextLine();
	              	    
	              	    // Add the new recipient via Email Machine 
	              	    emailMachine.Add_Recipient(line);
	                    break;
	                case 2:
	                	System.out.println("Enter the details of the email to send.");
	                	System.out.print("E-mail Address: ");
	                	
	                	// Email of the Recipient
	              	    String RecipientAddress = scanner.nextLine();
	              	    
	              	    System.out.print("Enter the subject: ");
	              	    // Subject of the email
	              	    String Subject = scanner.nextLine();
	              	    
	              	    System.out.print("Enter the content: ");
	              	    // Content of the email
	              	    String Content = scanner.nextLine();
	              	    
	              	    // Send the email via Email Machine
	              	    emailMachine.sendEmail(RecipientAddress, Subject, Content);
	                    break;
	                case 3:	
	                	System.out.println("Enter the date, you want to know who are celebrating their birthdays in the given format");
	                	System.out.println("Date format - yyyy/MM/dd (eg: 2022/08/02)");
	                	// Date we want to display who is celebrating their birthday
	                	System.out.print("Date: ");
	              	    String date = scanner.nextLine();
	              	    
	              	    // Show the people who are celebrating their birthday on the given date
	              	    emailMachine.showBirthdayList(date);
	                    break;
	                case 4:
	                	System.out.println("Enter the date, you want to know the details of the emails which were sent on that day");
	                	System.out.println("Date format - yyyy/MM/dd (eg: 2022/08/02)");
	                	// Date we want to display the details of the sent mails
	                	System.out.print("Date: ");
	              	    String SentEmailDate = scanner.nextLine();
	              	    
	              	    // Show the details of the sent mails on the given date
	              	    emailMachine.getEmailDetail(SentEmailDate);
	                    break;
	                case 5:
	                    // Get the total number of recipients saved in the machine
	              	    emailMachine.getRecipientCount();
	                    break;
	                default: 
	              	    // If the user enters value not in the range of 1-5 or the value is not -1
	              	    System.out.println("Please enter a valid input in the range of 1 to 5\n "
	              	    		+ "Or -1 to exit");
	          }
            }
            catch(Exception e) {
                System.out.println("Please enter valid integer input from the  given choices");
            }

    	}
    	scanner.close();
    }
}

/*******************************************************************************************/

/**
 * The EmailMachine class is a singleton class. This provides a simplified interface
 * to the Email Client. Using Facade Design pattern.
 */
class EmailMachine {
    private static final EmailMachine INSTANCE = new EmailMachine();
    private final String fileName = "clientList.txt"; // Contact detail file
    
    private Email_Sender emailSender;
    private RecipientFactory recipientFactory;

    
    private EmailMachine(){
        // Create instances upon creating the machine
        emailSender = new Email_Sender();
        recipientFactory = RecipientFactory.getInstance();
        Start();
    }
    
    /**
     * The function returns the singleton instance of the class
     * 
     * @return The instance of the EmailMachine class.
     */
    public static EmailMachine getInstance() {
        return INSTANCE;
    }
    
    /**
     * Upon starting the machine, de-serialize the sent emails
     * Read the clients in the file, and update the previous list
     */
    private void Start() {
        System.out.println("Welcome to the Email Client....");
        Serializer.GetSerializedEmails();
        ArrayList<String> data = FileHandler.Read(fileName);
        recipientFactory.updateExistingList(data);
    }
    
    /**
     * This function serializes the sent emails and shuts down the machine
     */
    public void shutDown() {
        Serializer.SerializeSentEmails();
        System.out.println("Thank you for using the Email Client!");
    }
    
    /**
     * Operation 1 -> 
     * It creates a new recipient in the recipient factory and writes the recipient to a file
     * 
     * @param data The data to be added to the file.
     */
    public void Add_Recipient(String data) {
        if (recipientFactory.addRecipient(data)) 
            FileHandler.write(fileName, data);
    }
    
   /**
    * Operation 2->
    * It sends an email to the recipient address, if the address is valid, and if the email is sent, it
    * saves the email in the sent emails array list
    * 
    * @param RecipientAddress The email address of the recipient.
    * @param Subject The subject of the email
    * @param Content The content of the email
    */
    public void sendEmail(String RecipientAddress, String Subject, String Content) {
        if (!InputValidator.isValidEmail(RecipientAddress)) return;
        
        boolean sentStatus = emailSender.sendMail(RecipientAddress, Subject, Content);
        if (sentStatus) {
            Email email = new Email(RecipientAddress, Subject, Content, new Date());
            Serializer.addNewMail(email);
        }
    }

    /**
     * Operation 3 -> 
     * This function shows a list of people who have a birthday on the given date.
     * 
     * @param date The date of the birthday.
     */
    public void showBirthdayList(String date) {
        recipientFactory.getBirthdayPeople(date);
    }
    
    /**
     * Operation 4 ->
     * It gets the email details on a given date
     * 
     * @param date The date of the email you want to retrieve.
     */
    public void getEmailDetail(String date) {
        Serializer.getMailsOnDate(date);
    }
    
    /**
     * Operation 5->
     * The function getRecipientCount() prints the total number of recipients
     */
    public void getRecipientCount() {
        System.out.println("The total number recipients are " + Recipient.getCount());
    }
}

/*******************************************************************************************/

// This class handles operations with recipient objects
class RecipientFactory {
    private static final RecipientFactory INSTANCE = new RecipientFactory();
    private ArrayList<Wishable> FriendList;
    private ArrayList<String> emailList;
    private Email_Sender emailSender;
    
    private RecipientFactory(){
        this.FriendList = new ArrayList<Wishable>();
        this.emailSender = new Email_Sender();
        this.emailList = new ArrayList<String>();
    }
    
    /**
     * The function returns the singleton instance of the class
     * 
     * @return The instance of the RecipientFactory class.
     */
    public static RecipientFactory getInstance() {
        return INSTANCE;
    }
    
    /**
     * It takes the recipient details and validates the input
     * If it is a valid detail, it creates a new object and adds to the recipient list
     * 
     * @param data String
     * @return A boolean value, indicating whether given data is added or not.
     */
    public boolean addRecipient(String data) {
        boolean added = true;
        try {
            Recipient newRecipient = null;
            if (!data.contains(":")) throw new Invalid_Input("Invalid input Format");
            
            String[] details = data.split(":");
            
            if (details.length != 2) throw new Invalid_Input("Invalid input Format");
            
            String type = details[0];
            
            // Format the given data
            ArrayList<String> info = StringFormatter.formatLine(details[1]);
            
            // Check for valid Office worker
            if (type.equals("Official") && InputValidator.isValidOfficial(info) && isExistAlready(info.get(1))) {
                newRecipient = new Office_worker(info.get(0), info.get(1), info.get(2));
            }
            // Check for valid Officie friend
            else if (type.equals("Office_friend") && InputValidator.isValidOfficialFriend(info) && isExistAlready(info.get(1))) {
                Date birthday = StringFormatter.formatDate(info.get(3));
                newRecipient = new Official_Friend(info.get(0), info.get(1), info.get(2), birthday);
                Wishable friend = (Wishable) newRecipient;
                FriendList.add(friend);
                sendBirthdayGreetingsToday(friend);

            }
            // Check for valid Personal friend
            else if (type.equals("Personal") && InputValidator.isValidPersonalFriend(info) && isExistAlready(info.get(2))) {
                Date birthday = StringFormatter.formatDate(info.get(3));
                newRecipient = new Personal(info.get(0), info.get(1), info.get(2), birthday);
                Wishable friend = (Wishable) newRecipient;
                FriendList.add(friend);
                sendBirthdayGreetingsToday(friend);
            }
            else 
                added = false;
            
        }
        catch(Exception e){
            System.out.println(e);
            e.printStackTrace();
        }
        
        return added;
    }
    
    /**
     * This function takes in an ArrayList of details of recipient, and for each recipient detail 
     * in the ArrayList, it checks whether we can add the recipient or display a message to the client
     * saying can't add the recipient
     * 
     * @param data ArrayList of Strings
     */
    public void updateExistingList(ArrayList<String> data) {
        for (String details: data) {
            if(!addRecipient(details)) {
                System.out.println("Could not add the current recipient....");
                System.out.println("Invalid details or details already exist in machine");
                System.out.println(details + "\n");
            }
        }
        
    }
    
    /**
     * It takes a date as input, checks if it's valid, then checks if any of the people in the friend
     * list have their birthday on that date, and if so, prints their names
     * 
     * @param date the date in the format of dd/mm/yyyy
     */
    public void getBirthdayPeople(String date) {
        
        if (!InputValidator.isValidDate(date)) 
            return;
        
        ArrayList<String> birthdayNames = new ArrayList<String>();
        
        Date reqDate = StringFormatter.formatDate(date);
        
        for (Wishable person: FriendList) {
            if (isSameDay(reqDate, person.getBirthday())) {
                Recipient birthdayGuy = (Recipient) person;
                birthdayNames.add(birthdayGuy.getName());
            }
        }
        
        if (birthdayNames.size() == 0) {
            System.out.println("None of your friends are celebrating their birthday on "+ date +" :(\n");
        }
        else {
            String message = String.format("The people who are celebrating their birthday on %s", date);
            showOutput(birthdayNames, message);
        }
        
        
    }
    
    /**
     * It sends birthday greetings to a person if it's their birthday today
     * 
     * @param person A Wishable object (personal friend or official friend)
     */
    private void sendBirthdayGreetingsToday(Wishable person) {
        Date date = new Date();
        if (isSameDay(date, person.getBirthday())) {
            String emailAddress = ( (Recipient) person ).getEmail();
            Email email = new Email(emailAddress, "Happy Birthday Friend!", person.wishHappyBirthday(), new Date());

            // Before sending the email check if the email already sent that day
            // Otherwise it will spam their mail box
            if (!Serializer.isAlreadySent(email)) {
                boolean sentStatus = emailSender.sendGreetings(person);
                if (sentStatus) {
                    Serializer.addNewMail(email);
                }
            }
        }
    }
    
    /**
     * It checks if the email address is already in the file, if it is, it returns
     * false, if it isn't, it adds it to the ArrayList and returns true
     * 
     * @param emailAddress The email address to be added to the file.
     * @return The method is returning a boolean value.
     */
    private boolean isExistAlready(String emailAddress) {
        boolean status =  emailList.contains(emailAddress);
        if (status)
            System.out.println("Email address Already exists in the file");
        else
            emailList.add(emailAddress);
        return !status;
    }
    
    /**
     * Check if both days are same.
     * i.e Both month and day are same not year (To check birthdays)
     * 
     * @param date1 The first date to compare.
     * @param date2 The date to compare to.
     * @return The method is returning a boolean value, whether they are the same or not.
     */
    private boolean isSameDay(Date date1, Date date2) {
        Calendar calendar1 = Calendar.getInstance();
        calendar1.setTime(date1);
        
        Calendar calendar2 = Calendar.getInstance();
        calendar2.setTime(date2);
        
        return calendar1.get(Calendar.MONTH) == calendar2.get(Calendar.MONTH) &&
                calendar1.get(Calendar.DAY_OF_MONTH) == calendar2.get(Calendar.DAY_OF_MONTH);

    }
    
    /**
     * Displays the words in the array list
     * 
     * @param words The list of strings.
     * @param message The message sent by the machine.
     */
    private void showOutput(ArrayList<String> words, String message) {
        System.out.println(message);
        System.out.println();
        for (String word: words) {
            System.out.println(word);
        }
        System.out.println();
    }
}

/*******************************************************************************************/

/**
 * This is the Email class which creates email objects 
 * We serialize these objects in to a file
 * 
 */
class Email implements Serializable{

    private static final long serialVersionUID = 2L;
    private String email;
    private String subject;
    private String content;
    private Date sentDate;
    
    public Email(String email, String subject, String content, Date sentDate) {
        this.email = email;
        this.subject = subject;
        this.content = content;
        this.sentDate = sentDate;
    }

    // Getters and Setters

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Date getSentDate() {
        return sentDate;
    }

    public void setSentDate(Date sentDate) {
        this.sentDate = sentDate;
    }
}

/*******************************************************************************************/

// This is a utility class that Serializes and Deserializes the emails
class Serializer {
    
    private static final String fileName = "SentEmails.ser";
    private static ArrayList<Email> sentMails = new ArrayList<Email>();
    
    /**
     * Serialize the sent mails array list to a file
     */
    public static void SerializeSentEmails() {
        FileOutputStream fileOut = null;
        ObjectOutputStream out = null;
        try {
            fileOut = new FileOutputStream(fileName);
            out = new ObjectOutputStream(fileOut);
            out.writeObject(sentMails);
        } 
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        finally{
            if (out != null){
                try{
                    out.close();
                    fileOut = null;
                }
                catch (Exception e) {}
                out = null;
            }

            if (fileOut != null){
                try{
                    fileOut.close();
                }
                catch (Exception e) {}
                fileOut = null;
            }
        }
    }
    
    /**
     * De-serialize the sent mails from the file and put it to the array list
     */
    @SuppressWarnings("unchecked")
    public static void GetSerializedEmails() {
        FileInputStream fileIn = null;
        ObjectInputStream in = null;
        try {
            fileIn = new FileInputStream(fileName);
            in  = new ObjectInputStream(fileIn);
            ArrayList<Email> alreadySentMails;
            
            if (fileIn.available() != 0) {
                alreadySentMails = (ArrayList<Email>) in.readObject();
                sentMails.addAll(alreadySentMails);
            }
            
            in.close();
        } 
        catch (FileNotFoundException e) {
            System.out.println("File not found");
            e.printStackTrace();
        } 
        catch (IOException e) {
            System.out.println("Error loading email from file");
            e.printStackTrace();
        } 
        catch (ClassNotFoundException e) {
            System.out.println("Error loading email from file");
            e.printStackTrace();
        }
        finally{
            if (in != null){
                try{
                    in.close();
                    fileIn = null;
                }
                catch (Exception e) {}
                in = null;
            }

            if (fileIn != null){
                try{
                    fileIn.close();
                }
                catch (Exception e) {}
                fileIn = null;
            }
        }
    }
    
   /**
    * This function adds a new email to the sentMails list
    * 
    * @param email The email to be added to the list.
    */
    public static void addNewMail(Email email) {
        sentMails.add(email);
    }
    
    /**
     * It checks if the email has already been sent
     * 
     * @param email the email to be sent
     * @return A boolean value.
     */
    public static boolean isAlreadySent(Email email) {
        for (Email sentMail: sentMails) {
            if (email.getEmail().equals(sentMail.getEmail()) && email.getSubject().equals(sentMail.getSubject()))
                return true;
        }
        return false;
    }
    
    
    /**
     * It prints out all the emails sent on a given date
     * 
     * @param date the date to search for
     */
    public static void getMailsOnDate(String date) {
        if (!InputValidator.isValidDate(date)) return;
            
        System.out.printf("Emails sent on %s\n", date);
        
        boolean found = false;
        // Check all the sent mails
        for (Email email: sentMails) {
            // Check if both are sent on the same date
            if (date.equals(StringFormatter.makeDateString(email.getSentDate()))) {
                System.out.println();
                System.out.println("Email address: " + email.getEmail());
                System.out.println("Subject: " + email.getSubject());
                System.out.println("Conent: " + email.getContent());
                found = true;
            }
        }
        
        if (!found)
            System.out.println("None");

        System.out.println();
    }
        
}

/*******************************************************************************************/

// This is the class used to send emails
class Email_Sender {
    private Properties prop;
    final private String username = "joeljavaprojecttest@gmail.com";
    final private String password = "qrrpfmekbdownvji";
    final private Session session;
    
    public Email_Sender() {
        prop = new Properties();
        prop.put("mail.smtp.host", "smtp.gmail.com");
        prop.put("mail.smtp.port", "587");
        prop.put("mail.smtp.auth", "true");
        prop.put("mail.smtp.starttls.enable", "true");
        
        session = Session.getInstance(prop, new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(username, password);
                    }
                });
    }
    
    /**
     * This sends email to anyone => Operation 2
     * 
     * @param RecipientAddress Email address of the recipient we want to send
     * @param Subject The subject of the email
     * @param Content The content of the email
     * @return boolean value indicating whether the email sent or not
     */
    public boolean sendMail(String RecipientAddress, String Subject, String Content ){
        boolean sent = false;
        try {
            
            String content = Content;
            String receiverEmail = RecipientAddress;
            
            String email = String.format("%1$s"
                                            + "\nBest Regards,"
                                            + "\nT.Joel Sathiyendra"
                                            + "\nCSE-20 Batch", content);

            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(username));
            message.setRecipients(
                    Message.RecipientType.TO,
                    InternetAddress.parse(receiverEmail)
            );
            message.setSubject(Subject);
            message.setText(email);

            Transport.send(message);
            sent = true;

        } 
        catch (MessagingException e) {
            System.out.println("Error sending mail :(");
            return false;
        }
        
        return sent;
    }
    
    /**
     * This sends greeting mails (Wishing Happy Birthday)
     * 
     * @param person This only accepts the person who is an instance of wishable
     * @return the boolean value whether the mail sent successfully or not
    */
    public boolean sendGreetings(Wishable person) {
        boolean sent = false;
        try {
            
            String birthdayWish = person.wishHappyBirthday();
            String name = ((Recipient) person).getName();
            String receiverEmail = ((Recipient) person).getEmail();
            
            String birthdayMsg = String.format("Dear %1$s,"
                                            + "\n\n  %2$s"
                                            + "\n\nRegards,"
                                            + "\nT.Joel Sathiyendra"
                                            + "\nCSE-20 Batch", name, birthdayWish);

            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(username));
            message.setRecipients(
                    Message.RecipientType.TO,
                    InternetAddress.parse(receiverEmail)
            );
            message.setSubject("Happy Birthday Friend!");
            message.setText(birthdayMsg);

            Transport.send(message);
            sent = true;
            return sent;

        } 
        catch (MessagingException e) {
            System.out.println("Error sending mail :(");
            e.printStackTrace();  
        }
        
        return sent;
    }
    
}

/*******************************************************************************************/

// This is an utility class to validate the input
class InputValidator {
    
    private static ValidatorChain valChain = new ValidatorChain();
    
    /**
     * The function checks if it is a valid email
     * 
     * @param email The email address to validate.
     * @return A boolean value.
     */
    public static boolean isValidEmail(String email) {
        try {
            if (!valChain.validate(email)) 
                throw new Invalid_Input("Invalid Email Address");
        }
        catch (Invalid_Input exception) {
            System.out.println(exception);
            return false;
        }
        return true;
    }
    
    /**
     * The function checks if it is a valid date
     * 
     * @param date The date to be validated
     * @return A boolean value.
     */
    public static boolean isValidDate(String date) {
        try {
            if (!valChain.validate(date)) 
                throw new Invalid_Input("Invalid Date");
        }
        catch (Invalid_Input exception) {
            System.out.println(exception);
            return false;
        }
        return true;
    }
    
    /**
     * It checks if the input is valid for an office worker who is not a friend
     * 
     * @param details ArrayList of Strings containing the details of the official
     * @return The method is returning a boolean value, whether it is valid or not.
     */
    public static boolean isValidOfficial(ArrayList<String> details) {
        try {
            if (details.size() != 3) 
                throw new Invalid_Input("Parameters expected 3, please enter name, email and designation");
            
            if (!isValidEmail(details.get(1))) 
                return false;
        }
        catch(Invalid_Input exception) {
            System.out.println(exception);
            return false;
        }
        return true;
    }
    
    /**
     * This function checks if the input is valid for an official friend
     * 
     * @param details ArrayList of Strings
     * @return The method is returning a boolean value, whether it is valid or not.
     */
    public static boolean isValidOfficialFriend(ArrayList<String> details) {
        try {
            if (details.size() != 4) {
                throw new Invalid_Input("Parameters expected 4, please enter name, email, designation, and birthday");
            }
            if (!isValidEmail(details.get(1))) 
                return false;
            
            if (!isValidDate(details.get(3))) 
                return false;
        }
        catch (Invalid_Input exception) {
            System.out.println(exception);
            return false;
        }
        return true;
    }
    
    /**
     * This function checks if the input is valid for a personal friend
     * 
     * @param details ArrayList of Strings
     * @return The method is returning a boolean value, whether it is valid or not.
     */
    public static boolean isValidPersonalFriend(ArrayList<String> details) throws Invalid_Input {
        try {
            if (details.size() != 4) {
                throw new Invalid_Input("Parameters expected 4, please enter name, nick name, email, and birthday");
            }
            if (!isValidEmail(details.get(2))) 
                return false;
            
            if (!isValidDate(details.get(3))) 
                return false;
        }
        catch(Invalid_Input exception) {
            System.out.println(exception);
            return false;
        }
        return true;
    }

}

/*******************************************************************************************/

/**
 * This is the Validator abstract class in the Design pattern Chain of Responsibility
 * 
 */
abstract class Validator {
    private Validator next;
    
    // Setting the next validator in the constructor
    public Validator(Validator next) {
        this.next = next;
    }
    
    // Get the next validator
    public Validator getNext() {
        return next;
    }
    
    // Abstract method to validate
    abstract public boolean isValid(String data);
}

/*******************************************************************************************/

/*
* This is the date validator in the chain of validators
* This checks if the given date is a valid date.
*/
class DateValidator extends Validator{
    
    // Desired format of the date
    private DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
    
    // Chaining the next validator
    public DateValidator(Validator next) {
        super(next);
    }
    
    /**
    * If the data contains an 10 characters, then we can validate the date so validate it. 
    * If it doesn't contain an 10 charactersl, then it is not a date, so check if there is a 
    * next validator in the chain. If there is, then pass the data to the next validator. If
    * there isn't, then return false
    * 
    * @param data The data to be validated.
    * @return The boolean value of whether it is valid or not.
    */
    @Override
    public boolean isValid(String data) {
        try {
            // Validating the input
            ;
            if (data.contains("/")){
                dateFormat.setLenient(false);
                dateFormat.parse(data);
            }
            // Check for the next validator
            else if (getNext() == null)
                return false;
            else
                return getNext().isValid(data);
        } 
        catch (ParseException e) {
            return false;
        }
        return true;
    }

}

/*******************************************************************************************/

/*
* This is the email validator in the chain of validators
* This checks if the given email is a valid email.
*/
class EmailValidator extends Validator{
    
    private InternetAddress emailAddress;
    
    public EmailValidator(Validator next) {
        super(next);
    }

   /**
    * If the data contains an @ symbol, then it is an email address, so validate it. If it doesn't
    * contain an @ symbol, then it is not an email address, so check if there is a next validator in
    * the chain. If there is, then pass the data to the next validator. If there isn't, then return
    * false
    * 
    * @param data The data to be validated.
    * @return The boolean value of the flag (whether it is valid or not).
    */
    @Override
    public boolean isValid(String data) {
        boolean flag = true;
        try {
            if (data.contains("@")) {
                emailAddress = new InternetAddress(data);
                emailAddress.validate();
            }
            else if (getNext() == null)
                return false;
            else
                return getNext().isValid(data);
        }
        catch(AddressException e) {
            flag = false;
        }
        return flag;
    }

}

/*******************************************************************************************/

/**
 * This is the chain which links all the validators in the 
 * chain of responsibility design pattern.
 */
class ValidatorChain {
    private EmailValidator emailValidator;
    private DateValidator dateValidator;
    
    // We link all the validators in the chain
    public ValidatorChain() {
        emailValidator = new EmailValidator(null);
        dateValidator = new DateValidator(emailValidator);
    }
    
    /**
     * We start the validation using dateValidator
     * Then if we could not validate we move on to the next one, if there exist another one
     * 
     * @param data The data to be validated.
     * @return The boolean value of whether it is valid or not.
     */
    public boolean validate(String data) {
        return dateValidator.isValid(data);
    }
}

/*******************************************************************************************/

/**
 * This is the Recipient class. the parent of all recipient classes
 * Since we don't want to create an instance of this class, this is an abstract class
 * 
 */
abstract class Recipient {
    private String name; 
    private String email;
    // This is to maintain the total number of recipients in the system
    private static int count = 0;
    
    public Recipient(String name, String email) {
        this.name = name;
        this.email = email;
        count++;
    }
    
    // Getters and Setters
    public static int getCount(){
        return count;
    }

    public void setName(String newName) {
        this.name = newName;
    }
    
    public String getName() {
        return this.name;
    }
    
    public String getEmail() {
        return this.email;
    }

    public void setEmail(String email){
        if (InputValidator.isValidEmail(email))
            this.email = email;
    }
}

/*******************************************************************************************/

/**
 * This is the interface used to link the people who we should wish happy birthday
 */
interface Wishable {
    public String wishHappyBirthday();
    public Date getBirthday();
}

/*******************************************************************************************/

/**
 * This is an abstract class which has the child classes Official_Friend & Office_worker'
 * We don't need to create an instance of this class
 * 
 */
abstract class Official extends Recipient{
    
    private String designation;

    public Official(String name, String email, String designation) {
        super(name, email);
        this.designation = designation;
    }
    
    // Getter and Setter methods
    public String getDesignation() {
        return this.designation;
    }

    public void setDesignation(String newDesignation){
        this.designation = newDesignation;
    }

}

/*******************************************************************************************/

/**
 * This is a the class for Personal friends, we should wish them birthday on their birthday
 * So we implement the Wishable interface
 * 
 */
class Personal extends Recipient implements Wishable{
    
    private String nickName;
    private Date birthday;
    
    public Personal(String name, String nickName, String email, Date birthday) {
        super(name, email);
        this.nickName = nickName;
        this.birthday = birthday;
    }
    
    // Getters and Setters
    public String getNickName() {
        return this.nickName;
    }

    public void setNickName(String newNickName){
        this.nickName = newNickName;
    }

    public void setBrithday(Date newBirthday){
        this.birthday = newBirthday;
    }
    
    // Overriding the methods implemented in the interface
    @Override
    public Date getBirthday() {
        return this.birthday;
    }

    @Override
    public String wishHappyBirthday() {
        String message = "Hugs and love on your birthday.";
        return message;
    }
    
}

/*******************************************************************************************/

/**
 * This is a the class for office friends, we should wish them birthday on their birthday
 * So we implement the Wishable interface
 * 
 */
class Official_Friend extends Official implements Wishable{
    
    private Date birthday;

    public Official_Friend(String name, String email, String designation, Date birthday) {
        super(name, email, designation);
        this.birthday = birthday;
    }
    
    // Overriding the methods implemented in the interface
    @Override
    public Date getBirthday() {
        return birthday;
    }

    @Override
    public String wishHappyBirthday() {
        String message = "Wish you a Happy Birthday. ";
        return message;
    }
    
}

/*******************************************************************************************/

/**
 * This is Office worker class, which contains the people who work in the office.
 * But they are not friends with you.
 */
class Office_worker extends Official{

    public Office_worker(String name, String email, String designation) {
        super(name, email, designation);
    }

}

/*******************************************************************************************/

// This is a utility class used to handle files
class FileHandler {
    
    /**
     * Reads the file and returns an ArrayList of Strings, where each String is a line from the file
     * 
     * @param fileName The name of the file you want to read.
     * @return The method is returning an ArrayList of Strings.
     */
    public static ArrayList<String> Read(String fileName) {
        ArrayList<String> data = new ArrayList<String>();
        FileReader reader = null;
        BufferedReader bufferReader = null;
        try {
            reader = new FileReader(fileName);
            bufferReader = new BufferedReader(reader);
            String line = null;
            while ((line = bufferReader.readLine()) != null) {
                data.add(line);
            }
        } 
        catch (IOException e) {
            System.out.println("The file is not found to read!");
        }
        finally{
            if (bufferReader != null) {
                try {
                    bufferReader.close();
                    reader = null;
                }
                catch(Exception e) {}
                bufferReader = null;
            }
            
            if (reader != null) {
                try {
                    reader.close();
                }
                catch(Exception e) {}
                reader = null;
            }
        }
        return data;
    }
    
    /**
     * This writes a new line to the file
     * 
     * @param fileName The name of the file you want to add the new line
     * @param line The new line we need to add
     */
    public static void write(String fileName, String line) {
        FileWriter filewriter = null;
        BufferedWriter bufferWriter = null;
        try {
            filewriter = new FileWriter(fileName, true);
            bufferWriter = new BufferedWriter(filewriter);
            bufferWriter.newLine();
            bufferWriter.write(line);
            
            bufferWriter.close();
            filewriter.close();
        }
        catch(IOException e) {
            System.out.println(e);
            return;
        }
        finally{
            if (bufferWriter != null){
                try{
                    bufferWriter.close();
                    filewriter = null;
                }
                catch(Exception e){}
                bufferWriter = null;
            }

            if (filewriter != null){
                try{
                    filewriter.close();
                }
                catch(Exception e){}
                filewriter = null;
            }
        }
        
    }
}

/*******************************************************************************************/

// This is a utility class used to format the strings
class StringFormatter {
    
    /**
     * This formats the line which has space seperated strings.
     * Make them an array lise of strings
     * 
     * @param line The line of the CSV file that is being read
     * @return An ArrayList of Strings
     */
    public static ArrayList<String> formatLine(String line) {
        // Split the line with commas
        String[] elements = line.split(",");
        // Convert the array to List
        List<String> temp = Arrays.asList(elements);
        // Make it a ArrayList
        ArrayList<String> curLine = new ArrayList<String>(temp);
        curLine = removeSpaces(curLine);
        return curLine;
    }
    
    /**
     * It takes an ArrayList of Strings as input, removes all the spaces from the beginning and end of
     * each String, and returns the modified ArrayList
     * 
     * @param wordList ArrayList of Strings
     * @return The method is returning the ArrayList wordList.
     */
    public static ArrayList<String> removeSpaces(ArrayList<String> wordList){
        int n = wordList.size();
        for (int i=0; i<n; i++) {
            String word = wordList.get(i);
            word = word.trim();
            wordList.set(i, word);
        }
        return wordList;
    }
    
    
    /**
     * This is used to format the date which we get from the user
     * It takes a string in the format of "yyyy/MM/dd" and returns a Date object
     * 
     * @param date The date to be formatted.
     * @return A date object
     */
    public static Date formatDate(String date) {
        Date curDate = null;
        try {
            DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
            curDate = dateFormat.parse(date);
        } 
        catch (ParseException e) {
            System.out.println("Invalid format given");
            return null;
        }
        return curDate;
        
    }
    
    /**
     * It takes a date object and returns a string in the format of "yyyy/MM/dd"
     * 
     * @param date The date to be formatted
     * @return A string of the date in the format of yyyy/MM/dd
     */
    public static String makeDateString(Date date) {
        String curDate = "";
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
        curDate = dateFormat.format(date);
        return curDate;
    }
    
}

/*******************************************************************************************/

/**
 * This class is used to throw an exception when the user enters an invalid input.
 * This is a custrom exception
 */
class Invalid_Input extends Exception{
    private static final long serialVersionUID = 1L;

    public Invalid_Input(String OccurredException) {
        super(OccurredException);
    }
}
