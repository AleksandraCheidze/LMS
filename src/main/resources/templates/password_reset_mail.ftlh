<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Password Reset Email</title>
    <style>
        body {
            font-family: 'Helvetica Neue', Helvetica, Arial, sans-serif;
            margin: 0;
            padding: 20px;
            background-color: #f4f4f4;
            color: #333;
        }

        .content {
            max-width: 600px;
            margin: auto;
            background: white;
            padding: 20px;
            border-radius: 8px;
            box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
        }

        h2 {
            color: #007bff;
        }

        a.button {
            display: inline-block;
            background-color: #007bff;
            color: white;
            padding: 10px 20px;
            text-decoration: none;
            border-radius: 5px;
            transition: background-color 0.3s ease;
        }

        a.button:hover {
            background-color: #0056b3;
        }

        p {
            margin: 16px 0;
        }
    </style>
</head>
<body>
<div class="content">
    <h2>Password Reset</h2>
    <br>
    <p>You have requested to reset your password. Please click on the link below to reset your password:</p>
    <a href="${baseUrl}/#/auth/confirm/${confirmationUUID}" class="button">Reset Password</a>
    <p>If you didn't request this password reset, you can safely ignore this email.</p>
</div>
</body>
</html>
