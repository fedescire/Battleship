<?php

	include("class/dbclass.php");
	include("class/game.php");
	include("class/gameinfo.php");
	include("class/playerinfo.php");
	
	$arr=null;
	//Registrieren('Fede', 'Fedep');
	//Login('Fede','Fedep');
	//setSpiel('Marvin', 'Spiel5');
	//JoinSpiel(3, 'Marvin');
	//getSpiele();
	//setSchiffPosition(1, 'Fede',1, 5, 5, 'h');
	//getSchiffposition(1, 'Fede');
	//setSpielZug(1);
	//updateSpielZug(1,'Fede',5,5);
	//getSpielZug(1);
	//setFinishGame(10, "Fede", 14000);
	//getHighscore();
	//getMitspieler(1,'Fede');

	//getPlayerInfo(1);
	//abbrechenGame(int SpielID, int AufgeberName);
	
	
	function Registrieren($Username, $Password)
	{
		$check=null;
		$xmlquery=new dbclass();
		$check=$xmlquery->prepare('Select use_name from tuser where use_name=?;')->set($Username)->execute();
		if(isset($check[0]) && $check[0]->use_name==$Username)
		{
			return 'Fehler';
		}
		else
		{
			$xmlquery->prepare('insert into tuser values(?,?)')->set($Username,$Password)->executeNonQuery();;
		}
	}

	function Login($UserName, $Password)
	{
		$check=null;
		$xmlquery=new dbclass();
		$check=$xmlquery->prepare('Select use_name from tuser where use_name= ? and use_passwort=?;')->set($UserName,$Password)->execute();		
		if(isset($check[0]) && !is_null($check[0]->use_name) && $check[0]->use_name==$UserName)
		{
			return 'ok';
		}
		else
		{
			return 'Fehler';
		}
	}

	function setSpiel($SpielerName, $SpielName)	//int = SpielID
	{
		$xmlquery=new dbclass();
		$tmp="0000-00-00 00:00:00";
		$newspielid=$xmlquery->prepare('insert into tspiel values(?,?,?,?,?,?,?,?)')->set(null,$SpielName,null,null,null,$tmp,null,$SpielerName)->executeNonQuery();
		return $newspielid;
	}

	function JoinSpiel($SpielID, $SpielerName)
	{
		$xmlquery=new dbclass();
		$xmlquery->prepare('update tspiel set spi_fgast_name=? where spi_id=?;')->set($SpielerName,$SpielID)->executeNonQuery();
		echo 'ok';
		//überprüfen ob funkt hat
	}

	function getSpiele() //Gibt Liste mit Spielen zurück
	{
		$xmlquery=new dbclass();
		$arr=$xmlquery->prepare('Select spi_id, spi_name, spi_fhost_name from tspiel where spi_fgast_name is null')->set()->execute();
		$gamearray=array();
		foreach($arr as $data){
			$tmp = new game();
			$tmp->spielID=$data->spi_id;
			$tmp->spielName=$data->spi_name;
			$tmp->host=$data->spi_fhost_name;
			$gamearray[]=$tmp;
		}
		return $gamearray;
	}

	function setSchiffPosition($SpielID, $SpielerName,$SchiffModel, $x, $y, $Richtung)
	{
		$xmlquery=new dbclass();
		$xmlquery->prepare('insert into tschiffpos values(?,?,?,?,?,?,?)')->set(null,$SchiffModel,$x,$y,$SpielerName,$Richtung,$SpielID)->executeNonQuery();
	}

	function getSchiffposition($SpielID, $GegnerName)
	{
		$xmlquery=new dbclass();
		$arr=$xmlquery->prepare('Select schiffpos_x,schiffpos_y,schiffpos_model,schiffpos_richtung,schiffpos_spiel_id,schiffpos_spi_fuser_name from tschiffpos where schiffpos_spi_fuser_name=? and schiffpos_spiel_id=?')->set($GegnerName,$SpielID)->execute();
		$schiffposarray=array();
		foreach($arr as $data){
			$tmp=new gameinfo();
			$tmp->x_kord=$data->schiffpos_x;
			$tmp->y_kord=$data->schiffpos_y;
			$tmp->schiffmodell=$data->schiffpos_model;
			$tmp->richtung=$data->schiffpos_richtung;
			$tmp->spielid=$data->schiffpos_spiel_id;
			$tmp->spieler=$data->schiffpos_spi_fuser_name;
			$schiffposarray[]=$tmp;
		}
		return $schiffposarray;
	}	

	function setSpielZug($SpielID)
	{
		$xmlquery=new dbclass();
		$xmlquery->prepare('insert into tspielzug values(?,?,?,?,?)')->set(null,null,null,null,$SpielID)->executeNonQuery();		
	}

	function updateSpielZug($SpielID,$SpielerName,$x_pos,$y_pos)
	{
		$xmlquery=new dbclass();
		$xmlquery->prepare('update tspielzug set spielzug_use_name=?, spielzug_x_pos=?, spielzug_y_pos=? where spielzug_spi_fid=?;')->set($SpielerName,$x_pos,$y_pos,$SpielID)->executeNonQuery();
	}

	function getSpielZug($SpielID) //SpielerID (eigene) überprüft wer letzten Zug gemacht. Wenn !=SpielerID, dann wird ZUg zurück gegeben
	{
		$xmlquery=new dbclass();
		$arr=$xmlquery->prepare('Select spielzug_use_name,spielzug_x_pos, spielzug_y_pos from tspielzug where spielzug_spi_fid='.$SpielID.';')->set()->execute();
		$spielzugarray=array();
		foreach($arr as $data){
			$tmp=new gameinfo();
			$tmp->spieler=$data->spielzug_use_name;
			$tmp->x_kord=$data->spielzug_x_pos;
			$tmp->y_kord=$data->spielzug_y_pos;
			$spielzugarray[]=$tmp;
		}
		return $spielzugarray;
	}	

	function setFinishGame($SpielID, $GewinnerName, $SpielerPunkte)
	{
		$xmlquery=new dbclass();
		$xmlquery->prepare('update tspiel set spi_gewinner_name=?, spi_gewinnerpunkte=?, spi_time_ende=null where spi_id=?;')->set($GewinnerName,$SpielerPunkte,$SpielID)->executeNonQuery();	
	}

	function getHighscore()
	{
		$xmlquery=new dbclass();
		$arr=$xmlquery->prepare('Select use_name, spi_gewinnerpunkte from tspiel, tuser where spi_gewinner_name=use_name order by spi_gewinnerpunkte desc;')->set()->execute();
		$highscorelist=array();
		foreach($arr as $data){
			$tmp=new playerinfo();
			$tmp->username=$data->use_name;
			$tmp->highscore=$data->spi_gewinnerpunkte;
			$highscorelist[]=$tmp;
		}
		return $highscorelist;

	}

	function getMitspieler($SpielID,$SpielerName)
	{
		$xmlquery=new dbclass();
		$tmp=new playerinfo();
		$arr=$xmlquery->prepare('Select use_name from tuser,tspiel where spi_id=? and (spi_fgast_name!=? or spi_fhost_name!=? and use_name!=?);')->set($SpielID,$SpielerName,$SpielerName,$SpielerName)->execute();

		$tmp->username=$arr[0]->use_name;
		var_dump($tmp);
		return $tmp;
	}


	/*function abbrechenGame(int SpielID, int AufgeberName) //und setFinishGame aufrufen.
	{

	}*/

	/*function getPlayerInfo($SpielerName)//Statistik von einem Spieler
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
	}*/

?>

