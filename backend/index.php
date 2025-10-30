<?php
/**
 * Router for PMSSS Scholarship Backend
 * Handles routing and provides API information
 */

header('Content-Type: application/json');
header('Access-Control-Allow-Origin: *');
header('Access-Control-Allow-Methods: GET, POST, PUT, DELETE, OPTIONS');
header('Access-Control-Allow-Headers: Content-Type, Authorization');

// Handle preflight requests
if ($_SERVER['REQUEST_METHOD'] === 'OPTIONS') {
    http_response_code(200);
    exit;
}

$requestUri = $_SERVER['REQUEST_URI'];

// If accessing root, show API info
if ($requestUri === '/' || $requestUri === '/index.php') {
    echo json_encode([
        'status' => 'success',
        'message' => 'PMSSS Scholarship API Server',
        'version' => '1.0.0',
        'endpoints' => [
            'Authentication' => [
                'POST /api/auth.php?action=register' => 'Register new user',
                'POST /api/auth.php?action=login' => 'User login',
                'GET /api/auth.php?action=me' => 'Get current user (requires JWT)'
            ],
            'Applications' => [
                'POST /api/applications.php' => 'Submit application (requires JWT)',
                'GET /api/applications.php?action=my' => 'Get user applications (requires JWT)',
                'GET /api/applications.php?action=view&id=XXX' => 'View application (requires JWT)',
                'GET /api/applications.php?action=track&id=XXX' => 'Track application status'
            ]
        ],
        'server_time' => date('Y-m-d H:i:s'),
        'documentation' => 'See BACKEND-README.md for details'
    ], JSON_PRETTY_PRINT);
    exit;
}

// For other routes, show 404
http_response_code(404);
echo json_encode([
    'status' => 'error',
    'message' => 'Endpoint not found',
    'requested_uri' => $requestUri,
    'available_endpoints' => [
        '/api/auth.php',
        '/api/applications.php'
    ]
], JSON_PRETTY_PRINT);
