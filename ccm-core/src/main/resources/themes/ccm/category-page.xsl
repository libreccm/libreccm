<?xml version="1.0"?>
<xsl:stylesheet version="2.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:ccm="http://xmlns.libreccm.org">

    <xsl:template match="page">
        
        <html>
            <head>
                <title>Category page</title>
            </head>
            <body>
                <xsl:apply-templates select="greetingItem" />
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
