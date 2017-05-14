package model;

import java.util.Iterator;
import java.util.LinkedList;

import controller.GameController;

/**
 * Klasa implementująca Model wzorca MVC.
 * 
 * Klasa odpowiada za logikę gry, w szczególności przechowuję logiczną
 * reprezentację planszy gracza jak i oponenta.
 * 
 * Odpowiada również za sprawdzanie poprawności dodawania statków i
 * sprawdzenie wystąpienia końca gry.
 * 
 * @author Piotr Walas
 */
public class GameModel {
	
	/**
	 * Ogólna liczba statków (a dokładniej sumaryczna ilość ich części).
	 */
	private int overal;
	
	/**
	 * Lista {@link java.util.LinkedList} przechowująca statki gracza.
	 */
	private LinkedList<Ship> ships;

	/**
	 * Stanowi logiczną reprezentację planszy gracza.
	 */
	private FieldState [][] yourBoard;
	/**
	 * Stanowi logiczną reprezentację planszy oponenta.
	 */
	private FieldState [][] enemyBoard;
	
	/**
	 * Rozmiar planszy (plansza jest kwadratowa,
	 * a jedną linie i kolumnę zajmują indeksy).
	 */
	private static final int size = GameController.size;
	
	/**
	 * Ile pozostało częsci statków graczowi.
	 */
	private int yourLeft;
	
	/**
	 * Ile pozostało częsci statków oponentowi.
	 */
	private int enemyLeft;
	
	/**
	 * Tworzy nową instację GameModelu.
	 */
	public GameModel()
	{
		overal = GameController.overal;
		reset();	
	}

	/**
	 * Metoda dodaje statek na planszę gracza w wyznaczone przez niego miejsce.
	 * 
	 * Uprzednio następuję sprawdzenie czy w dane miejsce można dodać statek.
	 * 
	 * @param x
	 * 		Współrzędna x pierwszej (od lewej lub górnej) części statku.
	 * @param y
	 * 		Współrzędna y pierwszej (od lewej lub górnej) części statku.
	 * @param length
	 * 		Długość statku.
	 * @param horizontal
	 * 		Orientacja przestrzenna statku.
	 * @return
	 * 		Czy dodano statek na planszę.
	 */
	public boolean addShip(int x, int y, int length, boolean horizontal)
	{
		if(checkIfPossibleAddition(x, y, length, horizontal) == true)
		{
			if(horizontal == true)
			{
				for(int j = 0; j < length ; j++)
					yourBoard[x][y+j] = FieldState.SHIP;
				
			}
			else
			{
				for(int i = 0; i < length ; i++)
					yourBoard[x+i][y] = FieldState.SHIP;
			}
			ships.add(new Ship(x,y,length,horizontal));
			return true;
		}
		else
			return false;
	}
	
	/**
	 * Metoda sprawdza czy możliwe jest dodanie statku w rządane miejsce na planszy gracza.
	 * 
	* @param x
	 * 		Współrzędna x pierwszej (od lewej lub górnej) części statku.
	 * @param y
	 * 		Współrzędna y pierwszej (od lewej lub górnej) części statku.
	 * @param length
	 * 		Długość statku.
	 * @param horizontal
	 * 		Orientacja przestrzenna statku.
	 * @return
	 * 		Czy można dodać statek na planszę w danym miejscu.
	 */
	public boolean checkIfPossibleAddition(int x, int y, int length, boolean horizontal)
	{	
		
		int xc, yc, xa, ya;
			
			if(x!=1)
				xc = x-1;
			else
				xc = x;
			
			if(x!=size-1)
				xa = x + 1;
			else
				xa = x;
			
			if(y!=1)
				yc = y-1;
			else
				yc = y;
			
			if(y != size-1)
				ya = y + 1;
			else
				ya = y;
			
			
			try
			{
				if(horizontal == true)
				{
					if(y+length < size)
						ya = y + 1;
					else
						ya = y;
					
					for( int i = 0; i < 1 + (x-xc) + (xa-x) ; i++)
					{
						for(int j = 0; j < length + (y-yc) + (ya - y); j++)
						{
							if(xc+i == size || yc+j == size)	return false;
							if(yourBoard[xc+i][yc+j].equals(FieldState.SHIP) )
								return false;				
						}
					}
					
				}
					
				else
				{
					if(x+length < size)
						xa = x + 1;
					else
						xa = x;
					
					for( int i = 0; i < length + (x-xc)+(xa-x) ; i++)
					{
						for(int j = 0; j < 1 + (y-yc) + (ya-y); j++)
						{
							if(xc+i == size || yc+j == size)	return false;
							if(yourBoard[xc+i][yc+j].equals(FieldState.SHIP))
								return false;				
						}
					}
					
				}
					
			}
			catch (ArrayIndexOutOfBoundsException e)
			{
				e.printStackTrace();
				System.out.println("Table index wrong");
			}
			
			return true;
		}

	
	
