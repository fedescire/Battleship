<?php

	include("class/dbclass.php");
	
	$arr=null;
	//Registrieren('Fede', 'Fedep');
	//Login('Fede','Fedep');
	//setSpiel('Fede', 'Spiel4');
	//JoinSpiel(3, 'Marvin');
	//getSpiele();
	//setSchiffPosition(1, 'Fede',1, 5, 5, 'h');
	//getSchiffposition(1, 'Fede');
	//setSpielZug(1);
	//updateSpielZug(1,'Fede',5,5);
	//getSpielZug(1);
	//setFinishGame(3, "Marvin", 800);
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
			return 'Username existiert bereits';
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
		$check=$xmlquery->prepare('Select use_name from tuser where use_name= ? and use_passwort=?;')->set($UserName,$Password)->execute();		if(isset($check[0]) && !is_null($check[0]->use_name) && $check[0]->use_name==$UserName)
		{
			return 'ok';
		}
		else
		{
			echo "fehler";
			return 'Fehler';
		}
	}

	function setSpiel($SpielerName, $SpielName)	//int = SpielID
	{
		$xmlquery=new dbclass();
		$tmp="0000-00-00 00:00:00";
		$xmlquery->prepare('insert into tspiel values(?,?,?,?,?,?,?,?)')->set(null,$SpielName,null,null,null,$tmp,null,$SpielerName)->executeNonQuery();
	}

	function JoinSpiel($SpielID, $SpielerName)
	{
		$xmlquery=new dbclass();
		$xmlquery->prepare('update tspiel set spi_fgast_name=? where spi_id=?;')->set($SpielerName,$SpielID)->executeNonQuery();
	}

	function getSpiele() //Gibt Liste mit Spielen zurück
	{
		$xmlquery=new dbclass();
		$arr=$xmlquery->prepare('Select spi_id, spi_name, spi_fhost_name from tspiel where spi_fgast_name is null')->set()->execute();
		/*foreach($arr as $data){
			echo $data->spi_id;
			echo $data->spi_name;
			echo $data->spi_fhost_name;
		}*/
		return $arr;
	}

	function setSchiffPosition($SpielID, $SpielerName,$SchiffModel, $x, $y, $Richtung)
	{
		$xmlquery=new dbclass();
		$xmlquery->prepare('insert into tschiffpos values(?,?,?,?,?,?,?)')->set(null,$SchiffModel,$x,$y,$SpielerName,$Richtung,$SpielID)->executeNonQuery();
	}

	function getSchiffposition($SpielID, $GegnerName)
	{
		$xmlquery=new dbclass();
		$arr=$xmlquery->prepare('Select schiffpos_x,schiffpos_y from tschiffpos where schiffpos_spi_fuser_name=? and schiffpos_spiel_id=?')->set($GegnerName,$SpielID)->execute();
		/*foreach($arr as $data){
			echo $data->schiffpos_x;
			echo $data->schiffpos_y;
		}*/
		return $arr;
	}

	function setSpielZug($SpielID)
	{
		$xmlquery=new dbclass();
		$xmlquery->prepare('insert into tspielzug values(?,?,?,?,?)')->set(null,null,null,null,$SpielID)->executeNonQuery();		
		//sicherheitsabfrage schauen ob es schon gibt
	}

	function updateSpielZug($SpielID,$SpielerName,$x_pos,$y_pos)
	{
		$xmlquery=new dbclass();
		$xmlquery->prepare('update tspielzug set spielzug_use_name=?, spielzug_x_pos=?, spielzug_y_pos=? where spielzug_spi_fid=?;')->set($SpielerName,$x_pos,$y_pos,$SpielID)->executeNonQuery();
		//sicherheitsabfrage schauen ob es spielzug schon gibt
	}

	function getSpielZug($SpielID) //SpielerID (eigene) überprüft wer letzten Zug gemacht. Wenn !=SpielerID, dann wird ZUg zurück gegeben
	{
		$xmlquery=new dbclass();
		$arr=$xmlquery->prepare('Select spielzug_use_name,spielzug_x_pos, spielzug_y_pos from tspielzug where spielzug_spi_fid='.$SpielID.';')->set()->execute();
		/*foreach($arr as $data){
			echo $data->spielzug_use_name;
			echo $data->spielzug_x_pos;
			echo $data->spielzug_y_pos;
		}*/
		return $arr;
	}	

	function setFinishGame($SpielID, $GewinnerName, $SpielerPunkte)
	{
		$xmlquery=new dbclass();
		$xmlquery->prepare('update tspiel set spi_gewinner_name=?, spi_gewinnerpunkte=?, spi_time_ende=null where spi_id=?;')->set($GewinnerName,$SpielerPunkte,$SpielID)->executeNonQuery();	
		//timestamp beginn fehler
	}

	function getHighscore()
	{
		$xmlquery=new dbclass();
		$arr=$xmlquery->prepare('Select use_name, spi_gewinnerpunkte from tspiel, tuser where spi_gewinner_name=use_name order by spi_gewinnerpunkte desc;')->set()->execute();
		/*foreach($arr as $data){
			echo $data->use_name;
			echo $data->spi_gewinnerpunkte;
		}*/

		return $arr;
	}

	function getMitspieler($SpielID,$SpielerName)
	{
		$xmlquery=new dbclass();
		$arr=$xmlquery->prepare('Select use_name from tuser,tspiel where spi_id=? and (spi_fgast_name!=? or spi_fhost_name!=? and use_name!=?);')->set($SpielID,$SpielerName,$SpielerName,$SpielerName)->execute();
		$mitspieler= $arr[0]->use_name;
		
		return $mitspieler;
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

