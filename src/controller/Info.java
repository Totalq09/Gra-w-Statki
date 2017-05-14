package controller;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.GraphicsEnvironment;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JDialog;

import gui.GameGui;


/**
 * Klasa obługująca okno wyświetlające dodatkowe informację, w szczególnosci o wygranej/przegranej.
 * 
 * @author Piotr Walas
 *
 */
public class Info {

	/**
	 * Referencja do Widoku aplikacji.
	 */
	GameGui gameGui;
	
	/**
	 * Wyświetlana informacja
	 */
	String message;
	
	/**
	 * Obiekt okna dialogowego.
	 */
	public JDialog d;
	
	/**
	 * Czcionka używana w oknie.
	 */
	Font font;
	
	/**
	 * Metoda incjalizująca okno informacyjne.
	 * 
	 * @param gui
	 * 		Referencja do Widoku aplikacji.
	 * @param info
	 * 		Wyświetlana informacja
	 * @throws InterruptedException
	 * 		Generowany jest wyjątek, jesli podczas polaczenia nastapia bledy
	 */
	public Info(GameGui gui,String info) throws InterruptedException
	{
		gameGui = gui;
		message = info;
		d = new JDialog(gameGui.getFrame(),message,false);
		
		try {
			font = Font.createFont(Font.TRUETYPE_FONT, this.getClass().getResourceAsStream("/resources/vinque.TTF")).deriveFont(Font.PLAIN,12f);
		} catch (FontFormatException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		JButton ok = new JButton(info);
		ok.setBackground(GameGui.background);
		ok.setFont(font.deriveFont(28f));
		ok.addActionListener(new ActionListener()
				{
					@Override
					public void actionPerformed(ActionEvent e) {
						
						d.setVisible(false);
					}
					
			
				});
		
		d.add(ok);
		d.setMinimumSize(new Dimension(400,200));
		d.setMaximumSize(new Dimension(400,200));
		
		d.pack();
		
		Point p = new Point(gameGui.getFrame().getLocation());
		p.x += gameGui.getFrame().getWidth()/2-d.getWidth()/2;
		p.y+=gameGui.getFrame().getHeight()/2-d.getHeight()/2;
		
		d.setLocation(p);
		d.setVisible(true);
	}
}
