<?xml version="1.0"?>
<xsl:stylesheet version="2.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:ccm="http://xmlns.libreccm.org"
                exclude-result-prefixes="ccm xsl">
    
    <xsl:template name="footer">
        <footer>
            <ul>
                <li>
                    <a href="/impressum">
                        <!--Impressum-->
                        <xsl:value-of select="ccm:localize('footer.impressum')" />
                    </a>
                </li>
                <li>
                    <a href="/privacy">
                        <!--Privacy-->
                        <xsl:value-of select="ccm:localize('footer.privacy')" />
                    </a>
                </li>
                <li>
                    <code>imported</code>
                </li>
            </ul>
        </footer>
    </xsl:template>
    
</xsl:stylesheet>