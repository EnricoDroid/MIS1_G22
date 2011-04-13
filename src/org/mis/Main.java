package org.mis;

import org.jfree.ui.RefineryUtilities;

import org.mis.gen.*;
import org.mis.sim.*;


/**
 * La classe main si occupa dell'elaborazione dei parametri passati al programma e,
 * a seconda di questi, procede al test dei generatori
 * oppure instanzia la classe simulatore con i parametri appropriati
 * @author 
 * @author 
 * @author 
 */

public class Main {

		private static int k;
		private static final int N =1000000;
		private static int ix;
		private static int range=100;
		private static double tx = 0.0333;
		private static Istogramma ist;
		private static int numOss;
		private static int n100;
		private static boolean stab = false;
		private static boolean logMode = false;
		private static boolean testMode = false;
		private static Grafico grafMe;
		private static Grafico grafVa;

		
		/**
		 * Funzione che si occupa di eseguire il test del generatore random
		 * e di stamparne i risultati
		 */
		
		private static void TestGenRandom() {
			Random gen = new Random(ix);
			double tot = 0;
			double tot2 = 0;	
			int x;
			int[] istogramma = new int[78+1];
			for (int i = 0; i< N; ++i) {
				x = gen.nextNumber2_78();
				istogramma[(int)x]++;
				tot += x;
				tot2 += Math.pow(x,2);
			}
			System.out.println("Test generatore random\nmedia:    "+tot/N+" (0.5)\nvarianza: "+(tot2/N-Math.pow((tot/N),2))+" (0.0)\n");
			ist = new Istogramma("Test generatore random", "Valore estratto", "Occorrenze", N/76.0, false, true);
			stampaIst(ist, istogramma);
		}
		
		
		/**
		 * Funzione che si occupa di eseguire il test del generatore erlangiano
		 * e di stamparne i risultati
		 */
		
		private static void TestGenErlang() {
			
			GeneratoreKerlangiano erl = new GeneratoreKerlangiano(ix, tx, k);
			double tot = 0;
			double tot2 = 0;
			double x;
			int[] istogramma=new int[range+1];
			
			for (int i = 0; i< N; ++i) {
				x = erl.nextErlang();
				istogramma[(int)(x*range)]++;
				tot += x;
				tot2 += Math.pow(x,2);
			}
			System.out.println("Test generatore " + k + "-erlangiano\nmedia:    "+tot/N+" ("+(tx)+")\nvarianza: "+(tot2/N-Math.pow((tot/N),2))+" ("+Math.pow(tx,2)/k+")\n");
			ist = new Istogramma("Test generatore " + k + "-erlangiano","Valore estratto", "Occorrenze", N/100.0, false, true);
			stampaIst(ist, istogramma);
		}

		/**
		 * Funzione che si occupa di eseguire il test del generatore iperesponenziale p=0.6
		 * e di stamparne i risultati
		 */
		
		private static void TestGenIperesp() {
			Random rand = new Random(Seme.getSeme());
			GeneratoreIperEsponenziale ipexp = new GeneratoreIperEsponenziale(tx, rand, 0.6);
			double tot = 0;
			double tot2 = 0;
			double x;
			int[] istogramma=new int[range+1];
			for (int i = 0; i< N; ++i) {
				x = ipexp.nextIperExp();
				istogramma[(int)(x*range)]++;
				tot += x;
				tot2 += Math.pow(x,2);
			}
			System.out.println("Test generatore iperesponenziale\nmedia:    "+tot/N+" ("+(tx)+")\nvarianza: "+(tot2/N-Math.pow((tot/N),2))+" ("+Math.pow(tx,2)*(1/(2*0.4*0.6)-1)+")\n");
			ist = new Istogramma("Test generatore iperesponenziale", "Valore estratto", "Occorrenze",N/100.0, false, true);
			stampaIst(ist, istogramma);
		}
		
		/*
		 * Classe main del nostro simulatore
		 * avvia la simulazione
		 * @param String[] args
		 */
		
