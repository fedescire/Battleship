import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.net.URL;
import java.net.URLConnection;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.io.IOUtils;
import org.jdom2.Document;
import org.jdom2.Element;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;


public class functions {
	//Verschiedenene Spielfelder
	int[][] myfeld=new int[10][10];
	int[][] otherfeld=new int[10][10];
	
	// username wird bei erfolgter Anmeldung gesetzt
	String username="test";
	//Spieleid wird nach dem Spielstart gesetzt
	int spieleid;
	public String request(String function){
		try {
			String url1="http://localhost/Projekt/battleship.php?"+function;
			URL url = new URL(url1);
			URLConnection con = url.openConnection();
			String encoding = con.getContentEncoding();
			InputStream in = con.getInputStream();
			encoding = encoding == null ? "UTF-8" : encoding;
			String body = IOUtils.toString(in, encoding);
			System.out.println(body);
			return body;
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("Fehler bei der Verbindung (funktion request");
			return null;
		}
	}	
	
	public List xmlparse(String answ){
		try {
			 DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			    DocumentBuilder builder = factory.newDocumentBuilder();
			    InputSource is = new InputSource(new StringReader(answ));
			 org.w3c.dom.Document doc= builder.parse(is);
			
			
			//Wurzelement holen
			Element element = ((Document) doc).getRootElement(); 
			String wurzel=""+element.getName();
			
			//Kindelemente auslesen (eintrag ist der name des Elements):
			List alleKinder = element.getChildren(); 
			int länge=alleKinder.size();
			//return alleKinder;
			System.out.println("test");
			//Ausgeben
			for(int zahl=1;zahl<länge;zahl++){
				if((((Element) alleKinder.get(zahl)).getName()).equals("eintrag")){
					System.out.println(((Element) alleKinder.get(zahl)).getValue()); 
				}
			}
			
		} catch (SAXException e) {
			//TODO Fehlermeldungen:
			return null;
		} catch (IOException e) {
			return null;
		} catch (ParserConfigurationException e) {
			return null;
		}
		return null;
	}
	
	public boolean login(String pass,String user, boolean olduser){
		String help=null;
		
		//Password verschlüsseln/ hash berechnen
		MessageDigest m;
		try {
			m = MessageDigest.getInstance("MD5");
			m.reset();
			m.update(pass.getBytes());
			byte[] digest = m.digest();
			BigInteger bigInt = new BigInteger(1,digest);
		
			//Passoword ist ab hier verschlüsselt
			String pw = bigInt.toString(16);
			if(olduser){
				//Spieler meldet sich an mit PW und username
				help= request("login&password="+pw+"&user="+user);
			}else{
				help=request("Registrieren&password="+pw+"&user="+user);
			}
			if(help.contains("<Status>ok</status>")){
				return true;
			}else{
				return false;
			}
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public String creategame(String spielname ){
		if(username!=null){
			
			//Das Spiel wird erstellt, ohne Probleme geht er ins polling und wartet auf einen Mitspieler
			
			String ok= request("setSpiel&id=username&spielname="+spielname);
			if(ok.contains("<status>ok</status>")){
				String[] h1 = ok.split(">");
				String[] h2 = h1[(h1.length-1)].split("<");
				spieleid=Integer.parseInt(h2[0]);
			}
			
			Boolean a=true;
			while(a){
				try {
					TimeUnit.SECONDS.sleep(5);
					String rück= request("SpielID="+spieleid+"&funktion=getMitspieler&SpielerID="+username);
					if(ok.contains("<status>ok</status>")){
						//TODO gegner ID auslesen
						a=false;
					}
					
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
			return ""+spieleid;
		}else{
			return "nobody logged in";
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
	
	public void startgame(int[][] myfeld){
		for(int a=0;a<10;a++){
			for(int b=0;b<10;b++){
				if(myfeld[a][b]!=0){
					//Feld hat z.B. wert 51--> 5 ist schiffsmodell und 1 die Richtung 1=Horizontal,2=wertikal
					int modell=myfeld[a][b]/10;
					int direct=myfeld[a][b]-(modell*10);
					//TODO mit TROJ ausmachen dass ich auch die Direction übergebe!!!!!
					request("setSchiffPosition&x="+a+"&y="+b+"&schiffmodell="+modell+"&direct="+direct);
				}
			}
		}
		//TODO eigenen schiffpositionen auslesen und an den WS senden (setSchiffPosition())
		//TODO die gegnerischen schiffpositionen von WS auslesen und abspeichern (getSchiffPosition())
	}
	
	public void polling(int x,int y){
		boolean warte=true;
		String a= request("SpielID="+spieleid+"&funktion=setSpielZug&x="+x+"&y="+y);
		//TODO schauen ob die Daten angekommen sind
		while(warte){
			 try {
				 
				// wie sleep, in der klammer kommt die Zeit, die der Client warten muss
				TimeUnit.SECONDS.sleep(5);
				String pos= request("SpielID="+spieleid+"&funktion=getSpielZug");
				//TODO rückgabewert kontrollieren und abspeichern
				if(pos=="richtige Position"){
					warte=false;
					//TODO auf Spielfeld die Punkte setzen
				}				
			} catch (InterruptedException e) {
				System.out.println("Error in der Funktion polling");
				e.printStackTrace();
			}
		}
	}

	public void gameend(){
		//TODO schauen ob das Spiel zu ende oder Abgebrochen ist
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

