package org.mis.processi;

public class Job extends Processo {
	
	private int id;
	private static int identificatore=0;
	private int jobClass;
	private Terminale generatoDa;
	private double tempoIngrSist; //istante in cui il job è entrato nel sistema
	private boolean trovato;
	
	public Job(Terminale t){
		super("job " + identificatore++);
		id=identificatore;
		jobClass = 1;
		generatoDa = t;
		trovato = false;
	}
	
	
	/**
	 * Questa funzione restituisce l'id del job
	 * @return id
	 */
	
	public int getId(){
		return id;
	}
	
	/**
	 * Funzione che serve per impostare la classe del Job
	 * @param int jc
	 */
	
	public void setJobClass(int jc) {
		jobClass = jc;
	}
	
	/**
	 * Funzione che serve per prelevare la classe del Job
	 * @return
	 */
	
	public int getJobClass() {
		return jobClass;
	}
	
	/**
	 * Questa funzione restituisce il terminale che l'ha generato
	 * @return Terminale generatoDa
	 */
	
	public Terminale getGeneratoDa()
	{
		return generatoDa;
	}
	
	/**
	 * Questa funzione memorizza il tempo di ingresso del Job nel sistema
	 * @param tempo
	 */
	
	public void setIngresso(double tempo)
	{
		this.tempoIngrSist = tempo;
	}
	
	/**
	 * Questa funzione preleva il tempo di ingresso del Job nel sistema
	 * @return tempoIngrSist
	 */
	
	public double getIngresso()
	{
		return this.tempoIngrSist;
	}

	/**
	 * Questa funzione memorizza se il Job ha trovato i dati cercati in Disk
	 * @param trov
	 */
	
	public void setTrovato(boolean trov)
	{
		trovato = trov;
	}
	
	/**
	 * Questa funzione restituisce true se il Job ha trovato i dati cercati in Disk
	 * @return
	 */
	
	public boolean getTrovato()
	{
		return trovato;
	}


}