<?php
require "init.php";

$work_id = $_POST["work_id"];
$work_date = $_POST["work_date"];
$work_type = $_POST["work_type"];
$work_ticket = $_POST["work_ticket"];
$user_id_fk = $_POST["user_id_fk"];
$work_notes = $_POST["work_notes"];

$sql_query = "UPDATE work_table 
			  SET work_date = '$work_date', work_type = '$work_type', work_ticket = '$work_ticket', user_id_fk = '$user_id_fk', work_notes = '$work_notes' 
			  WHERE work_id = '$work_id';";

if($con->query($sql_query) === TRUE){
	echo "Work successfully updated.";
}else{
	 echo "Error with updating work. Please Try again.";
}

mysqli_close($con);

?>