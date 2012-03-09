<#include "../common/mainTemplate.ftl">

<@mainTemplate
    title="Ecran d'accueil"
    scripts=["/public/js/bbeeg/search/search.js",
             "/public/js/bbeeg/content/content-status.js",
             "/public/js/bbeeg/content/content-helper.js",
             "/public/js/bbeeg/common/widgets/chainedSelect.js",
             "/public/js/bbeeg/search/search-content.js",
             "/public/js/bbeeg/content/content-lifecycle-helper.js"]
    stylesheets=["/public/css/bbeeg/search.css"]
    useChosen=true>
<script>

    $(function() {
        <#if searchMode != 1>
            loadAllAuthors();
        </#if>
        loadAllDomains();
        loadAllContentTypes();

        /* Switch between 'Simple search' and 'Advanced search' modes */
        $('.advanced_search_button').click(
                function() {
                    $('.toggle').toggle();
                    return false;
                }).next().hide();

        var dates = $("#from, #to").datepicker({
            defaultDate: "",
            dateFormat: "yy-mm-dd",
            changeMonth: true,
            numberOfMonths: 1,
            onSelect: function(selectedDate) {
                var option = this.id == "from" ? "minDate" : "maxDate", instance = $(this).data("datepicker"), date = $.datepicker.parseDate(
                        instance.settings.dateFormat ||
                                $.datepicker._defaults.dateFormat,
                        selectedDate, instance.settings);
                dates.not(this).datepicker("option", option, date);
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
                        .progressIndicator($("#progressIndicator"))
                        .globalResultTemplate($("#contentResult"))
                        .resultElementTemplate($("#contentLineResult"))
                        .selectorForClickableOfSearchNext("#searchNext")
                        .selectorWhereResultsWillBeAppended("#contentResults")
        );

        SearchQuery.bindSearchToForm(
                new SearchQuery.SearchContext().targetForm($("#advancedSearchForm"))
                        .resultElement($("#searchResultsComponent"))
                        .process(process)
                        .progressIndicator($("#progressIndicator"))
                        .globalResultTemplate($("#contentResult"))
                        .resultElementTemplate($("#contentLineResult"))
                        .selectorForClickableOfSearchNext("#searchNext")
                        .selectorWhereResultsWillBeAppended("#contentResults")
        );

        /* $("#adSearchCriterias").chainedSelect(
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
        );*/

        // Enabling auto query when scrollbar is at the bottom of the window
        var SCROLLBAR_THRESHOLD = 100; // 100 pixels before the bottom of the screen, we consider the scrollbar is at the bottom
        $(window).scroll(function(args) {
            if ($("#searchResultsComponent #searchNext").length != 0 // Button "searchNext" exists and is not disabled
                    && $("#searchResultsComponent #searchNext").attr('disabled') != "disabled") {
                var scrollbarIsAtBottom = $(window).scrollTop() + SCROLLBAR_THRESHOLD >= $(document).height() - $(window).height();
                if (scrollbarIsAtBottom) {
                    $("#searchNext").click();
                }
            }
        });

        extractSearchCriterionFromUrl();
        // If we manage the connected user contents or we administrate validated contents, display them immediately
        <#if searchMode == 1 || searchMode == 2>
            $('#simpleSearchButton').click();
        </#if>

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
<div id="progressIndicator">
    <div>Recherche en cours</div>
    <img src="/public/images/ajax/indicator_large.gif"/>
</div>
<div id="searchResultsComponent" class="search-results-component">
</div>
<script id="criteriaSelectedItem" type="text/x-jquery-tmpl">
    <li class="search-choice"><span>{{= label}}</span><a href="#" name="{{= value}}" class="search-choice-close"></a></li>
</script>
<script id="domainItemTemplate" type="text/x-jquery-tmpl">
    <option value="{{= id}}"> {{= label}} </option>
</script>
<script id="authorItemTemplate" type="text/x-jquery-tmpl">
   <option value="{{= id}}">{{= firstName}} {{= lastName}}</option>
</script>
<script id="contentTypeItemTemplate" type="text/x-jquery-tmpl">
    <option value="{{= id}}"> {{= title}} </option>
</script>
<script id="contentResult" type="text/x-jquery-tmpl">
    <div id="contentResults">
        {{tmpl(results) "#contentLineResult"}}
    </div>
    <div id="noResultText">
        Aucun résultat ne correspond aux critères de recherche
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
                <div class="content-result-status">
                    <span class="label">{{= statusLabel}}</span>
                </div>

                <img src="/public/images/bbeeg/comment.png"
                     alt="Commentaires"
                     class="display-rejection-comments-button hand_cursor settings_item comments_icon"
                     onclick="contentLifeCycleHelper.displayRejectionDialog({{= id}}, '{{= status}}')"
                    {{if !(contentLifeCycleHelper.isDisplayRejectionCommentsAuthorized(status)) }}
                        style="visibility:hidden"
                    {{/if}}
                />
            <#else>
                <div class="tab_right label_hidden">
            </#if>
        </div>
        <div class="left_part hand_cursor" onclick="viewContent({{= id}})">
            <div class="icon_type {{= contentHelper.getIcon(type)}}"></div>
            <div class="content-result-title"><a href="/content/{{= id}}/view.html">{{= title}}</a></div>
            <div class="content-result-author"><img src="/public/images/bbeeg/author.png"/><a href="mailto:{{= author.email}}">{{= author.firstName}} {{= author.lastName}}</a> </div>
            <div class="content-result-creation-date"><img src="/public/images/bbeeg/calendar.png"/> {{= creationDate}}</div>
        </div>

        <div class="right_part">
            <div class="texts_zone hand_cursor" onclick="viewContent({{= id}})">
                <div class="content-result-description">{{= formatDescription(description)}}</div>
                <div class="keywords_container">
                    {{each(i, tag) tags}}
                        {{if i < 5 && tag != null && tag != ""}}
                            <span class="keyword keyword_content">{{= tag}}</span>
                        {{/if}}
                    {{/each}}
                </div>
            </div>
            <div class="settings_menu">
                <#if searchMode == 1>
                    <img src="/public/images/bbeeg/edit.png" alt="Editer"
                        class="edit-button hand_cursor settings_item"
                        onclick="contentLifeCycleHelper.editAction({{= id}})"
                        {{if !(contentLifeCycleHelper.isEditAuthorized(status))}}
                            style="visibility:hidden"
                        {{/if}}
                    />
                    <img src="/public/images/bbeeg/publish.png" alt="Publier"
                        class="publish-button hand_cursor settings_item"
                        onclick="contentLifeCycleHelper.publishAction('item-{{= id}}', {{= id}}, '{{= status}}')"
                        {{if !(contentLifeCycleHelper.isPublishAuthorized(status))}}
                             style="visibility:hidden"
                        {{/if}}
                    />
                    <img src="/public/images/bbeeg/delete.png" alt="Supprimer"
                        class="delete-button hand_cursor settings_item"
                        onclick="contentLifeCycleHelper.deleteAction('item-{{= id}}', {{= id}}, '{{= status}}')"
                        {{if !(contentLifeCycleHelper.isDeleteAuthorized(status))}}
                             style="visibility:hidden"
                        {{/if}}
                    />
                </#if>
            </div>

            <#if searchMode == 2>
                <div class="settings_menu">
                    <img src="/public/images/bbeeg/validate.png"
                         alt="Accepter"
                         class="accept-button hand_cursor settings_item"
                         onclick="contentLifeCycleHelper.acceptAction('item-{{= id}}', {{= id}}, '{{= status}}')"
                         {{if !(contentLifeCycleHelper.isAcceptAuthorized(status)) }}
                             style="visibility:hidden"
                         {{/if}}
                    />

                    <img src="/public/images/bbeeg/rejected.png"
                         alt="Rejeter"
                         class="reject-button hand_cursor settings_item"
                         onclick="contentLifeCycleHelper.rejectAction('item-{{= id}}', {{= id}}, '{{= status}}')"
                         {{if !(contentLifeCycleHelper.isRejectAuthorized(status)) }}
                             style="visibility:hidden"
                         {{/if}}
                    />

                    <img src="/public/images/bbeeg/comment.png"
                         alt="Commentaires"
                         class="display-publication-comments-button hand_cursor settings_item"
                         onclick="contentLifeCycleHelper.displayPublicationDialog({{= id}}, '{{= status}}')"
                         {{if !(contentLifeCycleHelper.isDisplayPublicationRequestCommentsAuthorized(status)) }}
                            style="visibility:hidden"
                         {{/if}}
                    />
                </div>
            </#if>
        </div>
    </div>
</script>

<!-- Action dialogs definitions -->
<div style="visibility: hidden">
    <div id="deleteActionDialog" title="Supprimer">
        <p class="dialogMessage"></p>
    </div>
</div>

<div style="visibility: hidden">
    <div id="updateStatusSuccessDialog" title="Modification du statut">
        <p>Le statut du contenu a été mis à jour avec succès.</p>
    </div>
</div>

<div style="visibility: hidden;">
    <div id="publishContentDialog" title="Demande de publication"></div>
</div>

<div style="visibility: hidden">
    <div id="rejectReasonDialog" title="Refus de validation"></div>
</div>

<#if searchMode == 2>
<div style="visibility: hidden">
    <div id="updateStatusImpossible" title="Echec de la mise à jour">
        <p>Le contenu ne peut pas être mis à jour car son statut ne le permet pas.</p>
    </div>
</div>
</#if>

<script id="publicationRejectionTemplate" type="text/x-jquery-tmpl">
    <br/>
    <p>{{= title}}</p>
    <br/>
    <textarea {{if contentStatus !== ContentStatus.DRAFT}}disabled{{/if}} rows="10" cols="50"style="width:100%"></textarea>
    <br/>
    <br/>
    <div id="publicationDates">
        <div class="search-publicationDates-labels">
            <div><label for="startPublicationDate">Début de publication :</label></div>
            <div><label for="endPublicationDate">Fin de publication : </label></div>
        </div>
        <div class="search-publicationDates-values">
            {{if contentStatus === ContentStatus.DRAFT}}
                <div><input type="text" id="startPublicationDate" name="startPublicationDate"/></div>
                <div><input type="text" id="endPublicationDate" name="endPublicationDate"/></div>
            {{else}}
                <div class="readOnly" id="startPublicationDate"></div>
                <div class="readOnly" id="endPublicationDate"></div>
            {{/if}}
        </div>
        {{if contentStatus === ContentStatus.DRAFT}}
            <p class='search-publicationDates-tip'>Les dates de publication sont facultatives, si non spécifiées le contenu sera disponible jusqu'à sa suppression.</p>
        {{/if}}
    </div>
    {{if contentStatus === ContentStatus.DRAFT}}
        <p class='search-publicationDates-message'>Le contenu sera disponible sur la plateforme après acceptation du modérateur selon la plage de publication définie.</p>
    {{/if}}
    </div>
</script>
</@mainTemplate>