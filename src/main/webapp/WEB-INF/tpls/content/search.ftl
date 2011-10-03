<#include "../common/mainTemplate.ftl">

<#if searchMode == 0>
<#assign selectMenu = "search">
<#else>
<#assign selectMenu = "administration">
</#if>

<@mainTemplate
    title="Ecran d'accueil"
    selectedMenuItem=selectMenu
    scripts=["/public/js/bbeeg/search/search.js", "/public/js/bbeeg/common/widgets/chainedSelect.js"]
    stylesheets=["/public/css/bbeeg/search.css"]
    useChosen=true>
<script>
    function updateStatus(containerId, contentId, status, comment, callback) {
        $.post('/content/' + contentId + '/status/' + status,
            "comment="+comment,
            function (data) {
                callback();
            }
        );
    }

    <#if searchMode == 1>
    function sendUpdateStatus(containerId, contentId, status, comment) {
        updateStatus(containerId, contentId, status, comment,
                function () {
                    alert('Le contenu a bien été mis à jour');
                    if (status === 'DELETED') {
                        $("#" + containerId).remove();
                    } else {
                        $("#" + containerId + ' .publish-button')[0].disabled = true;
                        $("#" + containerId + ' .content-result-status').text(status);
                    }
                }
        );
    }

    function publishContent(containerId, contentId, status) {
        var newStatus = null;

        if (status === "DRAFT") {
            newStatus = 'TO_BE_VALIDATED';
        } else {
            $("#updateStatusImpossible").dialog({show: 'slide', hide: 'slide'});
        }

        if (newStatus !== null) {
            sendUpdateStatus(containerId, contentId, newStatus, '');
        }
    }

    function editContent(containerId, contentId, status) {
        var newStatus = null;

        if (status === 'DRAFT') {
            window.location = "/content/" + contentId + "/edit.html";
            // Do not change the status just save the changes

        } else if (status === 'VALIDATED') {
            // if rejected duplicate the content and set the new version to draft
        } else if (status === 'REJECTED') {
            // if rejected duplicate the content and set the new version to draft
        } else {
            // edit should not be possible

        }

        if (newStatus !== null) {
            sendUpdateStatus(containerId, contentId, newStatus, '');
        }
    }

    function isContentEditable(contentStatus) {
        if (contentStatus === 'DRAFT' || contentStatus === 'REJECTED' || contentStatus === 'VALIDATED') {
            return true;
        }
        return false;
    }



    function deleteContent(containerId, contentId, status) {
        var newStatus = null;

        if (status === "VALIDATED") {
            newStatus = 'TO_BE_DELETED';
        } else {
            newStatus = 'DELETED';
        }

        if (newStatus !== null) {
            sendUpdateStatus(containerId, contentId, newStatus, '');
        }
    }
    </#if>

    <#if searchMode == 2>
        function sendUpdateStatus(containerId, contentId, status, comment) {
            updateStatus(containerId, contentId, status, comment,
                    function () {
                        $("#" + containerId).remove();
                    }
            );
        }

        function acceptContent(containerId, contentId, status) {
            var newStatus = null;

            if (status === "TO_BE_VALIDATED") {
                newStatus = 'VALIDATED';
            } else {
                if (status === "TO_BE_DELETED") {
                    newStatus = 'DELETED';
                }
                else {
                    $("#updateStatusImpossible").dialog({show: 'slide', hide: 'slide'});
                }
            }

            if (newStatus !== null) {
                sendUpdateStatus(containerId, contentId, newStatus, '');
            }
        }


        function rejectContentCallback(containerId, contentId, status, comment) {
            var newStatus = null;

            if (status === "TO_BE_VALIDATED") {
                newStatus = 'REJECTED';
            } else {
                if (status === "TO_BE_DELETED") {
                    newStatus = 'VALIDATED';
                }
                else {
                    alert('Le contenu ne peut pas être mis à jour car son status ne le permet pas.');
                }
            }

            if (newStatus !== null) {
                sendUpdateStatus(containerId, contentId, newStatus, comment);
            }
        }

        function rejectContent(containerId, contentId, status) {
            $("#rejectReasonDialog").dialog(
                    {   show: 'slide',
                        hide: 'slide',
                        buttons: [
                        {
                            text: "Annuler",
                            click: function() { $(this).dialog("close"); }
                        },
                        {
                            text: "Valider",
                            click: function() {
                                rejectContentCallback(containerId, contentId, status, $("#rejectReasonMsg").val());
                                console.log($("#rejectReasonMsg").val());
                                $(this).dialog("close");
                            }
                        }
                        ]
                    }
            );
        }
    </#if>

    <#if searchMode != 1>
        function loadAuthors() {
            $.getJSON(
                    '/content/author/all',
                    function success(data) {
                        var container = $("#adSearchAuthors");
                        container.children().remove();
                        $("#authorItemTemplate").tmpl(data).appendTo(container);
                        $("#adSearchAuthors").trigger("liszt:updated");
                    }
            );
        }
    </#if>

    function loadContentTypes() {
        $.getJSON(
            '/content/type/all',
            function success(data) {
                var container = $("#adSearchType");
                container.children().remove();
                $("#contentTypeItemTemplate").tmpl(data).appendTo(container);
                $("#adSearchType").trigger("liszt:updated");
            }
        );
    }

      function loadDomains() {
        $.getJSON(
            '/domain/all',
            function success(data) {
                var container = $("#adSearchDomains");
                container.children().remove();
                $("#domainItemTemplate").tmpl(data).appendTo(container);
                $("#adSearchDomains").trigger("liszt:updated");
            }
        );
    }

    $(function() {
        <#if searchMode != 1>
            loadAuthors();
        </#if>
        loadDomains();
        loadContentTypes();


        /* Switch between 'Simple search' and 'Advanced search' modes */
        $('.accordion h3').click(function() {
		    $('.accordion').toggle();
            return false;
	    }).next().hide();


        var dates = $( "#from, #to" ).datepicker({
			defaultDate: "",
            dateFormat: "yy-mm-dd",
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
        $("#adSearchCriterias").chosen();
        $("#adSearchDomains").chosen();
        $("#adSearchAuthors").chosen();
        SearchQuery.bindSearchToForm(
                new SearchQuery.SearchContext().targetForm($("#simpleSearchForm"))
                        .resultElement($("#searchResultsComponent"))
                        .globalResultTemplate($("#contentResult"))
                        .resultElementTemplate($("#contentLineResult"))
                        .selectorForClickableOfSearchNext("#searchNext")
                        .selectorWhereResultsWillBeAppended("#contentResults")
        );
        SearchQuery.bindSearchToForm(
                new SearchQuery.SearchContext().targetForm($("#advancedSearchForm"))
                        .resultElement($("#searchResultsComponent"))
                        .globalResultTemplate($("#contentResult"))
                        .resultElementTemplate($("#contentLineResult"))
                        .selectorForClickableOfSearchNext("#searchNext")
                        .selectorWhereResultsWillBeAppended("#contentResults")
        );
        $("#adSearchCriterias").chainedSelect(
                {
                   'ajaxUrlsPerDepth' : [
                      "/content/criterias?depth=0",
                      "/content/criterias?depth=1&parent={value}",
                      "/content/criterias?depth=2&parent={value}",
                      "/content/criterias?depth=3&parent={value}"
                    ],
                    'targetFieldForSelectedOption' : $("#criterias"),
                    'displaySelectionTarget' : $("#criteriasList"),
                    'templateForDisplaySelectionItem' : $("#criteriaSelectedItem"),
                    'selectorForClosingLinkInDisplaySelectionItemTemplate' : "a.search-choice-close",
                    'selectMenuContainer' : $("#searchCriteriaMenuContainer")
                }
        );

        // Enabling auto query when scrollbar is at the bottom of the window
        var SCROLLBAR_THRESHOLD = 100; // 100 pixels before the bottom of the screen, we consider the scrollbar is at the bottom
        $(window).scroll(function(args){
            if($("#searchResultsComponent #searchNext").length != 0 // Button "searchNext" exists and is not disabled
                      && $("#searchResultsComponent #searchNext").attr('disabled') != "disabled"){
                var scrollbarIsAtBottom = $(window).scrollTop() + SCROLLBAR_THRESHOLD >= $(document).height() - $(window).height();
                if(scrollbarIsAtBottom){
                    $("#searchNext").click();
                }
            }
        });
    });
</script>

<div id="searchComponent">
    <div class="accordion">
        <h3><a href="#">Recherche simple</a></h3>
    </div>
    <div class="accordion simple" style="display:none">
        <h3><a href="#">Recherche avancée</a></h3>
    </div>

    <div class="accordion">
         <form action="/content/search" id="simpleSearchForm">
            <label for="simpleSearchQuery">Recherche</label> : <input id="simpleSearchQuery" type="text" name="query" size="80" />
            <input id="simpleSearchButton" type="submit" value="Rechercher"/>

            <input name="searchMode" style="visibility: hidden;" value="${searchMode}"/>
         </form>
    </div>

    <div class="accordion advanced" style="display:none">
        <form action="/content/advancedSearch" id="advancedSearchForm">
            <label for="simpleSearchQuery">Recherche</label> : <input id="advancedSearchQuery" type="text" name="query" size="80" />

            <div class="criteria-line">
                <label for="from">Date de création : Entre</label>
                <input type="text" id="from" name="from" style="width: 175px;" />
                <label for="to">et</label>
                <input type="text" id="to" name="to" style="width: 175px;"/>
            </div>
            <div class="criteria-line">
                <div class="criteria-label"><label for="adSearchType">Types de contenu</label> :</div>
                <div class="criteria-field">
                    <select id="adSearchType" name="searchTypes" class="chzn-select side-by-side clearfix" multiple>
                    </select>
                </div>
            </div>
            <!--<div class="criteria-line">
                <div class="criteria-label"><label for="adSearchCriterias">Critères </label> :</div>
                <div class="criteria-field">
                    <input type="hidden" id="criterias" name="criterias" value="" />
                    <div class="chzn-container-multi criteria-field">
                        <ul class="chzn-choices" id="criteriasList" style="font-size: 13px;">
                        </ul>
                    </div>
                    <div id="searchCriteriaMenuContainer" class="criteria-field">
                        <select id="adSearchCriterias">
                        </select>
                    </div>
                </div>
            </div>-->
            <div class="criteria-line">
                <div class="criteria-label"><label for="adSearchDomains">Domaines</label> :</div>
                <div class="criteria-field">
                    <select id="adSearchDomains" name="domains" class="chzn-select side-by-side clearfix" multiple>
                    </select>
                </div>
            </div>
            <#if searchMode != 1>
                <div class="criteria-line">
                    <div class="criteria-label"><label for="adSearchAuthors">Auteur</label> :</div>
                    <div class="criteria-field">
                        <select id="adSearchAuthors" name="authors" class="chzn-select side-by-side clearfix" multiple>
                        </select>
                    </div>
                </div>
            </#if>
            <div class="criteria-line">
                <input id="advancedSearchButton" class="button" type="submit" value="Rechercher"/>
            </div>

            <input name="searchMode" style="visibility: hidden;" value="${searchMode}"/>
        </form>
    </div>
</div>

<div id="searchResultsComponent" class="search-results-component">
    Aucun résultat trouvé !
</div>
<script id="criteriaSelectedItem" type="text/x-jquery-tmpl">
    <li class="search-choice"><span>{{= label}}</span><a href="#" name="{{= value}}" class="search-choice-close"></a></li>
</script>
<script id="domainItemTemplate" type="text/x-jquery-tmpl">
    <option value="{{= id}}"> {{= label}} </option>
</script>
<script id="authorItemTemplate" type="text/x-jquery-tmpl">
   <option value="{{= id}}">{{= name}} </option>
</script>
<script id="contentTypeItemTemplate" type="text/x-jquery-tmpl">
    <option value="{{= id}}"> {{= title}} </option>
</script>
<script id="contentResult" type="text/x-jquery-tmpl">
    <h3>Résultats de la recherche</h3>

    <div id="contentResults">
        {{tmpl(results) "#contentLineResult"}}
    </div>
    <button id="searchNext"><img src="/public/images/ajax/indicator.gif" class="spinner" />Résultats suivants</button>
</script>
<script id="contentLineResult" type="text/x-jquery-tmpl">
    <div id="item-{{= id}}" class="content-result search_result content_type">
        <div class="tab_left"></div>
        <div class="tab_right"
        {{if status == 'VALIDATED'}}
                       style="background-color: #7cbe2e;"
                    {{else status == 'TO_BE_VALIDATED'}}
                         style="background-color: #D94F00;"
                    {{else status == 'TO_BE_DELETED' }}
                         style="background-color: #7A1818;"
                    {{else status == 'REJECTED'}}
                         style="background-color: #ff2d00;"
                    {{else status == 'DRAFT'}}
                         style="background-color: #222526;"
                    {{else}}
                         style="background-color: #dfdfdf"
                    {{/if}}>

            <#if searchMode != 0>
                <div class="content-result-status"><span
                    {{if status == 'VALIDATED'}}
                        class="label validated"
                    {{else status == 'TO_BE_VALIDATED'}}
                        class="label to_validated"
                    {{else status == 'TO_BE_DELETED' }}
                        class="label to_deleted"
                    {{else status == 'REJECTED'}}
                        class="label rejected"
                    {{else}}
                        class="label"
                    {{/if}}>

                    {{if status == 'VALIDATED'}}
                        Validé
                    {{else status == 'TO_BE_VALIDATED'}}
                        A&nbsp;Valider
                    {{else status == 'TO_BE_DELETED' }}
                        A&nbsp;Supprimer
                    {{else status == 'REJECTED'}}
                         Rejeté
                    {{else status == 'DRAFT'}}
                         Brouillon
                    {{else}}
                        = "Error"
                    {{/if}}</span></div>
            </#if>
        </div>
        <div class="left_part">
            <div class="icon_type"></div>
            <div class="content-result-title"><a href="/content/{{= id}}/view.html">{{= title}}</a></div>
            <div class="content-result-author"><img src="/public/images/bbeeg/author.png"/> {{= author.name}}</div>
            <div class="content-result-creation-date"><img src="/public/images/bbeeg/calendar.png"/> {{= creationDate}}</div>
        </div>

        <div class="right_part">
            <div class="texts_zone">
                <div class="content-result-description">{{= description}}</div>
                <div class="keywords"><span class="label keyword_content">a dynamiser</span><span class="label keyword_content">a dynamiser</span><span class="label keyword_content">a dynamiser</span><span class="label keyword_content">a dynamiser</span></div>
            </div>
            <div id="settings_menu">
                <#if searchMode == 1>
                    <img src="/public/images/bbeeg/edit.png" alt="Editer"
                            class="edit-button hand_cursor settings_item"
                            onclick="editContent('item-{{= id}}', {{= id}}, '{{= status}}')"
                            {{if !(isContentEditable(status))}}
                                disabled
                            {{/if}}
                            />

                    <img src="/public/images/bbeeg/publish.png" alt="Publier"
                            class="publish-button hand_cursor settings_item"
                            onclick="publishContent('item-{{= id}}', {{= id}}, '{{= status}}')"
                            {{if status != 'DRAFT'}}
                                disabled
                            {{/if}}
                            />
                    <img src="/public/images/bbeeg/delete.png" alt="Supprimer"
                            class="delete-button hand_cursor settings_item"
                            onclick="deleteContent('item-{{= id}}', {{= id}}, '{{= status}}')"
                            {{if status == 'TO_BE_DELETED'}}
                                disabled
                            {{/if}}
                            />
                </#if>
            </div>


            <#if searchMode == 2>
                <button type="button" onclick="acceptContent('item-{{= id}}', {{= id}}, '{{= status}}')">Accepter</button>
                <button type="button" onclick="rejectContent('item-{{= id}}', {{= id}}, '{{= status}}')">Rejeter</button>
            </#if>

        </div>
    </div>


</script>



<#if searchMode == 2>
<div style="visibility: hidden">
    <div id="rejectReasonDialog" title="Motif du rejet">
        <p> Indiquez le motif du rejet: </p>
        <textarea id="rejectReasonMsg" rows="10" cols="50"></textarea>
    </div>
</div>
</#if>

<#if searchMode == 2>
<div style="visibility: hidden">
    <div id="updateStatusImpossible" title="Echec de la mise à jour">
        <p>Le contenu ne peut pas être mis à jour car son status ne le permet pas.</p>
    </div>
</div>
</#if>


</@mainTemplate>
