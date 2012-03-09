<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <!-- Enable Chrome Frame -->
    <meta http-equiv="X-UA-Compatible" content="chrome=1">

    <link rel="stylesheet" href="/public/css/bbeeg/bbeeg.css"/>
    <link rel="stylesheet" href="/public/css/bbeeg/login.css"/>

    <script type="application/javascript"
            src="http://ajax.googleapis.com/ajax/libs/jquery/1.6.2/jquery.min.js"></script>

    <script type="application/javascript">
        function cleanMessageError() {
            $('#msg').text('');
        }

        function validate() {
            // Show spinner
            $(".spinner").css('display', 'inline');
            $.ajax(
                {
                    type:"GET",
                    url:'/connection',
                    dataType:'json',
                    data:$('#loginForm').serialize(),
                    success:function success(response) {
                        if (response.status === "SUCCESS") {
                            window.location = response.object.url;
                        } else {
                            $(".spinner").css('display', 'none');
                            $('#msg').text(response.object.msg);
                        }
                        setTimeout(cleanMessageError, 5000);
                    }
                }
            );

        }

        $(function() {
            $("#connectionButton")
                .click(function() {
                    validate();
                });
            $("body").keydown(function(event) {
                if (event.which == 13) {
                    event.preventDefault();
                    validate();
                }
            });
        });
    </script>
    <link rel="shortcut icon" type="image/x-icon" href="/public/images/bbeeg/favicon.ico"/>
    <title> Page de connexion </title>
</head>
<body>
<!--[if IE]>
    <script type="text/javascript"
     src="http://ajax.googleapis.com/ajax/libs/chrome-frame/1/CFInstall.min.js"></script>

    <style>
     .chromeFrameInstallDefaultStyle {
       width: 100%; /* default is 800px */
       border: 5px solid blue;
     }
    </style>

    <div id="prompt">
     <!-- if IE without GCF, prompt goes here -->
</div>

<script>
    // The conditional ensures that this code will only execute in IE,
    // Therefore we can use the IE-specific attachEvent without worry
    window.attachEvent("onload", function() {
        CFInstall.check({
            mode: "inline", // the default
            node: "prompt"
        });
    });
</script>
<![endif]-->
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
                            <button id="connectionButton" type="button"><img src="/public/images/ajax/indicator.gif" class="spinner" /> Connexion </button>
                        </div>
                    </form>
                </div>
            </div>
        </div>
    </div>
</body>
</html>