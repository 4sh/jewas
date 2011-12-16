<#macro eegContentHeader content>
    <script type="text/javascript" src="/public/js/bbeeg/content/content-helper.js"></script>

    <script type="text/javascript">
        $(
            function() {
                $('.icon_type').addClass(contentHelper.getIcon('${content.header().type()}'));

                 /* Bind the show description button */
                $(".showHide_button").toggle(
                    function() {
                        $("#descriptionContainer").removeClass("description_hidden description_show").addClass("description_show");
                        $(".showHide_button").text("Cacher la description");
                        $(".description_toggle").toggle();
                    },
                    function () {
                        $("#descriptionContainer").removeClass("description_hidden description_show").addClass("description_hidden");
                        $(".showHide_button").text("Lire la suite");
                        $(".description_toggle").toggle();
                });
            }
        )
    </script>

    <div id="descriptionContainer" class="description_hidden">
        <div class="view_common_header">
            <div class="tab_view"></div>
            <div class="left_part">
                <div class="view_title">
                    <div class="icon_type"></div>
                    <div class="view_content_title">${content.header().title()}</div>
                </div>

                <div class="view_author"><img src="/public/images/bbeeg/author.png"/> ${content.header().author().firstName()} ${content.header().author().lastName()}</div>
                <div class="view_calendar"><img src="/public/images/bbeeg/calendar.png"/> ${content.header().creationDate()} </div>

                <div class="description_toggle">
                    <div class="view_domain"><b>Domaine(s) :</b>
                        <#list content.header().domains() as item>
                            <span class="domain">${item.label()}</span>
                        </#list>
                    </div>

                    <div class="view_keyword"><b>Mot(s) clef(s) :</b>
                        <#list content.header().tags() as item>
                            <span class="keyword keyword_content"
                            style="display:<#if !item?? || item == "" >none<#else>inline</#if>">${item}</span>
                         </#list>
                    </div>
                </div>
                <div class="view_description">${content.header().description()}</div>

                <a class="showHide_button" href="">Lire la suite</a>
            </div>
        </div>
    </div>

    <div class="description_show" style="visibility: hidden;">
        <div class="view_common_header">
            <div class="tab_view"></div>
            <div class="left_part">
                <div class="view_title">
                    <div class="icon_type"></div>
                    <div class="view_content_title">${content.header().title()}</div>
                </div>

                <div class="view_author"><img src="/public/images/bbeeg/author.png"/> ${content.header().author().firstName()} ${content.header().author().lastName()}</div>

                <div class="view_calendar"><img src="/public/images/bbeeg/calendar.png"/> ${content.header().creationDate()} </div>

                <div class="view_description">${content.header().description()}</div>

               <div class="showHide_button"><a href="">Hide description</a></div>
            </div>
        </div>
    </div>



</#macro>