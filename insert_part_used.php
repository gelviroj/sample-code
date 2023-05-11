<?php
require "init.php";
$work_id = $_POST["work_id"];
$part_id = $_POST["part_id"];


$sql_query = "INSERT INTO parts_used_table (work_id, part_id)
			  VALUES ('$work_id','$part_id')";

if($con->query($sql_query) === TRUE) {
	echo "Part used successfully added.";
}
else {
	echo "Error with adding part used. Please Try again.";
}

mysqli_close($con);

?>