
import java.io.*;
import java.net.*;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Date;
import java.util.Hashtable;

public class ServerCloud{

	private static Map<Integer,Server> servers; //Map com os servidores
	private static Map<String,User> users;//Map com os users
	private int porto;
	private ServerSocket serverCloud;
	private static Controler controler = new ControlerImpl();

	public ServerCloud(int porto){
		servers = new HashMap<Integer,Server>();
		users = new HashMap<String,User>();
		this.porto = porto;
		controler = new ControlerImpl();
	}

	private void startServerCloud(){

		try{
			clearScreen();

			System.out.println("---> SERVER CLOUD <---");

			// Criar a sockect com o porto defenido
			this.serverCloud = new ServerSocket(this.porto);

			int idClient = 1;

			while(true){
				
				System.out.println("> ServerCloud -> Running, waiting for connection...\n");

				Socket clientSocket = serverCloud.accept();

				System.out.println("> ServerCloud -> Connection received! Create client thread to handle connection...\n");

				ClientHandler threadClient = new ClientHandler(clientSocket, idClient, controler);
				
				idClient = idClient + 1;
				
				new Thread(threadClient).start();

			}

		} catch(IOException e){
			e.printStackTrace();
		}
	}
	
	public static void clearScreen() {  
    	System.out.print("\033[H\033[2J");  
    	System.out.flush();  
	}  

	public static void main(String args[]) throws IOException{

		// Users
		User rafa =  new User("rafa@gmail.com","silva",false,100.00);
		User terra = new User("terra@gmail.com","sousa",false,350.00);
		User daniel = new User("daniel@gmail.com","vieira",false,500.00);
		User jose = new User("jose@gmail.com","ramos",false,150.00);

		// Server
		Server s1 = new Server("t1.micro",1.00,0);
		Server s2 = new Server("t1.micro",1.00,0);
		Server s3 = new Server("t1.micro",1.00,0);

		Server s4 = new Server("t1.large",2.00,0);
		Server s5 = new Server("t1.large",2.00,0);

		Server s6 = new Server("t3.micro",1.00,0);
		Server s7 = new Server("t3.micro",1.00,0);
		Server s8 = new Server("t3.micro",1.00,0);
		Server s9 = new Server("t3.micro",1.00,0);

		Server s10 = new Server("t3.large",3.00,0);
		Server s11 = new Server("t3.large",3.00,0);
		Server s12 = new Server("t3.large",3.00,0);

		Server s13 = new Server("m1.micro",5.00,0);
		Server s14 = new Server("m1.micro",5.00,0);
		Server s15 = new Server("m1.micro",5.00,0);

		Server s16 = new Server("m1.large",0.50,0);

		Server s17 = new Server("m2.large",1.00,0);

		Server s18 = new Server("s1.micro",5.00,0);
		Server s19 = new Server("s1.micro",5.00,0);

		Server s20 = new Server("m5.large",4.00,0);
		Server s21 = new Server("m5.large",4.00,0);
		Server s22 = new Server("m5.large",4.00,0);
		
		ServerCloud serverCloud = new ServerCloud(9999);

		// Put Users on map users
		users.put(rafa.getEmail(),rafa);
		users.put(terra.getEmail(),terra);
		users.put(daniel.getEmail(),daniel);
		users.put(jose.getEmail(),jose);

		// Put Servers on map servers
		servers.put(s1.getIDServer(),s1);
		servers.put(s2.getIDServer(),s2);
		servers.put(s3.getIDServer(),s3);
		servers.put(s4.getIDServer(),s4);
		servers.put(s5.getIDServer(),s5);
		servers.put(s6.getIDServer(),s6);
		servers.put(s7.getIDServer(),s7);
		servers.put(s8.getIDServer(),s8);
		servers.put(s9.getIDServer(),s9);
		servers.put(s10.getIDServer(),s10);
		servers.put(s11.getIDServer(),s11);
		servers.put(s12.getIDServer(),s12);
		servers.put(s13.getIDServer(),s13);
		servers.put(s14.getIDServer(),s14);
		servers.put(s15.getIDServer(),s15);
		servers.put(s16.getIDServer(),s16);
		servers.put(s17.getIDServer(),s17);
		servers.put(s18.getIDServer(),s18);
		servers.put(s19.getIDServer(),s19);
		servers.put(s20.getIDServer(),s20);
		servers.put(s21.getIDServer(),s21);
		servers.put(s22.getIDServer(),s22);

		controler = new ControlerImpl(servers,users);

		serverCloud.startServerCloud();

	}
}