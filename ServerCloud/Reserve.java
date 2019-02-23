import java.util.HashMap;
import java.util.*;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;


public class Reserve{

	private static int sequence = 1;
	private int idReserve;
	private String username_client;      // UserName of Reserve
	private String type_server;          // Type of Server
	private double priceServer;          // Price of Server
	private double current_price = 0;    // Current Price of Reserve
	private double priceFinal;           // Price final Reserve
	private int typeReserve;             // 0 -> means auction / 1 -> request
	private Date date_begin;
	private Date date_final;
	private int state;                   // 0 -> running | 1 -> finnish
	private int statePay;                // 0 -> unpay | 1 -> pay

	public Reserve(String username_client, String type_server, double price, int type, Date date){
		this.idReserve = sequence++;
		this.type_server=type_server;
		this.username_client = username_client;
		this.priceServer = price;
		this.typeReserve=type;
		this.date_begin=date;
		this.state = 0;
		this.statePay = 0;
		this.priceFinal = 0;
	}

	//Set
	public void setIdReserve(int u){ this.idReserve = u;}
	public void setPriceServer(double u){ this.priceServer = u;}
	public void setType_server(String type_server){ this.type_server = type_server;}
	public void setUsername_client(String username_client){ this.username_client = username_client;}
	public void setTypeReserve(int u){ this.typeReserve = u;}
	public void setDateBegin(Date date){this.date_begin=date;}
	public void setDateFinal(Date date){this.date_final=date;}
	public void setState(int i){this.state=i;}
	public void setStatePay(int i){this.statePay=i;}
	public void setPriceFinal(double i){this.priceFinal=i;}


	//Get
	public int getIDReserve(){ return this.idReserve;}
	public double getPriceServer(){ return this.priceServer;}
	public int getTypeReserve(){ return this.typeReserve;}
	public String getTypeServer(){return this.type_server;}
	public String getNameClient(){return this.username_client;}
	public Date getDateBegin(){return this.date_begin;}
	public Date getDateFinal(){return this.date_final;}
	public int getState(){return this.state;}
	public int getStatePay(){return this.statePay;}
	public double getPriceFinal(){return this.priceFinal;}

	// Aux methods
	public double getPriceCurrent(){
		double p = 0;
		Date aux = new Date();
	    long diff = aux.getTime() - this.date_begin.getTime();
		int diffhours = (int) (diff / (60 * 60 * 1000));
		if(diffhours >= 1){
			p = (double) (diffhours * this.priceServer);
		}
		return p;
	}

	public void finnishReserve(){
		this.current_price = 0;
		this.date_final = new Date();
		this.state = 1;
	    long diff = this.date_final.getTime() - this.date_begin.getTime();
		int diffhours = (int) (diff / (60 * 60 * 1000));
		if(diffhours >= 1){
			this.priceFinal = (double) (diffhours * this.priceServer);
		}
	}
}
