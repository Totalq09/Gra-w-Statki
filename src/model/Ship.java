package model;

import java.awt.Point;

/**
 * 
 * @author Piotr Walas
 *
 * Klasa reprezentuje statek, jako obiekt w grze.
 * 
 * 
 */
public class Ship {
	/**
	 * Tablica przechowuje wspolrzedne wszystkich czesci statku na planszy.
	 */
	Point points[];
	/**
	 * Licznik ile nietrafionych czesci jeszcze pozostalo.
	 */
	public int shipPartLeft;
	/**
	 * Flaga mowiaca, czy statek jest w pozycji horyzontalnej czy nie.
	 */
	boolean horizontal;
	/**
	 * Dlugosc statku
	 */
	int length;
	
	/**
	 * Tworzy statek, ustawiajac wspolrzedne poszczegolnych czesci,
	 * dlugosc statku i jego orientacje.
	 * 
	 * @param x
	 * 		Wspolrzedna x pierwszej (od lewej lub gornej) czesci statku.
	 * @param y
	 * 		Wspolrzedna y pierwszej (od lewej lub gornej) czesci statku.
	 * @param length
	 * 		Dlugosc statku.
	 * @param horizontal
	 * 		Orientacja przestrzenna statku.
	 */
	public Ship( int x, int y,int length, boolean horizontal)
	{
		points = new Point[length];
		this.length = length;
		shipPartLeft = length;
		this.horizontal = horizontal;
		
		
		if(horizontal)
		{
			for(int i = 0; i < length; i++)
			{
				points[i] = new Point();
				points[i].x = x;
				points[i].y = y+i;
			}
		}
		else
		{
			for(int i = 0; i < length; i++)
			{
				points[i] = new Point();
				points[i].x = x+i;
				points[i].y = y;
			}
		}
	}
	
	/**
	 * Metoda sprawdza czy gracz strzelajacy w podane wsporzedne
	 * trafil statek.
	 * @param x
	 * 		Wpolrzedna x punkt w ktory strzela gracz.
	 * @param y
	 * 		Wpolrzedna y punkt w ktory strzela gracz.
	 * @return
	 * 		Czy statek zostal trafiony.
	 */
	public boolean isHit(int x, int y)
	{
		if(horizontal)
		{
			if(points[0].x != x) return false;
			for(int i = 0; i < points.length; i++)
			{
				if(points[i].y == y) return true;
				
			}
			return false;
		}
		else
		{
			if(points[0].y != y) return false;
			for(int i = 0; i < points.length; i++)
			{
				if(points[i].x == x) return true;
		
			}
			return false;
		}
	}
	
	/**
	 * Metoda zmniejsza liczosc nietrafionych czesci statku.
	 */
	public void hit()
	{
		shipPartLeft--;
	}
	
	/**
	 * 
	 * @return
	 * 		Czy statek zosta� zniszczony (pozostalo nietrafionych czesci).
	 */
	public boolean isDestroyed()
	{
		if(shipPartLeft > 0)
			return false;
		else
			return true;
	}
	
	/**
	 * @return
	 * 		Czy statek jest w pozycji horyzontalnej.
	 */
	public boolean isHorizontal()
	{
		return horizontal;
	}
	
	/**
	 * @return
	 * 		Dlugosc statku.
	 */
	public int getLength()
	{
		return length;
	}
	
	/**
	 * @return
	 * 		Punkt {@link java.awt.Point} poczatkowy statku (pierwszy od lewa lub od gory).
	 */
	public Point getBegin()
	{
		return points[0];
	}
}
