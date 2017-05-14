package controller;

import model.*;

import java.awt.Color;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.*;
import java.net.*;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JOptionPane;

import gui.*;

/**
 * Klasa implementująca Kontroler wzorca MVC.
 * 
 * Klasa wykonuję następujące zadania:
 * 
 * 1.Odpowiada za ustanawianie połączenia między graczami, w tym inicjalizacje serwera.
 * 2.Odpowiada za komunikację między graczami.
 * 3.Odpowiada za komunikacje między modelem a widokiem.
 * 4.Przetwarza input od gracza, jak i od oponenta, aktualizując model i widok.
 * 
 * Podczas logowania sprawdzane jest, czy możliwe jest połączenie pod wybrany adres IP i port, 
 * jeśli nie, następuję inicjalizacja serwera. Następnie serwer czeka na połączenie oponenta.
 * Wtedy następuję inicjalizacja gry.
 * 
 * Protokół sieciowy wykorzystany w grze jest w budowie podobny do TCP, to znaczy
 * gracze wysyłają do siebie wiadomości, oczekując odpowiedzi zwrotnych.
 * 
 * @author Piotr Walas
 *
 */

public class GameController implements Runnable {
	
	/**
	 * Rozmiar planszy (plansza jest kwadratowa,
	 * a jedną linie i kolumnę zajmują indeksy).
	 */
	public  static final int size = 11;
	
	/**
	 * Ilość statków o długości 4
	 */
	public  static final int x4ship = 2;
	
	/**
	 * Ilość statków o długości 3
	 */
	public  static final int x3ship = 1;
	
	/**
	 * Ilość statków o długości 2
	 */
	public 	static final int x2ship = 2;
	
	/**
	 * Ilość statków o długości 1
	 */
	public  static final int x1ship = 1;
	
	
	/**
	 * Ogólna ilość części statków.
	 */
	public static final int overal = 4*x4ship+3*x3ship+2*x2ship+x1ship;
	
	/**
	 * Pole używane przy rozstawianiu statków na początku gry.
	 * Oznacza aktualny typ rozstawianego statku.
	 */
	private int actualShip = 0;
	
	/**
	 * Pole używane przy rozstawianiu statków na początku gry.
	 * Oznacza ile statków aktualnego typu rozstawiono.
	 */
	private int numberOfShip = 0;
	
	/**
	 * IP do którego łączy się gracz (lub swoje IP, jeśli jest serwerem)
	 */
	private String ip = "localhost";
	
	/**
	 * Port po którym łączy się gracz.
	 */
	private int port = 22222;
	
	/**
	 * Umożliwia utworzenie wątku.
	 */
	private Thread thread;
	
	/**
	 * Socket, za pomocą którego następuję komunikacja graczy.
	 * @see java.net.Socket
	 */
	private Socket socket;
	
	/**
	 * Strumień do którego wiadomości wysyła gracz, a czyta z niego gracz drugi.
	 * Ustanawiany podczas łączenia z drugim graczem.
	 * @see java.io.DataOutputStream
	 */
	private DataOutputStream outputStream;
	
	/**
	 * Strumień z którego wiadomości odczytuje gracz, a pisze do niego gracz drugi.
	 * Ustanawiany podczas łączenia z drugim graczem.
	 * @see java.io.DataInputStream
	 */
	private DataInputStream inputStream;
	
	/**
	 * Z oficjalnej dokumentacji:
	 * 
	 * This class implements server sockets. A server socket waits for requests to come in over the network. 
	 * It performs some operation based on that request, and then possibly returns a result to the requester. 
	 * The actual work of the server socket is performed by an instance of the SocketImpl class. 
	 * An application can change the socket factory that creates the socket implementation to configure 
	 * itself to create sockets appropriate to the local firewall.
	 *
	 * @see java.net.ServerSocket
	 */
	private ServerSocket serverSocket;
	
	/**
	 * Czy ustanowiono połączenie
	 */
	private boolean accepted = false;
	
