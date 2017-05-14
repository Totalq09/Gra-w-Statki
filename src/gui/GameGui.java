package gui;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.plaf.ColorUIResource;

import controller.*;
import model.FieldState;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.GraphicsEnvironment;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;

/**
 * Klasa reprezentująca Widok wzorca MVC.
 * 
 * Opowiedzialna jest za wyświetlne graczom pola gry,
 * czyli plansz gracza i oponenta, statków, trafień, chybień,
 * oraz informacji dodatkowych.
 * 
 * @author Piotr Walas
 *
 */
public class GameGui
{
	/**
	 */
	GraphicsEnvironment ge;
	/**
	 * Font używany w aplikacji.
	 */
	private Font font;
	
	/**
	 * Nadrzędna ramka gry.
	 */
	private JFrame frame;
	
	/**
	 * Wielkości plansz graczy.
	 */
	private static final int size = GameController.size;
	
	/**
	 * Kolor na jaki będzie zaznaczane trafienie.
	 */
	public static final Color hit = new Color(255,79,28);
	
	/**
	 * Kolor na jaki będzie zaznaczane chybienie.
	 */
	public static final Color miss = new Color(232,183,12);
	
	/**
	 * Kolor na jaki będzie zaznaczany statek.
	 */
	public static final Color ship = new Color(68,13,255);
	
	/**
	 * Kolor na jaki będzie zaznaczane, że na danych polach można dodać statek.
	 */
	public static final Color addable = new Color(12,219,232);
	
	/**
	 * Kolor tła pól indeksów.
	 */
	public static final Color background_index = new Color(178,63,9);
	
	/**
	 * Kolor standardowy pól.
	 */
	public static final Color background = new Color(0xE5E4E2);

	/**
	 * Reprezentacja graficzna pola gracza.
	 */
	private JButton [][] yourBoardView = new JButton[size][size];
	
	/**
	 * Reprezentacja graficzna pola oponenta.
	 */
	private JButton [][] enemyBoardView = new JButton[size][size];
	
	/**
	 * Etykieta wyświetlająca informacje dodatkowe.
	 */
	private JLabel infoLabel;
	
	/**
	 */
	String remainingInfo = "";
	/**
	 */
	String turnInfo = "";
	/**
	 */
	String additionalInfo;
	
