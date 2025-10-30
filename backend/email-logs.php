<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Email Logs - PMSSS Admin</title>
    <style>
        body { font-family: Arial, sans-serif; padding: 20px; background: #f5f5f5; }
        .container { max-width: 1200px; margin: 0 auto; background: white; padding: 30px; border-radius: 10px; box-shadow: 0 2px 10px rgba(0,0,0,0.1); }
        h1 { color: #667eea; }
        .log-entry { background: #f8f9fa; padding: 15px; margin: 10px 0; border-left: 4px solid #667eea; border-radius: 4px; }
        .log-time { color: #666; font-size: 14px; }
        .refresh-btn { background: #667eea; color: white; border: none; padding: 10px 20px; border-radius: 5px; cursor: pointer; margin-bottom: 20px; }
        .refresh-btn:hover { background: #5568d3; }
        pre { background: #f4f4f4; padding: 10px; border-radius: 4px; overflow-x: auto; }
    </style>
</head>
<body>
    <div class="container">
        <h1>📧 Email Logs</h1>
        <button class="refresh-btn" onclick="location.reload()">Refresh Logs</button>
        
        <div id="logs">
            <?php
            $logFile = __DIR__ . '/php-backend/logs/email-log.txt';
            if (file_exists($logFile)) {
                $logs = file_get_contents($logFile);
                if ($logs) {
                    echo '<pre>' . htmlspecialchars($logs) . '</pre>';
                } else {
                    echo '<p>No emails logged yet.</p>';
                }
            } else {
                echo '<p>Email log file not found. Emails will be logged here once sent.</p>';
            }
            ?>
        </div>
    </div>
</body>
</html>