	/**
	 * Czy twoja tura
	 */
	private boolean yourTurn = false;
	
	/**
	 * Czy gracz jest serwerem.
	 */
	private boolean server = false;
	
	/**
	 * Czy gracz oczekuje na informacje zwrotną
	 */
	private boolean waitForRespond = false;
	
	/**
	 * Czy trwa etap inicjalizacji
	 */
	private boolean initializing = true;
	
	/**
	 * Czy aktualnie dodawany statek jest w pozycji horyzontalnej
	 * Niezbędna przy rzostawianiu statków na planszy.
	 */
	private boolean horizontal = true;
	
	/**
	 * Okno informacyjne.
	 */
	Info i;
	
	/**
	 */
	private boolean ready = false;
	
	/**
	 * Czy gracze są w trakcie gry.
	 */
	private boolean inGame = false;
	
	/**
	 * Czy gracz zaczyna grę.
	 */
	private boolean globalTurn = false;

	/**
	 */
	private int x;
	/**
	 */
	private int y;
	/**
	 */
	private int length;
	
	/**
	 * Pole to liczy ilośc błędów, które wystąpiły w trakcie trwania połączenia między graczami.
	 * Jeśli wystapi błąd, uznaję się, że gracz sie rozłączył lub nastąpił poważny błąd
	 * i gra jest zakończana.
	 */
	int error = 0;
	
	/**
	 * Referencja do modelu.
	 */
	private GameModel gameModel;
	
	/**
	 * Referencja do widoku.
	 */
	private GameGui gameGui;
	
