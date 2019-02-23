import java.io.*;
import java.net.Socket;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.lang.*;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.Hashtable;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;
import static java.util.Comparator.*;

public class ClientHandler implements Runnable{

	private Socket clientSocket;
    private static Map<Integer, Server> servers;
    private static Map<String,User> users;
    private static Map<Integer,Reserve> reserves;
    private BufferedReader in;
    private PrintWriter out;
    private ReentrantLock lockClient;
    private int idClient = -1;
    private Hashtable<String, PrintWriter> clients_connected;

    public ClientHandler(Socket client, Map<Integer, Server> servers, Map<String, User> users, int id, Hashtable<String, PrintWriter> cl, Map<Integer,Reserve> reserves) throws IOException{
    	this.clientSocket = client;
        this.out = new PrintWriter(client.getOutputStream(), true);
        this.in = new BufferedReader( new InputStreamReader(client.getInputStream()));
    	this.servers = servers;
    	this.users = users;
        this.reserves = reserves;
    	this.lockClient = new ReentrantLock();
    	this.idClient = id;
        this.clients_connected = cl;
    }

    public synchronized void registerClient(PrintWriter writer, String email){
        if(!this.clients_connected.containsKey(email)){
            this.clients_connected.put(email, writer);
        }
    }

    private synchronized String register(String email, String pass){
        this.lockClient.lock();

        if(this.users.containsKey(email)){
            this.lockClient.unlock();
            return "[User already exists]";

        } 
        
        User us =  new User(email, pass, true);
        this.users.put(email,us);
        registerClient(out,email);

        this.lockClient.unlock();

        return "[User registered]";
    }

    private synchronized String login(String email, String pass){
        if(!this.users.containsKey(email)){
            return "[User does not exist]";
        } 
        else {
            if(this.users.get(email).getConnection() ==  true){
                return "[User already connected]";
            } 
            else {
                if(this.users.get(email).getPassword().equals(pass)){
                    this.users.get(email).setConnection(true);
                    registerClient(out,email);
                    return "[User successfully connected]";
                }
                else {
                    return "[Incorrect password]";
                }
            }
        }
    }

    private synchronized String logout(String email){
        if(this.users.get(email).getConnection() == true) {
            this.users.get(email).setConnection(false);
            this.clients_connected.remove(email);
            return "[Logout done successfully]";
        } else {
            return "[User already logout]";    
        }
    }

    private synchronized String putMoney(String email, double money){
    	if(this.users.containsKey(email)){
    		double aux = this.users.get(email).getMoney();
    		aux = aux + money;
    		this.users.get(email).setMoney(aux);
    		return "[Put money done successfully]";  
    	}
        return "[Put money done insuccessfully]";  
    }

    private synchronized String checkAcount(String email){
       double pay = 0;
       for(Reserve r : this.reserves.values()){
       		if(r.getNameClient().equals(email) && r.getState()==1 && r.getStatePay()==0){
       			pay += r.getPriceFinal();
       		}
       }
       String res = users.get(email).toString() + " |Debt: "+pay;
       return res;
    }

    private synchronized int closeConnection(){
        try{
            this.clientSocket.shutdownInput();
            this.clientSocket.shutdownOutput();
            this.clientSocket.close();
            return 1;
        } catch(IOException e){
            return 0;
        }  
    }

    private synchronized boolean getReservesUser(String user){
        boolean flag = false;
        for(Reserve r : reserves.values()){
            if(r.getNameClient().equals(user)){
               flag = true;
               String type_reserve = "";
               if(r.getTypeReserve()==0){
                    type_reserve = "Auction";
               } else {
                    type_reserve = "Request";
               }
               String state = "";
               if(r.getState()==0){
                    state = "Running";
               } else {
                    state = "Finnish";
               }
               out.println("|"+r.getIDReserve()+"| " +r.getTypeServer()+ "("+state+") -> " + type_reserve+ " ("+r.getPriceCurrent()+"€)");
            }
        }  
        return flag;
    }
    
    private synchronized String checkDebt(String email){
    	double soma = 0;

    	for(Reserve r: this.reserves.values()){
    		if(r.getNameClient().equals(email) && r.getState()==1 && r.getStatePay()==0)
    			soma += r.getPriceFinal();
    	}
        if(soma != 0){
            return "[Your Debt is: " +soma+" €]";
        } else {
            return "[You don't have Debts]";
        }
    }

    private synchronized String payDebt(String email, String valor){
        double money = Double.valueOf(valor);
        double account = this.users.get(email).getMoney();

        if(account < money){
            return "[You don´t have sufficient founds to pay, put money]";
        }

        List<Reserve> reserves_unpay = new ArrayList<>();

        for(Reserve r : this.reserves.values()){
            if(r.getNameClient().equals(email) && r.getState()==1 && r.getStatePay()==0){
                reserves_unpay.add(r);
            }
        }

        reserves_unpay.sort(comparing(Reserve::getPriceFinal));
        int count = 0;

        for(Reserve r : reserves_unpay){
            if((r.getPriceFinal() > money) || (account < money)){
                return "[You don't have founds to pay reserveID: " +r.getIDReserve()+", not founds]";
            } else {
                count++;
                r.setStatePay(1);
                this.users.get(email).setMoney(account-r.getPriceFinal());
            }  
        }
        if(count == reserves_unpay.size()){
            return "[All reserves have been pay]";
        } else {
            return "[Not all reserves hava been pay]";
        }
    } 

