<#macro viewContentHeaders content>
    <script type="text/javascript">
        $(
            function() {
                $("#domains").chosen();
            }
        );
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
                <#list content.header().domains() as item>
                    <span class="keyword keyword_content">${item.label()}</span>
                </#list>
            </div>

        </div>
        <div class="right_part">
            <div class="view_description">${content.header().description()}</div>
        </div>
        <#-- A compléter avec les champs présents dans la spec ... -->
    </div>
</#macro>