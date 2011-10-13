<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">

    <link rel="stylesheet" href="/public/css/bbeeg/bbeeg.css"/>

    <script type="application/javascript"
            src="http://ajax.googleapis.com/ajax/libs/jquery/1.6.2/jquery.min.js"></script>

    <script type="application/javascript">
        function cleanMessageError() {
            $('#msg').text('');
        }

        function validate() {
            $.ajax(
                {
                    type: "GET",
                    url: '/connection',
                    dataType: 'json',
                    data: $('#loginForm').serialize(),
                    success: function success(response) {
                    if (response.status === "SUCCESS") {
                        window.location = response.object.url;
                    } else {
                        $('#msg').text(response.object.msg);
                    }
                    setTimeout(cleanMessageError, 5000);
                    }
                }
            );
        }
    </script>
    <link rel="shortcut icon" type="image/x-icon" href="/public/images/bbeeg/favicon.ico"/>
    <title> Page de connexion </title>
</head>
<body>
    <div class="container">
        <div class="grid">
            <div class="center_login">
                <div class="login_panel">
                    <h4>Bienvenue sur BBEEG !</h4>

                    <div id="msg" style="color: #ff2d00" class="bottom_space "></div>

                    <form id="loginForm">
                        <div class="line_login">
                            <div class="style_label label_login">Identifiant :</div>
                            <div class="input_login">
                                <input name="login" type="text" style="width: 215px;"/>
                            </div>
                        </div>
                        <br />
                        <div class="line_login">
                            <div class="style_label label_login bottom_space">Mot de passe : </div>
                            <div class="input_login">
                                <input name="password" type="password" style="width: 215px;"/>
                            </div>
                        </div>
                        <div class="button_login">
                            <button type="button" onclick="validate()"> Connexion </button>
                        </div>
                    </form>
                </div>
            </div>
        </div>
    </div>
</body>
</html>