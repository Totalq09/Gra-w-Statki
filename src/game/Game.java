package game;

import controller.*;
import model.*;
import gui.*;

/**
 * Nadrzędna klasa zawierająca wszystkie elementy niezbędne do działania aplikacji 
 * zaimplementowanej zgodnie ze wzorcem MVC.
 * 
 * Zawiera również klase odpowiedzialną za implementację muzyki.
 * 
 * @author Piotr Walas
 *
 */
public class Game {

	public static void main(String[] args) {
		
		/**
		 * Obiekt odpowiedzialny za muzykę w grze.
		 * @see Music
		 */
		Music music;
		if(args.length != 0 && args[0].equals("-music"))
		music = new Music();
		
		/**
		 * Model aplikacji
		 * @see model.GameModel
		 */
		GameModel gameModel = new GameModel();
		
		/**
		 * Widok aplikacji
		 * @see gui.GameGui
		 */
		GameGui gameGUI = new GameGui();
		
		/**
		 * Konroler aplikacji
		 * @see controller.GameController
		 */
		GameController gameController = new GameController(gameModel, gameGUI);
			
		gameGUI.getFrame().setVisible(true);		
	}

}

