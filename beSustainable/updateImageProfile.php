<?PHP 
include 'conn.php';

$json=array();

	
	$idconsumer = $_POST["idconsumer"];
	$img = $_POST["img"];
	
	// Create connection
	$conn = mysqli_connect($hostname_localhost,$username_localhost,$password_localhost,$database_localhost);
	// Check connection
	if ($conn->connect_error) {
	    die("Connection failed: " . $conn->connect_error);
	} 

	$sql = "update consumer SET img = ".$img." ".
			"where idconsumer = ".$idconsumer;

	if (mysqli_query($conn, $sql)) {
    	//echo "Record updated successfully";
    	$result['message'] =  'Record updated successfully';
    	$json['request'][] = $result;
	} else {
	    //echo "Error updating record: " . mysqli_error($conn);
    	$result['message'] =  'Error updating record.'/*. mysqli_error($conn)*/;
    	$json['request'][] = $result;
	}

	mysqli_close($conn);
	echo json_encode($json);

?>