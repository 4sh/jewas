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

        // shift arguments if data argument was omitted
        if ( jQuery.isFunction( data ) ) {
            type = type || callback;
            callback = data;
        } else if(typeof data == 'object') {
            // If data is : { "foo":"bar", "array":["val1", "val2"] }
            // JQuery will call url with content following content parameters :
            // foo=bar&array[]=val1&array[]=val2
            // instead of :
            // foo=bar&array=val1&array=val2
            console.log("WARNING: passing an object to $."+method+"() as data attribute could be" +
                " badly handled by jquery when it comes to arrayed values. Prefer using $(form).serialize()" +
                " instead of $(form).serializeToObject() in such a case !");
        } else if(typeof data == 'string') {
            // Don't do anything
        }

        // Adding the httpMethod tunelling via a query parameter
        if(url.indexOf("?") != -1){
            url += "&";
        } else {
            url += "?";
        }
        url += "__httpMethod="+method;

        return jQuery.ajax({
            type: "post",
            url: url,
            data: data,
            success: callback,
            dataType: type
        });
    }
});