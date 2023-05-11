<?php
require "init.php";

$sql_query = "SELECT * from user_table";

if(!$con->query($sql_query)) {
	echo "Error in connecting to database.";
}
else{
	$result = $con->query($sql_query);
	if($result->num_rows > 0) {
		$return_arr['tech_array'] = array();
		while($row = $result->fetch_array()){
			array_push($return_arr['tech_array'], array(
				'user_id' => $row['user_id'],
				'first_name' => $row['first_name'],
				'last_name' => $row['last_name'],
				'username' => $row['username'],
			));
		}
		echo json_encode($return_arr);
	}else{
	   	$return_arr['tech_array'] = array();
		echo json_encode($return_arr);
	}
}
mysqli_close($con);

?>