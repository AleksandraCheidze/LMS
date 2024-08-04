<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>AIT-TR LMS Email Confirmation</title>
    <style>
        body {
            font-family: 'Helvetica Neue', Helvetica, Arial, sans-serif;
            margin: 0;
            padding: 20px;
            background-color: #f4f4f4;
            color: #333;
        }

        .content {
            max-width: 800px;
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
            margin-top: 10px;
            margin-bottom: 10px;
            text-decoration: none;
            border-radius: 5px;
            transition: background-color 0.3s ease;
            font-weight: bold;
        }

        a.button:hover {
            background-color: #0056b3;
        }

        p {
            margin: 16px 6px;
        }

        .highlight {
            color: #007bff;
            font-weight: bold;
        }
    </style>
</head>
<body>
<div class="content">
    <h2>Hello ${firstName} ${lastName},</h2>

    <p>Welcome to AIT-TR! We are thrilled that you have decided to join our community of future IT specialists.</p>

    <p>To complete the registration process and start your journey into the world of technology, please follow the link
        below to set your unique password. This will grant you access to our Learning Management System (LMS).</p>
    <p>Your email address <span class="highlight">${email}</span>, provided during registration, will be used as your
        login ID for the system.</p>

    <a href="${baseUrl}/#/auth/confirm/${confirmationUUID}" class="button">Set Password and Activate Account</a>

    <p>This link will remain active until: ${expiryDate}. If you did not register for AIT-TR, please disregard this message.</p>

    <p>Should you have any questions or need assistance during your learning process, our support team is always here to
        help.</p>

    <p>We wish you an engaging learning experience and success on your path to becoming an IT specialist!</p>

    <p>Best regards,</p>
    <p>The AIT-TR Team</p>

    <br>
    <hr>
    <br>

    <h2>Здравствуйте, ${firstName} ${lastName},</h2>

    <p>Добро пожаловать в AIT-TR! Мы очень рады, что вы решили присоединиться к нашему сообществу будущих
        IT-специалистов.</p>

    <p>Чтобы завершить процесс регистрации и начать свой путь в мире технологий, пожалуйста, перейдите по ссылке ниже и
        установите свой уникальный пароль. Это даст вам доступ к нашей системе управления обучением (LMS).</p>

    <p>Ваш email адрес <span class="highlight">${email}</span>, указанный при регистрации, будет использоваться как ваш
        логин для входа в систему.</p>

    <a href="${baseUrl}/#/auth/confirm/${confirmationUUID}" class="button">Установить пароль и активировать аккаунт</a>

    <p>Эта ссылка будет активна до: ${expiryDate}. Если вы не регистрировались в AIT-TR, пожалуйста, проигнорируйте это
        сообщение.</p>

    <p>Если у вас возникнут вопросы или вам потребуется помощь в процессе обучения, наша команда поддержки всегда готова
        помочь.</p>
    <p>Желаем вам увлекательного обучения и успехов на вашем пути к становлению IT-специалистом!</p>

    <p>С наилучшими пожеланиями,</p>
    <p>Команда AIT-TR</p>
</div>
</body>
</html>
