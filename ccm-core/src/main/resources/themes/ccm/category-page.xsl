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
        
    </xsl:template>

</xsl:stylesheet>
