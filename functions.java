
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.concurrent.TimeUnit;
import org.apache.commons.io.IOUtils;
import org.json.*;



public class functions {

	int spielID;
	String gegnerId;
	String Username;
	public String getUsername() {
		return Username;
	}

	public void setUsername(String username) {
		Username = username;
	}

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
	
	//REPLACE PASST
	public String replace(String wert){
		wert= wert.replace("\"", "");
		wert = wert.replace("\r", "");
		wert = wert.replace("\n", "");
		return wert;
	}
	
	//CREATEGAME PASST AUSER RÜCKGABE DER DATENBANK
	public String creategame(String spielname, String username ){
		String ruck=null;
		//Das Spiel wird erstellt, ohne Probleme geht er ins polling und wartet auf einen Mitspieler
			
		//Rückgabe der SpielID des erstellten Spiels
		String spieleid=""+request("funktion=setSpiel&SpielerID="+username+"&SpielName="+spielname);
		if(spieleid.contains("Fehler 23000null")){
			return "error";
		}
		spieleid = replace(spieleid);
		if(spieleid!=""){
			Boolean a=true;
			while(a){
				try {
					//Rückgabe der gegner ID oder bei keinem gegner NULL
					
					//TODO Rückgabe testen!!
					ruck= request("SpielID="+spieleid+"&funktion=getMitspieler&Username="+username);
					ruck=replace(ruck);
					if(ruck!="null"){
						a=false;
					}else{
						TimeUnit.SECONDS.sleep(5);
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			//Spielzug erstellen!!
			request("funktion=setSpielzug&SpielID="+spieleid);
			gegnerId=ruck;
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
		JSONObject obj = new JSONObject(word);
		JSONArray arr = obj.getJSONArray("lese");
	    return arr;
	}

	//STARTGAME passt
	public int[][] startgame(int[][] myfeld, int spielid, String username){
		for(int a=0;a<10;a++){
			for(int b=0;b<10;b++){
				if(myfeld[a][b]!=0){
					//Feld hat z.B. wert 51--> 5 ist schiffsmodell und 1 die Richtung 1=Horizontal,2=Vertikal
					int modell=myfeld[a][b]/10;
					int direct=myfeld[a][b]-(modell*10);
					request("funktion=setSchiffPosition&x="+a+"&y="+b+"&Schiffmodell="+modell+"&direction="+direct+"&SpielID="+spielid+"&SpielerID="+username);
				}
			}
		}
		String antw=request("funktion=getSchiffPosition&SpielID="+spielid+"&GegnerID="+gegnerId);
		JSONArray json=jsonparse(antw);
		int[][] ab=new int[10][10];
		for(int a=0;a<10;a++){
			for(int b=0;b<10;b++){
				ab[a][b]=0;
			}
		}
		int lange=json.length();
		for(int za=0;za<lange;za++){
			int x=Integer.parseInt(json.getJSONObject(za).getString("x_kord"));
			int y=Integer.parseInt(json.getJSONObject(za).getString("y_kord"));
			ab[x][y]=Integer.parseInt(json.getJSONObject(za).getString("schiffmodell"));		
		}
		//TODO antw. parsen und auslesen für die Rückgabe
		return ab;//gegnerfeld;
	}
	
	//POLLING passt
	public void polling(int x,int y, int spieleid, String user){
		boolean warte=true;
		request("funktion=setSpielZug&SpielID="+spieleid+"&x="+x+"&y="+y+"&SpielerID="+user);
		while(warte){
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
	
	
	//HIGHSCORE PASST
	public String[][] highscore(){
		String a=request("funktion=getHighscore");
		JSONArray arr=jsonparse(a);
		int lan=arr.length();
		String[][] ruck=new String[lan][2];
		for (int i = 0; i <lan; i++)
		{
		    ruck[i][0] =arr.getJSONObject(i).getString("username");
		    ruck[i][1] =arr.getJSONObject(i).getString("highscore");
		}		
		return ruck;
	}
  
}
