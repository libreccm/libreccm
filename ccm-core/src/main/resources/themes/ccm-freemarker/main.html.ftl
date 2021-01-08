<#macro ccm_main scripts=[]>
<html>
    <head>
        <title>Category page</title>
        <link rel="stylesheet" href="${themeUrl}/style.css" />
        <#list scripts as script>
            <script src="${themeUrl}/${script}" />
        </#list>
    </head>
    <body>
        <header>
            <a href="https://www.libreccm.org">
                <img alt="LibreCCM Logo"
                     src="${themeUrl}/images/libreccm.png" />
            </a>
        </header>
        <main>
            <#nested>
        </main>
        <footer>
            <p>LibreCCM basic theme. The customize create your own theme.</p>
        </footer>
    </body>
</html>
</#macro>
