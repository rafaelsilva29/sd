

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
    private BufferedReader in;
    private PrintWriter out;
    private int idClient = -1;
    private Controler controler;

    public ClientHandler(Socket client, int id, Controler c) throws IOException{
    	this.clientSocket = client;
        this.out = new PrintWriter(client.getOutputStream(), true);
        this.in = new BufferedReader( new InputStreamReader(client.getInputStream()));
    	this.idClient = id;
        this.controler = c;
    }

    public synchronized int closeConnection(){
        try{
            this.clientSocket.shutdownInput();
            this.clientSocket.shutdownOutput();
            this.clientSocket.close();
            return 1;
        } catch(IOException e){
            return 0;
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
                                   out.println(this.controler.register(email,pass,out));
                                   break;

            case ("LOGIN"): i = aux[2].length();
                            j = aux[3].length();
                            email = aux[2].substring(7,i);
                            pass = aux[3].substring(9,j);
                            out.println(this.controler.login(email,pass,out));
                            break;

            case ("LOGOUT"): i = aux[2].length();
                             email = aux[2].substring(7,i);
                             temp = this.controler.logout(email);
                             out.println(temp);
                             break;

            case ("PUTMONEY"): String amount = aux[3].replace(" Amount:","");
                               email = aux[2].replace(" Email:","");
                               double mo = Double.valueOf(amount);
                               out.println(this.controler.putMoney(email,mo));
                               break;

            case ("CHECKDEBT") : email = aux[2];
            					 out.println(this.controler.checkDebt(email));
            					 break;

            case ("PAYDEBT") :  String[] ress = aux[2].split("\\:");
                                email = ress[0];
                                String m = ress[1];
            					out.println(this.controler.payDebt(email,m));
            					break;

            case ("CHECK ACCOUNT") : email = aux[2].replace(" Email:","");
            						 out.println(this.controler.checkAcount(email));
            						 break;

            case ("FINNISHRESERVE") : String[] reserve = aux[2].split("\\:");
            						  email = reserve[1];
            						  String idReserve = reserve[0];
            						  out.println(this.controler.finnishReserve(idReserve,email));
            						  break;
                                
           case ("SERVERLIST") : Map<String,Tuple> ser = new HashMap<>();
                                 ser.putAll(this.controler.getAllServer());
                                 if(ser.isEmpty()){
                                    out.println("empty");  
                                 } else {
                                    for(Map.Entry<String, Tuple> entry : ser.entrySet()){
                                        Tuple obj = entry.getValue();
                                        String server = entry.getKey();
                                        out.println("|"+server+"| -> "+obj.getPri() +" €/h" + " Available: "+obj.getDisp());
                                    }
                                    out.println("quit");
                                 }
                                  break; 

            case ("RESERVELIST") : List<Reserve> reser = new ArrayList<>();
                                   reser.addAll(this.controler.getReservesUser(aux[2]));
                                   if(reser.isEmpty()){
                                       out.println("empty");
                                   } else {
                                        for(Reserve r : reser){
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
                                        out.println("quit");

                                   }
                                   break;

           case ("CHECKSERVER") :   String[] server = aux[2].split("\\:");
            						int l = this.controler.checkOffer(server[0],server[1],server[2]);
            						double price = Double.valueOf(server[1]);
            						if(l==1){
                                        Reserve r = new Reserve(server[2],server[0],price,1, new Date());
                                        this.controler.putReserve(r);
                                        out.println("[Reserve is finnish, ID: "+ r.getIDReserve() +"]");
                                    } 
            						else if(l==0){ 
                							Reserve r = new Reserve(server[2],server[0],price,0, new Date());
                							this.controler.putReserve(r);
                							out.println("[Reserve is finnish, ID: "+ r.getIDReserve() +"]");
                                    } else if(l==2){
                                    	    price =  this.controler.checkPriceServer(server[2]);
                                    	    Reserve r = new Reserve(server[2],server[0],price,1, new Date());
                                    	    this.controler.putReserve(r);
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
            
            boolean flag= true;
            String line;

            while((line = in.readLine()) != null && flag) {          
                flag = handler(line);
            }   
            
        } catch(IOException e){
            System.out.println(e.getMessage());
        }	
    }
}

