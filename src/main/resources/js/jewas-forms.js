(function($){

    //Attach this new method to jQuery
    $.fn.extend({

        serializeToObject: function() {
            var arrayRepresentation = $(this).serializeArray();
            var objectRepresentation = {};

            for(i in arrayRepresentation){
                if(typeof objectRepresentation[arrayRepresentation[i].name] == 'undefined'){
                    objectRepresentation[arrayRepresentation[i].name] = arrayRepresentation[i].value;
                } else if(typeof objectRepresentation[arrayRepresentation[i].name] == 'array'){
                    objectRepresentation[arrayRepresentation[i].name][objectRepresentation[arrayRepresentation[i].name].length] = arrayRepresentation[i].value;
                } else {
                    objectRepresentation[arrayRepresentation[i].name] = [ objectRepresentation[arrayRepresentation[i].name], arrayRepresentation[i].value];
                }
            }
            return objectRepresentation;
        }
    });
//pass jQuery to the function,
//So that we will able to use any valid Javascript variable name
//to replace "$" SIGN. But, we'll stick to $
})(jQuery);

jQuery.each([ "put", "delete" ], function(i, method){
    jQuery[ method ] = function(url, data, callback, type){
        // Defining a special httpMethod parameter
        // This httpMethod parameter will be used by jewas to change the POST method by a PUT/DELETE one
        var magicHttpMethod = { "__httpMethod": method };

        // shift arguments if data argument was omitted
        if ( jQuery.isFunction( data ) ) {
            type = type || callback;
            callback = data;
            data = magicHttpMethod;
        } else {
            data = $.extend(data, magicHttpMethod);
        }

        return jQuery.ajax({
            type: "post",
            url: url,
            data: data,
            success: callback,
            dataType: type
        });
    }
});