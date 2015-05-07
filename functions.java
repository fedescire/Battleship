/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Federico
 */

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.concurrent.TimeUnit;
import org.apache.commons.io.IOUtils;
import org.json.*;



public class Functions {

    
        static private Functions f = new Functions();
        static public Functions getInstance(){
            return f;
        }
        int restpunkte=100;
        int gespunkte=0;
        private String gameName;
	private int spielId;
	String gegnerId;
	String Username;
        
        private int[][] ownField;
        private int[][] enemyField;
        
	public String getUsername() {
		return Username;
	}

	public void setUsername(String username) {
		Username = username;
                System.out.println("[Functions]Username: "+Username);
	}

	String Spielname;
        private String hostToUse;

	//REQUEST PASST
	public String request(String function){
		try {
			String url1="http://fedesawesomeserver.mynetgear.com";//?"+function;
                        //String url1="http://"+hostToUse+"/battleship/battleship.php?"+function;
			URL url = new URL(url1);
			URLConnection con = url.openConnection();
                        con.addRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:25.0) Gecko/20100101 Firefox/25.0");
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
        //In der Funktion Login wird über die Datenbank getested ob der Benutzer existiert und dass Password übereinstimmt. Des weiteren beinhaltet diese Funktion die Möglichkeit sich zu Registreiren
	public boolean login(String user,String pw, boolean olduser){
		String help=null;	
		//Wenn der User bereits existiert ist olduser true (Spieler hat auf login geklickt), wenn der Spieler nicht existiert wird er neu angelegt (Registireren geklickt)
		if(olduser){
			//Anfrage an den Websrvice über die Funtkion request, als Rückgabe gibt es error oder ok
			help= request("funktion=Login&Password="+pw+"&Username="+user);
		}else{
			//Anfrage an den Websrvice über die Funtkion request, als Rückgabe gibt es error oder ok
			help=request("funktion=Registrieren&Password="+pw+"&Username="+user);
		}
		//Zum Überprüfen werden alle /n und /r gelöscht
		help=replace(help);
		//Wenn die Antwort ok ist wird zurückgegeben dass alles geklappt hat, ansosnten wird false zurückgegeben
		if(help.equals("ok") ){
			return true;
		}else{
			return false;
		}
	}
	
	//REPLACE PASST
        //Diese Funktion ersetzt einfach alle \",\n und \r und gibt den String zurück
	public String replace(String wert){
		wert= wert.replace("\"", "");
		wert = wert.replace("\r", "");
		wert = wert.replace("\n", "");
		return wert;
	}
	
	//CREATEGAME PASST AUSER RÜCKGABE DER DATENBANK
        //Diese Funktion erstellt ein neues Spiel und wartet bis ihr ein gegner beitritt;
	public String creategame(String spielname, String username ){
		String ruck=null;
		
		//Spiel wird erstellt und mann bekommt die SpielID zurück
		String spieleid=""+request("funktion=setSpiel&SpielerID="+username+"&SpielName="+spielname);
		//Wenn ein fehler zrucükkommt wird die Funtkion beendet und das gemeldet
		if(spieleid.contains("Fehler 23000")){
			return "error";
		}
		//Alle \n und \r werden ersetzt
		spieleid = replace(spieleid);
		//WEnn die SpielID nicht leer ist geht er weiter
		if(spieleid!=""){
			//Solange kein gegner existiert "pollt" er weiter bi einer gefunden wird
			while(true){
				try {
					//Rückgabe bei keinem Gegner ist null
					ruck= request("SpielID="+spieleid+"&funktion=getMitspieler&Username="+username);
					//Bei der Antwort werden alle \n und \r ersetzt
					ruck=replace(ruck);
					//Wenn die Rückgabe nicht null ist gibt es einen gegner und die Schleife wird beendet
					if(ruck!="null"){
						break;
					}else{
						//Bei keinem gegner wartet er 5 Sekunden und geht frägt dann erneut an
						TimeUnit.SECONDS.sleep(5);
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			//Spielzug erstellen mit noch nichts drinnen
			request("funktion=setSpielzug&SpielID="+spieleid);
			//GegnerID wird gesetzt
			gegnerId=ruck;
			//SpielID wird gesetzt
			spielId=Integer.parseInt(spieleid);
			return "startGame";
		}else{
			return "error";
		}
	}
	
	//JOINGAME PASST
	// Diese Funktion wird benutzt, um einen SPiel beizutreten
	public boolean joinGame(int id,String spielername){
		//Rückgabe inform von ok (Teilnhemen geklappt) oder error (Teilnahme nicht geklappt)
		String help=request("funktion=joinSpiel&SpielID="+id+"&SpielerID="+spielername);
		//Kontrolle ob es geklappt hat und Rückgabe ob ja oder nein
		if(help.contains("ok")){
			return true;
		}else{
			return false;
		}
	}
	
	
	//GETGAMES PASST
	//Diese Funktion ruft alle offenen Spiele aus der Datenbank ab
	public String[][] getGames(){
		//Alle Spiele werden geholt
		String answ=request("funktion=getSpiele");
		JSONArray json=jsonparse(answ);
		int lange=json.length();		
		//Rückgabe des Wertes an GUI inform eines 2D arrays mit 3 Spalten ID,Spielname,Gegnername
		String[][] liste=new String[lange][3];		
		for (int i = 0; i <lange; i++)
		{
			//Hier wird das JSON ausgelesen und in einen 2 Dimensionalen Strin gespeichert um der GUI das Auslesen zu ermöglichen
		    liste[i][0] = json.getJSONObject(i).getString("spielID");
		    liste[i][1] = json.getJSONObject(i).getString("spielName");
		    liste[i][2] = json.getJSONObject(i).getString("host");
		}		
		return liste;
	}
	
	//JSONPARSE PASST JUHUUUUU
        //Diese Funtkion parst den String der Rückgabe in ein JSONArray um das Auslesen zu ermöglichen
	public JSONArray jsonparse(String word){
            word="{\"lese\":"+word+"}";
            JSONObject obj = new JSONObject(word);
            JSONArray arr = obj.getJSONArray("lese");
	    return arr;
	}

	//STARTGAME passt
        //In dieser Funktion werden alle Schiffe an die Datenbank übergeben und diedes Gegenrs ausgelesen
	public int[][] startgame(int[][] myfeld, String username){
		for(int a=0;a<10;a++){
			for(int b=0;b<10;b++){
				if(myfeld[a][b]!=0){
					//Feld hat z.B. wert 51--> 5 ist schiffsmodell und 1 die Richtung 1=Horizontal,2=Vertikal
					int modell=myfeld[a][b]/10;
					int direct=myfeld[a][b]-(modell*10);
					request("funktion=setSchiffPosition&x="+a+"&y="+b+"&Schiffmodell="+modell+"&direction="+direct+"&SpielID="+spielId+"&SpielerID="+username);
				}
			}
		}
		//Gegnerischen Schiffe auslesen
		String antw=request("funktion=getSchiffPosition&SpielID="+spielId+"&GegnerID="+gegnerId);
		JSONArray json=jsonparse(antw);
		int[][] ab=new int[10][10];
		for(int a=0;a<10;a++){
			for(int b=0;b<10;b++){
				ab[a][b]=0;
			}
		}
		int lange=json.length();
		for(int za=0;za<lange;za++){
			//Gegnerischen Schiffe in das Spielfeld speichern
			int x=Integer.parseInt(json.getJSONObject(za).getString("x_kord"));
			int y=Integer.parseInt(json.getJSONObject(za).getString("y_kord"));
			ab[x][y]=Integer.parseInt(json.getJSONObject(za).getString("schiffmodell"));		
		}
		return ab;//gegnerfeld;
	}
	
	//POLLING passt
        //Diese Funktion setzt den aktuellen Spielzug und wartet bis der Gegner gespielt hat
	public int[] polling(int x,int y, String user, Boolean treffer){
		boolean warte=true;
		//Aktueller Spielzug wird gesendet
		request("funktion=setSpielZug&SpielID="+spielId+"&x="+x+"&y="+y+"&SpielerID="+user);
		//Wenn ein Schiff getroffen wurde, wird die Punktzahl erhöht
		if(treffer){
			gespunkte=gespunkte+restpunkte;
		}
		//Die möglichen Restpunkte werden um 1 herabgesetzt
		restpunkte--;
		//Warten bis dere Gegner einen Zug gemacht hat
		int[] a=new int[2];
		while(warte){
			 try {
				 
				// wie sleep, in der klammer kommt die Zeit, die der Client warten muss in Sekunden
				TimeUnit.SECONDS.sleep(2);
				//Gegnerische Spielzug wurde abgerufen und geparst
				JSONArray json=jsonparse(request("SpielID="+spielId+"&funktion=getSpielZug"));
				String usera=json.getJSONObject(0).getString("spieler");
				//wenn der Spielername ungleich meinem Username ist wird der SPielzug ausgelesen
				if(!usera.equals(Username)){
					//Die X und y koordinate des gegenrs wird ausgelsenen
					a[0]=Integer.parseInt(json.getJSONObject(0).getString("x_kord"));
					a[1]=Integer.parseInt(json.getJSONObject(0).getString("y_kord"));
					//Spielzug wird zurückgegeben
					warte=false;
					return a;
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
				return null;
			}
		}
		return a;
	}

	public Boolean gameend(){
            String antw=request("funktion=setFinishSpiel&SpielID="+spielId+"&GewinnerID="+Username+"&SpielerPunkte="+gespunkte);
		antw=replace(antw);
		if(antw.equals("null")){
			return true;
		}else{
			return false;
		}
	}
	
        
        //In dieser Funktion wird der Highscore zum Auslesen von der Datenbank geholt
	public String[][] highscore(){
		//Highscore wird ausgelesen
		String a=request("funktion=getHighscore");
		//JSON wird geparst
		JSONArray arr=jsonparse(a);
		int lan=arr.length();
		//String für die Antwort wird erstellt
		String[][] ruck=new String[lan][2];
		//JSON wird ausgelesen
		for (int i = 0; i <lan; i++)
		{
			//Daten des Jsons werden in ein 2D array gespeichert
		    ruck[i][0] =arr.getJSONObject(i).getString("username");
		    ruck[i][1] =arr.getJSONObject(i).getString("highscore");
		}		
		//Antwort wird zurückgegeben
		return ruck;
	}

    /**
     * @return the spielId
     */
    public int getSpielId() {
        return spielId;
    }

    /**
     * @param spielId the spielId to set
     */
    public void setSpielId(int spielId) {
        this.spielId = spielId;
    }

    /**
     * @return the ownField
     */
    public int[][] getOwnField() {
        return ownField;
    }

    /**
     * @param ownField the ownField to set
     */
    public void setOwnField(int[][] ownField) {
        this.ownField = ownField;
    }

    /**
     * @return the enemyField
     */
    public int[][] getEnemyField() {
        return enemyField;
    }

    /**
     * @param enemyField the enemyField to set
     */
    public void setEnemyField(int[][] enemyField) {
        this.enemyField = enemyField;
    }

    /**
     * @return the gameName
     */
    public String getGameName() {
        return gameName;
    }

    /**
     * @param gameName the gameName to set
     */
    public void setGameName(String gameName) {
        this.gameName = gameName;
    }

    /**
     * @return the hostToUse
     */
    public String getHostToUse() {
        return hostToUse;
    }

    /**
     * @param hostToUse the hostToUse to set
     */
    public void setHostToUse(String hostToUse) {
        this.hostToUse = hostToUse;
    }
  
}
