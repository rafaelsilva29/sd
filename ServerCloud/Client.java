
import java.io.*;
import java.net.*;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Client{

	private Socket clientSocket;
	private BufferedReader systemIn;
	private BufferedReader in;
	private PrintWriter out;
	private String hostName;
	private int porto;
	private Menu menu;
	private Thread receive;

	public Client(String host, int porto)  throws Exception{
		this.hostName = host;
		this.porto = porto;
		this.menu = new Menu();
	}

	private void menuViewReserves(String email, Map<String,String> reserves){
		try{
			String userInput;

			while((userInput = systemIn.readLine()) != null){
				int option = -1;

				try{
					option = Integer.valueOf(userInput);
					
					String response;

					switch(option){
						case 1: {
								int flag = 0;
								while(flag != 1){
									System.out.print("Choose a ID of a Reserve: ");
							    	String idReserve = systemIn.readLine();
									if(reserves.containsKey(idReserve)){
										flag = 1;
									   
							    	    out.println("|FINNISHRESERVE|" + idReserve + ":" + email);

							    		response = in.readLine();
							    		clearScreen();

							    		System.out.println("\n> ClientHandler -> "+response+"\n");

							    		option = 0;
							    		this.menu.menuUser();
									}

									if(flag==0){
										System.out.println("[ERROR] -> IDReserve Invalid.");	
									}
								}
								break;
							   }

						case 0:	clearScreen();
							    this.menu.menuUser();
							    break;
					}

				if(option == 0){
					break;
				}

				} catch(NumberFormatException e){
					System.out.println("[ERROR] -> '"+userInput+"' is not an integer. Please insert one.\n");
				} 
			}
		} catch (IOException e)  {
			System.out.println("[ERROR]: " +e.getMessage());
		} 
	}

	private void menuCatalogueServers(String email, Map<String,Integer> servers){
		try{
			String userInput;

			while((userInput = systemIn.readLine()) != null){
				int option = -1;
				try{
					option = Integer.valueOf(userInput);
					String response;

					switch(option){
						case 1: {
								int flag = 0;
								while(flag != 1){
									System.out.print("Choose a name of a server: ");
							    	String nameServer = systemIn.readLine();

									if(servers.containsKey(nameServer)){
										flag = 1;
											System.out.print("Price: ");
							    			String price = systemIn.readLine();
							    			out.println("|CHECKSERVER|" + nameServer + ":" + price + ":" + email);
											
											clearScreen();

							    			response = in.readLine();

							    			System.out.println("\n> ClientHandler -> "+response+"\n");	

							    			option = 0;
							    			this.menu.menuUser();
									}

									if(flag==0){
										System.out.println("[ERROR] -> ServerName Invalid.");	
									}
								}
								break;
							   }

						case 0:	clearScreen();
							    this.menu.menuUser();
							    break;
					}

				if(option == 0){
					break;
				}

				} catch(NumberFormatException e){
					System.out.println("[ERROR] -> '"+userInput+"' is not an integer. Please insert one.\n");
					System.out.print(">> ");
				} 
			}
		} catch (IOException e)  {
			System.out.println("[ERROR]: " +e.getMessage());
		} 
	}

	private void menuUserInfo(String email){
		try{
			String userInput;

			while((userInput = systemIn.readLine()) != null){
				int option = -1;
				try{
					option = Integer.valueOf(userInput);
					String response;

					switch(option){
						case 1: clearScreen();
								out.println("|CHECK ACCOUNT| Email:" + email);

								response = in.readLine();

								String [] aux = response.split("\\|");

								System.out.println("\n> ClientHandler -> [Check Account]\n");

								for(int i=1; i<aux.length ;i++){
									System.out.println(aux[i]);	
								}

								String input;
								boolean flag = false;
								System.out.println("\nIf you want to leave write 'quit'...");
								System.out.print(">> ");
								while((input = systemIn.readLine()) != null && !input.equals("quit")){
									System.out.println("\nIf you want to leave write 'quit'");
									System.out.print(">> ");
								}
								if(input.equals("quit")){
									clearScreen();
									this.menu.menuUserInformation();
								}
								break;

						case 2:	clearScreen();
								System.out.println("Amount:");
						    	String temp = systemIn.readLine();
		
					        	out.println("|PUTMONEY| Email:" + email + "| Amount:" + temp);
							    response = in.readLine();

							    clearScreen();
							    System.out.println("\n> ClientHandler -> "+response+"\n");

								this.menu.menuUserInformation();
								break;

						case 3: clearScreen();
						 		out.println("|CHECKDEBT|" + email);

						 		response = in.readLine();

						 		if(response.equals("[You don't have Debts]")){
						 			 clearScreen();
						 			 System.out.println("\n> ClientHandler -> "+response+"\n");
						 			 this.menu.menuUserInformation();

						 		} else {
						 			System.out.println("\n> ClientHandler -> "+response+"\n");
						 			System.out.println("You want to pay Debt? [Y/n]");
						 			System.out.print(">> ");
						 			String input1;
						 			while((input1 = systemIn.readLine()) != null){
						 				if(input1.equals("Y")){
						 					break;
						 				} else if(input1.equals("n")){
						 					break;
						 				}
										System.out.println("You want to pay Debt? [Y/n]");
						 				System.out.print(">> ");
									}

									if(input1.equals("Y")){
										System.out.print("How much do you want to pay: ");
										String money = systemIn.readLine();
										out.println("|PAYDEBT|" + email + ":" + money);

										response = in.readLine();

										clearScreen();

										System.out.println("\n> ClientHandler -> "+response+"\n");

										this.menu.menuUserInformation();

									} else if(input1.equals("n")){
										 clearScreen();
 										 this.menu.menuUserInformation();
									}
						 		}
						 		break;
						
						case 0: clearScreen();
								this.menu.menuUser();
								break;

					} 

					if(option == 0){
						break;
					}

				} catch(NumberFormatException e){
					System.out.println("[ERROR] -> '"+userInput+"' is not an integer. Please insert one.\n");
					this.menu.menuUserInformation();
				} 

			}
		} catch (IOException e)  {
			System.out.println("[ERROR]: " +e.getMessage());
		} 
	}

	private void menuUser(String email) throws IOException, ParseException{

		try{
			String userInput;

			while((userInput = systemIn.readLine()) != null) { 
				int option = -1;
				try{
					option = Integer.valueOf(userInput);
							
					String response = "";

					switch(option){
						case 1 : clearScreen();
							     this.menu.menuUserInformation();
								 menuUserInfo(email);
								 break;
									
						case 2 : {
							     clearScreen();
							     Map<String,Integer> servers = new HashMap<String,Integer>();
								 this.menu.menuCatalogueBegin();
								 out.println("|SERVERLIST|");
								 String [] aux;
								 response = in.readLine();
 								 if(!response.equals("empty")){
 								 	System.out.println(response);
 								 	aux = response.split("\\|");
 								 	servers.put(aux[1],1);
								 	while((response=in.readLine()) != null && !response.equals("quit")){
								 		System.out.println(response);
								 		aux = response.split("\\|");
								 		servers.put(aux[1],1);
								 	}
								 	this.menu.menuCatalogueFinal();
								 	menuCatalogueServers(email,servers);
								 }else {
								 	clearScreen();
								 	System.out.println("\n> ClientHandler -> [No Servers Available]\n");
								 	this.menu.menuUser();
								 }
								 break;
								}

						case 3 : {
							     clearScreen();
								 out.println("|RESERVELIST|"+email);
								 response = in.readLine();
								 if(!response.equals("empty")){
								 	 Map<String,String> reserves = new HashMap<String,String>();
								 	 String [] aux;
								 	 this.menu.menuViewReservesBegin();	
								 	 System.out.println(response);
								     aux = response.split("\\|");
								     reserves.put(aux[1],"");
									 while((response=in.readLine()) != null && !response.equals("quit")){
									 	System.out.println(response);
										aux = response.split("\\|");
									 	reserves.put(aux[1],"");	
									 }
									 this.menu.menuViewReservesFinal();
									 menuViewReserves(email,reserves);
								 } else {
								 	clearScreen();
								 	System.out.println("\n> ClientHandler -> [No reserves in your name]\n");
								 	this.menu.menuUser();
								 }
								 break;
								}

						case 0 : out.println("|LOGOUT| Email:" + email);
								 response = in.readLine();
								 clearScreen();
								 System.out.println("\n> ClientHandler -> " +response+ "\n");
								 this.menu.menuInit();
								 break;
					}

					if(option == 0){
						break;
					}

				} catch(NumberFormatException e){
					clearScreen();
					System.out.println("[ERROR] -> '"+userInput+"' is not an integer. Please insert one.\n");
					this.menu.menuUser();
				} 		
			} 
		} catch (IOException e)  {
			System.out.println("[ERROR]: " +e.getMessage());
		}	
	}

	private void startMenu() throws IOException, ParseException{

		clearScreen();
		this.menu.menuInit();

		String userInput;
		String response;
	
        try{
       		while((userInput = systemIn.readLine()) != null && !userInput.equals("0")) {

       			try{
       				int converse = Integer.valueOf(userInput);
  					switch (converse){
  						case 1 : {
  								clearScreen();
  								System.out.println("Set Email:");
                        		String u = systemIn.readLine();
                        		System.out.println("Set Password:");
                        		String p = systemIn.readLine();
                        		out.println("|REGISTRATION| Email:" + u + "|Password:" + p);
                        		clearScreen();
                        		response = in.readLine();
                        		System.out.println("\n> ClientHandler -> " +response+ "\n");

                        		if(!response.equals("[User already exists]")){ 
                        			this.menu.menuUser();
                            		menuUser(u);    
                        		}
                        		else {
									this.menu.menuInit();
                       	 		}
                       	 		break;
                    	}
                    	case 2 : {
                    			clearScreen();
                    			System.out.println("Email:");
                            	String u = systemIn.readLine();
                            	System.out.println("Password:");
                            	String p = systemIn.readLine();
                            	out.println("|LOGIN| Email:" + u + "|Password:" + p );
                            	clearScreen();
                            	response = in.readLine();
                            	System.out.println("\n> ClientHandler -> " +response+ "\n");

                            	if (response.equals("[User successfully connected]")){ 
                                	this.menu.menuUser();
                            		menuUser(u); 
                    			}
                    			if ((response.equals("[Incorrect password]")) || (response.equals("[User does not exist]")) || (response.equals("[User already connected]"))){ 
									this.menu.menuInit();
								}
								break;	
                    		}
                    	
  					}		

  				} catch(NumberFormatException e){
  					clearScreen();
					System.out.println("[ERROR] -> '"+userInput+"' is not an integer. Please insert one.\n");
					this.menu.menuInit();
				}
            }  

		} catch (IOException e) {
			System.out.println("[ERROR]: " +e.getMessage());
		} 
	}

	private void startClient() throws Exception{

		try{
			System.out.println("---> CLIENT <---");
			System.out.println("> Connecting to server...");

			this.clientSocket = new Socket(this.hostName, this.porto);

			out = new PrintWriter(clientSocket.getOutputStream(), true);
        	in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

        	systemIn = new BufferedReader(new InputStreamReader(System.in));

        	this.receive = new Thread(new ClientReceive(this.clientSocket));

        	startMenu();

		} catch (UnknownHostException e) {
			System.out.println("ERROR: Server doesn't exist!");	
		} catch (Exception e) {
			System.out.println("[ERROR]: " + e.getMessage());
		} finally{	
			try{
				out.println("|CLOSE CONNECTION|");
				this.clientSocket.shutdownInput();
            	this.clientSocket.shutdownOutput();
            	this.clientSocket.close();
			} catch(IOException e){
				System.out.println("[ERROR]: " + e.getMessage());
			}	
		}
	}

	public static void clearScreen() {  
    	System.out.print("\033[H\033[2J");  
    	System.out.flush();  
	}  

	public static void main(String args[]) throws IOException, Exception{
		
		Client client = new Client("127.0.0.1",9999);
		client.startClient();

	}
}
