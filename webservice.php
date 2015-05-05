<?php

/*
*   Die Klasse battleship.php, ist ein Webservice, der Anfragen über GET bekommt, sie in einem Switch an die verlangte Funktion weitergibt 
*   und dann die Werte die er bekommt in ein JSON umwandelt und sie dann an den Client zurück gibt.  
*
*/

	if(!empty($_GET)){
		$function = $_GET['funktion'];
		$return = null;
        
        
			include("webservice.php");

			function Answer($ans){

					$msg = json_encode($ans);

					echo $msg;
			}

			switch ($function) {
					
					case 'setSpiel':
		 				$return = setSpiel($_GET['SpielerID'], $_GET['SpielName']);
						Answer($return);
					break;
                    case 'setSpielzug':
                        $return = setSpielZug($_GET['SpielID']);
						Answer($return);
					break;
					case 'getSpiele':
						$return = getSpiele();
						Answer($return);
					break;
					case 'setSchiffPosition':
						$return = setSchiffPosition($_GET['SpielID'], $_GET['SpielerID'], $_GET['Schiffmodell'],$_GET['x'], $_GET['y'],$_GET["direction"]);
						Answer($return);
					break;
					case 'getSchiffPosition':
						$return = getSchiffPosition($_GET['SpielID'],$_GET['GegnerID']);
						Answer($return);
					break;
					case 'joinSpiel':
						$return = joinSpiel($_GET['SpielID'], $_GET['SpielerID']);
						Answer($return);
					break;
					case 'getMitspieler':
						$return = getMitspieler($_GET['SpielID'],$_GET['Username']);
						Answer($return);
					break;
					case 'getSpielZug':
						$return = getSpielZug($_GET['SpielID'], $_GET['SpielerID']);
						Answer($return);
					break;
					case 'setSpielZug':
						$return = updateSpielZug($_GET['SpielID'], $_GET['SpielerID'], $_GET['x'], $_GET['y']);
						Answer($return);
					break;
					case 'getPlayerInfo':
						$return = getPlayerInfo($_GET['SpielerID']);
						Answer($return);
					break;
					case 'getHighscore':
						$return = getHighscore();
						Answer($return);
					break;
					case 'Login':
						$return = Login($_GET['Username'], $_GET['Password']);
						Answer($return);
					break;
					case 'Registrieren':
						$return = Registrieren($_GET['Username'],$_GET['Password']);
						Answer($return);
					break;
					case 'setFinishSpiel':
						$return = setFinishGame($_GET['SpielID'],$_GET['GewinnerID'],$_GET['SpielerPunkte']);
						Answer($return);
					break;
				default:
					$return = "Fehler";
					Answer($return);
					break;
			}


	}
?>