    private synchronized boolean getAllServer(){
    	boolean flag = false;
    	Map<String, Tuple> servers_aux = new HashMap<String,Tuple>();

    	for(Server s : servers.values()){
            if(s.getState()==0 || s.getState()==2){
            	if(!servers_aux.containsKey(s.getType())){
            		flag = true;
            		int count = countServer(s.getType());
            		Tuple obj = new Tuple(count, s.getPrice());
            		servers_aux.put(s.getType(),obj);
            	}
            }
        }

        for(Map.Entry<String, Tuple> entry : servers_aux.entrySet()){
        	Tuple obj = entry.getValue();
            String server = entry.getKey();
        	out.println("|"+server+"| -> "+obj.getPri() +" €/h" + " Available: "+obj.getDisp());
        }
        return flag;
    }

    private synchronized int countServer(String server){
    	int count = 0;
    	for(Server s : this.servers.values()){
    		if(s.getState()==0 || s.getState()==2){
    			if(server.equals(s.getType())){
    				count++;
    			}
    		}
    	}
    	return count;
    }

    private synchronized String finnishReserve(String idReserve, String email){
    	int id = Integer.valueOf(idReserve);
    	if(this.reserves.containsKey(id)){
    		Reserve r = this.reserves.get(id);
    		if(r.getNameClient().equals(email)){
    			r.finnishReserve();
    			for(Server s : this.servers.values()){
    				if(s.getType().equals(r.getTypeServer()) && (s.getState()==1 || s.getState()==2)){
    					if(s.getState()==2){
    						s.removeToQueue(email);
    						s.setState(0);
    					} else {
    						s.setState(0);
    					}
    					return "[Successfully on finnish Reserve: " +idReserve+ " -> Price: " +r.getPriceFinal()+"]";
    				}
    			}
    		}
    	}
    	return "[Error on finnish a Reserve: " +idReserve+"]";
    }

