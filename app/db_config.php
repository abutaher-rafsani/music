<?php
/**
 * Google Cloud & MySQL Database Configuration
 * Configured for Google Cloud SQL (MySQL) with secure environment variables and SQLite fallback.
 */

$host = getenv('DB_HOST') ?: 'localhost';
$dbname = getenv('DB_NAME') ?: 'kliqbd_db';
$username = getenv('DB_USER') ?: 'root';
$password = getenv('DB_PASS') ?: '';
$socket = getenv('DB_SOCKET') ?: ''; // e.g., /cloudsql/my-project:us-central1:my-instance

try {
    if (!empty($socket)) {
        $dsn = "mysql:unix_socket=$socket;dbname=$dbname;charset=utf8mb4";
    } else {
        $dsn = "mysql:host=$host;dbname=$dbname;charset=utf8mb4";
    }

    $pdo = new PDO($dsn, $username, $password, [
        PDO::ATTR_ERRMODE => PDO::ERRMODE_EXCEPTION,
        PDO::ATTR_DEFAULT_FETCH_MODE => PDO::FETCH_ASSOC,
        PDO::ATTR_TIMEOUT => 5
    ]);
} catch (PDOException $e) {
    // Fallback for isolated runtime sandbox environments
    $dbFile = __DIR__ . '/kliq_platform.sqlite';
    $pdo = new PDO('sqlite:' . $dbFile, null, null, [
        PDO::ATTR_ERRMODE => PDO::ERRMODE_EXCEPTION,
        PDO::ATTR_DEFAULT_FETCH_MODE => PDO::FETCH_ASSOC
    ]);
}

return $pdo;
