<?php
require "init.php";

$asset_id_fk = $_POST["asset_id_fk"];
$work_date = $_POST["work_date"];
$work_type = $_POST["work_type"];
$work_ticket = $_POST["work_ticket"];
$user_id_fk = $_POST["user_id_fk"];
$work_notes = $_POST["work_notes"];

$sql_query = "INSERT INTO work_table (asset_id_fk, work_date, work_type, work_ticket, user_id_fk, work_notes) 
			  VALUES ('$asset_id_fk', '$work_date', '$work_type', '$work_ticket', '$user_id_fk', '$work_notes');";

if($con->query($sql_query) === TRUE){
	echo "Work successfully added.";
}else{
	 echo "Error with adding to log. Please Try again.";
}

mysqli_close($con);

?>