	/**
	 * Metoda sprawdza czy statek mający jedną ze swoich części w podanym punkcie został zatopiony.
	 * 
	 * 
	 * @param x
	 * 		Współrzędna x pola planszy.
	 * @param y
	 * 		Współrzędna y pola planszy.
	 * @return
	 * 		Czy statek został zatopiony.
	 */
	public Ship isDestroyed(int x, int y)
	{		
		Iterator<Ship> iter = ships.iterator();
		Ship temp = null;
		while(iter.hasNext())
		{
			temp = iter.next();
			if(!temp.isHit(x, y))
				continue;
			else
			{
				temp.hit();
				if(!temp.isDestroyed())
					return null;
				else
				{
					System.out.println("ZNISZCZONY");
					ships.remove(temp);
					return temp;
				}
			}		
		}
		return null;
	}
	
	/**
	 * Metoda bada czy ustawia pola na planszy przeciwnika w które gracz nie powinien już strzelać,
	 * gdyż na pewno nie ma tam statku.
	 * 
	 * Takowe powstają jeśli gracz zestrzeli statek przeciwnika,
	 * a statek ten przedstawiony jest w parametrach poniższej metody.
	 * 
	 * Pola te zostają ustawione na wartość {@link FieldState#MISS}
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
				enemyBoard[x][y-1] = FieldState.MISS;
			if(y+length < size )
				enemyBoard[x][y+length] = FieldState.MISS;
			
			if(x>1)
			{
				//enemyBoard[x-1][y] = FieldState.MISS;
				if(y > 1)
				{
					for(int i = -1; i < length; i++)
					enemyBoard[x-1][y+i] = FieldState.MISS;
				}
				else
					for(int i = 0; i < length; i++)
						enemyBoard[x-1][y+i] = FieldState.MISS;
					
				if(y+length < size )
					enemyBoard[x-1][y+length] = FieldState.MISS;
			}
			
			if(x<size-1)
			{
				enemyBoard[x+1][y] = FieldState.MISS;
				if(y > 1)
				{
					for(int i = -1; i < length; i++)
					enemyBoard[x+1][y+i] = FieldState.MISS;
				}
				else
					for(int i = 0; i < length; i++)
						enemyBoard[x+1][y+i] = FieldState.MISS;
				
				if(y+length < size - 1)
					enemyBoard[x+1][y+length] = FieldState.MISS;
			}
		}
		
		
		else
		{
			if(x > 1)
				enemyBoard[x-1][y] = FieldState.MISS;
			if(x+length < size)
				enemyBoard[x+length][y] = FieldState.MISS;
			
			if(y>1)
			{
				enemyBoard[x][y-1] = FieldState.MISS;
				if(x > 1)
				{
					for(int i = -1; i < length; i++)
					enemyBoard[x+i][y-1] = FieldState.MISS;
				}
				else
					for(int i = 0; i < length; i++)
						enemyBoard[x+i][y-1] = FieldState.MISS;
					
				if(x+length < size)
					enemyBoard[x+length][y-1] = FieldState.MISS;
			}
			
			if(y<size-1)
			{
				enemyBoard[x][y+1] = FieldState.MISS;
				if(x > 1)
				{
					for(int i = -1; i < length; i++)
					enemyBoard[x+i][y+1] = FieldState.MISS;
				}
				else
					for(int i = 0; i < length; i++)
						enemyBoard[x+i][y+1] = FieldState.MISS;
				
				if(x+length < size)
					enemyBoard[x+length][y+1] = FieldState.MISS;
			}

		}
	}
	
	/**
	 * Metoda ustawia pole gracza
	 * 
	 * Jeśli wartość pola wynosi {@link FieldState#HIT} deinkrementuję pozostałą liczbę części statków gracza.
	 * 
	 * @param x
	 * 		Współrzędna x pola planszy.
	 * @param y
	 * 		Współrzędna y pola planszy.
	 * @param state
	 * 		Wartość na jakie ustawiamy to pole.
	 */
	public void setYourField(int x, int y, FieldState state)
	{
		yourBoard[x][y] = state;
		
		if(state.equals(FieldState.HIT))
		{
			yourLeft--;
		}
			
	}
		
