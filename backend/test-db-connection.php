<?php
// Test Database Connection
// Run this from command line: php test-db-connection.php

echo "🔌 Testing MySQL Database Connection...\n";
echo "=====================================\n\n";

// Load database config
require_once 'config/database.php';

echo "Configuration:\n";
echo "  Host: " . DB_HOST . "\n";
echo "  User: " . DB_USER . "\n";
echo "  Pass: " . str_repeat('*', strlen(DB_PASS)) . "\n";
echo "  Database: " . DB_NAME . "\n\n";

// Test connection
echo "Attempting connection...\n";

try {
    $database = new Database();
    $conn = $database->connect();
    
    if ($conn) {
        echo "✅ SUCCESS! Connected to MySQL server.\n\n";
        
        // Test if database exists
        echo "Checking database...\n";
        $stmt = $conn->query("SELECT DATABASE()");
        $dbName = $stmt->fetchColumn();
        
        if ($dbName) {
            echo "✅ Database '$dbName' is active!\n\n";
            
            // Check tables
            echo "Checking tables...\n";
            $stmt = $conn->query("SHOW TABLES");
            $tables = $stmt->fetchAll(PDO::FETCH_COLUMN);
            
            if (count($tables) > 0) {
                echo "✅ Found " . count($tables) . " tables:\n";
                foreach ($tables as $table) {
                    echo "   - $table\n";
                    
                    // Count records
                    $countStmt = $conn->query("SELECT COUNT(*) FROM $table");
                    $count = $countStmt->fetchColumn();
                    echo "     (Records: $count)\n";
                }
                echo "\n";
                echo "🎉 Database is fully set up and ready!\n";
                echo "You can now start using the portal.\n";
            } else {
                echo "⚠️  WARNING: No tables found!\n";
                echo "You need to import the database schema.\n\n";
                echo "Run this command:\n";
                echo "mysql -u root -p pmsss_scholarship < database.sql\n";
            }
        }
        
    } else {
        echo "❌ FAILED: Could not connect to database.\n";
        echo "Check your credentials in config/database.php\n";
    }
    
} catch (PDOException $e) {
    echo "❌ ERROR: " . $e->getMessage() . "\n\n";
    
    if (strpos($e->getMessage(), 'Access denied') !== false) {
        echo "💡 Solution: Check your password in config/database.php\n";
    } else if (strpos($e->getMessage(), 'Unknown database') !== false) {
        echo "💡 Solution: Create the database first:\n";
        echo "   mysql -u root -p\n";
        echo "   CREATE DATABASE pmsss_scholarship;\n";
        echo "   USE pmsss_scholarship;\n";
        echo "   SOURCE database.sql;\n";
    } else if (strpos($e->getMessage(), 'Connection refused') !== false) {
        echo "💡 Solution: MySQL service is not running.\n";
        echo "   Start it from Services or run: net start MySQL80\n";
    }
}

echo "\n=====================================\n";
?>
