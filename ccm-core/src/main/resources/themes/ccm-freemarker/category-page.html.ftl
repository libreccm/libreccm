<!DOCTYPE html>
<html>
    <head>
        <title>Category page</title>
        <link rel="stylesheet" href="${getContextPath()}/theming/ccm/style.css" />
        </head>
    <body>
        <main>
            <ul class="news">

                <#list newsList.items as item>
                <li>
                    <span>
                        <!--<pre>
                            <xsl:value-of select="count(./attachments[name='.images']/attachments[1])" />
                        </pre>-->

                        <#list item.attachments as attachmentList>
                            <#if attachmentList.name = ".images">
                        <img src="${getContextPath()}/content-sections/info/images/uuid-${attachmentList.attachments[0].asset.uuid}" 
                             width="354"
                             height="286" 
                             alt="" />
                            </#if>
                        </#list>
                        </span>
                    <span>
                        "${item.title}"
                        </span>
                    </li>
                    </#list>
                </ul>
            <div class="boxes">
                <#list articles.items as item>
                <div>
                    <h1>
                            ${item.title}"
                        </h1>
                    <p>
                         <#list item.attachments as attachmentList>
                            <#if attachmentList.name = ".images">
                        <img src="${getContextPath()}/content-sections/info/images/uuid-${attachmentList.attachments[0].asset.uuid}" 
                             alt="" />
                            </#if>
                        </#list>
                        ${item.description}"
                        </p>
                    </div>
                </#list>
                </div>

            <h2>Example of Theme Utils</h2>
            <dl>
                <dt>
                    <code>getContextPath</code>
                    </dt>
                <dd>
                    <code>
                    ${getContextPath()}
                        </code>
                    </dd>
                <dt>
                    <code>getSetting</code>
                    </dt>
                <dd>
                    <code>
                    ${getSetting("settings.properties", "example.setting", "n/a")}
                        </code>
                    </dd>
                <dt>
                    <code>truncateText</code>
                    </dt>
                <dd>
                    <code>
                    ${truncateText("0123456789 123456789 123456789", 20)}
                        </code>
                    </dd>
                <dt>
                    <code>localized('label.critical')</code>
                    </dt>
                <dd>
                ${localize("label.critical", "texts/labels")}
                    </dd>
                <dt>
                    <code>localized('label.error')</code>
                    </dt>
                <dd>
                ${localize("label.error", "texts/labels")}
                    </dd>
                <dt>
                    <code>localized('label.ok')</code>
                    </dt>
                <dd>
                ${localize("label.ok", "texts/labels")}
                    </dd>
                <dt>
                    <code>localized('label.warning')</code>
                    </dt>
                <dd>
                ${localize("label.warning", "texts/labels")}
                    </dd>
                </dl>

            </main>
    <#include "footer.html.ftl">
        </body>
    </html>

