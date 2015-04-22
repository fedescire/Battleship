import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import org.apache.commons.io.IOUtils;
import org.json.*;



public class functions {

	int spielID;
	String Spielname;

	//REQUEST PASST
	public String request(String function){
		try {
			String url1="http://localhost:8080/battleship/battleship.php?"+function;
			URL url = new URL(url1);
			URLConnection con = url.openConnection();
			String encoding = con.getContentEncoding();
			InputStream in = con.getInputStream();
			encoding = encoding == null ? "UTF-8" : encoding;
			String body = IOUtils.toString(in, encoding);
			return body;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}	

	//LOGIN PASST
	public boolean login(String user,String pw, boolean olduser){
		String help=null;	
		//Spieler meldet sich an mit PW und username
		if(olduser){
			//Rückgabe http Seite  mit OK oder Error
			help= request("funktion=Login&Password="+pw+"&Username="+user);
		}else{
			//Rückgabe http Seite  mit OK oder Error
			System.out.println("test");
			help=request("funktion=Registrieren&Password="+pw+"&Username="+user);
		}
		//Rückgabe überprüfen und antwort ob ok oder nicht
		if(help.contains("ok")){
			return true;
		}else{
			return false;
		}
	}
	
	//CREATEGAME PASST
	public String creategame(String spielname, String username ){
		String ruck=null;
		//Das Spiel wird erstellt, ohne Probleme geht er ins polling und wartet auf einen Mitspieler
			
		//Rückgabe der SpielID des erstellten Spiels
		String spieleid=""+request("funktion=setSpiel&SpielerID="+username+"&SpielName="+spielname);
		if(spieleid.contains("Fehler 23000null")){
			return "error";
		}
		spieleid = spieleid.replace("\"", "");
		int test=Integer.parseInt(spieleid);
		System.out.println(test);
		spieleid = spieleid.replace("\r", "");
		spieleid = spieleid.replace("\n", "");
		if(spieleid!=""){
			Boolean a=true;
			/*while(a){
				try {
					TimeUnit.SECONDS.sleep(5);
					//Rückgabe der gegner ID oder bei keinem gegner NULL
					ruck= request("SpielID="+spieleid+"&funktion=getMitspieler&Username="+username);
					if(ruck!="null"){
						a=false;
					}
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}*/
			//System.out.println(spieleid);
			//request("funktion=setSpielzug&SpielID="+spieleid);
			return ruck+":"+spieleid;
		}else{
			return "error";
		}
	}
	
	//JOINGAME PASST
	public boolean joinGame(int id,String spielername){
		//Rückgabe inform von ok (Teilnhemen geklappt) oder error (Teilnahme nicht geklappt)
		String help=request("funktion=joinSpiel&SpielID="+id+"&SpielerID="+spielername);	
		if(help.contains("ok")){
			return true;
		}else{
			return false;
		}
	}
	
	
	//GETGAMES PASST
	public String[][] getGames(){
		String answ=request("funktion=getSpiele");
		JSONArray json=jsonparse(answ);
		int lange=json.length();		
		//Rückgabe des Wertes an GUI inform eines 2D arrays mit 3 Spalten ID,Spielname,Gegnername
		String[][] liste=new String[lange][3];		
		for (int i = 0; i <lange; i++)
		{
		    liste[i][0] = json.getJSONObject(i).getString("spielID");
		    liste[i][1] = json.getJSONObject(i).getString("spielName");
		    liste[i][2] = json.getJSONObject(i).getString("host");
		}		
		return liste;
	}
	
	//JSONPARSE PASST JUHUUUUU
	public JSONArray jsonparse(String word){
		word="{\"lese\":"+word+"}";
		System.out.println(word);
		JSONObject obj = new JSONObject(word);
		JSONArray arr = obj.getJSONArray("lese");
	    return arr;
	}

	//WARTE AUF GUI
	public int[][] startgame(int[][] myfeld, int spielid){
		for(int a=0;a<10;a++){
			for(int b=0;b<10;b++){
				if(myfeld[a][b]!=0){
					//Feld hat z.B. wert 51--> 5 ist schiffsmodell und 1 die Richtung 1=Horizontal,2=Vertikal
					int modell=myfeld[a][b]/10;
					int direct=myfeld[a][b]-(modell*10);
					//TODO mit TROJ ausmachen dass ich auch die Direction übergebe!!!!!
					request("setSchiffPosition&x="+a+"&y="+b+"&schiffmodell="+modell+"&direct="+direct+"&spielID="+spielid);
				}
			}
			
		}
		//int[][] gegnerfeld= new int[10][10];
		//String antw=request("getSchiffPosition&SpielID="+spielid);
		//TODO antw. parsen und auslesen für die Rückgabe
		return null;//gegnerfeld;
	}
	
	public void polling(int x,int y, int spieleid, String user){
		boolean warte=true;
		String a= request("SpielID="+spieleid+"&funktion=setSpielZug&x="+x+"&y="+y+"&SpielerID="+user);
		System.out.println("test:"+a);
		//TODO schauen ob die Daten angekommen sind
		/*while(warte){
			 try {
				 
				// wie sleep, in der klammer kommt die Zeit, die der Client warten muss in Sekunden
				TimeUnit.SECONDS.sleep(2);
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
		}*/
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
