
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

public class ControlerImpl implements Controler {

	private static Map<Integer, Server> servers;
    private static Map<String,User> users;
    private static Map<Integer,Reserve> reserves;
    private static Hashtable<String, PrintWriter> clients_connected;


    public ControlerImpl(){
    	servers = new HashMap<>();
    	users = new HashMap<>();
    	reserves = new HashMap<>();
    	clients_connected = new Hashtable<>();
    }

    public ControlerImpl(Map<Integer, Server> servers, Map<String,User> users){
    	this.servers = servers;
    	this.users = users;
    }

    public synchronized void registerClient(PrintWriter writer, String email){
        if(!clients_connected.containsKey(email)){
            clients_connected.put(email, writer);
        }
    }

    public synchronized String register(String email, String pass, PrintWriter out){
        if(users.containsKey(email)){
            return "[User already exists]";

        } 
        User us =  new User(email, pass, true);
        users.put(email,us);
        registerClient(out,email);

        return "[User registered]";
    }

    public synchronized String login(String email, String pass, PrintWriter out){
        if(!users.containsKey(email)){
            return "[User does not exist]";
        } 
        else {
            if(users.get(email).getConnection() ==  true){
                return "[User already connected]";
            } 
            else {
                if(users.get(email).getPassword().equals(pass)){
                    users.get(email).setConnection(true);
                    registerClient(out,email);
                    return "[User successfully connected]";
                }
                else {
                    return "[Incorrect password]";
                }
            }
        }
    }

    public synchronized String logout(String email){
        if(users.get(email).getConnection() == true) {
            users.get(email).setConnection(false);
            clients_connected.remove(email);
            return "[Logout done successfully]";
        } else {
            return "[User already logout]";    
        }
    }

    public synchronized String putMoney(String email, double money){
    	if(users.containsKey(email)){
    		double aux = users.get(email).getMoney();
    		aux = aux + money;
    		users.get(email).setMoney(aux);
    		return "[Put money done successfully]";  
    	}
        return "[Put money done insuccessfully]";  
    }

    public synchronized String checkAcount(String email){
       double pay = 0;
       for(Reserve r : reserves.values()){
       		if(r.getNameClient().equals(email) && r.getState()==1 && r.getStatePay()==0){
       			pay += r.getPriceFinal();
       		}
       }
       String res = users.get(email).toString() + " |Debt: "+pay;
       return res;
    }

    public synchronized List<Reserve> getReservesUser(String user){
        List<Reserve> res = new ArrayList<>();
        for(Reserve r : reserves.values()){
            if(r.getNameClient().equals(user)){
               res.add(r);
            }
        }  
        return res;
    }
    
    public synchronized String checkDebt(String email){
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

    public synchronized String payDebt(String email, String valor){
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

    public synchronized Map<String,Tuple> getAllServer(){
    	Map<String,Tuple> servers_aux = new HashMap<String,Tuple>();
    	for(Server s : servers.values()){
            if(s.getState()==0 || s.getState()==2){
            	if(!servers_aux.containsKey(s.getType())){
            		int count = countServer(s.getType());
            		Tuple obj = new Tuple(count, s.getPrice());
            		servers_aux.put(s.getType(),obj);
            	}
            }
        }
        return servers_aux;
    }

    public synchronized int countServer(String server){
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

    public synchronized String finnishReserve(String idReserve, String email){
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

    public synchronized int checkOffer(String server, String p, String email){
        List<Server> servers_free = new ArrayList<Server>(); // Lista de servidores livres
        List<Server> servers_auction = new ArrayList<Server>(); // Lista de servidores auction
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
 					 
 					 // Ordena Queue do servidor
            		 Map<String, Double> resultSort = s.getQueue().entrySet().stream()
                										          .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                                                                  .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                                                                  (oldValue, newValue) -> oldValue, LinkedHashMap::new));

            		if(s.getPrice() == price){
            			// Avisar que roubou o servidor ao utilizador que pertencia
            			String user = resultSort.keySet().iterator().next();
                        System.out.println("> ClientHandler -> Avisa User: "+user );

                        //sendMessage(user,"[Lost a Reserve(Auction) of a Server in your Name]");

            			s.cleanQueue();
                    	s.setState(1);
                    	return 1;

               		 } else if(s.getPrice() < price){
               		 	// Avisar que roubou o servidor ao utilizador que pertencia
               		 	String user = resultSort.keySet().iterator().next();
                        System.out.println("> ClientHandler -> Avisa User: "+user );

                        //sendMessage(user,"[Lost a Reserve(Auction) of a Server in your Name]");

               		 	s.cleanQueue();
                		s.setState(1);
                		return 2;

               		 } else if(s.getPrice() > price){
               		 	 // Verifica Queue do servidor
               		 	
                     	for(Double d : resultSort.values()){
                     		if(price > d){
                     			// Avisar que roubou o servidor ao utilizador que pertencia
               		 			String user = resultSort.keySet().iterator().next();
                                System.out.println("> ClientHandler -> Avisa User: "+user );

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

    public synchronized double checkPriceServer(String server){
    	double res = 0;
    	for(Server  s : this.servers.values()){
    		 if(s.getType().equals(server) && (s.getState() == 0 || s.getState() == 2)){
    		 	res =  s.getPrice();
    		 }	
    	}	
    	return res;
    }

    public synchronized void sendMessage(String clientReceiver, String msg){
        if(this.clients_connected.containsKey(clientReceiver) ){
            if(this.users.get(clientReceiver).getConnection()==true){
                PrintWriter bw = this.clients_connected.get(clientReceiver);
                bw.println(msg);       
             }   
        }
    }

    public synchronized void putReserve(Reserve r){
    	this.reserves.put(r.getIDReserve(),r);
    }
}