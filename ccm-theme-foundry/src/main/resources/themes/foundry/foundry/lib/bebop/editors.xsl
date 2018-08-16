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

<!-- This file was copied from  Mandalay  and edited to fit into Foundry -->

<!-- EN
  Processing html editors
-->

<!-- Autor: Sören Bernstein -->

<xsl:stylesheet     xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                    xmlns:bebop="http://www.arsdigita.com/bebop/1.0" 
                    xmlns:cms="http://www.arsdigita.com/cms/1.0"
                    xmlns:foundry="http://foundry.libreccm.org" 
                    xmlns:nav="http://ccm.redhat.com/navigation" 
                    exclude-result-prefixes="xsl bebop cms foundry nav" 
                    version="2.0">
  
    <!-- DE Benutze DHTML-Editor (HTMLArea) -->
    <!-- EN Use DHTML-Editor (HTMLArea) -->
    <xsl:template match="bebop:dhtmleditor">
    
        <xsl:variable name="first-match">
            <xsl:value-of select="//bebop:dhtmleditor/@name"/>
        </xsl:variable>
        <xsl:if test="@name=$first-match">
      
            <script type="text/javascript">
                _editor_url = "/assets/htmlarea/";
                _editor_lang = "en";
                var numEd = 0;
            </script>
      
            <script type="text/javascript" src="/assets/htmlarea/htmlarea.js"/>
      
            <script type="text/javascript">
                <xsl:for-each select="bebop:plugin">
                    HTMLArea.loadPlugin("<xsl:value-of select="@name"/>");
                </xsl:for-each>
        
                // Using Styled.js didn't seem to work anymore with htmlarea 3.0rc3,
                // so instead we configure the editor here
        
                var css_plugin_args = {
                combos : [
                { label: "Style",
                options: {  "None" : "",
                "Main" : "main",
                "Dark" : "dark",
                "Medium" : "medium",
                "Light" : "light"
                }
                }
                ]
                };
        
                <xsl:for-each select="//bebop:dhtmleditor">
                    var config_<xsl:value-of select="@name"/> = null;
                    var editor_<xsl:value-of select="@name"/> = null;
                </xsl:for-each>
        
                function initDocument() {
                <xsl:for-each select="//bebop:dhtmleditor">
                    config_<xsl:value-of select="@name"/> = new HTMLArea.Config();
                    editor_<xsl:value-of select="@name"/> = new HTMLArea("ta_<xsl:value-of select="@name"/>", config_<xsl:value-of select="@name"/>);
          
                    config_<xsl:value-of select="@name"/>.registerButton("insertlink", "Insert link", _editor_url + "images/ed_link.gif", false, function(editor) {
                    <!-- Modified to add the open in new window button NJ-20062403-->
                    <!--  editor._popupDialog("insert_link.html", function(param) {
                    if (!param) {   // user must have pressed Cancel
                    return false;
                    }
                    var furl = param["f_url"];
                    var sel = editor._getSelection();
                    var range = editor._createRange(sel);
                    editor._doc.execCommand("createlink", false, furl);
                    }, null); -->
                    var sel = editor._getSelection();
                    var range = editor._createRange(sel);
                    var compare = 0;
                    if (HTMLArea.is_ie) {
                    compare = range.compareEndPoints("StartToEnd", range);
                    } else {
                    compare = range.compareBoundaryPoints(range.START_TO_END, range);
                    }
                    if (compare == 0) {
                    alert("You need to select some text before creating a link");
                    return;
                    }
                    editor._popupDialog("insert_link.html", function(param) {
                    if (!param) {   // user must have pressed Cancel
                    return false;
                    }
                    var sel = editor._getSelection();
                    var range = editor._createRange(sel);
                    if (range.insertNode) { // Standards compliant version
                    var link = document.createElement("a");
                    var linkText = range.extractContents();
                    link.href=param["f_url"];
                    if (param["f_external"]) link.target="_blank";
                    link.appendChild(linkText);
                    range.insertNode(link);
                    } else if (range.pasteHTML) { // Alternative non standards version
                    var target = "";
                    if (param["f_external"]) {
                    target='target="_blank"';
                    }
                    range.pasteHTML('&lt;a href="' + param["f_url"] + '"' + target + '&gt;' + range.text + '&lt;/a&gt;');
                    }
                    }, null);
                    });
          
                    config_<xsl:value-of select="@name"/>.sizeIncludesToolbar = false;
                    config_<xsl:value-of select="@name"/>.statusBar = false;
                    config_<xsl:value-of select="@name"/>.toolbar = [[ "formatblock", "space", "bold", "italic", "underline", "strikethrough", "separator", "subscript", "superscript", "separator", "copy", "cut", "paste", "space", "undo", "redo", "space", "removeformat", "killword"  ], [ "justifyleft", "justifycenter", "justifyright", "justifyfull", "separator", "lefttoright", "righttoleft", "separator", "orderedlist", "unorderedlist", "outdent", "indent", "separator", "textindicator", "separator", "inserthorizontalrule", "insertlink", "insertimage", "inserttable", "htmlmode", "separator", "popupeditor", "separator" ]];
                    <xsl:if test="bebop:config/@hidden-buttons">
                        config_<xsl:value-of select="@name"/>.hideSomeButtons("<xsl:value-of select="bebop:config/@hidden-buttons"/>");
                    </xsl:if>
                    editor_<xsl:value-of select="@name"/>.config.pageStyle = "@import url(/assets/htmlarea/htmlarea.css);";
          
                    <xsl:for-each select="bebop:plugin">
                        <xsl:choose>
                            <xsl:when test="@name = 'CSS'">
                                editor_<xsl:value-of select="../@name"/>.registerPlugin(<xsl:value-of select="@name"/>, css_plugin_args);
                            </xsl:when>
                            <xsl:otherwise>
                                editor_<xsl:value-of select="../@name"/>.registerPlugin(<xsl:value-of select="@name"/>);
                            </xsl:otherwise>
                        </xsl:choose>
                    </xsl:for-each>
          
                    setTimeout(function() {editor_<xsl:value-of select="@name"/>.generate();}, 500*numEd);
                    numEd++;
                </xsl:for-each>
                }
        
                HTMLArea.init();
                HTMLArea.onload = initDocument;
        
                function wordClean_<xsl:value-of select="@name"/>() {
                editor_<xsl:value-of select="@name"/>._wordClean();
                }
            </script>
            <!--
                  <style type="text/css">
                    textarea { background-color: #fff; border: 1px solid 00f; }
                  </style>
            -->      
        </xsl:if> 
    
        <xsl:call-template name="process-label">
            <xsl:with-param name="widget" select="."/>
        </xsl:call-template>
        <div style="width:560px; border:1px outset #666;">
            <textarea id="ta_{@name}" 
                      name="{@name}" 
                      rows="{@rows}" 
                      cols="{@cols}" 
                      wrap="{@wrap}" 
                      style="width:100%">
                <xsl:value-of disable-output-escaping="no" select="text()"/>
            </textarea>
        </div>
    
    </xsl:template>
  
    <!-- DE Benutze FCKEditor -->
    <!-- EN Use FCKEditor -->
    <xsl:template match="bebop:fckeditor">
    
        <xsl:variable name="first-match">
            <xsl:value-of select="//bebop:fckeditor/@name"/>
        </xsl:variable>

        <!-- EN Start of the FCKeditor component code -->
        <xsl:if test="@name=$first-match">    
      
            <script type="text/javascript">
                _editor_url = "/assets/fckeditor/";
                _editor_lang = "en";
                var numEd = 0;
            </script>
      
            <script type="text/javascript" src="/assets/fckeditor/fckeditor.js"/>
      
            <script type="text/javascript">
                <xsl:for-each select="//bebop:fckeditor">
                    var editor_<xsl:value-of select="@name"/> = null;
                </xsl:for-each>
        
                window.onload = function() {
                <xsl:for-each select="//bebop:fckeditor">
                    editor_<xsl:value-of select="@name"/> = new FCKeditor("ta_<xsl:value-of select="@name"/>") ;
                    editor_<xsl:value-of select="@name"/>.Width = 
                    <xsl:choose>
                        <xsl:when test="@metadata.width">
                            '<xsl:value-of select="@metadata.width"/>'; 
                        </xsl:when>
                        <xsl:otherwise>
                            '100%';
                        </xsl:otherwise>
                    </xsl:choose>
                    editor_<xsl:value-of select="@name"/>.Height = 
                    <xsl:choose>
                        <xsl:when test="@metadata.height">
                            '<xsl:value-of select="@metadata.height"/>'; 
                        </xsl:when>
                        <xsl:otherwise>
                            '400';
                        </xsl:otherwise>
                    </xsl:choose>
          
                    editor_<xsl:value-of select="@name"/>.BasePath = "/assets/fckeditor/" ;
                    editor_<xsl:value-of select="@name"/>.PluginsPath = editor_<xsl:value-of select="@name"/>.BasePath + "editor/plugins/" ;
                    <xsl:if test="bebop:config/@path">
                        editor_<xsl:value-of select="//bebop:fckeditor/@name"/>.Config['CustomConfigurationsPath'] = "<xsl:value-of select="//bebop:fckeditor/bebop:config/@path"/>";
                    </xsl:if>
                    editor_<xsl:value-of select="@name"/>.ToolbarSet = "Basic";
                    editor_<xsl:value-of select="@name"/>.ReplaceTextarea();
                    }
                </xsl:for-each>       
        
            </script>
      
            <!--      
                  <style type="text/css">
                    textarea { background-color: #fff; border: 1px solid 00f; }
                  </style>
            -->      
        </xsl:if>
        <!-- EN End of FCKeditor setup -->
    
        <xsl:call-template name="process-label">
            <xsl:with-param name="widget" select="."/>
        </xsl:call-template>
        <textarea id="ta_{@name}" name="{@name}" style="width:100%" rows="{@rows}" cols="{@cols}" wrap="{@wrap}">
            <xsl:value-of disable-output-escaping="no" select="text()"/>
        </textarea>
    
    </xsl:template>
    
    <xsl:template match="bebop:tinymce">
        
        <xsl:variable name="first-match">
            <xsl:value-of select="//bebop:tinymce/@name" />
        </xsl:variable>

        <xsl:if test="@name=$first-match">
            
            <!--<script src="{./@editor_src}"></script>-->
            <script src="{./bebop:config[@name='TinyMCE.Config']/@path}">
            </script>
            
        </xsl:if>
        
         <xsl:call-template name="process-label">
            <xsl:with-param name="widget" select="."/>
        </xsl:call-template>
        <textarea id="ta_{@name}"
                  class="tinymce"
                  name="{@name}" 
                  rows="{@rows}" 
                  cols="{@cols}" 
                  wrap="{@wrap}">
            <xsl:value-of disable-output-escaping="no" select="text()"/>
        </textarea>
                        
    </xsl:template>
 
    <!-- DE Benutze Xinha -->
    <!-- EN Use Xinha -->
    <xsl:template match="bebop:xinha">
    
        <xsl:variable name="first-match">
            <xsl:value-of select="//bebop:xinha/@name"/>
        </xsl:variable>

        <xsl:if test="@name=$first-match">
 
            <script type="text/javascript">
                _editor_url = "<xsl:value-of select="@editor_url"/>";
                _editor_lang ="<xsl:value-of select="$lang"/>";
                <!--        _editor_skin = "silva";-->
        
                <!-- DE Definiere, welche Textareas zu Xinha-Editoren werden sollen -->
                <!-- EN Define all textares which should become xinha editors -->
                xinha_editors = [
                <xsl:for-each select="//bebop:xinha">
                    'ta_<xsl:value-of select="@name"/>'<xsl:if test="position() != last()">, </xsl:if>
                </xsl:for-each>
                ];
        
                <!-- DE Lade die angegebenen Plugins falls angegeben -->
                <!-- EN Load the mentioned plugins if any-->
                xinha_plugins = null;
                <xsl:if test="bebop:plugin">
                    xinha_plugins = [
                    <xsl:for-each select="bebop:plugin">
                        '<xsl:value-of select="@name"/>'<xsl:if test="position() != last()">, </xsl:if>
                    </xsl:for-each>
                    ];
                </xsl:if>
            </script>      
    
            <!-- DE Lade die externe JavaScript-Datei für Xinha -->
            <script type="text/javascript" src="{@editor_src}"/>
      
            <!-- DE Lade die angegebene Konfiguration -->
            <script type="text/javascript">
                <xsl:attribute name="src">
                    <xsl:choose>
                        <xsl:when test="bebop:config[@name='XinhaConfig']">
                            <xsl:value-of select="bebop:config[@name='XinhaConfig']/@path"/>
                        </xsl:when>
                        <xsl:otherwise>
                            <xsl:value-of select="bebop:config[@name='Xinha.Config']/@path"/>
                        </xsl:otherwise>
                    </xsl:choose>
                </xsl:attribute>
            </script>
    
        </xsl:if>
    
        <xsl:call-template name="process-label">
            <xsl:with-param name="widget" select="."/>
        </xsl:call-template>
        <textarea id="ta_{@name}" name="{@name}" rows="{@rows}" cols="{@cols}" wrap="{@wrap}">
            <xsl:value-of disable-output-escaping="no" select="text()"/>
        </textarea>
    </xsl:template>
    
    <xsl:template match="bebop:ccmeditor">
        
        <script data-main="{@editor_src}"
                src="{$context-prefix}/webjars/requirejs/2.3.5/require.min.js" />
        <textarea id="ta_{@name}"
                  name="{@name}"
                  class="editor-textarea"
                  rows="{@rows}"
                  cols="{@cols}"
                  wrap="{@wrap}"
                  data-context-prefix = "{$context-prefix}"
                  data-dispatcher-prefix="{$dispatcher-prefix}"
                  data-current-contentsection-id="{./@current-contentsection-id}"
                  data-current-contentsection-primaryurl="{./@current-contentsection-primaryurl}">
            <xsl:value-of disable-output-escaping="no"
                          select="text()" />
        </textarea>
        
    </xsl:template>
    
</xsl:stylesheet>
