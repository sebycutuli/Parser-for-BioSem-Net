package net.biosem.parser.wikipedia;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.BufferedReader;
import java.io.PrintWriter;
import java.io.IOException;
import java.lang.invoke.ConstantCallSite;
import java.nio.charset.Charset;
import java.text.Normalizer;
import java.text.Normalizer.Form;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Logger;
import java.util.regex.Pattern;


public class Main {
	
	private final static Logger LOGGER = Logger.getLogger("Main"); 
	
	//private static final int  tipoPersDb= 1;
	private static final int  tipoBioDb= 1;
	private static final int  tipoDescrDb= 2;
	
	private static  boolean  storeDb = true;
	private static  boolean  storeFile = true;
	private static  boolean  trunkDb = false;
	private static  boolean  allFile = false;
	
	private static final int contLineaSoglia = 0; //da modificare per riprendere la scansione dopo un "GC Error"
	
    private static final Charset UTF8_CHARSET = Charset.forName("UTF-8");
	private static final boolean clean = true;
	
    static public void main(String argv[]) throws InterruptedException, IOException {    
            	
    	 List<String> listaLineeBio = new ArrayList<String>(); 
    	 String descrizione = new String("");
    	 String finalString = new String("");
    	 
    	 String intestazione = 	"################################# Progetto Parser/Scanner XML Wikipedia #############################################################\n\n";
    	 
    	 String[] arrayFile = new String[1];
       	 //arrayFile[0] = "fileTest/pages-articles/itwiki-20140508-pages-articles1.xml";
    	 //arrayFile[0] = "fileTest/pages-articles/itwiki-20140508-pages-articles2.xml"; 
    	 //arrayFile[0] = "fileTest/pages-articles/itwiki-20140508-pages-articles3.xml";
    	 //arrayFile[0] = "fileTest/pages-articles/itwiki-20140508-pages-articles4.xml";
    	 
    	 arrayFile[0] = "F:/File DUMP Wiki/dumps del 20140525/pages-articles/itwiki-20140525-pages-articles2.xml";
    	  	 
    	 //arrayFile[0] = "fileTest/pages-articles/itwiki-20140508-pages-articles4/itwiki-20140508-pages-articles4.001";
    	 //arrayFile[0] = "fileTest/pages-articles/itwiki-20140508-pages-articles4/itwiki-20140508-pages-articles4.xml.002";
    	 //arrayFile[0] = "fileTest/pages-articles/itwiki-20140508-pages-articles4/itwiki-20140508-pages-articles4.xml.003";
    	 //arrayFile[0] = "fileTest/pages-articles/itwiki-20140508-pages-articles4/itwiki-20140508-pages-articles4.xml.004";

    	 BufferedReader in = null;
    	 BufferedReader specialIn = null;
    	 //PrintWriter outBio = null;
    	 Scanner sc = new Scanner(System.in);

    	 System.out.println(intestazione);
    	 
    	 //connessione al Database
    	 Database db = new Database("wikipages","","root","localhost");
    	 if(db.connect()){
    		 	System.out.println("Connessione al Database ESEGUITA.");    		 	
    	 }
    	 if(trunkDb){
    		 db.trunkDb();
    	 }
    	 
    	 long startTime = System.currentTimeMillis();

    	 
    	 int idPersonaggio = -1;
    	 int idAttributo = -1;
		 int contBio = 0;
		 int contDescr = 0;
		 int contLinea = 0;
    	 String nome_cognome = new String("");
    	 String titolo_pagina = new String("");
    	 int id_pagina=0;

    	 if(storeFile){
			 //outPersonaggio = new PrintWriter(new FileWriter("fileTest/outputPersonaggi.txt"));
			 //outBio = new PrintWriter(new FileWriter("fileTest/outputBiografie.txt"));
		 
	    	 try{
	    		 
	    		 for(String itemFile : arrayFile){
	    			 
	    			 System.out.println("File XML in input: "+itemFile+", premi un tasto per continuare");
	    			 sc.nextLine();
	
		    		 in = new BufferedReader(new FileReader(itemFile));
	
		    		 String l;
		    		 boolean isTabellaBio = false;
		    		 
		    		 while ( (l = in.readLine()) != null ){
		    			 //System.out.println(l);
		    			 if(contLinea >= contLineaSoglia){
							 
		    				 //controllo titolo pagina
			    			 if(l.matches("(.*)<title>(.*)")){
			    				 titolo_pagina=l.replace("<title>", "").replace("</title>", "").trim();			    				
			    				 //System.out.println("riga titolo = "+titolo_pagina);
			    			 }
			    		 	
			    			 //controllo id pagina
			    			 if(l.matches("(.*)<id>(.*)")){
			    				id_pagina=Integer.parseInt(l.replace("<id>", "").replace("</id>", "").trim());			    				
			    				//System.out.println("id = "+id_pagina);
			    			 }
			    			 
			    			 //controllo se contiene PreData(perchè all'interno trovo '}}')
			    			 if(l.matches("(.*)PreData(.*)")){
			    				 l = in.readLine();
			    				 contLinea++;
			    			 }
			    			 
			    			//controllo se contiene revision
			    			 if(l.matches("(.*)<revision>(.*)")){
			    				 l = in.readLine();
			    				 l = in.readLine();
			    				 contLinea=contLinea+2;
			    			 }
		    				 
			    			 
				    		//controllo se contiene username
				    		 if(l.matches("(.*)username(.*)")){
				    			 l = in.readLine();
				    			 l = in.readLine();
			    				 contLinea=contLinea+2;
				    		 }
			    				 
			    			 if(isTabellaBio == false && l.matches("\\{\\{Bio.*")){
			    				 isTabellaBio = true;
			    				 listaLineeBio = new ArrayList<String>();
			    			 }			    		 			    			
			    			 
			    			 if(isTabellaBio == true){
			    				 if(l.matches("^\\|[ ]*.*")){
			    					 listaLineeBio.add(l);
			    				 }
			    			 }			    			 			    			
		    			 
			    			 //controllo tabella "Biografia"
			    			 if(isTabellaBio == true && l.matches(".*\\}\\}") && listaLineeBio.size() > 0){			    				 
			    				 
			     				//qualche controllo in più (opzionale)
			    				 
								 if (listaLineeBio.size() > 1 && titolo_pagina.indexOf(":")==-1 &&id_pagina!=0) {
									 
									 		if (getMatchingStrings(listaLineeBio,"\\|[ ]*Nome[ ]*=.*").size() > 0 && 
												getMatchingStrings(listaLineeBio,"\\|[ ]*Cognome[ ]*=.*").size() > 0) {
												 
									 			//System.out.println("\n#### Bio "+contBio+": #### \n");
												/* if(storeFile){
													 outBio.println("#### Bio "+contBio+": ####");
												 }*/
												 
												 if(storeDb){
													//memorizzo la Biografia e ne ricavo l'ID
													//System.out.println("id = "+id_pagina);
													//idPersonaggio = db.insertPersonaggio(id_pagina, titolo_pagina);
													idPersonaggio=id_pagina;
													db.insertPersonaggio(id_pagina, titolo_pagina);
												 }
												 
												 for (String string : listaLineeBio) {
													 
													 //memorizzo solo gli attributi valorizzati
													 finalString = customRow(string);
													 String[] finalProperty = finalString.split("=");
													 if(finalProperty.length > 1 && !finalProperty[1].trim().equals("")){
														 
														 idAttributo = db.checkExistAttributo(finalProperty[0].trim());
														/* if(idAttributo != -1 ){
															 System.out.println(finalString);
														 }*/
														 
														 if(storeDb){
															 //store su DB
															 if(idAttributo != -1 ){
																 db.insertDato(idPersonaggio, idAttributo, tipoBioDb, finalProperty[1].trim());
																 //controllo nome_cognome o titolo_nome																 
																 
																 //System.out.println("Prima Linea "+firstLineaBio+"\n"+"Seconda Linea "+secondLineaBio+"\n"+"Terza Linea "+thirdLineaBio+"\n");

																 /*if(finalProperty[0].trim().equals("Nome") || finalProperty[0].trim().equals("Cognome") && !titolo_nome.equals("")){
																	 if(finalProperty[1].trim().length() > 0){
																		 nome_cognome = nome_cognome + finalProperty[1].trim() + " ";
																	 }else{
																		 nome_cognome = ""; //non mi serve, tengo conto del Titolo + Nome
																	 }
																 }
																 if(finalProperty[0].trim().equals("Titolo") || finalProperty[0].trim().equals("Nome")){
																	 if (finalProperty[1].trim().indexOf("'")!=-1){
																		 titolo=true;
																		 titolo_nome = titolo_nome + finalProperty[1].trim().substring(1, finalProperty[1].trim().length()-1) + " ";
																	 }
																	 else{
																		 titolo_nome = titolo_nome + finalProperty[1].trim() + " ";
																		 titolo=true;
																	 }
																 }*/
															 }
															 // Elenco attributi già Normalizzato
															 //else{
															 // idAttributo = db.insertAttributo(finalProperty[0].trim());
															 // db.insertDato(idPersonaggio, idAttributo, tipoBioDb, finalProperty[1].trim());
															 //}
															 
														 }
														/* if(storeFile){
															 //store su File
															 if(idAttributo != -1 ){
																 outBio.println(finalString);
															 }
														 }*/
													 }
													 idAttributo = -1;
												 }
												 
												 //Aggiorna Nome+Cognome del Personaggio
												/* nome_cognome = db.getNomeCognomeFromDati(idPersonaggio);
												 if(db.checkExistPersonaggio(nome_cognome)==-1)
													 db.updateNomeCognomePersonaggio(idPersonaggio, nome_cognome.trim());
												 else db.updateNomeCognomePersonaggio(idPersonaggio, titolo_pagina.trim());*/
												 
												 contBio++;
												 
												 //controllo l'esistenza di una possibile Descrizione associata alla Biografia (deve essere sulle righe successive)
												 specialIn = new BufferedReader(in);
												 int contRowDescr = 0;
												 boolean descr = false;
												 while((l = in.readLine()) != null && contRowDescr<2){
													 //System.out.println(l);

													/* if(contRowDescr == 2){ //può capitare come prima o seconda linea successiva alla Biografia
														 break;
													 }*/
													 if(l.matches("[a-zA-Z[È]].*")){ //controllo se la descrizione matcha con qualsiasi carattere
													 //if(l.matches("[a-zA-Z](È).*")){ //controllo se la descrizione matcha con qualsiasi carattere

													 	 descrizione = l;
														 finalString = customRow(descrizione);
														 
														 //System.out.println("#### Descr di Bio "+(contBio-1)+": ####");
														 //System.out.println(finalString);
														 
														 /*if(storeFile){
															 //store su File
															 outBio.println("#### Descr di Bio "+(contBio-1)+": ####");
															 outBio.println(finalString+"\n\n");
														 }*/					    					
								    					 contDescr++;
								    					 descr = true;
								    					 //break;
													 }
													contRowDescr++;
												 }
												 if(storeDb && descr){
													 //store su DB
													 idAttributo = db.checkExistAttributo("altra descrizione");
													 if(idAttributo != -1 ){
														 db.insertDato(idPersonaggio, idAttributo, tipoDescrDb, finalString);
													 }
												 }									
								 }
								 }
								 isTabellaBio = false;
								 listaLineeBio = null;
								 idPersonaggio = -1;
								 idAttributo = -1;
								 nome_cognome="";
								 id_pagina=0;
								 //titolo_pagina="";
			    			 }
		    			 }
			    			 contLinea++;
		    		 }//fine while principale
		    		 
		    		 if(allFile == false){
		    			 break; //quindi solo il primo file
		    		 }
	    		 }// fine for
	    		 
	    		 //System.out.println("\n\nTOT dei Possibili Tabelle: "+contPersonaggio);
	    		 System.out.println("TOT dei Possibili Biografie: "+contBio);
	    		 System.out.println("TOT dei Possibili Descrizioni: "+contDescr);
	    		 System.out.println("contLinea="+contLinea);
				
	    	}finally{
	    		 if(in != null)
	    			 in.close();
	    		 /*if(outBio != null){
	    			 outPersonaggio.close();
	    		 	 outBio.close();
	    		 }*/
	    		 if(sc != null)
	    			 sc.close();
	    	}
    	 
    	 }
    	 
    	 long endTime = System.currentTimeMillis();
    	 System.out.println("Tempo Esecuzione: " + (endTime - startTime) + " millisecondi => secondi "+(endTime - startTime)/1000+" minuti=> "+((endTime - startTime)/1000)/60);
    	 System.out.println("FINE PROGRAMMA.");
    	 db.disconnect();    	 
    }
    
