<?php
/**
 * Create/Update Admin User
 * This script creates or updates the admin user with correct password
 */

require_once 'config/database.php';

// Admin credentials
$username = 'admin';
$password = 'admin123'; // Default password
$name = 'System Administrator';
$email = 'admin@pmsss.gov.in';
$role = 'super_admin';

try {
    $database = new Database();
    $db = $database->connect();

    // Generate password hash
    $passwordHash = password_hash($password, PASSWORD_BCRYPT);

    // Check if admin exists
    $stmt = $db->prepare("SELECT id FROM admins WHERE username = ?");
    $stmt->execute([$username]);
    $existing = $stmt->fetch(PDO::FETCH_ASSOC);

    if ($existing) {
        // Update existing admin
        $updateStmt = $db->prepare("
            UPDATE admins 
            SET password_hash = ?, name = ?, email = ?, role = ?, is_active = 1
            WHERE username = ?
        ");
        $updateStmt->execute([$passwordHash, $name, $email, $role, $username]);
        echo "✓ Admin user updated successfully!\n\n";
    } else {
        // Create new admin
        $insertStmt = $db->prepare("
            INSERT INTO admins (username, password_hash, name, email, role, is_active, created_at)
            VALUES (?, ?, ?, ?, ?, 1, NOW())
        ");
        $insertStmt->execute([$username, $passwordHash, $name, $email, $role]);
        echo "✓ Admin user created successfully!\n\n";
    }

    echo "========================================\n";
    echo "Admin Login Credentials:\n";
    echo "========================================\n";
    echo "Username: $username\n";
    echo "Password: $password\n";
    echo "Email: $email\n";
    echo "Role: $role\n";
    echo "========================================\n";
    echo "\n⚠️  IMPORTANT: Change this password after first login!\n\n";

} catch (PDOException $e) {
    echo "Error: " . $e->getMessage() . "\n";
}
