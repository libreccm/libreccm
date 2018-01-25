<?xml version="1.0"?>
<xsl:stylesheet version="2.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:ccm="http://xmlns.libreccm.org"
                exclude-result-prefixes="ccm xsl">

    <xsl:output method="html"
                 doctype-system="about:legacy-compat"
                 indent="yes"
                 encoding="utf-8"/>
    
    <xsl:template match="page">
        
        <html>
            <head>
                <title>Category page</title>
                <link rel="stylesheet" href="{ccm:getContextPath()}/theming/ccm/style.css" />
            </head>
            <body>
                <main>
                    <ul class="news">
                        <xsl:for-each select="/page/newsList/items">
                            <li>
                                <span>
                                    <!--<pre>
                                        <xsl:value-of select="count(./attachments[name='.images']/attachments[1])" />
                                    </pre>-->
                                    <img src="{ccm:getContextPath()}/content-sections/info/images/uuid-{./attachments[name='.images']/attachments[1]/asset/uuid}" 
                                         width="354" 
                                         height="286" 
                                         alt="" />
                                </span>
                                <span>
                                    <xsl:value-of select="./title" />
                                </span>
                            </li>
                        </xsl:for-each>
                    </ul>
                    <div class="boxes">
                        <xsl:for-each select="/page/articles/items">
                            <div>
                                <h1>
                                    <xsl:value-of select="./title" />
                                </h1>
                                <p>
                                    <img src="{ccm:getContextPath()}/content-sections/info/images/uuid-{./attachments[name='.images']/attachments[1]/asset/uuid}" alt="" />
                                    <xsl:value-of select="./description" />
                                </p>
                            </div>
                        </xsl:for-each>
                    </div>
                    <!--<xsl:apply-templates select="greetingItem" />-->
                </main>
                <footer>
                    <ul>
                        <li>
                            <a href="/impressum">Impressum</a>
                        </li>
                        <li>
                            <a href="/privacy">Privacy</a>
                        </li>
                    </ul>
                </footer>
                
            </body>
        </html>
    </xsl:template>

    

    <xsl:template match="greetingItem">
        
        <h1>
            <xsl:value-of select="./title" />
            
        </h1>
        <p>
            <xsl:value-of select="./description" />
        </p>
        <xsl:value-of disable-output-escaping="true" select="./text" />
        
        <h2>Example of Theme Utils</h2>
        <dl>
            <dt>
                <code>getContextPath</code>
            </dt>
            <dd>
                <code>
                    <xsl:value-of select="ccm:getContextPath()" />
                </code>
            </dd>
            <dt>
                <code>getSetting</code>
            </dt>
            <dd>
                <code>
                    <xsl:value-of select="ccm:getSetting('settings.properties', 'example.setting', 'n/a')" />
                </code>
            </dd>
            <dt>
                <code>truncateText</code>
            </dt>
            <dd>
                <code>
                    <xsl:value-of select="ccm:truncateText('0123456789 123456789 123456789', 20)" />
                </code>
            </dd>
        </dl>
        
    </xsl:template>

</xsl:stylesheet>
