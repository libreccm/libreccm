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
                <xsl:apply-templates select="indexItem" />
            </body>
        </html>
    </xsl:template>

    <xsl:template match="indexItem">
        
        <h1>
            <xsl:value-of select="./title" />
        </h1>
        
    </xsl:template>

</xsl:stylesheet>
