<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:ccm="http://xmlns.libreccm.org">

    <xsl:template match="ccm:categoryPage">
        
        <html>
            <head>
                <title>Category page</title>
            </head>
            <body>
                <xsl:apply-template select="ccm:greetingItem" />
            </body>
        </html>
    </xsl:template>

    <xsl:template match="ccm:greetingItem">
        
        <h1>
            <xsl:value-of select="./ccm:title" />
        </h1>
        
    </xsl:template>

</xsl:stylesheet>
