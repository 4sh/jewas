<#include "../common/mainTemplate.ftl">

<#if compressedJS == "true">
    <#assign chosenJS = "/public/js/chosen/chosen.jquery.min.js">
    <#else>
        <#assign chosenJS = "/public/js/chosen/chosen.jquery.js">
</#if>

<@mainTemplate title="Ecran d'accueil" selectedMenuItem="search" scripts=[chosenJS, "/public/js/bbeeg/search/search.js"] stylesheets=["/public/css/chosen/chosen.css"]>

<script id="authorItemTemplate" type="text/html">
    <option value="{{= id}}"> {{= name}} </option>
</script>

<script>
    function loadAuthors() {
        $.getJSON(
            '/content/author/all',
            function success(data) {
                var container = $("#adSearchAuthor");
                container.children().remove();
                $("#authorItemTemplate").tmpl(data).appendTo(container);
            }
        );
    }

    $(function() {
        $("#searchComponent").accordion({
            autoHeight: false,
            navigation: true
        });
        //$("#adSearchDate").datepicker();
        var dates = $( "#from, #to" ).datepicker({
			defaultDate: "",
            dateFormat: "dd-mm-yy",
			changeMonth: true,
			numberOfMonths: 1,
			onSelect: function( selectedDate ) {
				var option = this.id == "from" ? "minDate" : "maxDate",
					instance = $( this ).data( "datepicker" ),
					date = $.datepicker.parseDate(
						instance.settings.dateFormat ||
						$.datepicker._defaults.dateFormat,
						selectedDate, instance.settings );
				dates.not( this ).datepicker( "option", option, date );
			}
		});

        $("#adSearchType").chosen();
        SearchQuery.bindSearchToForm(
                new SearchQuery.SearchContext().targetForm($("#simpleSearchForm"))
                        .resultElement($("#searchResultsComponent"))
                        .globalResultTemplate($("#contentResult"))
                        .resultElementTemplate($("#contentLineResult"))
                        .selectorForClickableOfSearchNext("#searchNext")
                        .selectorWhereResultsWillBeAppended("#contentResults")
        );

        loadAuthors();
    });
</script>

<div id="searchComponent">
    <h3><a href="#">Recherche simple</a></h3>

    <div>
        <form action="/content/search" id="simpleSearchForm">
            <label for="simpleSearchQuery">Recherche</label> : <input id="simpleSearchQuery" type="text" name="query"/>
            <input id="simpleSearchButton" type="submit" value="Rechercher"/>
        </form>
    </div>
    <h3><a href="#">Recherche avancée</a></h3>

    <div>
        <form action="/content/advancedSearch" id="advancedSearchForm">
            <div style="display:block;">
                <!--<label for="adSearchDate">Date de création :</label>-->
                <!--<input type="text" id="adSearchDate" name="date"/>-->
                <label for="from">Date de création : Entre</label>
                <input type="text" id="from" name="from"/>
                <label for="to">et</label>
                <input type="text" id="to" name="to"/>
            </div>

            <div style="width: 600px; display:block;">
                <div style="float:left"><label for="adSearchType">Types de contenu</label> :</div>
                <div style="float:left">
                    <select id="adSearchType" class="chzn-select side-by-side clearfix" multiple style="width: 350px">
                        <option value="doc">Document</option>
                        <option value="img">Images</option>
                        <option value="aud">Audio</option>
                        <option value="txt">Texte simple</option>
                        <option value="eeg">EEG Vidéo</option>
                        <option value="vid">Vidéo</option>
                    </select>
                </div>
            </div>
            <div style="display:block; clear: both;"><label for="adSearchAuthor">Auteur</label> : <select
                    id="adSearchAuthor"></select></div>
            <input id="advancedSearchButton" type="submit" value="Rechercher"/>
        </form>
    </div>
</div>

<div id="searchResultsComponent" style="margin-top: 10px;" class="ui-widget">
    No results found yet !
</div>
<script id="contentResult" type="text/x-jquery-tmpl">
    <h3 style="color: #eb8f00;" class="ui-widget">Résultats de la recherche</h3>

    <div id="contentResults">
        {{tmpl(results) "#contentLineResult"}}
    </div>
    <button id="searchNext">Résultats suivants</button>
</script>
<script id="contentLineResult" type="text/x-jquery-tmpl">
    <!-- Yeah I know css direct in html is weird... will externalize this in css after sandboxing -->
    <div class="contentResult" style="margin: 10px;">
        <div style="width: 400px; float:left;" class="ui-widget ui-helper-reset">{{= title}}</div>
        <div style="width: 90px; float:left;" class="ui-widget ui-helper-reset">{{= author}}</div>
        <div style="width: 200px; float:left;" class="ui-widget ui-helper-reset">{{= creationDate}}</div>
        <div style="clear: both; width: 800px; font-style: italic;" class="ui-widget ui-helper-reset">{{= description}}</div>
    </div>
</script>

</@mainTemplate>
