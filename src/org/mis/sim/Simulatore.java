package org.mis.sim;

import java.util.Collections;
import java.util.Vector;
import java.util.List;

import org.mis.processi.*;

/**
 * genero il tempo
aggiungo il tempo al simtime add
chiamo hold(getSimTime)
aggiungo alla lista di hold

 * La classe si occupa di istanziare tutte le componenti del nostro impianto e di avviare la simulazione
 * @author 
 * @author 
 * @author 
 */

public class Simulatore {

	private static int nClient = 10;
	private Cpu cpu;
	private Disk disk;
	private Host[] host;
	private Printer[] stampanti;
	private Terminale[] client;
	private Osservazione osservazione;
	private SimTime clock;
	private final int run = 50;
	private final int oss = 6000;
	private static Log log;
	
	public List<Processo> hold;
	public List<Processo> passivate;
	
	private static boolean stab = false;
	private boolean logging = false;
	private static String logAcc = "0123456789";
	
	/*
	 * logAcc decide l'accuratezza del file di log
	 * 1 cambio centro
	 * 2 job uscito da centro
	 * 3 preleva coda
	 * 4 accodamento
	 * 5 nuovo job
	 * 6 uscita job
	 * 7 numero cicli
	 * 8 cambio classe
	 * 9 centro libero
	 * 0 TMR
	 */
	
	
	public Simulatore(int nClient, boolean stab, boolean log, String logAcc)
	{
		Simulatore.nClient = nClient;
		Simulatore.stab = stab;
		this.logging = log;
		Simulatore.logAcc = logAcc;
	}
	
	/**
	 * Questa funzione avvia la simulazione sia per la stabilizzazione
	 * che per l'analisi dei risultati
	 */
	
	public void avvia()
	{
		passivate = new Vector<Processo>();
		hold = new Vector<Processo>();
		
		log = new Log(1, logging);

		creaCentri();
		clock = new SimTime();
		if(stab) osservazione = new Osservazione(run, oss);
		else osservazione = new Osservazione(run);

		creaJob();
		
		
		
		System.out.println("***Inizio Simulazione " + nClient +" client ***");

		
		
		System.out.println("***Fine Simulazione " + nClient +" client ***\n");
		if(logging) log.close();
	}
	
	/**
	 * Funzione che crea i vari centri dell'impianto
	 */
	
	public void creaCentri()
	{
		creaTerminali();
		creaHost();
		creaStampanti();
		cpu = new Cpu();
		this.passivate.add(cpu);
		disk = new Disk();
		this.passivate.add(disk);
	}
	
	/**
	 * Questa funzione crea i client
	 */
	
	public void creaTerminali()
	{
		client = new Terminale[nClient];
		for(int t=0; t<nClient; t++)
		{
			client[t] = new Terminale();
			this.passivate.add(client[t]);
		}
	}
	
	/**
	 * Questa funzione crea gli host
	 */
	
	public void creaHost()
	{
		host = new Host[nClient];
		for(int t=0; t<nClient; t++)
		{
			host[t] = new Host();
			this.passivate.add(host[t]);
		}
	}
	
	/**
	 * Questa funzione crea le stampanti
	 */
	
	public void creaStampanti()
	{
		stampanti = new Printer[nClient];
		for(int t=0; t<nClient; t++)
		{
			stampanti[t] = new Printer();
			this.passivate.add(stampanti[t]);
		}
	}
		
	/**
	 * Questa funzione crea un job per ogni client
	 */
	
	public void creaJob()
	{
		for(int t=0; t<nClient; t++)
		{
			clock.add(client[t].getTempoCentro());
			client[t].hold(clock.getSimTime());
			passivate.add(client[t]);
			//client[t].nextJob();
			Collections.sort(passivate);
			
			if(Simulatore.logAcc("5")) log.scrivi("Client "+t+" in hold");

		}
	}
	
	/**
	 * Questa funzione restituisce true se è attiva la modalita di stabilizzazione
	 * @return stab
	 */
	
	public static boolean stab()
	{
		return stab;
	}
	
	/**
	 * Questa funzione serve per filtrare il dettaglio del log
	 */
	
	public static boolean logAcc(String c)
	{
		if(logAcc.contains(c)) return true;
		else return false;
	}
	
	/**
	 * Questa funzione restituisce la CPU del sistema
	 * @return cpu
	 */
	
	public Cpu getCpu()
	{
		return cpu;
	}
	
	/**
	 * Questa funzione restituisce il Disk del sistema
	 * @return disk
	 */
	
	public Disk getDisk()
	{
		return disk;
	}
	
	/**
	 * Questa funzione restituisce il vettore con gli Host del sistema
	 * @return host
	 */
	
	public Host[] getHost()
	{
		return host;
	}
	
	/**
	 * Questa funzione restituisce il vettore con le stampanti del sistema
	 * @return stampanti
	 */
	
	public Printer[] getStampante()
	{
		return stampanti;
	}
	
	/**
	 * Questa funzione restituisce il report delle osservazioni della simulazione
	 * @return osservazione
	 */
	
	public Osservazione getReportOsservazione()
	{
		return osservazione;
	}
	
	/**
	 * Questa funzione restituisce il vettore con gli Host del sistema
	 * @return log
	 */
	
	public Log getLog()
	{
		return log;
	}
	
	/**
	 * Questa funzione restituisce il numero dei client della simulazione in esecuzione
	 * @return nClient
	 */
	
	public static int getNClient()
	{
		return nClient;
	}
}
