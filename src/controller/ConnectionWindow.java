package controller;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.UIManager;
import javax.swing.plaf.ColorUIResource;

import gui.GameGui;


/**
 * Klasa obługująca okno logowania.
 * 
 * Umożliwia wybranie ip oraz port naszego przeciwnika, badz własnego, jeśli okaże sie, 
 * że dany gracz jest serwerem.
 * 
 * W szczególności gracz może nie wpisywać żadnych danych i wykorzystać wartości standardowe
 * @see	GameController
 * 
 * @author Piotr Walas
 *
 */
public class ConnectionWindow {
	/**
	 */
	JFrame frame;
	/**
	 * IP do którego gracz chce się połączyć.
	 */
	String ip;
	
	/**
	 * Port do którego gracz chce się połączyć.
	 */
	String port;
	
	/**
	 * Logo gry.
	 */
	ImageIcon image;
	
	/**
	 * Używana czcionka.
	 */
	Font font;
	
	/**
	 * Pole, w które gracz będzie mógł wpisać IP.
	 */
	JTextArea ipText;
	
	/**
	 * Pole, w które gracz będzie mógł wpisać Port.
	 */
	JTextArea portText;
	
	/**
	 * Szerokość okna.
	 */
	static final int width = 500;
	
	/**
	 * Wysokość okna.
	 */
	static final int height = 500;
	
	/**
	 * Liczba elementów okna
	 */
	static final int size = 5;
	
	/**
	 * Metoda tworzy nowe okno logowania,
	 * wczytując logo gry i inicjalizując przyciski.
	 * 
	 * Metoda wykorzystuję {@link java.awt.GridBagLayout}
	 * 
	 * Dodawany jest słuchacz na przycisk kończący procedurę logowania.
	 */
	public ConnectionWindow()
	{
		UIManager.put("Button.disabledText", new ColorUIResource(Color.BLACK)); 
		try {
			font = Font.createFont(Font.TRUETYPE_FONT, this.getClass().getResourceAsStream("/resources/vinque.TTF")).deriveFont(Font.PLAIN,12f);
		} catch (FontFormatException | IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		frame = new JFrame();	
		
		frame.setIconImage(new ImageIcon(getClass().getResource("/resources/anchor.jpg")).getImage());
		
		image = new ImageIcon(this.getClass().getResource("/resources/main.jpg"));

		GridBagLayout layout = new GridBagLayout();
		
		GridBagConstraints c = new GridBagConstraints();
		
		JPanel yourPanel = new JPanel();
		yourPanel.setLayout(layout);
		
		
		 c.weightx = 1;
         c.weighty = 3;
         c.fill = GridBagConstraints.BOTH;
         c.gridx = 0;
         c.gridy = 0 ;
         c.anchor = GridBagConstraints.CENTER;
        
         JLabel label = new JLabel("",image,JLabel.CENTER);
         
        yourPanel.add(label,c);
		
		 c.weightx = 1;
         c.weighty = 1;
         c.fill = GridBagConstraints.BOTH;
         c.gridx = 0;
         c.gridy = 3 ;
         c.anchor = GridBagConstraints.CENTER;
		
		JButton ipButton = new JButton();
		ipButton.setText("IP");
		ipButton.setFont(font);
		ipButton.setEnabled(false);
		yourPanel.add(ipButton, c);
		
		 c.weightx = 1;
         c.weighty = 1;
         c.fill = GridBagConstraints.BOTH;
         c.gridx = 0;
         c.gridy = 4 ;
         c.anchor = GridBagConstraints.CENTER;
         
         ipText = new JTextArea();
         ipText.setFont(font);
         ipText.setEditable(true);
         
     	yourPanel.add(ipText, c);
         
     	 c.weightx = 1;
         c.weighty = 1;
         c.fill = GridBagConstraints.BOTH;
         c.gridx = 0;
         c.gridy = 5 ;
         c.anchor = GridBagConstraints.CENTER;
		
		JButton portButton = new JButton();
		portButton.setText("PORT");
		portButton.setFont(font);
		portButton.setEnabled(false);
		
		yourPanel.add(portButton, c);
		
		 c.weightx = 1;
         c.weighty = 1;
         c.fill = GridBagConstraints.BOTH;
         c.gridx = 0;
         c.gridy = 6 ;
         c.anchor = GridBagConstraints.CENTER;
         
         portText = new JTextArea();
         portText.setFont(font);
         portText.setEditable(true);
         
     	yourPanel.add(portText, c);
     	
     	 c.weightx = 1;
         c.weighty = 1;
         c.fill = GridBagConstraints.BOTH;
         c.gridx = 0;
         c.gridy = 7 ;
         c.anchor = GridBagConstraints.CENTER;
		
		JButton acceptButton = new JButton();
		acceptButton.setText("START");
		acceptButton.setFont(font);
		acceptButton.setEnabled(true);
		acceptButton.setBackground(GameGui.background);
		
		yourPanel.add(acceptButton, c);
		
		frame.add(yourPanel);
		
		frame.setMaximumSize(new Dimension(width,height));
		frame.setMinimumSize(new Dimension(width,height));
		
		frame.setTitle("Warship");
		//frame.pack();
		frame.setVisible(true);
		frame.setLocationRelativeTo(null);
		
		///////////////////////////////////////////////////
		
		acceptButton.addActionListener(new ActionListener()
		{

			@Override
			public void actionPerformed(ActionEvent e) 
			{
				ip = ipText.getText();
				port = portText.getText();
				
				frame.setVisible(false);
			}
			
		});
	}
	
	/**
	 * @return
	 * 		Czy okno jest widoczne.
	 */
	boolean isVisible()
	{
		return frame.isVisible();
	}
	
	/**
	 * @return
	 * 		String z IP wpisanym przez gracza.
	 */
	String getIP()
	{
		return ip;
	}
	
	/**
	 * @return
	 * 		String z Portem wpisanym przez gracza.
	 */
	String getPort()
	{
		return port;
	}
	
}
