
import java.util.HashMap;
import java.util.concurrent.locks.ReentrantLock;
import java.util.*;


public class Server{

	private static int sequence = 1;
	private int idServer;
	private String type_server;
	private double price_server;
	private int state; // 0 -> free | 1 -> ocupaded | 2 -> auction
	private Map<String,Double> queue_users; // String -> User_email | Double -> auction price

	public Server(String type, double hourPrice, int state){
		this.idServer = sequence++;
		this.type_server = type;
		this.price_server = hourPrice;
		this.state = state;
		this.queue_users = new HashMap<>();
	}

	//Set
	public void setPrice(double u){this.price_server = u;}
	public void setType(String u){ this.type_server = u;}
	public void setState(int i){this.state = i;}

	//Get
	public double getPrice(){ return this.price_server;}
	public String getType(){ return this.type_server;}
	public int getState(){return this.state;}
	public Map<String,Double> getQueue(){ return this.queue_users;}
	public int getIDServer(){return this.idServer;}

	// Remove from queue
	public int removeToQueue(String user){
		if(this.queue_users.containsKey(user)){
			this.queue_users.remove(user);
			return 1;
		} else{return 0;}
	}

	// Add to queue
	public synchronized int addToQueue(String user, double price){
		if(!this.queue_users.containsKey(user)){
			this.queue_users.put(user,price);
			return 1;
		} else{return 0;}
	}

	// Clena queue
	public void cleanQueue(){
		this.queue_users =  new HashMap<>();
	}
	
}