    private static List<String> getMatchingStrings(List<String> list, String regex) {

    	  ArrayList<String> matches = new ArrayList<String>();
    	  Pattern p = Pattern.compile(regex);

    	  for (String s:list) {
    	    if (p.matcher(s).matches()) {
    	      matches.add(s);
    	    }
    	  }

    	  return matches;
    }
    
    private static String customRow(String row) {

  	  	String finalString = new String("");
  	  	
  	  	//NORMALIZZO la riga
  	  	
	  	  	//sostituisco gli apici ''' e poi '' con "
		  	finalString = row.replaceAll("\'\'\'","\'").replaceAll("\'\'", "\'");
		  	
		  	//scelgo la PRIMA OPZIONE tra gli OR all'interno delle parentesi quadre
			 if(finalString.matches(".*\\[\\[.*\\]\\].*")){
				 finalString = finalString.replaceAll("(\\[\\[)([^\\[\\]]*)(\\|)([^\\[\\]]*)(\\]\\])", "$4");
				 //finalString = finalString.replaceAll("(\\[\\[)([^\\[\\]]*)(\\]\\])", "$3");
			 }
			 
			 //elimino |
			 finalString = finalString.replaceAll("\\|","");
			 
			 //elimino {{....}}
		     finalString = finalString.replaceAll("\\{\\{.*\\}\\}", "");
		     
		     //elimino &....;
		     finalString = finalString.replaceAll("\\&.*;", "");
		     
		     //elimino {{....;
		     finalString = finalString.replaceAll("\\{\\{", "");
		     
		     //elimino }}....;
		     finalString = finalString.replaceAll("\\}\\}", "");
		     
		     //elimino [....;
		     finalString = finalString.replaceAll("\\[", "");
		     
		     //elimino ]....;
		     finalString = finalString.replaceAll("\\]", "");

  	  return finalString;
  }
    
