<#include "common/mainTemplate.ftl">

<@mainTemplate title="Ecran d'accueil"
selectedMenuItem="dashboard"
scripts=["/public/js/jit/jit.js",
"/public/js/bbeeg/home.js"]
stylesheets=["/public/css/tabs/tabs.css",
"/public/css/bbeeg/home.css",
"/public/css/jit/base.css",
"/public/css/jit/RGraph.css",
"/public/css/bbeeg/bbeeg.css",
"/public/css/bbeeg/dashboard.css"]>

<script type="application/javascript">
    $(function () {
        initialize();
    });
</script>


<div id="dashboard_menu">
    <a href="/home.html">
        Plan des connaissances
    </a>
    <a href="">Cours & Tests</a>
    <a href="/dashboard/dashboard.html">
        Contenus
    </a>

    <div id="dashboard_menuright">
    </div>
</div>
<div id="knowledgeMap" align="center" style="width: 940px; height: 940px">
   
</div>

</@mainTemplate>