		public static void main(String[] args) {
			long Tempo1;
			long Tempo2;
			long Tempo;
			
			Tempo1=System.currentTimeMillis();
			
			Seme.apri();
			
			ix=Seme.getSeme();
			
			if (elaboraOpzioni(args))
			{
				if (testMode)
				{
					System.out.println("Avviata modalita' test");
					TestGenRandom();
					k = 2;
					TestGenErlang();
					TestGenIperesp();
					k = 3;
					TestGenErlang();
				}
				else
				{
					int clien = 120; //se stabilizzazione la simulazione parte solo con 120 client altrimenti prova da 10 a 120
					if(stab){
						System.out.println("Avviata modalità stabilizzazione");
						grafMe = new Grafico("Stabilizzazione Gordon - Media", "Osservazioni", "Jobs/s", "Media");
						grafVa = new Grafico("Stabilizzazione Gordon - Varianza", "Osservazioni", "Jobs/s", "Varianza");
												
						for (int n=1; n<=numOss;n++){
							//System.out.println("********INIZIO BLOCCO p RUN********");
							
							double[] xn=new double[50]; //medie campionarie di ogni run
							double xjn=0; //variabile appoggio per stima media gordon
							double var=0; //variabile appoggio per stima varianza gordon
							Simulatore simulatore = new Simulatore(clien, stab, logMode, n);
							simulatore.simInit();
							
							for (int k=0;k<50;k++){
								
								simulatore.avvia();
								Osservazione ossN=simulatore.getOsservazioni();

								xn[k]=ossN.getMedia();
								simulatore.resetSim();
							}
							
							for (int j=0;j<50;j++){
								xjn+=xn[j];
							}
							
							double en = (xjn/50);
							
							for (int j=0;j<50;j++){
								var+=Math.pow((xn[j]-en),2);
							}
							
							double s2xn = (var/49);
							
							grafMe.addValue(n, en);
							grafVa.addValue(n,s2xn);
							//System.out.println("x(n)="+xjn+"\ne(n)="+en+"\ns^2(x(n))="+s2xn);
							//System.out.println("********FINE BLOCCO p RUN********");
							progress(n);
							Seme.chiudi();
							Seme.apri();
						}
						
					stampaGraf(grafMe);
					stampaGraf(grafVa);
					}
					else if (!stab){
						clien = 10;
					
						for(; clien<=120; clien += 10)
						{
							Simulatore simulatore = new Simulatore(clien, stab, logMode, 300, 5);
							simulatore.simInit();
							simulatore.avvia();
							
							if (clien == 20)
							{
								int[] istogramma = simulatore.getOsservazioni().getDistDisk();
								ist = new Istogramma("Distribuzione tempi di risposta Disk", "Raggruppamento in intervalli da 1/100 s dei tempi di risposta","Jobs", Math.round(simulatore.getOsservazioni().getMediaTr() * 100), true, false);
								stampaIst(ist, istogramma);	
							
								Osservazione oss = simulatore.getOsservazioni();
								
								System.out.println("Media:\t" + oss.getMedia());
								System.out.println("Varianza:\t" + oss.getVarianza());
								System.out.println("Intervallo di confidenza:\tda " + oss.getIntervConfid()[0] + " a " + oss.getIntervConfid()[1]);
								
								System.out.println("MediaTr:\t" + oss.getMediaTr());
								System.out.println("VarianzaTr:\t" + oss.getVarianzaTr());
								System.out.println("Intervallo di confidenzaTr:\tda " + oss.getIntervConfidTr()[0] + " a " + oss.getIntervConfidTr()[1]);
							}
							
							Seme.chiudi();
							Seme.apri();
						}
					}
				}
			}
			else
			{
				System.out.println("\nLe opzioni valide sono le seguenti: ");
				System.out.println("-l (per il log su file dettagliato)");
				System.out.println("-s numosservazioni (per la modalità di stabilizzazione)");
				System.out.println("-t (per la modalità di test dei generatori)");
				System.out.println("-h (per visualizzare l'aiuto)"); 
				System.out.println("ATTENZIONE: si sconsiglia di abilitare il log mode" +
						" (-l) per un lungo periodo di simulazione perche' occupa molto spazio" +
						" su disco");
			}
			
			Tempo2=System.currentTimeMillis();

			Tempo=Tempo2-Tempo1;
			System.out.println("Tempo impiegato dalla simulazione: " + (double)Tempo/1000 + " secondi.");
		}
		
		/**
		 * Funzione che si occupa di stampare nel terminale i valori per disegnare l'istogramma
		 * e in una finestra separata visualizza tale grafico
		 * @param Istogramma ist
		 */
		
