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
		
		try {  
			  
            URL url = new URL("https://ajax.googleapis.com/ajax/services/search/images?" +  
                        "v=1.0&q=laura%20pausini&key=ABQIAAAAMDidA1PAO0alsihAElsy3xTLCrE5uk8Ud_JrDKiWLKYeT0PD8xQ9hbFvmXJ2enaXdFRHJflbRAe36A&userip=192.168.1.51");  
  
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
        	    String urlImage = (String) result0.get("url");
        	    //System.out.println(urlImage);        	    
        	    URL url2 = new URL(urlImage);
        	    Image image = ImageIO.read(url2);
        	    System.out.println(result0.toString());
        	    /*JFrame frame = new JFrame();
        	    JLabel label = new JLabel(new ImageIcon(image));
        	    frame.getContentPane().add(label, BorderLayout.CENTER);
        	    frame.pack();
        	    frame.setVisible(true);*/
        	    //URLConnection urlC = url.openConnection();  
        	    //urlC.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 5.1; rv:19.0) Gecko/20100101 Firefox/19.0"); 
        	} else {
        	    System.out.println("Image Not Found");
        	}
        	
    		

		}
		
		catch(Exception e){
			System.out.println(e);
		}
	}
}