	/**
	 * Tworzą nową instancje Widoku
	 * 
	 * Wykorzystuje {@link java.awt.GridBagLayout}
	 */
	public GameGui()
	{
		infoLabel = new JLabel();
		setTurnInfo(false);
		
		frame = new JFrame();
		frame.setTitle("Warship");

		frame.setIconImage(new ImageIcon(getClass().getResource("/resources/anchor.jpg")).getImage());
	
		
		//JPanel gamePanel = new JPanel();
		//gamePanel.setLayout(new GridBagLayout());
		
		GridBagConstraints c = new GridBagConstraints();
		
		try {
			font = Font.createFont(Font.TRUETYPE_FONT, this.getClass().getResourceAsStream("/resources/vinque.TTF")).deriveFont(Font.PLAIN,12f);
		} catch (FontFormatException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
		
		GridLayout layout = new GridLayout(size,size);
		JPanel yourButtonPanel = new JPanel();
		yourButtonPanel.setLayout(layout);
		
		JPanel enemyButtonPanel = new JPanel();
		enemyButtonPanel.setLayout(layout);
		
		for(int i = 0; i < size; i++)
			for(int j = 0; j < size; j++)
			{
				yourBoardView[i][j] = new JButton();
				yourButtonPanel.add(yourBoardView[i][j]);
				
				enemyBoardView[i][j] = new JButton();
				enemyButtonPanel.add(enemyBoardView[i][j]);
			}
		
		
		frame.setLayout(new GridBagLayout());
		
		c.weightx = 11;
        c.weighty = 11;
        c.fill = GridBagConstraints.BOTH;
        c.gridx = 0;
        c.gridy = 0 ;
        c.anchor = GridBagConstraints.WEST;
		
		frame.add(yourButtonPanel, c);
		
		c.weightx = 11;
        c.weighty = 11;
        c.fill = GridBagConstraints.BOTH;
        c.gridx = 1;
        c.gridy = 0 ;
        c.anchor = GridBagConstraints.EAST;
		
		frame.add(enemyButtonPanel,c);
		
		infoLabel.setFont(font.deriveFont(16f));

		c.weightx = 2;
        c.weighty = 1;
        c.gridwidth = 2;
        c.fill = GridBagConstraints.BOTH;
        c.gridx = 0;
        c.gridy = 1;
        c.anchor = GridBagConstraints.WEST;
        
        infoLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        frame.add(infoLabel,c);	
		
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(1100,500);
		frame.setLocationRelativeTo(null);
		frame.setFocusable(true);
	}
	
	/**
	 * Metoda resetuję widok gry.
	 */
	public void boardReset()
	{
		UIManager.put("Button.disabledText", new ColorUIResource(Color.BLACK)); 
		
		for(int i = 1; i < size; i++)
			for(int j = 1; j < size; j++)
			{
				yourBoardView[i][j].setBackground(background);
				enemyBoardView[i][j].setBackground(background);
			}
		
		for(int i = 0, j = 1; j < size ; j++)
		{
			yourBoardView[i][j].setBackground(background_index);
			yourBoardView[i][j].setText("" + j);
			yourBoardView[i][j].setEnabled(false);
			yourBoardView[i][j].setFont(font);
		
			enemyBoardView[i][j].setBackground(background_index);
			enemyBoardView[i][j].setText("" + j);
			enemyBoardView[i][j].setEnabled(false);
			enemyBoardView[i][j].setFont(font);
		}
		
		for(int i = 1, j = 0; i < size ; i++)
		{
			char c = (char) ('A' + i-1);
			yourBoardView[i][j].setFont(font);
			yourBoardView[i][j].setBackground(background_index);
			yourBoardView[i][j].setText("" + c);
			yourBoardView[i][j].setEnabled(false);
		
			enemyBoardView[i][j].setFont(font);
			enemyBoardView[i][j].setBackground(background_index);
			enemyBoardView[i][j].setText("" + c);
			enemyBoardView[i][j].setEnabled(false);
			
		}
		
		yourBoardView[0][0].setBackground(background_index);
		yourBoardView[0][0].setEnabled(false);
		enemyBoardView[0][0].setBackground(background_index);
		enemyBoardView[0][0].setEnabled(false);
	}
	
	/**
	 * Metoda pozwala na ustawienie rządanego pola planszy gracza 
	 * odpowiednim kolorem, symbolizującym odpowiedni stan (trafienie, chybienie itp.)
	 * @param x
	 * 		Współrzędna x pola planszy.
	 * @param y
	 * 		Współrzędna y pola planszy.
	 * @param c
	 * 		Rządany kolor.
	 */
	public void setYourButton(int x, int y, Color c)
	{
		yourBoardView[x][y].setBackground(c);
	}
	
	/**
	 * Metoda pozwala na ustawienie rządanego pola planszy przeciwnika 
	 * odpowiednim kolorem, symbolizującym odpowiedni stan (trafienie, chybienie itp.).
	 * @param x
	 * 		Współrzędna x pola planszy.
	 * @param y
	 * 		Współrzędna y pola planszy.
	 * @param c
	 * 		Rządany kolor.
	 */
	public void setEnemyButton(int x, int y , Color c)
	{
		enemyBoardView[x][y].setBackground(c);
	}
	
	/**
	 * Metoda pozwala na ustawienie rządanych pól planszy gracza 
	 * odpowiednim kolorem, symbolizującym odpowiedni stan (można strzelać, statek itp.).
	 * @param x
	 * 		Współrzędna x pola planszy.
	 * @param y
	 * 		Współrzędna y pola planszy.
	 * @param length
	 * 		Rządana długość
	 * @param horizon
	 * 		Kierunek ustawiania pól.
	 * @param c
	 * 		Rządany kolor.
	 */
	public void initializeButtons(int x, int y, int length, boolean horizon, Color c)
	{
		if(horizon == true)
		{
			for(int j = 0; j < length; j++)
			{
				setYourButton(x, y+j, c);
			}
				
		}
		else
		{
			for(int i = 0; i < length; i++)
			{
				setYourButton(x+i, y, c);
			}
			
		}
	}
	
	/**
	 * Metoda ustawia pola na planszy przeciwnika w które gracz nie powinien już strzelać,
	 * gdyż na pewno nie ma tam statku.
	 * 
	 * Takowe powstają jeśli gracz zestrzeli statek przeciwnika,
	 * a statek ten przedstawiony jest w parametrach poniższej metody.
	 * 
	 * Pola te zostają ustawione na wartość {@link GameGui#miss}
	 * 
	 * @param x
	 * 		Współrzędna x pierwszej (od lewej lub górnej) części statku.
	 * @param y
	 * 		Współrzędna y pierwszej (od lewej lub górnej) części statku.
	 * @param length
	 * 		Długość statku.
	 * @param horizontal
	 * 		Orientacja przestrzenna statku.
	 */
	public void markUnshootable(int x, int y, int length, boolean horizontal)
	{
		if(horizontal)
		{
			if(y > 1)
				enemyBoardView[x][y-1].setBackground(miss);
			if(y+length < size )
				enemyBoardView[x][y+length].setBackground(miss);
			
			if(x>1)
			{
				//enemyBoardView[x-1][y].setBackground(miss);
				if(y > 1)
				{
					for(int i = -1; i < length; i++)
					enemyBoardView[x-1][y+i].setBackground(miss);
				}
				else
					for(int i = 0; i < length; i++)
						enemyBoardView[x-1][y+i].setBackground(miss);
					
				if(y+length < size)
					enemyBoardView[x-1][y+length].setBackground(miss);
			}
			
			if(x<size-1)
			{
				//enemyBoardView[x+1][y].setBackground(miss);
				if(y > 1)
				{
					for(int i = -1; i < length; i++)
					enemyBoardView[x+1][y+i].setBackground(miss);
				}
				else
					for(int i = 0; i < length; i++)
						enemyBoardView[x+1][y+i].setBackground(miss);
					
				if(y+length < size )
					enemyBoardView[x+1][y+length].setBackground(miss);
			}
		}
		else
		{
			if(x > 1)
				enemyBoardView[x-1][y].setBackground(miss);
			if(x+length < size )
				enemyBoardView[x+length][y].setBackground(miss);
			
			if(y>1)
			{
				enemyBoardView[x][y-1].setBackground(miss);
				if(x > 1)
				{
					for(int i = -1; i < length; i++)
					enemyBoardView[x+i][y-1].setBackground(miss);
				}
				else
					for(int i = 0; i < length; i++)
						enemyBoardView[x+i][y-1].setBackground(miss);
					
				if(x+length < size)
					enemyBoardView[x+length][y-1].setBackground(miss);
			}
			
			if(y<size-1)
			{
				enemyBoardView[x][y+1].setBackground(miss);
				if(x > 1)
				{
					for(int i = -1; i < length; i++)
					enemyBoardView[x+i][y+1].setBackground(miss);
				}
				else
					for(int i = 0; i < length; i++)
						enemyBoardView[x+i][y+1].setBackground(miss);
					
				if(x+length < size)
					enemyBoardView[x+length][y+1].setBackground(miss);
			}

		}
		
	}
	
	/**
	 * Metoda ustawia słuchaczy na przyciski plansz gracza i oponenta.
	 * Będą one zbierać input od gracza, np. gdzie chce strzelić, 
	 * gdzie postawić statek, a w ogólnosci w który element planszy klika,
	 * bądz najeżdza kursorem.
	 * 
	 * Metoda zaopatrza również przyciski w dodatkowe informacje (współrzędne przycisku oraz przynależność 
	 * do planszy gracza lub oponenta).
	 * 
	 * Używa metody metodę {@link GameGui#addBoardMouseListener(MouseListener)}
	 * 
	 * @param listenForButtonPress
	 * 		Obiekt pozwalający ustalić na jaki przycisk aktualnie 
	 * 		klilka gracz i wykonać opowiednie akcje.
	 * 	
	 * @param listenForMouse
	 * 		Obiekt pozwalający ustalić na jaki przycisk aktualnie
	 * 		przesuwa się kursor i wykonać odpowiednie akcje.
	 */
	
	public void addBoardButtonsListener(ActionListener listenForButtonPress, MouseListener listenForMouse)
	{
		for(int i = 1; i < size; i++)
			for(int j = 1; j < size; j++)
			{
				yourBoardView[i][j].putClientProperty("field", size*i+j);
				yourBoardView[i][j].putClientProperty("board", 0);
				yourBoardView[i][j].addActionListener(listenForButtonPress);	
				
				enemyBoardView[i][j].putClientProperty("field", size*i+j);
				enemyBoardView[i][j].putClientProperty("board", 1);
				enemyBoardView[i][j].addActionListener(listenForButtonPress);	
			}
		
		addBoardMouseListener(listenForMouse);
	}
	
	/**
	 * Metoda ustawia słuchaczy na przyciski planszy gracza, które będą nasłuchiwać 
	 * czy gracz najechał kursorem na dany przycisk.
	 * 
	 * @param listenForMouse
	 * 		Obiekt pozwalający ustalić na jaki przycisk aktualnie
	 * 		przesuwa się kursor i wykonać odpowiednie akcje.
	 */
	private void addBoardMouseListener(MouseListener listenForMouse)
	{
		for(int i = 1; i < size; i++)
			for(int j = 1; j < size; j++)
			{
				yourBoardView[i][j].addMouseListener(listenForMouse);
				yourBoardView[i][j].addMouseMotionListener((MouseMotionListener) listenForMouse);
			}
	}
	
	/**
	 * Ustawia słuchacza ramce głównej, który pozwoli na obrót statków w trakcie rozstawiania.
	 * 
	 * @param keyListener
	 * 		Obiekt będący słuchaczem klawiatury.s
	 */
	public void addRotateListener(KeyListener keyListener)
	{
		frame.addKeyListener(keyListener);
	}
	
	/**
	 * @return
	 * 		Zwraca ramkę gry.
	 */
	public JFrame getFrame()
	{
		return frame;
	}
	
	/**
	 * Metoda ustawia tekst etykiety wyświetlający informacje o aktualnej kolejce gry.
	 * @param s
	 * 		Tekst etykiety.
	 * 
	 * Używa {@link GameGui#updateInfo()} w celu aktualizacji etykiety.
	 */
	public void setTurnInfo(boolean s)
	{
		if(s)
			turnInfo = "Twoja tura!";
		else
			turnInfo = "Tura przeciwnika!";
		
		updateInfo();
	}
	
	/**
	 * Metoda ustawia tekst etykiety  przechowującą informacje dodatkowe.
	 * @param s
	 * 		Tekst etykiety.
	 * 
	 * Używa {@link GameGui#updateInfo()} w celu aktualizacji etykiety.
	 */
	public void setTurnInfo(String s)
	{
		turnInfo = s;
		updateInfo();
	}
	
	/**
	 * Metoda ustawia tekst etykiety  przechowującą o pozostałych częściach statków do trafienia.
	 * @param n
	 * 		Ilosc czesci statku pozostalych graczowi
	 * @param m
	 * 		Ilosc czesci statku pozostalych przeciwnikowi
	 * 
	 * Używa {@link GameGui#updateInfo()} w celu aktualizacji etykiety.
	 */
	public void setRemainingInfo(int n, int m)
	{
		remainingInfo = "Tobie pozostalo: " + n + " elementow, przeciwnikowi: " + m;
		
		updateInfo();
	}
	
	/**
	 * Metoda ustawia tekst etykiety  przechowującą informacje pozostałe.
	 * @param s
	 * 		Tekst etykiety.
	 * 
	 * Używa {@link GameGui#updateInfo()} w celu aktualizacji etykiety.
	 */
	public void setRemainingInfo(String s)
	{
		remainingInfo = s;
		updateInfo();
	}
	
	/**
	 * Aktualizuję etykietę przechowującą informacje o aktualnej grze.
	 */
	private void updateInfo()
	{
		infoLabel.setText( turnInfo);
		
		if(remainingInfo != "")
			infoLabel.setText(infoLabel.getText() + "      " + remainingInfo);
	}	
}
