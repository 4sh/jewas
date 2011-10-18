<#macro eegContentHeader content>
    <script type="application/javascript" src="/public/js/bbeeg/content/contentHeaderHelper.js"></script>

    <script type="application/javascript">
        $(
            function() {
                $('.icon_type').addClass(contentHeaderHelper.getIcon('${content.header().type()}'));
            }
        )
    </script>

    <div class="description_hidden" style="visibility: hidden;">
        <div class="view_common_header">
            <div class="tab_view"></div>
            <div class="left_part">
                <div class="view_title">
                    <div class="icon_type"></div>
                    <div class="view_content_title">${content.header().title()}</div>
                </div>

                <div class="view_author"><img src="/public/images/bbeeg/author.png"/> ${content.header().author().surname()} ${content.header().author().name()}</div>
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

                <div class="view_description">${content.header().description()}</div>

               <div class="showHide_button"><a href="">Show more description</a></div>
            </div>
        </div>
    </div>

    <div class="description_show" style="visibility: visible;">
        <div class="view_common_header">
            <div class="tab_view"></div>
            <div class="left_part">
                <div class="view_title">
                    <div class="icon_type"></div>
                    <div class="view_content_title">${content.header().title()}</div>
                </div>

                <div class="view_author"><img src="/public/images/bbeeg/author.png"/> ${content.header().author().surname()} ${content.header().author().name()}</div>
                <div class="view_calendar"><img src="/public/images/bbeeg/calendar.png"/> ${content.header().creationDate()} </div>

                <#--<div class="view_domain"><b>Domaine(s) :</b>-->
                    <#--<#list content.header().domains() as item>-->
                        <#--<span class="domain">${item.label()}</span>-->
                    <#--</#list>-->
                <#--</div>-->

                <#--<div class="view_keyword"><b>Mot(s) clef(s) :</b>-->
                    <#--<#list content.header().tags() as item>-->
                        <#--<span class="keyword keyword_content">${item}</span>-->
                    <#--</#list>-->
                <#--</div>-->

                <div class="view_description">${content.header().description()}</div>

               <div class="showHide_button"><a href="">Hide description</a></div>
            </div>
        </div>
    </div>



</#macro>