	public GameController(GameModel theModel, GameGui theGUI)
	{
		this.gameModel = theModel;
		this.gameGui = theGUI;
		
		ConnectionWindow con = new ConnectionWindow();
		
		while(con.isVisible() == true)
		{
			try {
				Thread.sleep(30);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	
		try
		{
			if(!con.getIP().equals(""))
				ip = con.getIP();
			
			if(!con.getPort().equals(""))
				port = Integer.parseInt(con.getPort());
		}
		catch(NullPointerException e)
		{
			e.printStackTrace();
			System.exit(1);
		}
		
		
		if(!connect()) initializeServer();
		
		gameGui.addRotateListener(new RotateListener());
		gameGui.addBoardButtonsListener(new BoardButtonListener(), new BoardMouseListener());
		
		gameGui.boardReset();
		
		thread = new Thread(this, "TicTacToe");
		thread.start();
	}
	
	/**
	 * Metoda umożliwiająca wykonywanie pętli gry.
	 * 
	 * @see java.lang.Thread#run()
	 */
	public void run()
	{			
		while(true)
		{	
			if(accepted == false && server == true)
				listenForServerRequest();
			
			try {
				tick();
			} catch (InterruptedException e) {
				
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * "Główna" metoda gry, odpowiedzialna za komunikacje między graczami.
	 * 
	 * Metoda sprawdza, czy nastąpiło już rozstawienie statków. Jeśli nie, nadzoruje połączenie w taki sposób,
	 * by umożliwić to graczom, a następnie umożliwić normalną grę.
	 * 
	 * Podczas jej wykonywania gracz, którego NIE jest aktualnie tura, oczekuje na wiadomość od drugiego gracza,
	 * który w tym czasie wybiera miejsce do strzalu. Po otrzymaniu wiadomości, jeśli przeciwnik nie trafił, 
	 * odsyła wiadomość o pudle i oczekuje na potwierdzenie odbioru, by móc zmienić turę graczy i umożliwić
	 * graczowi strzał. Jeśli przeciwnik trafił, wysyła takową informacje i przeciwnik strzela dalej.
	 * 
	 * @throws InterruptedException
	 * 		Wyjątek wystąpi gdy zostanie utracona łączność z drugim graczem.
	 */
	private void tick() throws InterruptedException
	{	
		if(ready == false)
		{
			gameGui.setTurnInfo("Rozstaw swoje statki");
			Thread.sleep(20);
			return;
		}
		
		if(ready && inGame == false)
		{
			String toSend = "START";
			try {
				outputStream.writeUTF(toSend);
				outputStream.flush();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			
			gameGui.setTurnInfo("Przeciwnik wciaz rozstawia statki...");
			String k;
				try {
					k = inputStream.readUTF();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			gameGui.setTurnInfo(yourTurn);
			inGame = true;
		}
		
		thread.sleep(50); //potrzebna synchronizacja!
		if(!yourTurn && inGame)
		{
			boolean win = false;
			try{
					if(!waitForRespond)
					{
						String i = inputStream.readUTF();
						//System.out.println("Otrzymalem sygnal bez oczekiwania");
						String info[] = i.split(";");
						
						int x = Integer.parseInt(info[0])/size;
						int y = Integer.parseInt(info[0])%size;
						
						//System.out.println(x + " " +  y + " " + info[1]);
						
						FieldState f = gameModel.getField(x, y);
						if(info[1].equals("shoot"))
						{
							if(f.equals(FieldState.SHIP))
							{
								gameModel.setYourField(x, y, FieldState.HIT);
								gameGui.setYourButton(x,y, GameGui.hit);
								win = checkWin();
								String toSend;
								Ship temp;
								if((temp = gameModel.isDestroyed(x,y)) != null)
								{
									toSend = (temp.getBegin().x*size+temp.getBegin().y) + ";destroyed;" + temp.getLength()+";"+(x*size+y);
									if(temp.isHorizontal()==true)
										toSend += ";horizontal";
									else
										toSend += ";notHorizontal";
								}
									
								else
									toSend = info[0] + ";hit";
								
								try {
									outputStream.writeUTF(toSend);
									outputStream.flush();
								} catch (IOException e1) {
									e1.printStackTrace();
								}
							}
							else if(f.equals(FieldState.NULL) )
							{
								gameModel.setYourField(x, y, FieldState.MISS);
								gameGui.setYourButton(x,y, GameGui.miss);
								String toSend = info[0] + ";miss";
								yourTurn = true;
								gameGui.setTurnInfo(true);
								try {
									outputStream.writeUTF(toSend);
									outputStream.flush();
								} catch (IOException e1) {
									e1.printStackTrace();
								}
							}
						}
					}      
					else //if(waitForRespond)
					{
						String i = inputStream.readUTF();
						
						String info[] = i.split(";");
						
						int x = Integer.parseInt(info[0])/size;
						int y = Integer.parseInt(info[0])%size;
						
						FieldState f = gameModel.getField(x, y);
						
						if(info[1].equals("hit"))
						{
							gameModel.setEnemyField(x, y, FieldState.HIT);
							gameGui.setEnemyButton(x, y, GameGui.hit);
							waitForRespond = false;
							win = checkWin();
							yourTurn = true;
							gameGui.setTurnInfo(true);
						}
						else if(info[1].equals("miss"))
						{
							gameModel.setEnemyField(x, y, FieldState.MISS);
							gameGui.setEnemyButton(x, y, GameGui.miss);
							waitForRespond = false;
							
							String toSend = info[0] + ";yourTurn";
							
							outputStream.writeUTF(toSend);
							outputStream.flush();
						}
						else if(info[1].equals("destroyed"))
						{
							
							if(info[4].equals("horizontal"))
							{
								gameModel.setEnemyField(x, Integer.parseInt(info[3])%size, FieldState.HIT);
								gameGui.setEnemyButton(x, Integer.parseInt(info[3])%size, GameGui.hit);
								gameModel.markUnshootable(x,y,Integer.parseInt(info[2]), true);
								gameGui.markUnshootable(x, y, Integer.parseInt(info[2]),true);
							}
							else
							{
								gameModel.setEnemyField(Integer.parseInt(info[3])/size, Integer.parseInt(info[3])%size, FieldState.HIT);
								gameGui.setEnemyButton(Integer.parseInt(info[3])/size, Integer.parseInt(info[3])%size, GameGui.hit);
								gameModel.markUnshootable(x,y,Integer.parseInt(info[2]), false);
								gameGui.markUnshootable(x, y, Integer.parseInt(info[2]), false);
							}
							
							waitForRespond = false;
							win = checkWin();
							yourTurn = true;
							gameGui.setTurnInfo(true);
						}
					}

				}
				catch (IOException e) {
					e.printStackTrace();
					error++;
					
					if(error > 0)
					{
						JOptionPane.showMessageDialog(null, "Brak polaczenia z drugim graczem");
						System.exit(1);
					}
				}
			
			if(win == true)
			{
				win = false;
				
				while(i.d.isVisible() == true)
					Thread.sleep(25);
				
				reset();
			}
		}
		
	}
	
	/**
	 * Metoda ta umożliwia serwerowi oczekiwanie na przeciwnika.
	 * 
	 * Dopóki przeciwnik nie dołączy, serwer zostaje zawieszony.
	 * 
	 * Po dołączeniu drugiego gracza zestanawiane są stumienie, po których nastąpi komunikacja.
	 */
	private void listenForServerRequest()
	{
		Socket socket = null;
		gameGui.setTurnInfo("Oczekiwanie na polaczenie sie przeciwnika...");
		try
		{
			socket = serverSocket.accept();
			outputStream = new DataOutputStream(socket.getOutputStream());
			inputStream = new DataInputStream(socket.getInputStream());
			accepted = true;
			System.out.println("CLIENT HAS REQUESTED TO JOIN, AND WE HAVE ACCEPTED");
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Metoda pozwalająca na połączenie się do danego serwera.
	 * 
	 * Jeśli połączenie się nie powiedzie, uznaję się, że gracz zostaje serwerem.
	 * 
	 * Ustanawiane są stumienie, po których nastąpi komunikacja.
	 * @return
	 * 		Czy nawiązanie połączenia się powiodło.
	 */
	private boolean connect()
	{
		try
		{
			socket = new Socket(ip, port);
			outputStream = new DataOutputStream(socket.getOutputStream());
			inputStream = new DataInputStream(socket.getInputStream());
			accepted = true;
		}
		catch(IOException e)
		{
			System.out.println("Unable to connect to the address: " + ip + ":" + port + " | Starting a server");
			return false;
		}
		
		
		System.out.println("Successfully connected to the server.");
		return true;
	}
	
	/**
	 * Metoda odpowiedzialna za inicjalizacje serwera.
	 * 
	 * Połączenie zestawiane jest na konkretne ip: {@link GameController#ip} i port: {@link GameController#port}
	 * 
	 * Gracz będący serwerem rozpoczyna rozgrywkę.
	 */
	private void initializeServer()
	{
		try
		{
			serverSocket = new ServerSocket(port, 8, InetAddress.getByName(ip));
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
		yourTurn = true;
		gameGui.setTurnInfo(true);
		server = true;
	}
		
	/**
	 * Metoda sprawdza, czy gra się zakończyła (któryś z graczy wygrał).
	 * 
	 * Jeśli tak, to wyświetlana jest odpowiednia informacja.
	 * @return
	 * 		Czy koniec gry (gracz wygrał lub przegrał).
	 */
	private boolean checkWin()
	{
		String info = gameModel.checkWin();
		if(info != null)
		{
			try {
				i = new Info(gameGui, info);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}	
			return true;
		}
		gameGui.setRemainingInfo(gameModel.getYourLeft(), gameModel.getEnemyLeft());
		return false;
	}
	
	/**
	 * Metoda resetuję całą grę na początku rozgrywki.
	 *
	 * Wybierany jest (naprzemiennie) gracz, który rozpocznie następną rozgrywkę.
	 */
	public void reset()
	{	
		actualShip = 0;
		numberOfShip = 0;
		initializing = true;
		
		waitForRespond = false;
		
		if((globalTurn && server) || (!globalTurn && !server))
		yourTurn = true;	
		
		if((globalTurn && !server) || (!globalTurn && server))
		yourTurn = false;
		
		globalTurn = !globalTurn;
		
		ready = false;
		inGame = false;
		
		gameGui.setRemainingInfo("");
			
		gameModel.reset();
		gameGui.boardReset();
		
	}
	
	/**
	 * Klasa odpowiedzialna za implementacje obługi wyświetlania możliwości wstawienia statków w wybrane miejsce.
	 * 
	 * W trakcie rozstawiania statków, po najechaniu na dane pole planszy gracza podświetlane są pola na których
	 * zostanie umiejscowiony nowy statek.
	 * 
	 * @author Piotr Walas
	 *
	 */
	class BoardMouseListener implements MouseListener, MouseMotionListener
	{
		public void mouseMoved(MouseEvent e)
		{
			gameGui.getFrame().requestFocus();
			
			if(initializing == false)
				return;
					
			x = (int)((JButton) e.getSource()).getClientProperty("field")/size;
			y = (int)((JButton) e.getSource()).getClientProperty("field")%size;
			
			length = 4 - actualShip;
			
			if(horizontal && y+length > size)
				return;
			if(!horizontal && x+length > size)
				return;
			
			boolean possible = gameModel.checkIfPossibleAddition(x, y, length, horizontal);
			
			if(possible == true)
			{
				gameGui.initializeButtons(x,y,length,horizontal,GameGui.addable);
			}
		}
		
		public void mouseExited(MouseEvent e)
		{
			gameGui.getFrame().requestFocus();
			
			if(initializing == false)
				return;
			
			int x = (int)((JButton) e.getSource()).getClientProperty("field")/size;
			int y = (int)((JButton) e.getSource()).getClientProperty("field")%size;
			int length = 4 - actualShip;
			
			for(int i = 0; i < length; i++)
			{
				if(horizontal)
				{
					
					if(y+i >= size || gameModel.getField(x, y+i) == FieldState.SHIP)
						return;
				}
				else
				{
					if(x+i >= size || gameModel.getField(x+i, y) == FieldState.SHIP)
						return;
				}
			}
			
			gameGui.initializeButtons(x,y,length,horizontal,GameGui.background);		
			
		}
		

		@Override
		public void mouseDragged(MouseEvent e) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void mouseClicked(MouseEvent e) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void mousePressed(MouseEvent e) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void mouseReleased(MouseEvent e) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void mouseEntered(MouseEvent e) {
			// TODO Auto-generated method stub
			
		}	
		
	}
	
	/**
	 * Klasa będąca słuchaczem, dzięki któremu mozliwe jest pobieranie informacji w które pole
	 * i w którą planszę (swoją czy przeciwnika), kliknął gracz.
	 * 
	 * Dzięki temu w trakcie rozstawiania statków kontroler wie w które miejsca gracz chce postawic statki,
	 * a w trakcie gry w które strzela.
	 * 
	 * @author Piotr Walas
	 *
	 */
	class BoardButtonListener implements ActionListener
	{

		/**
		 *  W trakcie rozstawiania statków metoda odczytuje, w które miejsca gracz chce ustawić statki.
		 *  
		 *  W trakcie gry metoda odczytuje w które miejsca gracz strzela. Wysyła informacje
		 *  o wybranym polu do innego gracza, w celu otrzymania informacji zwrotnej trafienie/pudło.
		 * 
		 */
		@Override
		public void actionPerformed(ActionEvent e) {

			int x = (int)((JButton) e.getSource()).getClientProperty("field")/size;
			int y = (int)((JButton) e.getSource()).getClientProperty("field")%size;
			int b = (int)((JButton) e.getSource()).getClientProperty("board"); //czy klikam na swoja plansze
			
			gameGui.getFrame().requestFocus(); //inaczej nie zadziala keyListener
			
			if(initializing == true && b == 0 && accepted)
			{
				
				boolean add = false;
				
				switch(actualShip)
				{
					case 0:
						if((add = gameModel.addShip(x, y, 4, horizontal)) == true)
						{
							gameGui.initializeButtons(x,y,4,horizontal,GameGui.ship);
							numberOfShip++;
							if(numberOfShip == x4ship)
							{
								numberOfShip =0;
								actualShip++;
							}
							
						}
				break;
					case 1:
						if((add = gameModel.addShip(x, y, 3, horizontal)) == true)
						{
							gameGui.initializeButtons(x,y,3,horizontal,GameGui.ship);
							numberOfShip++;
							if(numberOfShip == x3ship)
							{
								numberOfShip = 0;
								actualShip++;
							}
							
						}
					case 2:
						if((add = gameModel.addShip(x, y, 2, horizontal)) == true)
						{
							gameGui.initializeButtons(x,y,2,horizontal,GameGui.ship);
							numberOfShip++;
							if(numberOfShip == x2ship)
							{
								numberOfShip = 0;
								actualShip++;
							}
							
						}
				break;
					case 3:
						if((add = gameModel.addShip(x, y, 1, horizontal)) == true)
						{
							gameGui.initializeButtons(x,y,1,horizontal,GameGui.ship);
							numberOfShip++;
							if(numberOfShip == x1ship)
							{
								initializing = false;
								ready = true;
								gameGui.setRemainingInfo(overal, overal);
								//System.out.println("Zainicjowalem");
							}
							
						}
				break;
									
				}
			}
			
			if(e.getSource() instanceof JButton && accepted == true && yourTurn == true && b == 1 && initializing == false && inGame == true)
			{
				if(((JButton) e.getSource()).getBackground().getRGB() != GameGui.background.getRGB())
				{
					return;
				}
				
					int a = ((int)((JButton) e.getSource()).getClientProperty("field"));	
					
					String toSend = a + ";shoot";
					
					try {
						outputStream.writeUTF(toSend);
						outputStream.flush();
						waitForRespond = true;
						yourTurn = false;
						gameGui.setTurnInfo(false);
					} catch (IOException e1) {
						e1.printStackTrace();
						
						error++;
						
						if(error > 0)
						{
							JOptionPane.showMessageDialog(null, "Brak polaczenia z drugim graczem");
							System.exit(1);
						}
					}
				
				}
				
			}
				
		}
		
	/**
	 * Klasa odpowiedzialna za umożliwienie wybrania orientacji dodawanych statków.
	 * Nasłuchuję czy został wybrany (kliknięty) odpowiedni klawisz.
	 *
	 * @author Piotr Walas
	 *
	 */
	class RotateListener implements KeyListener
	{
		/**
		 */
		@Override
		public void keyTyped(KeyEvent e) {
				
		}

		/**
		 * Metoda nasłuchuję, czy został wciśnięty odpowiedni przycisk.
		 * 
		 * Jeśli tak, to zmienia orientacje dodawanych statków.
		 * Istotna tylko przy dodawaniu nowych statków.
		 * 
		 */
		@Override
		public void keyPressed(KeyEvent e) {
			if(e.getKeyCode() == KeyEvent.VK_Q)
			{
				boolean tempHorizontal = horizontal;
				horizontal = !horizontal;
				
				if(initializing == false)
				{
					return;
				}
				else
				{
					for(int i = 0; i < length; i++)
					{
						if(tempHorizontal)
						{
							
							if(y+i >= size || gameModel.getField(x, y+i) == FieldState.SHIP)
								return;
						}
						else
						{
							if(x+i >= size || gameModel.getField(x+i, y) == FieldState.SHIP)
								return;
						}
					}
					
					gameGui.initializeButtons(x,y,length,tempHorizontal,GameGui.background);	
				}
			
			}		
			
		}
		
		/**
		 */
		@Override
		public void keyReleased(KeyEvent e) {
			
		}
		
	}
	
}