    @SuppressWarnings("unused")
	private static String cleaner(String row) {

  	  	String finalString = new String("");
  	  	
		     if(clean){
			     //converto caratteri Speciali Conosciuti
			     finalString = finalString.replaceAll("Ãš", "à");
			     
				 //convesione caratteri Speciali http://utf8-chartable.de/unicode-utf8-table.pl?utf8=char
				 finalString = decodeUTF8(finalString.getBytes());
		     }

  	  return finalString;
  }
    
    
    //procedure per conversioni
    @SuppressWarnings("unused")
	private static String removeDiacriticalMarks(String string) { //NON USARE
        return Normalizer.normalize(string, Form.NFD)
            .replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
    }
    
    @SuppressWarnings("unused")
	private static String removeASCII(String string) { //NON USARE
        return Normalizer.normalize(string, Form.NFD)
            .replaceAll("[^\\p{ASCII}]", "");
    }
    
    @SuppressWarnings("unused")
	private static String removeLatin(String string) { //NON USARE
        return Normalizer.normalize(string, Form.NFD)
            .replaceAll("[^\\p{InBasic_Latin}]", "");
    }
    
    static String decodeUTF8(byte[] bytes) {  //NON USARE
        return new String(bytes, UTF8_CHARSET);
    }
    static byte[] encodeUTF8(String string) {
        return string.getBytes(UTF8_CHARSET);
    }
    
}