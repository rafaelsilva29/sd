
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

public interface Controler {

     public String register(String email, String pass, PrintWriter writer);

     public String login(String email, String pass, PrintWriter out);

     public String logout(String email);

     public String putMoney(String email, double money);

     public String checkAcount(String email);

     public List<Reserve> getReservesUser(String user);

     public  String checkDebt(String email);

     public String payDebt(String email, String valor);

     public Map<String,Tuple> getAllServer();

     public int countServer(String server);

     public String finnishReserve(String idReserve, String email);

     public int checkOffer(String server, String p, String email);

     public double checkPriceServer(String server);

     public void sendMessage(String clientReceiver, String msg);

     public void putReserve(Reserve r);
    
}
