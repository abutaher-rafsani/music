<?php
/**
 * KLIQ Database Configuration (cPanel Production Ready)
 * Connects strictly to:
 * 1. ttpidapp_kliq_app (Main Kliq Application DB)
 * 2. ttpidapp_App (Bangladesh Address & Region DB)
 */

class Database {
    private static $mainPdo = null;
    private static $addressPdo = null;

    // Main Kliq Database: ttpidapp_kliq_app
    public static function main() {
        if (self::$mainPdo === null) {
            $host = getenv('DB_HOST') ?: 'localhost';
            $dbname = getenv('DB_NAME') ?: 'ttpidapp_kliq_app';
            $username = getenv('DB_USER') ?: 'ttpidapp_user';
            $password = getenv('DB_PASS') ?: 'secure_password';

            try {
                self::$mainPdo = new PDO("mysql:host=$host;dbname=$dbname;charset=utf8mb4", $username, $password, [
                    PDO::ATTR_ERRMODE => PDO::ERRMODE_EXCEPTION,
                    PDO::ATTR_DEFAULT_FETCH_MODE => PDO::FETCH_ASSOC,
                    PDO::ATTR_TIMEOUT => 10
                ]);
            } catch (PDOException $e) {
                // Return controlled error json if API, or die gracefully
                header('Content-Type: application/json; charset=utf-8');
                http_response_code(500);
                echo json_encode([
                    'success' => false,
                    'message' => 'Database connection error (ttpidapp_kliq_app): ' . $e->getMessage()
                ]);
                exit;
            }
        }
        return self::$mainPdo;
    }

    // Address Database: ttpidapp_App
    public static function address() {
        if (self::$addressPdo === null) {
            $host = getenv('DB_HOST') ?: 'localhost';
            $dbname = getenv('DB_ADDRESS_NAME') ?: 'ttpidapp_App';
            $username = getenv('DB_USER') ?: 'ttpidapp_user';
            $password = getenv('DB_PASS') ?: 'secure_password';

            try {
                self::$addressPdo = new PDO("mysql:host=$host;dbname=$dbname;charset=utf8mb4", $username, $password, [
                    PDO::ATTR_ERRMODE => PDO::ERRMODE_EXCEPTION,
                    PDO::ATTR_DEFAULT_FETCH_MODE => PDO::FETCH_ASSOC,
                    PDO::ATTR_TIMEOUT => 10
                ]);
            } catch (PDOException $e) {
                header('Content-Type: application/json; charset=utf-8');
                http_response_code(500);
                echo json_encode([
                    'success' => false,
                    'message' => 'Address database connection error (ttpidapp_App): ' . $e->getMessage()
                ]);
                exit;
            }
        }
        return self::$addressPdo;
    }
}
