package game;

import java.io.File;

import java.io.IOException;
import java.net.URL;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Sequence;
import javax.sound.midi.Sequencer;

import sun.audio.*;

/**
 * Klasa odpowiedzialna za odtwarzanie muzyki w grze.
 * 
 * Odtwarza zapetlony utwor zapisany w formacie MIDI.
 * 
 * @author Piotr Walas
 *
 */
public class Music {
	
	/**
	 * Wczytuje sekwencji (utwor MIDI) do odtworzenia.
	 */
	Sequence sequence;
	
	/**
	 * Odpowiada za odtworzenie utworu.
	 */
	Sequencer sequencer;
	
	/**
	 * Metoda tworzy nowy obiekt klasy Music. 
	 * 
	 * Odtwarza utwor MIDI.
	 * 
	 */
	public Music()
	{
		try {
			sequence = MidiSystem.getSequence(getClass().getClassLoader().getResource("shipmusic.mid"));
			
			sequencer = MidiSystem.getSequencer();
			
			sequencer.open();
			sequencer.setSequence(sequence);
			
			sequencer.start();
		} catch (InvalidMidiDataException | IOException | MidiUnavailableException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}

}