    private synchronized int checkOffer(String server, String p, String email){
        List<Server> servers_free = new ArrayList<Server>();    // Array of Free Servers
        List<Server> servers_auction = new ArrayList<Server>(); // Array of Servers in Aunction
        double price = Double.valueOf(p);

        for(Server s : this.servers.values()){
            if(s.getType().equals(server) && s.getState()==0){
                servers_free.add(s);
            }
        }

        for(Server s : this.servers.values()){
            if(s.getType().equals(server) && s.getState()==2){
                servers_auction.add(s);
            }
        }

        if(!servers_free.isEmpty()){

        	for(Server s : servers_free){
        		if(s.getPrice() == price){
                    s.setState(1);
                    return 1;
                } else if(s.getPrice() > price){
                    s.addToQueue(email,price);
                    s.setState(2);
                    return 0;
                } else if(s.getPrice() < price){
                	s.setState(1);
                	return 2;
                }
        	}

        } else {
        	
        	for(Server s : servers_auction){
            	if(s.getQueue().containsKey(email)){
            		return 3;
            	} else {
 					 
 					 // Ord the Queue of a Server by Auction Price
            		 Map<String, Double> resultSort = s.getQueue().entrySet().stream()
                										          .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                                                                  .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                                                                  (oldValue, newValue) -> oldValue, LinkedHashMap::new));

            		if(s.getPrice() == price){
            			// Avisar que roubou o servidor ao utilizador que pertencia
            			String user = resultSort.keySet().iterator().next();
                        System.out.println("> ClientHandler -> Avisa User: "+user+"\n");

                        //sendMessage(user,"[Lost a Reserve(Auction) of a Server in your Name]");

            			s.cleanQueue();
                    	s.setState(1);
                    	return 1;

               		 } else if(s.getPrice() < price){
               		 	// Avisar que roubou o servidor ao utilizador que pertencia
               		 	String user = resultSort.keySet().iterator().next();
                        System.out.println("> ClientHandler -> Avisa User: "+user+"\n");

                        //sendMessage(user,"[Lost a Reserve(Auction) of a Server in your Name]");

               		 	s.cleanQueue();
                		s.setState(1);
                		return 2;

               		 } else if(s.getPrice() > price){

               		 	//Check Queue from server
                     	for(Double d : resultSort.values()){
                     		if(price > d){
                     			// Avisar que roubou o servidor ao utilizador que pertencia
               		 			String user = resultSort.keySet().iterator().next();
                                System.out.println("> ClientHandler -> Avisa User: "+user+"\n");

                                //sendMessage(user,"[Lost a Reserve(Auction) of a Server in your Name]");

               		 			s.addToQueue(email,price);
                    			s.setState(2);
                    			return 0;
                     		} else {
                     			return 4;
                     		}
                     	}
               		 }
            	}
            }
        }	
        return -1;
    }

    private synchronized double checkPriceServer(String server){
    	double res = 0;
    	for(Server  s : this.servers.values()){
    		 if(s.getType().equals(server) && (s.getState() == 0 || s.getState() == 2)){
    		 	res =  s.getPrice();
    		 }	
    	}	
    	return res;
    }

    private synchronized void sendMessage(String clientReceiver, String msg){
        if(this.clients_connected.containsKey(clientReceiver) ){
            if(this.users.get(clientReceiver).getConnection()==true){
                PrintWriter bw = this.clients_connected.get(clientReceiver);
                bw.println(msg);       
             }   
        }
    }

    private boolean handler(String cmd) {
    	boolean res = true;
    	boolean f = false;
    	String [] aux = cmd.split("\\|");
        String option = aux[1];
        String email;
        String pass;
        String temp;
        double money;
        
        int i;
        int j;

        switch(option){
            case ("REGISTRATION"): i = aux[2].length();
                                   j = aux[3].length();
                                   email = aux[2].substring(7,i);
                                   pass = aux[3].substring(9,j);
                                   out.println(register(email,pass));
                                   break;

            case ("LOGIN"): i = aux[2].length();
                            j = aux[3].length();
                            email = aux[2].substring(7,i);
                            pass = aux[3].substring(9,j);
                            out.println(login(email,pass));
                            break;

            case ("LOGOUT"): i = aux[2].length();
                             email = aux[2].substring(7,i);
                             temp = logout(email);
                             out.println(temp);
                             break;

            case ("PUTMONEY"): String amount = aux[3].replace(" Amount:","");
                               email = aux[2].replace(" Email:","");
                               double mo = Double.valueOf(amount);
                               out.println(putMoney(email,mo));
                               break;

            case ("CHECKDEBT") : email = aux[2];
            					 out.println(checkDebt(email));
            					 break;

            case ("PAYDEBT") :  String[] ress = aux[2].split("\\:");
                                email = ress[0];
                                String m = ress[1];
            					out.println(payDebt(email,m));
            					break;

            case ("CHECK ACCOUNT") : email = aux[2].replace(" Email:","");
            						 out.println(checkAcount(email));
            						 break;

            case ("FINNISHRESERVE") : String[] reserve = aux[2].split("\\:");
            						  email = reserve[1];
            						  String idReserve = reserve[0];
            						  out.println(finnishReserve(idReserve,email));
            						  break;
                                
            case ("SERVERLIST") : f =  getAllServer();
            					  if(f){
            					  	 out.println("quit");
                                  } else{
                                     out.println("empty");
                                  }
                                  break; 

            case ("RESERVELIST") : f = getReservesUser(aux[2]);
                                   if(f){
                                      out.println("quit");
                                   } else{
                                      out.println("empty");
                                   }
                                   break;

            case ("CHECKSERVER") :  String[] server = aux[2].split("\\:");
            						int l = checkOffer(server[0],server[1],server[2]);
            						double price = Double.valueOf(server[1]);
            						if(l==1){
                                        Reserve r = new Reserve(server[2],server[0],price,1, new Date());
                                        this.reserves.put(r.getIDReserve(),r);
                                        out.println("[Reserve is finnish, ID: "+ r.getIDReserve() +"]");
                                    } 
            						else if(l==0){ 
                							Reserve r = new Reserve(server[2],server[0],price,0, new Date());
                							this.reserves.put(r.getIDReserve(),r);
                							out.println("[Reserve is finnish, ID: "+ r.getIDReserve() +"]");
                                    } else if(l==2){
                                    	    price = checkPriceServer(server[2]);
                                    	    Reserve r = new Reserve(server[2],server[0],price,1, new Date());
                                    	    this.reserves.put(r.getIDReserve(),r);
                                    	    out.println("[Reserve is finnish, ID: "+ r.getIDReserve() +"]");
                                    } else if(l==3){
            							out.println("[Error, you already auction that server]");
            						} else if(l==4){
            							out.println("[Error, insufficient money]");
            						}else {
            							out.println("[Error on create reserve]");
            						}
            						break;

            case ("CLOSE CONNECTION") : int flag = closeConnection();
                                        if(flag == 1){
                                            System.out.println("> ClientHandler -> Connection with <-> Terminal ID: "+this.idClient+ " closed..."+"\n");
                                        } else {
                                            System.out.println("> ClientHandler -> Connection with <-> Terminal ID: "+this.idClient+ " not closed..."+"\n");    
                                        }
                                        break;
         }
    	return res;
    }

    public void run(){

    	System.out.println("> ClientHandler -> New client on ServerCloud <-> Terminal ID: "+this.idClient+"\n");

        try{
            out = new PrintWriter(clientSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            
            boolean flag = true;
            String line;

            while((line = in.readLine()) != null && flag) {          
                flag = handler(line);
            }   
            
        } catch(IOException e){
            System.out.println(e.getMessage());
        }	
    }
}

