package model;

/**
 * 
 * 
 * Typ wyliczeniowy przechowujacy informacje o mozliwych 
 * wartosciach poszczegolnych na planszy
 * @see GameModel
 */

public enum FieldState {
	/**
	 * Brak wypelnienia
	 */
	NULL, 
	/**
	 * W tym polu znajduje sie czesc statku
	 */
	SHIP, 
	/**
	 * W tym polu znajduje sie czesc statku, ktora zostala trafiona
	 */
	HIT, 
	/**
	 * W to pole strzelil przeciwnik, ale nie ma tutaj statku
	 */
	MISS,
	/**
	 * W tym polu wystupuje indeksowanie planszy
	 */
	INDEX
}
