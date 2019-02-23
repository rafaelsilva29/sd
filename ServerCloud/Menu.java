public class Menu {
	
	public Menu(){}

	public void menuInit(){
		System.out.println("===================================");
		System.out.println("=                                 =");
		System.out.println("=             WELCOME             =");
		System.out.println("=                                 =");
		System.out.println("===================================");
		System.out.println("=                                 =");
		System.out.println("=           [1] SIGN UP           =");
		System.out.println("=           [2] SIGN IN           =");
		System.out.println("=           [0] EXIT              =");
		System.out.println("=                                 =");
		System.out.println("===================================");
		System.out.print(">> ");
	}

	public void menuUser(){
		System.out.println("===================================");
		System.out.println("=                                 =");
		System.out.println("=             PICK ONE            =");
		System.out.println("=                                 =");
		System.out.println("===================================");
		System.out.println("=                                 =");
		System.out.println("=      [1] USER INFORMATION       =");
	    System.out.println("=      [2] CATALOGUE SERVERS      =");
	    System.out.println("=      [3] VIEW RESERVES          =");
		System.out.println("=      [0] LOGOUT                 =");
		System.out.println("=                                 =");
		System.out.println("===================================");
		System.out.print(">> ");
	}

	public void menuUserInformation(){
		System.out.println("===================================");
		System.out.println("=                                 =");
		System.out.println("=            USER INFO            =");
		System.out.println("=                                 =");
		System.out.println("===================================");
		System.out.println("=                                 =");
		System.out.println("=        [1] CHECK ACCOUNT        =");
		System.out.println("=        [2] PUT MONEY            =");
		System.out.println("=        [3] PAY DEBT             =");  
		System.out.println("=        [0] GO BACK              =");
		System.out.println("=                                 =");
		System.out.println("===================================");
		System.out.print(">> ");
	}

	public void menuCatalogueBegin(){
		System.out.println("===================================");
		System.out.println("=                                 =");
		System.out.println("=        CATALOGUE SERVERS        =");
		System.out.println("=                                 =");
		System.out.println("===================================");
	}

    public void menuCatalogueFinal(){
		System.out.println("===================================");
		System.out.println("=            [1] START            =");
		System.out.println("=            [0] BACK             =");
		System.out.println("===================================");
		System.out.print(">> ");
	}

	public void menuViewReservesBegin(){
		System.out.println("===================================");
		System.out.println("=                                 =");
		System.out.println("=           VIEW RESERVES         =");
		System.out.println("=                                 =");
		System.out.println("===================================");
	}

	public void menuViewReservesFinal(){
		System.out.println("===================================");
		System.out.println("=           [1] FINISH            =");
		System.out.println("=           [0] BACK              =");
		System.out.println("===================================");
		System.out.print(">> ");
	}

}