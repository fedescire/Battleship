<?php
	if(!empty($_GET)){
		$function = $_GET['funktion'];
		
		$return = null;
			//include("webservice.php");

			function Answer($ans){

					echo $ans;
			}

			function setSpiel($SpielerID, $SpielName){
				return  $SpielerID ." ". $SpielName;
			}

			switch ($function) {
					
					case 'setSpiel':
						$ans = setSpiel($_GET['SpielerID'], $_GET['SpielName']);
						Answer($ans);
					break;
					case 'getSpiele':
						$return = getSpiele();
						Answer();
					break;
					case 'setSchiffPosition':
						$return = setSchiffPosition($_GET['SpielID'], $_GET['SpielerID'], $_GET['Schiffmodell'],$_GET['x'], $_GET['y']);
						Answer();
					break;
					case 'getSchiffPosition':
						$return = getSchiffPosition($_GET['SpielID'],$_GET['GegnerID']);
						Answer();
					break;
					case 'joinSpiel':
						$return = joinSpiel($_GET['SpielID'], $_GET['SpielerID']);
						Answer();
					break;
					case 'getMitspieler':
						$return = getMitspieler($_GET['SpielID']);
						Answer();
					break;
					case 'getSpielZug':
						$return = getSpielZug($_GET['SpielID'], $_GET['SpielerID']);
						Answer();
					break;
					case 'setSpielZug':
						$return = setSpielZug($_GET['SpielID'], $_GET['SpielerID'], $_GET['x'], $_GET['y']);
						Answer();
					break;
					case 'getPlayerInfo':
						$return = getPlayerInfo($_GET['SpielerID']);
						Answer();
					break;
					case 'getHighscore':
						$return = getHighscore();
						Answer();
					break;
					case 'Login':
						$return = Login($_GET['Username'], $_GET['Password']);
						Answer();
					break;
					case 'Registrieren':
						$return = Registrieren($_GET['Username'],$_GET['Password']);
						Answer();
					break;
					case 'setFinishSpiel':
						$return = setFinishSpiel($_GET['SpielID'],$_GET['GewinnerID'],$_GET['SpielerPunkte']);
						Answer();
					break;
					case 'abbrechenSpiel':
						$return = abbrechenSpiel($_GET['SpielID'],$_GET['AufgeberID']);
						Answer();
					break;
				
				default:

					$return = "Fehler";
					Answer();
					break;
			}


	}
?>
