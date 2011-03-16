package org.mis.processi;

import org.mis.gen.Random;
import org.mis.gen.Seme;


/**
 * La classe Printer e' una classe derivata dalla classe astratta centro. La classe rappresenta 
 * un centro senza coda poichè di tipo IS, cioè ha infiniti posti in esecuzione.
 * @author 
 * @author 
 * @author 
 */

public class Printer extends Processo{

	private Random genUn2_78;
	private Job current;

	/**
	 * E' il costruttore della classe il quale istanzia una stampante, il booleano occupato
	 * è ovviamente impostato a false essendo il centro di tipo IS.
	 * La legge di distribuzione nel tempo è la 12-erlangiana.
	 */
	
	public Printer(){
		super("Stampante");
		genUn2_78 = new Random(Seme.getSeme());
	}

	/**
	 * Funzione la quale ritorna un tempo con distribuzione 12-erlagiana. E' stato effettuato 
	 * l'override del metodo della superclasse centro. 
	 */
	

	public double getTempoCentro() {
		return genUn2_78.nextNumber2_78();
	}
	
	public void setCurJob(Job c){
		current=c;
	}


	public Job getJobCorrente(){
		
		return current;
	}


}
