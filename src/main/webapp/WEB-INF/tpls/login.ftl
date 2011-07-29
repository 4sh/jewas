<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">

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

    <title> Page de connexion </title>
</head>
<body>
    Bienvenue ! <br/>

    <div id="msg" style="color: red"></div>

    <form id="loginForm">
        <p> Identifiant <br/>
            <input name="login" type="text"/>
        </p>
        <p> Mot de passe <br/>
            <input name="password" type="password"/>
        </p>

        <button type="button" onclick="validate()"> Valider </button>
    </form>
</body>
</html>