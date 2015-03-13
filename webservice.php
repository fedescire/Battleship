<?php

	include("class/dbclass.php");
	
	$arr=null;
	//setSpielZug(11,1);
	//updateSpielZug(11,1,10,10);
	//getSpielZug(11,1);
	getPlayerInfo(1);
	//JoinSpiel(9, 2);

	function setSpiel($SpielerID, $SpielName)	//int = SpielID
	{
		$xmlquery=new dbclass();
		$tmp="0000-00-00 00:00:00";
		$xmlquery->prepare('insert into tspiel values(?,?,?,?,?,?,?,?)')->set(null,$SpielName,null,null,null,$tmp,null,$SpielerID)->executeNonQuery();

	}

	function getSpiele() //Gibt Liste mit Spielen zurück
	{
		$xmlquery=new dbclass();
		$arr=$xmlquery->prepare('Select spi_id, spi_name, spi_fhost_id from tspiel where spi_fgast_id is null')->set()->execute();
		/*foreach($arr as $data){
			echo $data->spi_id;
			echo $data->spi_name;
			echo $data->spi_fhost_id;
		}*/
		return $arr;
	}

	function setSchiffPosition($SpielID, $SpielerID,$SchiffModel, $x, $y, $Richtung)
	{
		$xmlquery=new dbclass();
		$xmlquery->prepare('insert into tschiffpos values(?,?,?,?,?,?,?)')->set(null,$SchiffModel,$x,$y,$SpielerID,$SpielID,$Richtung)->executeNonQuery();
	}

	function getSchiffposition($SpielID, $GegnerID)
	{
		$xmlquery=new dbclass();
		$arr=$xmlquery->prepare('Select schiffpos_x,schiffpos_y from tschiffpos where schiffpos_spi_fuser_id='.$GegnerID.' and schiffpos_spiel_id='.$SpielID)->set()->execute();
		/*foreach($arr as $data){
			echo $data->schiffpos_x;
			echo $data->schiffpos_y;
		}*/
		return $arr;
	}

	function JoinSpiel($SpielID, $SpielerID)
	{
		$xmlquery=new dbclass();
		$xmlquery->prepare('update tspiel set spi_fgast_id='.$SpielerID.' where spi_id='.$SpielID)->set()->executeNonQuery();
	}

	function setSpielZug($SpielID, $SpielerID)
	{
		$xmlquery=new dbclass();
		$xmlquery->prepare('insert into tspielzug values(?,?,?,?,?)')->set(null,null,null,$SpielerID,$SpielID)->executeNonQuery();		
		//sicherheitsabfrage schauen ob es schon gibt
	}

	function updateSpielZug($SpielID,$SpielerID,$x_pos,$y_pos)
	{
		$xmlquery=new dbclass();
		$xmlquery->prepare('update tspielzug set spielzug_use_fid='.$SpielerID.' spielzug_x_pos='.$x_pos.', spielzug_y_pos='.$y_pos.' where spielzug_spi_fid='.$SpielID)->set()->executeNonQuery();
		//sicherheitsabfrage schauen ob es spielzug schon gibt
	}

	function getSpielZug($SpielID, $SpielerID) //SpielerID (eigene) überprüft wer letzten Zug gemacht. Wenn !=SpielerID, dann wird ZUg zurück gegeben
	{
		$xmlquery=new dbclass();
		$arr=$xmlquery->prepare('Select spielzug_x_pos, spielzug_y_pos from tspielzug where spielzug_spi_fid='.$SpielID.' and spielzug_use_fid='.$SpielerID.';')->set()->execute();
		/*foreach($arr as $data){
			echo $data->spielzug_x_pos;
			echo $data->spielzug_y_pos;
		}*/
		return $arr;
	}

	function getPlayerInfo($SpielerID)//Statistik von einem Spieler
	{
		$xmlquery=new dbclass();
		$arr=$xmlquery->prepare('select * from vspielerstatistik where '.$SpielerID.'=');
		
		var_dump($arr);

		foreach($arr as $data){
			//echo $data->user_name;
			echo $data->Gespielte_Spiele;
			echo $data->Gewonnene_Spiele;
			echo $data->Verlorene_Spiele;
		}
	}

	/*List<Player> getHighscore();

	int Login(String UserName, string Password);

	int Registrieren(String Username, string Password);

	int setFinishGame(int SpielID, int GewinnerID, int SpielerPunkte);

	int abbrechenGame(int SpielID, int AufgeberID); //und setFinishGame aufrufen.

	int getMitspieler(int SpielID)*/
?>
