import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.IOUtils;
import org.jdom2.Document;
import org.jdom2.Element;


public class functions {
	//Verschiedenene Spielfelder
	int[][] myfeld=new int[10][10];
	int[][] otherfeld=new int[10][10];
	
	// TODO username wird bei erfoglter Anmeldung gesetzt (gemacht);
	String username;
	int spieleid;
	public String request(String function){
		try {
			String url1="http://localhost/Projekt/battleship.php?SpielID="+spieleid+"&funktion="+function;
			URL url = new URL(url1);
			URLConnection con = url.openConnection();
			String encoding = con.getContentEncoding();
			InputStream in = con.getInputStream();
			encoding = encoding == null ? "UTF-8" : encoding;
			String body = IOUtils.toString(in, encoding);
			System.out.println(body);
			return body;
			//TODO ckeck return
		} catch (IOException e) {
			e.printStackTrace();
			return "Error";
		}
	}	
	
	public void xmlparse(Document doc){
		//Wurzelement holen
		Element element = doc.getRootElement(); 
		String wurzel=""+element.getName();
		//Kindelemente auslesen (eintrag ist der name des Elements):
		List alleKinder = element.getChildren(); 
		int länge=alleKinder.size();
		
		//Ausgeben
		for(int zahl=1;zahl<länge;zahl++){
			if((((Element) alleKinder.get(zahl)).getName()).equals("eintrag")){
				System.out.println(((Element) alleKinder.get(zahl)).getValue()); 
			}
		}
	}
	
	public boolean login(String pw,String user, boolean newuser){
		//TODO Rückgabewerte konrtolliern
		if(newuser){
			String help= request("login&password="+pw+"&user="+user);
			if(help.equals("true")){
				username=user;
				return true;
			}else{
				return false;
			}
		}else{
			String help=request("Registrieren&password="+pw+"&user="+user);
			return true;
		}
	}
	
	public void creategame(String spielname ){
		if(username!=null){
			String ok= request("setSpiel&id=username&spielname="+spielname);
		}else{
			//TODO fehlermeldung
			System.out.println("Error: No user logged in");
		}
	}
	
	public void getGames(){
		String a=request("getSpiele");
		//TODO umwandeln in einen anderen Datentyp und vl auslesen von XML???
		//Liste mit allen Spielen erstellen für die Teilnahme??
	}
	
	public void joinGame(int id,String spielername){
		String help=request("joinSpiel&spielID="+id+"&SpielerID="+spielername);
		//TODO return korntollieren und schauen ob man dem Spiel beigetreten ist
	}
	
	public void startgame(){
		//TODO eigenen schiffpositionen auslesen und an den WS senden (setSchiffPosition())
		//TODO die gegnerischen schiffpositionen von WS auslesen und abspeichern (getSchiffPosition())
	}
	
	public void polling(int x,int y){
		boolean warte=true;
		String a= request("setSpielZug&x="+x+"&y="+y);
		//TODO schauen ob die Daten angekommen sind
		while(warte){
			 try {
				 
				// wie sleep, in der klammer kommt die Zeit, die der Client warten muss
				TimeUnit.SECONDS.sleep(5);
				String pos= request("setSpielZug&x="+x+"&y="+y);
				//TODO rückgabewert kontrollieren und abspeichern
				if(pos=="richtige Psoition"){
					warte=false;
					//TODO auf Spielfeld die Punkte setzen
				}				
			} catch (InterruptedException e) {
				//TODO Error
				e.printStackTrace();
			}
		}
	}

	public void gameend(){
		//TODO schauen ob das Spiel zu ende oder Abgegrochen ist
		//Punktestand an den WS senden
	}
	
	public void playerinfo(int SpielerID){
		String a=request("getPlayerInfo&SpielerID="+SpielerID);
		//TODO auslesen und aufbereiten für GUI
	}

	public void highscore(){
		String a=request("getHighscore");
		//Highscore auslesen
	}
  
}

