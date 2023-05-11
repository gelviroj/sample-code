<?php
require "init.php";

$part_used_id = $_POST["part_used_id"];

$sql_query = "DELETE FROM parts_used_table 
			  WHERE part_used_id = '$part_used_id'";

if($con->query($sql_query) === TRUE) {
	 	echo "Part used successfully deleted.";
}
else {
	 	echo "Error with deleting work. Please Try again.";
}

mysqli_close($con);
?>