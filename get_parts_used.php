<?php
require "init.php";
$work_id = $_POST["work_id"];
$response = array();

$sql_query = "SELECT parts_used_table.part_used_id, parts_used_table.work_id, parts_used_table.part_id, parts_table.part_name
			  FROM parts_used_table INNER JOIN parts_table 
			  ON parts_used_table.part_id = parts_table.part_id 
			  WHERE work_id = '$work_id';";

if(!$con->query($sql_query)) {
	echo "Error in connecting to database.";
}
else{
	$result = $con->query($sql_query);
	if($result->num_rows > 0) {
		$return_arr['parts_used_list'] = array();
		while($row = $result->fetch_array()){
			array_push($return_arr['parts_used_list'], array(
				'part_used_id' => $row['part_used_id'],
				'work_id' => $row['work_id'],
				'part_id' => $row['part_id'],
				'part_name' => $row['part_name'],
			));
		}
		echo json_encode($return_arr);
	}else{
	   	$return_arr['parts_used_list'] = array();
		echo json_encode($return_arr);
	}
}
mysqli_close($con);

?>