	/**
	 * Metoda ustawia pole przeciwnika
	 * 
	 * Jeśli wartość pola wynosi {@link FieldState#HIT} deinkrementuję pozostałą liczbę części statków przeciwnika.
	 * 
	 * @param x
	 * 		Współrzędna x pola planszy.
	 * @param y
	 * 		Współrzędna y pola planszy.
	 * @param state
	 * 		Wartość na jakie ustawiamy to pole.
	 */
	public void setEnemyField(int x, int y, FieldState state)
	{
		enemyBoard[x][y] = state;
		if(state.equals(FieldState.HIT))
			enemyLeft--;
	}
	
	/**
	 * 
	 * @param x
	 * 		Współrzędna x pola planszy.
	 * @param y
	 * 		Współrzędna y pola planszy.
	 * 		
	 * @return
	 * 		Wartość pola planszy gracza.
	 */
	public FieldState getField(int x, int y)
	{
		return yourBoard[x][y];	
	}
	
	
	/**
	 * Metoda resetuję planszę gracza i przeciwnika.
	 * 
	 * Usuwa z nich wszyskie statki, informacje o polach trafionych, nietrafionych, itp.
	 * 
	 * Resetuję także liczbę części statków gracza i przeciwnika.
	 */
	public void reset()
	{
		yourLeft = enemyLeft = overal;
		yourBoard = new FieldState[size][size];
		enemyBoard = new FieldState[size][size];
		
		ships = new LinkedList<>();
		
		for(int i = 1 ; i < size; i++)
			for(int j = 1 ; j < size; j++)
			{
				yourBoard[i][j] = FieldState.NULL;
				enemyBoard[i][j] = FieldState.NULL;
			}
		
		int i = 0;
		for(int j = 0; j < size; j++)
		{
			yourBoard[i][j] = FieldState.INDEX;
			enemyBoard[i][j] = FieldState.INDEX;
		}
		
		int j = 0;
		for(i = 1; i < size; i++)
		{
			yourBoard[i][j] = FieldState.INDEX;
			enemyBoard[i][j] = FieldState.INDEX;
		}			
	}

	/**
	 * Metoda sprawdza czy nastąpił koniec gry, zwracając odpowiedni string
	 * 
	 * @return
	 * 		Odpowiedni string - "Wygrałeś", "Przegrałeś" lub null jeśli koniec jeszcze nie nastąpił.
	 */
	public String checkWin()
	{
			try {
				if(enemyLeft == yourLeft && enemyLeft == 0)
				throw new Exception("Blad!");
			} catch (Exception e) {
			
				e.printStackTrace();
			}
		
		if(enemyLeft == 0)
			return new String("Wygrales!");
		if(yourLeft == 0)
			return new String("Przegrales...");

			
		return null;
	}
	
	/**
	 * @return
	 * 		Ile części statków zostało oponentowi.
	 */
	public int getEnemyLeft()
	{
		return enemyLeft;
	}
	
	/**
	 * @return
	 * 		Ile części statków zostało graczowi.
	 */
	public int getYourLeft()
	{
		return yourLeft;
	}
}
