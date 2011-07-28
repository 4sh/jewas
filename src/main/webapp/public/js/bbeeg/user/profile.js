

function loadUserInformations() {
    $.getJSON(
        '/user/infos',
        function success(data) {
            var infos = jQuery.parseJSON(data)

            $("#lastConnectionDate").append("Dernière date de connexion : " + infos.lastConnectionDate);
            $("#userName").val(infos.name);
            $("#userSurname").val(infos.surname);
            $("#userEmail").val(infos.email);
        }
    );
}

$(
    function() {
        loadUserInformations();
    }
);