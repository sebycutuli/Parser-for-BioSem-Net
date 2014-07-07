package net.biosem.parser.wikipedia;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.BufferedReader;
import java.io.PrintWriter;
import java.io.IOException;
import java.lang.invoke.ConstantCallSite;
import java.nio.charset.Charset;
import java.sql.SQLException;
import java.text.Normalizer;
import java.text.Normalizer.Form;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Logger;
import java.util.regex.Pattern;


public class PageLink {
	
	private static  boolean  cleanRelazioni = true;
	private static  boolean  relazioni = true;
			
    static public void main(String argv[]) throws InterruptedException, IOException {    
    	 
    	 String intestazione = 	"################################# Progetto Parser/Scanner XML Wikipedia #############################################################\n\n";
    	 System.out.println(intestazione);
    	 //PrintWriter outRel = null;
    	 
    	 //connessione al Database
    	 Database db = new Database("wikipages","","root","localhost");
    	 if(db.connect()){
    		 	System.out.println("Connessione al Database ESEGUITA.");    		 	
    	 }
    	 if(cleanRelazioni){
    		 db.cleanRelazioni();
    	 }

    	 long startTime = System.currentTimeMillis();
    	 
    	 if(relazioni){
			 int cont = 0;
			 System.out.println("Relazioni delle sole Biografie");
			 //outRel = new PrintWriter(new FileWriter("fileTest/outputRelazioniPageToPage.txt"));

			 try{
				 //outRel.println("Relazioni delle sole Biografie");
			 }
			 catch(Exception e){	    
				e.printStackTrace();
			 }
			 
				 //tiro fuori Nome+Cognome corrente				
				 List<Integer> listaPersonaggi = db.getIDPersonaggi();
				 for (int id : listaPersonaggi) {	
					System.out.println("\nIl Personaggio ");
					db.printPersonaggio(id);
					System.out.println("E' correlato con ");
					cont = db.checkPageToPage(id);						
					System.out.println("=====>"+cont+"\n\n");
					
					/*try{	
						outRel.flush();
						outRel.println(id + " � correlato con "+cont+" personaggi.");
						outRel.println("");
					 }
					 catch(Exception e){	    
						 e.printStackTrace();
					 }*/
			    }
    	 }
 	 
    	 
    	 long endTime = System.currentTimeMillis();
    	 System.out.println("Tempo Esecuzione: " + (endTime - startTime) + " millisecondi => secondi "+(endTime - startTime)/1000);
    	 System.out.println("FINE PROGRAMMA.");
    	 db.disconnect();    	 
    }

}