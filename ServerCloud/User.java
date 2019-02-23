import java.util.HashMap;

public class User{

	private String email;
	private String password;
	private boolean connection;
	private double money;

	public User(String email, String pass, boolean con){
		this.email = email;
		this.password = pass;
		this.connection = con;
		this.money = 0;
	}

	public User(String email, String password ,boolean connection ,double money){
		this.email = email;
		this.password=password;
		this.connection =connection;
		this.money = money;
   	}

   	//Set
	public void setEmail(String u){ this.email = u;}
	public void setPassword(String u){ this.password = u;}
	public void setConnection(boolean u){ this.connection = u;}
	public void setMoney(double  u){ this.money = u;}

   	//Get
    public String getEmail(){ return this.email;}
   	public String getPassword(){ return this.password;}
   	public boolean getConnection(){ return this.connection;}
   	public double getMoney(){ return this.money;}

	//Money
	public void takeMoney(double u){this.money=this.money-u;}
	public void putMoney(double u){this.money=this.money+u;}

	public String toString() {
        return "|Email: "+this.email+"|Password: " + this.password + "|Connection: "
                + this.connection + "|Money: " + this.money;
    }
}