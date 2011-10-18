function loadUserInformations() {
    $.getJSON(
        '/user/infos/',
        function success(infos) {
            $("#lastConnectionDate").append("Dernière date de connexion : " + infos.lastConnectionDate);
            $("#login").val(infos.login);
            $("#lastName").val(infos.lastName);
            $("#firstName").val(infos.firstName);
            $("#email").val(infos.email);
        }
    );
}

$(
    function() {
        $("#successDialog, #failureDialog").dialog({
            autoOpen: false,
            modal: false,
            show: 'drop',
            hide: 'drop'
        });

        loadUserInformations();

        // User profile form submission handler
        $("#userProfileForm").submit(function() {

            var dataToSend = {
                login : $('#login').val(),
                firstName : $('#firstName').val(),
                lastName : $('#lastName').val(),
                email : $('#email').val()
            };

            var login = $('#login').val();
            $.put('/user/infos/' + login,
                dataToSend,
                function(data) {
                    if (data.success) {
                        $("#successDialog").dialog('open');
                        setTimeout(function() {
                            $("#successDialog").dialog('close');
                        }, 3000);
                    } else {
                        $("#failureDialog").dialog('open');
                        setTimeout(function() {
                            $("#failureDialog").dialog('close');
                        }, 3000);
                    }
                }
            );
            return false;
        });

    }
);