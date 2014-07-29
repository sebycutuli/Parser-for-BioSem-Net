package net.biosem.parser.wikipedia;

import java.awt.BorderLayout;
import java.awt.Image;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class searchImage {

	public static void main(String[] args) {
		
	 Database db = new Database("test","","root","localhost");
   	 if(db.connect()){
   		 	System.out.println("Connessione al Database ESEGUITA.");    		 	
   	 }
		
		try {  			  
            
			 List<Integer> listaPersonaggi = db.getIDPersonaggi();
			 for (int id : listaPersonaggi) {
				String nome = db.printPersonaggio(id);
				System.out.println("\nIl Personaggio "+nome+" "+id);
				String urlImage = getUrlImage(nome);
				System.out.println(" ha URL immagine: "+urlImage);
				db.insertUrlImage(id, urlImage);
				//System.out.println("=====>"+cont+"\n\n");
			 }
		}		
		catch(Exception e){
			System.out.println(e);
		}
		db.disconnect(); 
	}
	
public static String getUrlImage (String nomePersonaggio){
		
        String urlImage = new String ("");

		try {  
			         
            URL url = new URL("https://ajax.googleapis.com/ajax/services/search/images?" +  
                    "v=1.0&q="+nomePersonaggio.replace(" ", "%20")+"&key=AIzaSyB1enrurNo01M3LI6gxlyLiNLByv8gt8JM");
  
            URLConnection connection = url.openConnection();  
            connection.addRequestProperty("Referer","http://www.defekas.com");  
  
            String line; 
            StringBuilder builder = new StringBuilder();  
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));  
              
            while((line = reader.readLine()) != null) {  
                builder.append(line);  
            }                
            JSONObject json = new JSONObject(builder.toString());
            JSONObject responseData = json.getJSONObject("responseData");
        	JSONArray jsonArray = responseData.getJSONArray("results");
        	if ((jsonArray != null) && (!jsonArray.isNull(0))) {
        	    JSONObject result0 = (JSONObject) jsonArray.get(0);
        	    urlImage = (String) result0.get("url");
        	    //System.out.println(urlImage);
        	} else {
        	    System.out.println("Image Not Found");
        	}
		}
		catch(Exception e){
			System.out.println(e);
			System.exit(0);
		}
		
			return urlImage;
	}
}
