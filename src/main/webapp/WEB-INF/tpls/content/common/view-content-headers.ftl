<#macro viewContentHeaders content>
    <script type="text/javascript" src="/public/js/bbeeg/content/contentHeaderHelper.js"></script>

    <script type="text/javascript">
        $(
            function() {
                $('.icon_type').addClass(contentHeaderHelper.getIcon('${content.header().type()}'));
            }
        )
    </script>

    <div class="view_common_header">
        <div class="tab_view"></div>
        <div class="left_part">
            <div class="view_title">
                <div class="icon_type"></div>
                <div class="view_content_title">${content.header().title()}</div>
            </div>

            <div class="view_author"><img src="/public/images/bbeeg/author.png"/> ${content.header().author().name()}</div>
            <div class="view_calendar"><img src="/public/images/bbeeg/calendar.png"/> ${content.header().creationDate()} </div>

            <div class="view_domain"><b>Domaine(s) :</b>
                <#list content.header().domains() as item>
                    <span class="domain">${item.label()}</span>
                </#list>
            </div>

            <div class="view_keyword"><b>Mot(s) clef(s) :</b>
                <#list content.header().tags() as item>
                    <span class="keyword keyword_content">${item}</span>
                </#list>
            </div>

        </div>
        <div class="right_part">
            <div class="view_description">${content.header().description()}</div>
            <!--<div class="settings_menu">
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
            </div>-->
        </div>
          <#-- A compléter avec les champs présents dans la spec ... -->
    </div>
</#macro>