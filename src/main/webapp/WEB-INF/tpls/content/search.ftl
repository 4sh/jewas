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
                        var statusStyle = getStatusStyle(status);
                        $('#' + containerId).find('.tab_right').removeClass().addClass('tab_right ' + statusStyle.class);
                        $('#' + containerId).find('.label').html(statusStyle.label)
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

    function process(results) {
        $(results).each(
            function (i, e) {
                if(!e) {
                    return;
                }
                var statusStyle = getStatusStyle(e.status);
                e.statusLabel = statusStyle.label;
                e.statusClass = statusStyle.class;
            }
    );
        return results;
    }

    function extractSearchCriterionFromUrl() {
        var url = window.location.href;
        if (url.indexOf('#') !== -1) {
            var splittedUrl = url.split('#');

            if (splittedUrl.length !== 2) {
                console.log("Error url should only contain one #. url:", url);
                return;
            }

            $('#simpleSearchQuery').val(decodeURI(splittedUrl[1]));
            $('#simpleSearchButton').click();
        }
    }

    function getStatusStyle(status) {
        var statusStyle = {};

        switch (status) {
            case 'VALIDATED':
                statusStyle.label = "Validé";
                statusStyle.class = "label_validated";
                break;
            case 'TO_BE_VALIDATED':
                statusStyle.label = "A&nbsp;Valider";
                statusStyle.class = "label_to_validated";
                break;
            case 'TO_BE_DELETED':
                statusStyle.label = "A&nbsp;Supprimer";
                statusStyle.class = "label_to_deleted"
                break;
            case 'REJECTED':
                statusStyle.label = "Rejeté";
                statusStyle.class = "label_rejected";
                break;
            case 'DRAFT':
                statusStyle.label = "Brouillon";
                statusStyle.class = "label_draft";
                break;
            default:
                statusStyle.label = "Error";
                statusStyle.class = "label_hidden";
        }
        return statusStyle;
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

    $(function() {

        <#if searchMode != 1>
            loadAuthors();
        </#if>
        loadDomains();
        loadContentTypes();

        /* Switch between 'Simple search' and 'Advanced search' modes */
        $('.advanced_search_button').click(function() {
		    $('.toggle').toggle();
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
                .process(process)
                .globalResultTemplate($("#contentResult"))
                .resultElementTemplate($("#contentLineResult"))
                .selectorForClickableOfSearchNext("#searchNext")
                .selectorWhereResultsWillBeAppended("#contentResults")
        );

        SearchQuery.bindSearchToForm(
            new SearchQuery.SearchContext().targetForm($("#advancedSearchForm"))
                .resultElement($("#searchResultsComponent"))
                .process(process)
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
        
        extractSearchCriterionFromUrl();

    });
</script>

<div id="searchComponent">



    <div class='toggle'>
        <form action="/content/search" id="simpleSearchForm">
            <div class="search_container">
                <div class="search_input"><input id="simpleSearchQuery" type="text" name="query" style="width:650px;"/></div>
                <div class="search_button"><input id="simpleSearchButton" type="submit" value="Rechercher"/></div>
                <div class="advanced_button">
                    <a href="#"class="advanced_search_button">+</a>
                </div>
            </div>


            <input name="searchMode" style="visibility: hidden;" value="${searchMode}"/>
         </form>
    </div>

    <div class="advanced toggle" style="display:none">
        <form action="/content/advancedSearch" id="advancedSearchForm">
            <div class="search_container">
                 <div class="search_input"><input id="advancedSearchQuery" type="text" name="query" style="width:650px;" /></div>
                <div class="search_button"><input id="advancedSearchButton" class="button" type="submit" value="Rechercher"/></div>
                <div class="advanced_button">
                    <a href="#" class="advanced_search_button">&#8211</a>
                </div>

                <div class="advanced_search_options">
                    <div class="criteria-line">
                        <label for="from">Date de création : Entre</label>
                        <input type="text" id="from" name="from" style="width: 100px;" />
                        <label for="to">et</label>
                        <input type="text" id="to" name="to" style="width: 100px;"/>
                    </div>

                    <div class="sepa_horizontal"></div>

                    <div class="criteria-line">
                        <div class="criteria-label"><label for="adSearchType">Types de contenu</label> :</div>
                        <div class="criteria-field">
                            <select id="adSearchType" name="searchTypes" class="chzn-select side-by-side clearfix" multiple style="width:450px">
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
                            <select id="adSearchDomains" name="domains" class="chzn-select side-by-side clearfix" multiple style="width:450px">
                            </select>
                        </div>
                    </div>

                    <#if searchMode != 1>
                        <div class="criteria-line">
                            <div class="criteria-label"><label for="adSearchAuthors">Auteur</label> :</div>
                            <div class="criteria-field">
                                <select id="adSearchAuthors" name="authors" class="chzn-select side-by-side clearfix" multiple style="width:450px">
                                </select>
                            </div>
                        </div>
                    </#if>

                </div>
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
    <div id="contentResults">
        {{tmpl(results) "#contentLineResult"}}
    </div>
    <button id="searchNext" class="next_result"><img src="/public/images/ajax/indicator.gif" class="spinner" />Résultats suivants</button>
</script>
<script id="tagItemTemplate" type="text/x-jquery-tmpl">
    <span class="keyword keyword_content"></span>
</script>


<script id="contentLineResult" type="text/x-jquery-tmpl">
    <div id="item-{{= id}}" class="content-result search_result content_type">
        <div class="tab_left"></div>

            <#if searchMode != 0>
                <div class="tab_right {{= statusClass}}">
                <div class="content-result-status"><span class="label">{{= statusLabel}}</span></div>
            <#else>
                <div class="tab_right label_hidden">
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
                <div class="keywords_container">
                    {{each(i, tag) tags}}
                        {{if i < 5}}
                            <span class="keyword keyword_content">{{= tag}}</span>
                        {{/if}}
                    {{/each}}
                </div>
            </div>
            <div class="settings_menu">
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
                <div class="settings_menu">
                    <img src="/public/images/bbeeg/validate.png" alt="Accepter" class="hand_cursor settings_item" onclick="acceptContent('item-{{= id}}', {{= id}}, '{{= status}}')"/>
                    <img src="/public/images/bbeeg/rejected.png" alt="Rejeter" class="hand_cursor settings_item" onclick="rejectContent('item-{{= id}}', {{= id}}, '{{= status}}')"/>
                </div>
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
