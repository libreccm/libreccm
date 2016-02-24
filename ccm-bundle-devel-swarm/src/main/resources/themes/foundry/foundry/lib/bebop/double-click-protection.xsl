<?xml version="1.0" encoding="utf-8"?>
<!DOCTYPE stylesheet [<!ENTITY nbsp '&#160;'>]>

<!-- 
    Copyright: 2006, 2007, 2008 Sören Bernstein
  
    This file is part of Mandalay.

    Mandalay is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 2 of the License, or
    (at your option) any later version.

    Mandalay is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with Mandalay.  If not, see <http://www.gnu.org/licenses/>.
-->

<!-- This file was copied from Mandalay and edited to fit into Foundry -->

<!-- EN
  Processing double click protection. This function is completely rewritten to allow 
  working with links AND buttons with one function.
-->

<!-- Autor: Sören Bernstein -->

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:bebop="http://www.arsdigita.com/bebop/1.0" 
                xmlns:cms="http://www.arsdigita.com/cms/1.0"
                xmlns:foundry="http://foundry.libreccm.org" 
                xmlns:nav="http://ccm.redhat.com/navigation" 
                exclude-result-prefixes="xsl bebop cms foundry nav" 
                version="2.0">
  
    <!-- DE Hier wird das javascript für den Doppelklickschutz eingebaut, falls es verwendet wird -->
    <!-- EN Inserting javascript for double click protection, if used on the page -->
    <xsl:template name="bebop:double-click-protection">
        <xsl:if test="$dcp-on-buttons or $dcp-on-links">
            <xsl:variable name="waitMessage" 
                          select="foundry:get-static-text('bebop', 
                                                          'double-click-protection/wait-message')"/>
                
            <script language="javascript">
                function doubleClickProtect(element) {
          
                if(element.nodeName == "INPUT") {

                // change button label
                elementClone = element.cloneNode(true);
                elementClone.value = "<xsl:value-of select="$waitMessage"/>";
          
                // set cloned button and hide the original one
                element.parentNode.insertBefore(elementClone, element);
                element.style.display = "none";
            
                // disable all submit buttons in this form
                formElements = element.form.elements;
                for(i = 0; i &lt; formElements.length; i++) {
                if(formElements[i].tagName == "INPUT" &amp;&amp; 
                formElements[i].type == "submit" &amp;&amp; 
                formElements[i] != element) 
                formElements[i].setAttribute("disabled", "disabled");
                }
            
                } else {
          
                // disable link
                link = element.getAttribute("href");
                element.text = "<xsl:value-of select="$waitMessage"/>";
                element.removeAttribute("href");
                location.href = link;
                }
          
                }
            </script>      
        </xsl:if>
    </xsl:template>
  
</xsl:stylesheet>
