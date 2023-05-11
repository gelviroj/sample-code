<?php
require "init.php";

$work_id = $_POST["work_id"];
$sql_query = "SELECT a.work_id, a.asset_id_fk, a.work_type, a.work_ticket, a.work_notes, a.work_date, b.username
			  FROM work_table as a 
			  INNER JOIN user_table as b on a.user_id_fk = b.user_id 
			  WHERE work_id like '$work_id';";

if(!$con->query($sql_query)) {
	echo "Error in connecting to database.";
}
else{
	$result = $con->query($sql_query);
	if($result->num_rows > 0) {
		$return_arr['work_detail'] = array();
		while($row = $result->fetch_array()){
			array_push($return_arr['work_detail'], array(
				'work_id' => $row['work_id'],
				'asset_id_fk' => $row['asset_id_fk'],
				'work_type' => $row['work_type'],
				'work_ticket' => $row['work_ticket'],
				'work_notes' => $row['work_notes'],
				'work_date' => $row['work_date'],
				'user' => $row['username']
			));
		}
		echo json_encode($return_arr);
	}else{
	   	$return_arr['work_detail'] = array();
		echo json_encode($return_arr);
	}
}
mysqli_close($con);

?>