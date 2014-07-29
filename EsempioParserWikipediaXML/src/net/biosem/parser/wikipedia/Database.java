package net.biosem.parser.wikipedia;


import java.io.FileWriter;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
/**
 * Classe che rappresenta una connessione al database. 
 * Contiene i metodi per eseguire query di consultazione o di aggiornamento.
 * Contiene inoltre i metodi per connettersi/disconnettersi al database.
 * 
 */
public class Database {

	private String DBname;
	private String passwd;
	private String user;
	private String host;
	private boolean connected;
	private Connection conn;
	PrintWriter outRel = null;
	
    private static final Charset UTF8_CHARSET = Charset.forName("UTF-8");

	/**
	 * Costruttore di classe
	 * 
	 * @param DBname	il nome del database
	 * @param passwd	la password di accesso
	 * @param user		il nome utente
	 * @param host		l'indirizzo del server
	 */
	public Database(String DBname, String passwd, String user,String host)
	{
		this.DBname = DBname;
		this.passwd = passwd;
		this.user = user;
		this.host = host;
		connected = false;	
}

/**
 * Permette di connettersi al database selezionato
 * 
 * @return	true se la connessione ha successo
 */
public boolean connect() {
   connected = false;
   try {

      Class.forName("com.mysql.jdbc.Driver");

      if (!DBname.equals("")) {
         if (user.equals("")) {
            conn = DriverManager.getConnection("jdbc:mysql://localhost/" + DBname + "/?characterEncoding=utf8");
         } else {

            if (passwd.equals("")) {
               conn = DriverManager.getConnection("jdbc:mysql://localhost/" + DBname + "?characterEncoding=utf8&user=" + user);
            } else {
               conn = DriverManager.getConnection("jdbc:mysql://localhost/" + DBname + "?characterEncoding=utf8&user=" + user + "&password=" + passwd);
            }
         }
         connected = true;
 		 outRel = new PrintWriter(new FileWriter("fileTest/outputRelazioniSuFile1.txt"));
      } else {
         System.out.println("Manca il nome del database!!");
         System.out.println("Scrivere il nome del database da utilizzare all'interno del file \"config.xml\"");
         System.exit(0);
      }
   } catch (Exception e) {e.getMessage(); }
   return connected;
}

public int checkExistAttributo(String attributo){
    try {
    	Statement stmt = conn.createStatement();    
       	ResultSet rs = stmt.executeQuery("SELECT id FROM attributi WHERE attributo = \""+attributo+"\"");   
       
		 while (rs.next()) {
			 int id = Integer.parseInt(rs.getString("id"));
			 return id;
		 }
			 
    }catch (Exception e) { 
    	e.printStackTrace(); 
    	e.getMessage(); 
     }   
    
    return -1;
}

public int checkExistPersonaggio(String nome){
    try {
    	Statement stmt = conn.createStatement();    
       	ResultSet rs = stmt.executeQuery("SELECT id FROM personaggi WHERE nome = \""+nome+"\"");   
       
		 while (rs.next()) {
			 int id = Integer.parseInt(rs.getString("id"));
			 return id;
		 }
			 
    }catch (Exception e) { 
    	e.printStackTrace(); 
    	e.getMessage(); 
     }   
    
    return -1;
}

//vedere meglio
public int checkExistRelazione(int id_personaggio1, int id_personaggio2){
    try {
    	Statement stmt = conn.createStatement();    
       	ResultSet rs = stmt.executeQuery("SELECT tot FROM relazioni WHERE id_personaggio = \""+id_personaggio1+"\" and id_personaggio_correlato = \""+id_personaggio2+"\"" );   //da vedere meglio
       
		 while (rs.next()) {
			 int tot = Integer.parseInt(rs.getString("tot"));
			 return tot;
		 }
			 
    }catch (Exception e) { 
    	e.printStackTrace(); 
    	e.getMessage(); 
     }   
    
    return -1;
}

public int insertAttributo(String attributo){
	try {
      	int key = executeUpdateGetId("INSERT into attributi (attributo) VALUES(\""+attributo+"\")");  
      	return key;
	 } catch (Exception e) { 
    	e.printStackTrace(); 
    	e.getMessage(); 
     }   
	
	return -1;
}
/*public int insertPersonaggio(int id, String nome){
	try {
		int key = executeUpdateGetId("INSERT into personaggi (id, nome) VALUES("+id+", \""+nome+"\")");  
		return key;
	 } catch (Exception e) { 
    	e.printStackTrace(); 
    	e.getMessage(); 
     }   
	
	return -1;
}*/

public void insertPersonaggio(int id, String nome){
	try {
	    executeUpdate("INSERT into personaggi (id, nome) VALUES("+id+", \""+nome+"\")");  
	 } catch (Exception e) { 
    	e.printStackTrace(); 
    	e.getMessage(); 
     }   
}

public void insertUrlImage(int id_personaggio, String url){
	try {
		executeUpdateGetId("INSERT into dati (id_personaggio, id_attributo, dato, id_tipo) VALUES("+id_personaggio+", 11,  \""+url+"\", 3)");  
	 } catch (Exception e) { 
    	e.printStackTrace(); 
    	e.getMessage(); 
     }   
}

public void updateNomeCognomePersonaggio(int id_personaggio, String nome_cognome){
	try {
		executeUpdate("UPDATE personaggi SET nome=\""+nome_cognome+"\" WHERE id="+id_personaggio);  
	 } catch (Exception e) { 
    	e.printStackTrace(); 
    	e.getMessage(); 
     }   
	
}

public int insertDato(int id_personaggio, int id_attributo, int id_tipo, String dato){
	try {
		int key = executeUpdateGetId("INSERT into dati (id_personaggio, id_attributo, dato, id_tipo) VALUES("+id_personaggio+", "+id_attributo+" ,  \""+dato+"\", "+id_tipo+")");  
      	return key;
	 } catch (Exception e) { 
    	e.printStackTrace(); 
    	e.getMessage(); 
     }   
	
	return -1;
}



public void trunkDb(){
	executeUpdate("DELETE FROM dati where id>0");
	executeUpdate("DELETE FROM personaggi where id>0");
	
	/* Nota: non cancellare gli attributi.
	 * L'elenco � stato normalizzato. Nella tabella ci sono tutti quelli Utili.
	executeUpdate("DELETE FROM attributi where id>0");
	*/
}

public void cleanRelazioni(){
	executeUpdate("DELETE FROM relazioni where id>0");
}

/**
 * Esegue altre Query ad-hoc
 * 
 */

public String getNomeCognomeFromDati(int id_personaggio){
	
	 Statement stmt;
	 Statement stmt2;
	 ResultSet rs;
	 ResultSet rs2;
	 String query = null;
	 String query2 = null;
	 String result = new String("");	 
	 String nomePersonaggio = new String("");
	 
	try {
		 stmt = conn.createStatement();		 
		 query = "SELECT dato FROM dati WHERE id_personaggio="+id_personaggio+" and `ID_ATTRIBUTO`=1";
		 //String queryEnc = new String(query.getBytes("UTF-8"), "UTF-8");		 
		 rs = stmt.executeQuery(query);
		 while (rs.next()) {
			 nomePersonaggio=rs.getString("dato");			 
		 }
		 stmt2 = conn.createStatement();
		 query2 = "SELECT dato FROM dati WHERE id_personaggio="+id_personaggio+" and `ID_ATTRIBUTO`=2";
		 //String queryEnc2 = new String(query2.getBytes("UTF-8"), "UTF-8");		 
		 rs2 = stmt2.executeQuery(query2);
		 while (rs2.next()) {
			 nomePersonaggio=nomePersonaggio+" "+rs2.getString("dato");			 
		 }
		 //result = new String(nomePersonaggio.getBytes("UTF-8"), "UTF-8");	

	} catch (Exception e) {
		e.printStackTrace();
	}    
	return nomePersonaggio;
}


public List<String> getNomiCognomi(String escludiCorrente){
	
	 List<String> listaPersonaggiBio = new ArrayList<String>();
	
	 Statement stmt;
	 ResultSet rs;
	 String query = null;
	 
	try {
		stmt = conn.createStatement();

		 query = "SELECT NOME FROM personaggi WHERE nome NOT LIKE \""+escludiCorrente+"\"";
		 String queryEnc = new String(query.getBytes("UTF-8"), "UTF-8");
		 rs = stmt.executeQuery(queryEnc);
		 
		 while (rs.next()) {
    		  listaPersonaggiBio.add(new String(rs.getString("NOME")));
		 }

	} catch (Exception e) {
		e.printStackTrace();
	}    

	System.out.println("getNomiCognomi size "+listaPersonaggiBio.size());
	return listaPersonaggiBio;
}

public List<String> getNomiCognomi(){
	
	 List<String> listaPersonaggiBio = new ArrayList<String>();
	
	 Statement stmt;
	 ResultSet rs;
	 String query = null;
	 
	try {
		stmt = conn.createStatement();

		 query = "SELECT NOME FROM personaggi";
		 String queryEnc = new String(query.getBytes("UTF-8"), "UTF-8");

		 rs = stmt.executeQuery(queryEnc);
		 while (rs.next()) {
   		  listaPersonaggiBio.add(new String(rs.getString("NOME")));
		 }

	} catch (Exception e) {
		e.printStackTrace();
	}    

	System.out.println("getNomiCognomi size "+listaPersonaggiBio.size());
	return listaPersonaggiBio;
}

public List<Integer> getIDPersonaggi(){
	
	 List<Integer> listaPersonaggiID = new ArrayList<Integer>();
	
	 Statement stmt;
	 ResultSet rs;
	 String query = null;
	 
	try {
		stmt = conn.createStatement();

		 query = "SELECT ID FROM personaggi";
		 rs = stmt.executeQuery(query);
		 while (rs.next()) {
  		  listaPersonaggiID.add(Integer.parseInt(rs.getString("ID")));
		 }

	} catch (Exception e) {
		e.printStackTrace();
	}    

	System.out.println("getIDPersonaggi size "+listaPersonaggiID.size());
	return listaPersonaggiID;
}

public int checkInDescrizioni(String nomeCogn){
	 List<Integer> listaPersonaggiCorrelati = new ArrayList<Integer>();
	 listaPersonaggiCorrelati.add(checkExistPersonaggio(nomeCogn));
	 
	 Statement stmt;
	 ResultSet rs;
	 String query = null;
	 int conteggio = 0;
	 int id_pers = 0;
	 String personaggio = new String("");
	 
	try {
		stmt = conn.createStatement();
		if(personaggio.length()<4)
			personaggio=" "+nomeCogn+" ";
		  else personaggio=" "+nomeCogn;
		 int idPersonaggioDaEscludere=checkExistPersonaggio(personaggio);
		 query = "SELECT ID_PERSONAGGIO FROM datiperrelazioni WHERE dato LIKE \"%"+personaggio+"%\" and  id_personaggio!="+idPersonaggioDaEscludere+""; //escludere id_personaggio corrente
		 rs = stmt.executeQuery(query);
		 while (rs.next()) {
			 id_pers = rs.getInt("ID_PERSONAGGIO");
			 listaPersonaggiCorrelati.add(id_pers);
			 System.out.print("personaggio correlato => ");
			 printPersonaggio(id_pers);
			 conteggio++;
		 }

	} catch (SQLException e) {
		e.printStackTrace();
	}    
	
	//if (conteggio>0 & conteggio<50)
		//insertRelazioni (listaPersonaggiCorrelati);
	return conteggio;
}

public int checkPageToPage(int id){	
	 List<Integer> listaPersonaggiCorrelati = new ArrayList<Integer>();
	 
	 Statement stmt;
	 ResultSet rs;
	 String query = null;
	 int conteggio = 0;
	 int id_personaggio_correlato = 0;
	 String personaggio = new String("");
	 
	try {
		 stmt = conn.createStatement();	
		 query = "SELECT pl_title FROM minilinks WHERE pl_from="+id+"";
		 rs = stmt.executeQuery(query);
		 while (rs.next()) {
			 personaggio = rs.getString("pl_title").replace("_", " ").replace("\"", "");			 
			 if(checkExistPersonaggio(personaggio)!=-1){
				 id_personaggio_correlato = checkExistPersonaggio(personaggio);
				 listaPersonaggiCorrelati.add(id_personaggio_correlato);
				 outRel.flush();
				 outRel.println(id+","+id_personaggio_correlato+",3"+"\n");
				 outRel.println(id_personaggio_correlato+","+id+",3"+"\n");
				 //System.out.print("personaggio correlato => "+personaggio+"\n");
			 	 conteggio++;
			 }
		 }

	} catch (SQLException e) {
		e.printStackTrace();
	}    
	if (conteggio>0)
		insertRelazioniToFile (listaPersonaggiCorrelati);
	return conteggio;
}
/*public void insertRelazioni(List<Integer> listaPersonaggiCorrelati){
	
	int id_personaggio1;
	int id_personaggio2;
	try {		
		for(int i=0; i<=listaPersonaggiCorrelati.size()-1;i++){
			for(int j=0; j<=listaPersonaggiCorrelati.size()-1;j++){
				int tot_relazioni=0;
				id_personaggio1=listaPersonaggiCorrelati.get(i);
				id_personaggio2=listaPersonaggiCorrelati.get(j);
				if(id_personaggio1 != id_personaggio2){						
						//System.out.println(id_personaggio1+" e "+id_personaggio2);
						tot_relazioni=checkExistRelazione(id_personaggio1, id_personaggio2);
						if (tot_relazioni==-1){
							//System.out.println("nuova relazione tra "+id_personaggio1+" e "+id_personaggio2);
							executeUpdate("INSERT into relazioni (id_personaggio, id_personaggio_correlato, tot) VALUES("+id_personaggio1+", "+id_personaggio2+", '1')");  
						}
							else {
								//System.out.println("aggiorno la relazione tra "+id_personaggio1+" e "+id_personaggio2);
								tot_relazioni++;
								executeUpdate("UPDATE relazioni SET tot="+tot_relazioni+" WHERE id_personaggio = "+id_personaggio1+" and id_personaggio_correlato = "+id_personaggio2+"");  
							}
				}
			}
		}
		
	 } catch (Exception e) { 
    	e.printStackTrace(); 
    	e.getMessage(); 
     }   
}*/

public void insertRelazioniToFile(List<Integer> listaPersonaggiCorrelati){
	
	int id_personaggio1;
	int id_personaggio2;
	try {		
		for(int i=0; i<=listaPersonaggiCorrelati.size()-1;i++){
			for(int j=0; j<=listaPersonaggiCorrelati.size()-1;j++){
				id_personaggio1=listaPersonaggiCorrelati.get(i);
				id_personaggio2=listaPersonaggiCorrelati.get(j);
				if(id_personaggio1 != id_personaggio2){		
					try{	
					outRel.flush();
					outRel.println(id_personaggio1+","+id_personaggio2+",1"+"\n");  
					//outRel.println("");
				 }
				 catch(Exception e){	    
					 e.printStackTrace();
				 }
				}
			}
		}
		
	 } catch (Exception e) { 
    	e.printStackTrace(); 
    	e.getMessage(); 
     }   
}

public void selectPage(){
	
	 Statement stmt;
	 ResultSet rs;
	 String query = null;
	 
	try {
		stmt = conn.createStatement();

		 query = "SELECT * FROM pagelinks WHERE pl_from=2";
		 //String queryEnc = new String(query.getBytes("UTF-8"), "UTF-8");		 
		 rs = stmt.executeQuery(query);
		 while (rs.next()) {
			 System.out.println(rs.getString("pl_title"));
		 }

	} catch (Exception e) {
		e.printStackTrace();
	}    
}

public String printPersonaggio(int id_personaggio){
	
	 Statement stmt;
	 ResultSet rs;
	 String query = null;
	 String nomePersonaggio = new String("");
	 
	try {
		stmt = conn.createStatement();

		 query = "SELECT nome FROM PERSONAGGI WHERE id="+id_personaggio;
		 String queryEnc = new String(query.getBytes("UTF-8"), "UTF-8");
		 
		 rs = stmt.executeQuery(queryEnc);
		 while (rs.next()) {
			 //System.out.println(rs.getString("nome"));
			 nomePersonaggio = rs.getString("nome");
		 }

	} catch (Exception e) {
		e.printStackTrace();
	}    
		return nomePersonaggio;
}

/**
 * Esegue una query di aggiornamento del database
 */
public void executeUpdate(String query) {
   int num = 0;
   try {
	   
	  String queryEnc = new String(query.getBytes(), "UTF-8");
      Statement stmt = conn.createStatement();
      num = stmt.executeUpdate(queryEnc);
      stmt.close();
   } catch (Exception e) {
      e.printStackTrace();
      e.getMessage();
   }
}

public int executeUpdateGetId(String query) {
	PreparedStatement pstmt;  
	int key = 0;  
	try {  
		
		 String queryEnc = new String(query.getBytes(), "UTF-8");
		 
		 
	pstmt = conn.prepareStatement(queryEnc, Statement.RETURN_GENERATED_KEYS);  

	pstmt.executeUpdate();  
	ResultSet keys = pstmt.getGeneratedKeys();  
	  
	keys.next();  
	key = keys.getInt(1);  
	keys.close();  
	pstmt.close();  

	} catch (Exception e) { e.printStackTrace(); }  
	return key;  
}

/**
 * Esegue la disconnessione dal database
 */
public void disconnect() {
   try {
      conn.close();
      connected = false;
   } catch (Exception e) { e.printStackTrace(); }
}

/**
 * Ottiene l'oggetto Connection associato a questo database
 */
public Connection getConnection()
{	
	return conn;
}

/**
 * Indica se la connessione se � attiva
 */
public boolean isConnected()
{
return this.connected;	
}

}
