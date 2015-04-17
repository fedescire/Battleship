<?php 

	/**
	* 
	***/
	class dbclass
	{
		var $QUERY;
		var $PARAM;
		var $PDO;
		var $STMT;
		
		function __construct()
		{
		}

		protected function connect(){
			try{
				$this->PDO = new PDO("mysql:host=localhost;dbname=db_battleship;", "root", "usbw");
			}
			catch(Exception $e){
				echo "Fehler " . $e->getMessage();
			}
		}

		protected function close(){
			$this->PDO = null;
		}

		protected function bind(){

			$this->STMT = $this->PDO->prepare($this->QUERY);

			$countParams = count($this->PARAM);
			for ($i=0; $i < $countParams; $i++) { 
					$this->STMT->bindValue($i+1, $this->PARAM[$i]);
			}
		}

		public function prepare($sqlQuery){
			$this->QUERY = $sqlQuery;
			return $this;
		}

		public function set(){
			$this->PARAM = func_get_args();
			return $this;
		}

		public function executeNonQuery(){
			try{
				$this->connect();

				$this->bind();
				
				if (!$this->STMT->execute()){ 
					throw new Exception($this->STMT->errorCode());
				}
				$tmp=$this->PDO->lastInsertId();
				$this->close();
				return $tmp;
			}
			catch(Exception $e){
				echo "Fehler " . $e->getMessage();
			}
		}


		public function execute(){
			try{
				$this->connect();

				$this->bind();

				if (!$this->STMT->execute()){ 
					throw new Exception($this->STMT->errorCode());
				}

				$count = 0;
				$arr = null;

				while(($row=$this->STMT->fetch(PDO::FETCH_OBJ))!= null){
					$arr[$count] = $row;
					$count++;
				}
				
				$this->close();

				return $arr;
			}
			catch(Exception $e){
				echo "Fehler " . $e->getMessage();
			}
		}
	}
	
 ?>
