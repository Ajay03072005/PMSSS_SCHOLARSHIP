<?php
header('Content-Type: application/json');
header('Access-Control-Allow-Origin: *');

echo json_encode(['status' => 'PHP is working']);

// Test database connection
try {
    $conn = new PDO(
        "mysql:host=localhost;port=3307;dbname=pmsss_scholarship",
        "root",
        "",
        array(
            PDO::ATTR_ERRMODE => PDO::ERRMODE_EXCEPTION,
            PDO::ATTR_DEFAULT_FETCH_MODE => PDO::FETCH_ASSOC
        )
    );
    
    echo "\n\nDatabase Connection: SUCCESS\n";
    
    // Test query
    $stmt = $conn->query("SELECT COUNT(*) as count FROM users");
    $result = $stmt->fetch();
    
    echo "Users table accessible: YES\n";
    echo "Number of users: " . $result['count'] . "\n";
    
    // Show database info
    $stmt = $conn->query("SELECT DATABASE() as db, USER() as user");
    $info = $stmt->fetch();
    echo "Connected to database: " . $info['db'] . "\n";
    echo "Connected as user: " . $info['user'] . "\n";
    
} catch(PDOException $e) {
    echo "\n\nDatabase Connection: FAILED\n";
    echo "Error: " . $e->getMessage() . "\n";
    echo "Error Code: " . $e->getCode() . "\n";
}
?>