		public static void stampaIst(Istogramma ist, int[] istogramma)
		{
			System.out.println("I valori per disegnare l'istogramma sono: ");
			System.out.print("[");
			for(int i = 0; i<istogramma.length;i++){
				System.out.print(istogramma[i]);
				ist.addvalues(istogramma[i], "Asse X", Integer.toString(i));
				if(i!=istogramma.length-1)
					System.out.print(" , ");
			}
			System.out.println("]");
			System.out.println();
			
			ist.pack();
			RefineryUtilities.centerFrameOnScreen(ist);
			ist.exportChartAsSVG(1000, 500);
			ist.setVisible(true);
		}
		
		/**
		 * metodo che apre e stampa a video il grafico del run di stabilizzazione
		 * @param Grafico ist
		 */
		
		public static void stampaGraf(Grafico graf)
		{
			
			graf.pack();
			RefineryUtilities.centerFrameOnScreen(graf);

			graf.exportChartAsSVG(1000, 500);
			graf.setVisible(true);			
		}
		
		/**
		 * metodo per la stampa della percentuale di lavoro completeato della stabilizzazione
		 * @param Percentuale
		 */
		public static void printProg(int percent){
		        StringBuilder bar = new StringBuilder("[");

		        for(int i = 0; i < 50; i++){
		            if( i < (percent/2)){
		                bar.append("=");
		            }else if( i == (percent/2)){
		                bar.append(">");
		            }else{
		                bar.append(" ");
		            }
		        }

		        bar.append("]   " + percent + "%     ");
		        System.out.print("\r"+bar.toString());
		 }
		
		public static void progress(int perc){
			n100=numOss/100;
			
			if (perc==(n100*5)){
					printProg(5);
				
			}
			else if (perc==(n100*15)){
				printProg(15);
				
			}
			else if (perc==(n100*25)){
				printProg(25);
				
			}
			else if (perc==(n100*35)){
				printProg(35);
				
			}
			else if (perc==(n100*50)){
				printProg(50);
			}
			else if (perc==(n100*60)){
				printProg(60);
			}
			else if (perc==(n100*75)){
				printProg(75);
			}
			else if (perc==(n100*85)){
				printProg(85);
			}
			else if (perc==(n100*99)){
				printProg(99);
			}
			else if (perc==numOss-1){
				printProg(100);
			}
		}
		
		/**
		 * Questa funzione controlla che i parametri passati in ingresso al programma siano corretti
		 * @param argomenti passati al programma
		 * @return ritorna true se il parsing degli argomenti ha avuto successo
		 */
		
		public static boolean elaboraOpzioni(String args[]) {
			boolean lInserted = false;
			boolean sInserted = false;
			boolean tInserted = false;
			for (int i = 0; i < args.length; i++) {
				if ((args[i].charAt(0) == '-') && (args[i].length() >= 2)) {
					if (args[i].charAt(1) == 'l') {
						if (!lInserted) {
							logMode = true;
							lInserted = true;
						} else {
							System.out.println("Parametro '" + args[i] + "' inserito 2 volte. ");
							return false;
						}
					} else if (args[i].charAt(1) == 's') {
						if (!sInserted) {
							StringBuilder num = new StringBuilder();
							for (int l=2;l<args[i].length();l++){
								num.append(args[i].charAt(l));
								}
							numOss=Integer.parseInt(num.toString());
							stab = true;			
							sInserted = true;
							if (numOss<1){
								System.out.println("Inserire un numero di osservazioni per la stabilizzazione maggiore di 1.\nEs. ./g22.jar -s1000'");
								return false;
							}
						} else {
							System.out.println("Parametro '" + args[i] + "' inserito 2 volte. ");
							return false;
						}
					} else if (args[i].charAt(1) == 't') {
						if (!tInserted) {
							testMode = true;
							tInserted = true;
						} else {
							System.out.println("Parametro '" + args[i] + "' inserito 2 volte. ");
							return false;
						}
					} else if (args[i].charAt(1) == 'h') {
						return false;
					} else {
						System.out.println("Parametro '" + args[i] + "' non valido. ");
						return false;
					}
				} else {
					System.out.println("Parametro '" + args[i] + "' non valido. ");
					return false;
				}
			}
			return true;
		}
}
