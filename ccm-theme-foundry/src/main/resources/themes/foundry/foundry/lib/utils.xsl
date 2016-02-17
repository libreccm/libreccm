<?xml version="1.0" encoding="utf-8"?>
<!DOCTYPE stylesheet [<!ENTITY nbsp '&#160;'>
                      <!ENTITY shy '&#173;'>]>
<!--
    Copyright 2014 Jens Pelzetter for the LibreCCM Foundation
    
    This file is part of the Foundry Theme Engine for LibreCCM
    
    Foundry is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 2 of the License, or
    (at your option) any later version.

    Foundry is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with Foundry  If not, see <http://www.gnu.org/licenses/>.

-->

<!--
    This file contains utility functions and templates for Foundry. Most of them are implemented as
    XSLT 2.0 functions.
--> 
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:xs="http://www.w3.org/2001/XMLSchema"
                xmlns:bebop="http://www.arsdigita.com/bebop/1.0"
                xmlns:cms="http://www.arsdigita.com/cms/1.0"
                xmlns:foundry="http://foundry.libreccm.org"
                xmlns:nav="http://ccm.redhat.com/navigation"
                exclude-result-prefixes="xsl xs bebop cms foundry nav"
                version="2.0">
                
    
    <foundry:doc-file>
        <foundry:doc-file-title>Utility functions</foundry:doc-file-title>
        <foundry:doc-file-desc>
            <p>
                This file provides several utility functions and templates.
            </p>
        </foundry:doc-file-desc>
    </foundry:doc-file>
    
    <foundry:doc section="devel" type="function">
        <foundry:doc-params>
            <foundry:doc-param name="value" mandatory="yes" type="string">
                <p>
                    The value to evaluate.
                </p>
            </foundry:doc-param>
        </foundry:doc-params>
        <foundry:doc-result type="boolean">
            <p>
                The evaluated boolean value.
            </p>
        </foundry:doc-result>
        <foundry:doc-desc>
            <p>
                A helper function for evaluating certain string values to boolean. This function has 
                two purposes. First it simplifies some expressions. for example if you have a
                template tag with a attribute containing a (pseudo) boolean value (attribute values
                are always treated as strings) you would have to write something like:
            </p>
            <pre>
                ...
                &lt;xsl:if test="./@attr = 'true'"&gt;
                ...
                &lt;/xsl:if&gt;
                ...
            </pre>
            <p>
                Using <code>foundry:boolean</code> this can be simplified to 
            </p>
            <pre>
                ...
                &lt;xsl:if test="foundry:boolean(./@attr)"&gt;
                ...
                &lt;/xsl:if&gt;
                ...
            </pre>
            <p>
                The more important purpose is to make the usage of boolean values more user 
                friendly, especially in the templates. Using <code>foundry:boolean</code> no only
                <code>true</code> is evaluated to boolean <code>true</code>. A number of other 
                strings is also evaluated to <code>true</code>:
            </p>
            <ul>
                <li>
                    <code>true</code>
                </li>
                <li>
                    <code>TRUE</code>
                </li>
                <li>
                    <code>yes</code>
                </li>
                <li>
                    <code>YES</code>
                </li>
                <li>
                    <code>t</code>
                </li>
                <li>
                    <code>T</code>
                </li>
                <li>
                    <code>y</code>
                </li>
                <li>
                    <code>Y</code>
                </li>
            </ul>
            <p>
                All other values are evaluated to <code>false</code>.
            </p>
        </foundry:doc-desc>
    </foundry:doc>
    <xsl:function name="foundry:boolean" as="xs:boolean">
        <xsl:param name="value"/>
        <xsl:choose>
            <xsl:when test="$value = 'true'
                            or $value = 'TRUE'
                            or $value = 'yes'
                            or $value = 'YES'
                            or $value = 't'
                            or $value = 'T'
                            or $value = 'y'
                            or $value = 'Y'">
                <xsl:sequence select="true()"/>
            </xsl:when>
            <xsl:otherwise>
                <xsl:sequence select="false()"/>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:function>
    
    <foundry:doc section="devel" type="function">
        <foundry:doc-desc>
            <p>
                Helper function for generating paths to theme resources like CSS files etc. Use this
                function instead of concatenating paths yourself. For example, instead of 
            </p>
            <pre>
                document(concat($theme-prefix, 'path/to/resource/file')
            </pre>
            <p>
                use <code>foundry:gen-path</code>:
            </p>
            <pre>
                document(foundry:gen-path('path/to/resource/file'))
            </pre>
            <p>
                <code>path/to/resource/file</code> is meant as a placeholder here. A real world
                example is a settings file, for example <code>conf/global.xml</code>. For this file
                a usage of the <code>foundry:gen-path</code> function would look like this
            </p>
            <pre>
                document(foundry:gen-path('conf/global.xml')
            </pre>
            <p>
                The advantage of this function is the encapsulation of the path generation process.
                <code>foundry:gen-path</code>. 
            </p>
        </foundry:doc-desc>
        <foundry:doc-parameters>
            <foundry:doc-parameter name="path" mandatory="yes" type="string">
                The path of the file to generate to path to, relative to the theme directory.
            </foundry:doc-parameter>
        </foundry:doc-parameters>
        <foundry:doc-result type="string">
            <p>
                The absolute path for the file.
            </p>
        </foundry:doc-result>
    </foundry:doc>
    <xsl:function name="foundry:gen-path" as="xs:string">
        <xsl:param name="path" as="xs:string"/>
        
        <xsl:sequence select="foundry:gen-path($path, '')"/>
    </xsl:function>
     
    <foundry:doc section="devel" type="function">
        <foundry:doc-desc>
            <p>
                Variant of <code>gen-path</code> with an additional <code>origin</code> 
                parameter. This parameter can have three values:
                If set to <code>true</code> the file is loaded from the 
                <code>foundry</code> directory.
            </p>
            <dl>
                <dt>empty string (<code>''</code>)</dt>
                <dd>
                    The path points to a resource in the theme directory. The return value is the
                    concatenation of the theme-prefix, a slash and the path provided as first 
                    parameter. In XPath Syntax: <code>concat($theme-prefix, '/', $path</code>. 
                </dd>
                <dt>master</dt>
                <dd>
                    If the theme mode (which is set in <code>conf/global.xml</code>) is set to 
                    <code>master</code> the result is the same as for the empty string. If the
                    the theme mode is set to <code>child</code> the generated path points to
                    the parent/master theme. More exactly the result is the concatenation of the
                    context-prefix environment variable, the string <code>/themes/</code>, the
                    name of the master theme (set in <code>conf/global.xml</code>, 
                    usally <code>foundry</code>), a slash the the path provided as first parameter.
                    Or in XPath syntax: 
                    <code>concat($content-prefix, '/themes/', $master-theme, '/', $path.)</code>.
                </dd>
                <dt>internal</dt>
                <dd>
                    The path points to an internal resource which is provided by Foundry. If the 
                    theme mode is <code>master</code> the generated path is the concatenation of
                    the theme prefix, the string <code>/foundry/</code> and the path provided as
                    first parameter (XPath: <code>concat($theme-prefix, '/foundry/', $path)</code>. 
                    If the theme mode is <code>child</code> the generated path
                    is the concatenation of the context prefix, the string 
                    <code>/themes/foundry/foundry/</code> and the path provided as first parameter
                    (XPath: <code>concat($context-prefix, '/themes/foundry/foundry/', $path)</code>).
                </dd>
            </dl>
        </foundry:doc-desc>
    </foundry:doc>
    <xsl:function name="foundry:gen-path" as="xs:string">
        <xsl:param name="path" as="xs:string"/>
        <xsl:param name="origin" as="xs:string"/>

        <xsl:choose>
            <xsl:when test="$origin = ''">
                <xsl:sequence select="concat($theme-prefix, '/', $path)"/>
            </xsl:when>
            <xsl:when test="$origin = 'master' and $theme-mode = 'master'">
                <xsl:sequence select="concat($theme-prefix, '/', $path)"/>
            </xsl:when>
            <xsl:when test="$origin = 'master' and $theme-mode = 'child'">
                <xsl:sequence select="concat($context-prefix, 
                                              '/themes/', 
                                              $master-theme, 
                                              '/',
                                              $path)"/>
            </xsl:when>
            <xsl:when test="$origin = 'internal' and $theme-mode = 'master'">
                <xsl:sequence select="concat($theme-prefix, '/foundry/', $path)"/>
            </xsl:when>
            <xsl:when test="$origin = 'internal' and $theme-mode = 'child'">
                <xsl:sequence select="concat($context-prefix, 
                                              '/themes/foundry/foundry/', 
                                              $path)"/>
            </xsl:when>
            <xsl:otherwise>
                <xsl:sequence select="concat($theme-prefix, '/',  $path)"/>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:function>
    
    <!-- Templates for outputting log messages -->
    <foundry:doc section="devel" type="function-template">
        <foundry:doc-params>
            <foundry:doc-param name="msg-level"
                               mandatory="yes"
                               type="string">
                The level of the message, indicating its severity 
            </foundry:doc-param>
            <foundry:doc-param name="message"
                               mandatory="yes"
                               type="string">
                The message text.
            </foundry:doc-param>
        </foundry:doc-params>
        <foundry:doc-result type="xs:string">
            <p>
                A message string of the form <code>[Foundry $level] $message</code> with 
                <code>$level</code> and <code>$message</code> replaced by the values of the 
                parameters.
            </p>
        </foundry:doc-result>
        <foundry:doc-desc>
            <p>
                A helper template used by the other message templates like 
                <code>foundry:message-warn</code>. Outputs a message (for example in the 
                application servers log using <code>xsl:message</code>.
                Concatenates the message level with the message. 
            </p>
            <p>
                This template should not be used directly. Use the other message templates instead.
            </p>
        </foundry:doc-desc>
        <foundry:doc-see-also>
            #foundry-message-debug
        </foundry:doc-see-also>
        <foundry:doc-see-also>
            #foundry-message-info
        </foundry:doc-see-also>
        <foundry:doc-see-also>
            #foundry-message-warn
        </foundry:doc-see-also>
        <foundry:doc-see-also>
            #foundry-message-error
        </foundry:doc-see-also>
    </foundry:doc>
    <xsl:template name="foundry:message">
        <xsl:param name="msg-level" as="xs:string"/>
        <xsl:param name="message" as="xs:string"/>
        
        <xsl:variable name="log-level" select="foundry:get-setting('', 'log-level')"/>
        
        <xsl:if test="foundry:log-level($log-level) &gt;= foundry:log-level($msg-level)">
            <xsl:message>
                <xsl:value-of select="concat('[Foundry ', upper-case($log-level), '] ', $message)"/>
            </xsl:message>
        </xsl:if>
    </xsl:template>
    
    <xsl:template name="foundry:message-debug">
        <xsl:param name="message" as="xs:string"/>
        
        <xsl:call-template name="foundry:message">
            <xsl:with-param name="msg-level" select="'debug'"/>
            <xsl:with-param name="message" select="$message"/>
        </xsl:call-template>
    </xsl:template>
    
    <foundry:doc section="devel" type="function-template">
        <foundry:doc-params>
            <foundry:doc-param name="message"
                               mandatory="yes"
                               type="string">
                The message text.
            </foundry:doc-param>
        </foundry:doc-params>
        <foundry:doc-result type="xs:string">
            <p>
                A message string of the form <code>[Foundry INFO] $message</code> with 
                <code>$message</code> replaced by the value of the parameter.
            </p>
        </foundry:doc-result>
        <foundry:doc-desc>
            <p>
                Helper function to generate an info message. This template generates a 
                <code>&lt;xsl:message&gt;</code> element which causes the XSL processor to output
                a message in the application server log. The message will on shown if the log level
                in the global configuration is set to <code>info</code> or <code>error</code>.
            </p>
        </foundry:doc-desc>
        <foundry:doc-see-also>
            #foundry-message-warn
        </foundry:doc-see-also>
        <foundry:doc-see-also>
            #foundry-message-error
        </foundry:doc-see-also>
    </foundry:doc>
    <xsl:template name="foundry:message-info">
        <xsl:param name="message" as="xs:string"/>
        
        <xsl:call-template name="foundry:message">
            <xsl:with-param name="msg-level" select="'info'"/>
            <xsl:with-param name="message" select="$message"/>
        </xsl:call-template>
    </xsl:template>
    
    <foundry:doc section="devel" type="function">
        <foundry:doc-params>
            <foundry:doc-param name="message"
                               mandatory="yes">
                The message text.
            </foundry:doc-param>
        </foundry:doc-params>
        <foundry:doc-result type="xs:string">
            <p>
                A message string of the form <code>[Foundry WARNING] $message</code> with 
                <code>$message</code> replaced by the value of the parameter.
            </p>
        </foundry:doc-result>
        <foundry:doc-desc>
            <p>
                Helper function to generate an info message. This function be used together with
                <code>&lt;xsl:message&gt;</code> to output a message in the CCM log warning
                the administrator about some things in the theme, for example a missing 
                configuration file. Example:
            </p>
            <pre>
                ...
                &lt;xsl:message&gt;
                    &lt;xsl:message select="foundry:message-info('Something is strange...')" /&gt;
                &lt;/xsl:message&gt;
            </pre>
        </foundry:doc-desc>
        <foundry:doc-see-also>
            #foundry-message-info
        </foundry:doc-see-also>
        <foundry:doc-see-also>
            #foundry-message-error
        </foundry:doc-see-also>
    </foundry:doc>
    <xsl:template name="foundry:message-warn">
        <xsl:param name="message" as="xs:string"/>
        
        <xsl:call-template name="foundry:message">
            <xsl:with-param name="msg-level" select="'warn'"/>
            <xsl:with-param name="message" select="$message"/>
        </xsl:call-template>
    </xsl:template>
    
    <foundry:doc section="devel" type="function">
        <foundry:doc-params>
            <foundry:doc-param name="message"
                               mandatory="yes">
                The message text.
            </foundry:doc-param>
        </foundry:doc-params>
        <foundry:doc-result type="xs:string">
            <p>
                A message string of the form <code>[Foundry ERROR] $message</code> with 
                <code>$message</code> replaced by the value of the parameter.
            </p>
        </foundry:doc-result>
        <foundry:doc-desc>
            <p>
                Helper function to generate an info message. This function be used together with
                <code>&lt;xsl:message&gt;</code> to output a message in the CCM log when 
                something goes wrong in the theme, for example when a layout file has a wrong 
                structure. Example:
            </p>
            <pre>
                ...
                &lt;xsl:message&gt;
                    &lt;xsl:message select="foundry:message-info('Some error has occurred...')" /&gt;
                &lt;/xsl:message&gt;
                ...
            </pre>
        </foundry:doc-desc>
        <foundry:doc-see-also>
            #foundry-message-info
        </foundry:doc-see-also>
        <foundry:doc-see-also>
            #foundry-message-warn
        </foundry:doc-see-also>
    </foundry:doc>
    <xsl:template name="foundry:message-error">
        <xsl:param name="message" as="xs:string"/>
        
        <xsl:call-template name="foundry:message">
            <xsl:with-param name="msg-level" select="'error'"/>
            <xsl:with-param name="message" select="$message"/>
        </xsl:call-template>
    </xsl:template>
    
    <xsl:function name="foundry:log-level" as="xs:integer">
        <xsl:param name="level"/>
                
        <xsl:choose>
            <xsl:when test="lower-case($level) = 'error'">
                <xsl:sequence select="0"/>
            </xsl:when>
            <xsl:when test="lower-case($level) = 'warn'">
                <xsl:sequence select="1"/>
            </xsl:when>
            <xsl:when test="lower-case($level) = 'info'">
                <xsl:sequence select="2"/>
            </xsl:when>
            <xsl:when test="lower-case($level) = 'debug'">
                <xsl:sequence select="3"/>
            </xsl:when>
            <xsl:otherwise>
                <xsl:sequence select="-1"/>
            </xsl:otherwise>
        </xsl:choose>
        
    </xsl:function>
    
    <!-- End templates for outputting log messages -->
    
    <foundry:doc section="devel" type="function">
        <foundry:doc-params>
            <foundry:doc-param name="node">
                The node from which the value of the attribute is read.
            </foundry:doc-param>
            <foundry:doc-param name="attribute-name">
                The attribute to check for.
            </foundry:doc-param>
            <foundry:doc-param name="default-value">
                The default value if the attribute is not set.
            </foundry:doc-param>
        </foundry:doc-params>
        <foundry:doc-result>
            <p>
                The value of the attribute if it is set on the current element, the 
                <code>default-value</code> otherwise.
            </p>
        </foundry:doc-result>
        <foundry:doc-desc>
            <p>
                A helper function for retrieving an attribute value from an element. If the 
                attribute is set on the current element the value of the attribute is used as 
                result. If the attribute is not set the <code>default-value</code> is used. This 
                method is used by several layout tags with optional attributes. A common use pattern 
                looks like this:
            </p>
            <pre>
                &lt;xsl:template match="example"&gt;
                    &lt;xsl:variable name="width" 
                select="foundry:get-attribute-value(current(), 'width', '640')" /&gt;
                    &lt;xsl:variable name="height" 
                select="foundry:get-attribute-value(current(), 'height', '480')" /&gt;
                /&lt;xsl:template&gt;
            </pre>
            <p>
                In this example, the element <code>example</code> has two optional attributes:
                <code>with</code> and <code>height</code>. If the attribute is set in processed XML,
                the value set there is used. Otherwise the default value (<code>640</code> 
                respectively <code>480</code>) is used. Without this function a code block like the
                one in the <code>xsl:choose</code> block of this function would be necessary for
                each of the variables.
            </p>
        </foundry:doc-desc>
    </foundry:doc>
    <xsl:function name="foundry:get-attribute-value">
        <xsl:param name="node"/>
        <xsl:param name="attribute-name"/>
        <xsl:param name="default-value"/>
        
        <xsl:choose>
            <xsl:when test="$node/@*[name() = $attribute-name]">
                <xsl:sequence select="$node/@*[name() = $attribute-name]"/>
            </xsl:when>
            <xsl:otherwise>
                <xsl:sequence select="$default-value"/>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:function>
    
    <foundry:doc section="devel" type="function">
        <foundry:doc-desc>
            <p>
                Convenient function for calling <code>foundry:get-setting</code> with only the
                module name and setting name. 
            </p>
        </foundry:doc-desc>
        <foundry:doc-params>
            <foundry:doc-param name="module" mandatory="yes" type="string">
                <p>
                    The module of the settings. May be an empty string (<code>''</code>). 
                </p>
            </foundry:doc-param>
            <foundry:doc-param name="setting" mandatory="yes" type="string">
                <p>
                    The name of the setting to retrieve.
                </p>
            </foundry:doc-param>
        </foundry:doc-params>
        <foundry:doc-result type="string">
            <p>
                The value of the setting.
            </p>
        </foundry:doc-result>
    </foundry:doc>
    <xsl:function name="foundry:get-setting" as="xs:string">
        <xsl:param name="module" as="xs:string"/>
        <xsl:param name="setting" as="xs:string"/>
        
        <xsl:sequence select="foundry:get-setting($module, $setting, '', '')"/>
    </xsl:function>
    
    <foundry:doc section="devel" type="function">
        <foundry:doc-desc>
            <p>
                Convenient function for calling <code>foundry:get-setting</code> with only the
                module name, the setting name and an default value. 
            </p>
        </foundry:doc-desc>
        <foundry:doc-params>
            <foundry:doc-param name="module" mandatory="yes" type="string">
                <p>
                    The module of the settings. May be an empty string (<code>''</code>). 
                </p>
            </foundry:doc-param>
            <foundry:doc-param name="setting" mandatory="yes"  type="string">
                <p>
                    The name of the setting to retrieve.
                </p>
            </foundry:doc-param>
            <foundry:doc-param name="default" mandatory="yes" type="string">
                <p>
                    A default value which is used when the setting is not configured.
                </p>
            </foundry:doc-param>
        </foundry:doc-params>
        <foundry:doc-result type="string">
            <p>
                The value of the setting or the default value if the setting is not configured.
            </p>
        </foundry:doc-result>
    </foundry:doc>
    <xsl:function name="foundry:get-setting" as="xs:string">
        <xsl:param name="module" as="xs:string"/>
        <xsl:param name="setting" as="xs:string"/>
        <xsl:param name="default" as="xs:string"/>
        
        <xsl:sequence select="foundry:get-setting($module, $setting, $default, '')"/>
    </xsl:function>
    
    <foundry:doc section="devel">
        <foundry:doc-params>
            <foundry:doc-param name="module" mandatory="yes" type="string">
                <p>
                    The module of the settings. At the moment this corresponds to the name of the file
                    in the <code>conf</code> directory. The empty string as value corresponds to the
                    <code>global.xml</code> file.
                </p>
            </foundry:doc-param>
            <foundry:doc-param name="setting" mandatory="yes" type="string">
                <p>
                    The name of the setting to retrieve.
                </p>
            </foundry:doc-param>
            <foundry:doc-param name="default" mandatory="no" type="string">
                <p>
                    The value to use if there is no entry for the setting in the settings file.
                </p>
            </foundry:doc-param>
            <foundry:doc-param name="node" mandatory="no" type="string">
                <p>
                    A node from the layout template which overrides the value from the configuration.
                </p>
            </foundry:doc-param>
        </foundry:doc-params>
        <foundry:doc-result type="string">
            <p>
                The value of the requested setting or if no value has been set the provided default 
                value. If no default value has been provided the result is an empty string.
            </p>
        </foundry:doc-result>
        <foundry:doc-desc>
            This function retrieves the value of a setting from the theme configuration. For
            more informations about the configuration system of Foundry please refer to the 
            <em>configuration</em> section of the Foundry documentation.
        </foundry:doc-desc>
    </foundry:doc>
    <xsl:function name="foundry:get-setting" as="xs:string">
        <xsl:param name="module" as="xs:string"/>
        <xsl:param name="setting" as="xs:string"/>
        <xsl:param name="default" as="xs:string"/>
        <xsl:param name="node"/>
        
        <xsl:choose>
            <xsl:when test="$node and $node != ''">
                <xsl:sequence select="$node"/>
            </xsl:when>
            <xsl:when test="$module = '' and document(foundry:gen-path('conf/global.xml', ''))/foundry:configuration/setting[@id=$setting]">
                <xsl:sequence select="document(foundry:gen-path('conf/global.xml', ''))/foundry:configuration/setting[@id=$setting]"/>
            </xsl:when>
            <xsl:when test="not($module = '') and document(foundry:gen-path(concat('conf/', $module, '.xml', '')))/foundry:configuration/setting[@id=$setting]">
                <xsl:sequence select="document(foundry:gen-path(concat('conf/', $module, '.xml', '')))/foundry:configuration/setting[@id=$setting]"/>
            </xsl:when>
            <xsl:otherwise>
                <xsl:choose>
                    <xsl:when test="$module = ''">
                        <xsl:message>
                            <xsl:value-of select="concat('[WARN] Setting &quot;', 
                                                         $setting, 
                                                         '&quot; not found in global.xml')"/>
                        </xsl:message>
                    </xsl:when>
                    <xsl:otherwise>
                        <xsl:message>
                            <xsl:value-of select="concat('[WARN] Setting &quot;', 
                                                         $setting, 
                                                         '&quot; not found in ',
                                                         $module, '.xml')"/>
                        </xsl:message>
                    </xsl:otherwise>
                </xsl:choose>
                
                <xsl:sequence select="$default"/>
            </xsl:otherwise>
        </xsl:choose>
        
    </xsl:function>
    
    <xsl:function name="foundry:get-static-text" as="xs:string">
        <xsl:param name="module" as="xs:string"/>
        <xsl:param name="id" as="xs:string"/>
        
        <xsl:sequence select="foundry:get-static-text($module, $id, true(), $lang)"/>
    </xsl:function>
    
    <xsl:function name="foundry:get-internal-static-text" as="xs:string">
        <xsl:param name="module" as="xs:string"/>
        <xsl:param name="id" as="xs:string"/>
        
        <xsl:sequence select="foundry:get-static-text($module, $id, true(), $lang, true())"/>
    </xsl:function>
    
    
    <xsl:function name="foundry:get-static-text" as="xs:string">
        <xsl:param name="module" as="xs:string"/>
        <xsl:param name="id" as="xs:string"/>
        <xsl:param name="html" as="xs:boolean"/>
        
        <xsl:sequence select="foundry:get-static-text($module, $id, $html, $lang)"/>
    </xsl:function>
    
    <xsl:function name="foundry:get-static-text" as="xs:string">
        <xsl:param name="module" as="xs:string"/>
        <xsl:param name="id" as="xs:string"/>
        <xsl:param name="html" as="xs:boolean"/>
        <xsl:param name="lang" as="xs:string"/>
        
        <xsl:sequence select="foundry:get-static-text($module, $id, $html, $lang, false())"/>
        
    </xsl:function>
    
    <foundry:doc section="devel" type="function">
        <foundry:doc-params>
            <foundry:doc-param name="module" mandatory="yes" type="string">
                <p>
                    The module of the settings. At the moment this corresponds to the name of the file
                    in the <code>texts</code> directory. The empty string as value corresponds to the
                    <code>global.xml</code> file.
                </p>
            </foundry:doc-param>
            <foundry:doc-param name="id" mandatory="yes" type="string">
                <p>
                    The name of the text to retrieve.
                </p>
            </foundry:doc-param>
            <foundry:doc-param name="lang" mandatory="no" type="string">
                <p>
                    The language to retrieve. Normally there is no need to set this parameter because
                    it is determined automatically.
                </p>
            </foundry:doc-param>
        </foundry:doc-params>
        <foundry:doc-result type="string">
            <p>
                The requested static text. If there is no value for the requested static text in the
                module provided by the module parameter the value depends if the debug mode is 
                enabled or not. If the debug mode is <em>not</em> not enabled the result is an empty 
                string. If the debug mode is enabled, a identifier of the text (the value of the 
                <code>id</code> parameter) is displayed. If you point the mouse pointer of the 
                placeholder, the complete path of the text is shown as hovering box.
            </p>
        </foundry:doc-result>
        <foundry:doc-desc>
            <p>
                Retrieves at static text. For more informations about static texts in Foundry please
                refer to the static texts section in the Foundry documentation.
            </p>
        </foundry:doc-desc>
    </foundry:doc>
    <xsl:function name="foundry:get-static-text" as="xs:string">
        <xsl:param name="module" as="xs:string"/>
        <xsl:param name="id" as="xs:string"/>
        <xsl:param name="html" as="xs:boolean"/>
        <xsl:param name="lang" as="xs:string"/>
        <xsl:param name="internal" as="xs:boolean"/>
        
        <xsl:variable name="origin" 
                      select="if ($internal)
                              then 'internal'
                              else ''"/>

        <xsl:choose>
            <xsl:when test="$module = '' and document(foundry:gen-path('texts/global.xml', $origin))/foundry:static-texts/text[@id=$id]/translation[@lang=$lang]">
                <xsl:sequence select="document(foundry:gen-path('texts/global.xml', $origin))/foundry:static-texts/text[@id=$id]/translation[@lang=$lang]"/>
            </xsl:when>
            <xsl:when test="not($module = '') and document(foundry:gen-path(concat('texts/', $module, '.xml'), $origin))/foundry:static-texts/text[@id=$id]/translation[@lang=$lang]">
                <xsl:sequence select="document(foundry:gen-path(concat('texts/', $module, '.xml'), $origin))/foundry:static-texts/text[@id=$id]/translation[@lang=$lang]"/>
            </xsl:when>
            <xsl:otherwise>
                <xsl:choose>
                    <xsl:when test="foundry:debug-enabled()">
                        <xsl:choose>
                            <xsl:when test="$html">
                                <span class="foundry-debug-missing-translation">
                                    <span class="foundry-placeholder">
                                        <xsl:value-of select="$id"/>
                                    </span>
                                    <span class="foundry-missing-translation-path">
                                        <xsl:choose>
                                            <xsl:when test="$module = ''">
                                                <xsl:value-of select="concat(foundry:gen-path('texts/global.xml'), '/foundry:static-texts/text[@id=', $id, ']/translation[@lang=', $lang, ']')"/>
                                            </xsl:when>
                                            <xsl:otherwise>
                                                <xsl:value-of select="concat(foundry:gen-path(concat('texts/', $module, '.xml')), '/foundry:static-texts/text[@id=', $id, ']/translation[@lang=', $lang, ']')"/>
                                            </xsl:otherwise>
                                        </xsl:choose>
                                    </span>
                                </span>
                                
                            </xsl:when>
                            <xsl:otherwise>
                                <xsl:sequence select="$id"/>
                            </xsl:otherwise>
                        </xsl:choose>
                    </xsl:when>
                    <xsl:otherwise>
                        <xsl:sequence select="''"/>
                    </xsl:otherwise>
                </xsl:choose>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:function>
    
    <foundry:doc section="devel" type="function">
        <foundry:doc-result>
            <p>
                <code>true</code> if the debug mode if active, <code>false</code> otherwise.
            </p>
        </foundry:doc-result>
        <foundry:doc-desc>
            <p>
                A helper function to determine if the debug mode should be enabled. The debug mode
                of foundry is automatically enabled if the theme is viewed as development theme.
            </p>
        </foundry:doc-desc>
    </foundry:doc>
    <xsl:function name="foundry:debug-enabled" as="xs:boolean">
        <xsl:choose>
            <xsl:when test="contains($theme-prefix, 'devel-themedir')">
                <xsl:sequence select="true()"/>
            </xsl:when>
            <xsl:otherwise>
                <xsl:sequence select="false()"/>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:function>
    
    <xsl:function name="foundry:parse-link">
        <xsl:param name="link"/>
        
        <xsl:sequence select="foundry:parse-link($link, $dispatcher-prefix)"/>
    </xsl:function>
    
    <foundry:doc section="devel" type="function">
        <foundry:doc-desc>
            <p>
                Helper template to adjust links. (Copied from Mandalay)
            </p>
        </foundry:doc-desc>
        <foundry:doc-param name="link">
            <p>The link to parse.</p>
        </foundry:doc-param>
        <foundry:doc-param name="prefix">
            <p>
                The prefix to use for the link. Default is the dispatcher prefix.
            </p>
        </foundry:doc-param>
        <foundry:doc-result>
            <p>
                The adjusted link.
            </p>
        </foundry:doc-result>
    </foundry:doc>
    <xsl:function name="foundry:parse-link">
        <xsl:param name="link"/>
        <xsl:param name="prefix"/>
        
        <xsl:choose>
            <xsl:when test="starts-with($link, 'http://')">
                <xsl:sequence select="$link"/>
            </xsl:when>

            <xsl:when test="starts-with($link, '#')">
                <xsl:sequence select="$link"/>
            </xsl:when>

            <xsl:when test="starts-with($link, '?')">
                <xsl:sequence select="$link"/>
            </xsl:when>

            <xsl:when test="starts-with($link, '*/')">
                <xsl:sequence select="substring($link, 2)"/>
            </xsl:when>

            <xsl:when test="starts-with($link, '/')">
                <xsl:choose>
                    <!-- DE Workaround für die unterschiedliche Angabe der Links (einige beinhalten das Prefix, andere nicht) -->
                    <!-- EN Workaround for different kind of link generation (some include the prefix, some don't) -->
                    <xsl:when test="starts-with($link, $prefix)">
                        <xsl:sequence select="$link"/>
                    </xsl:when>
                    <xsl:otherwise>
                        <xsl:sequence select="concat($prefix, $link)"/>
                    </xsl:otherwise>
                </xsl:choose>
            </xsl:when>

            <xsl:otherwise>
                <xsl:sequence select="concat($prefix, '/', $link)"/>
            </xsl:otherwise>
        </xsl:choose>
        
    </xsl:function>
    
    <!--<foundry:doc section="devel" type="function-template">
        <foundry:doc-desc>
            <p>
                Helper template for processing additional attributes. This are copied from the data 
                tree XML created by CCM to the HTML output generated by Foundry without any further
                processing.
            </p>
        </foundry:doc-desc>
    </foundry:doc>
    <xsl:template name="foundry:process-attributes">
        <xsl:for-each select="@*">
            <xsl:if test="(name() != 'href_no_javascript')
                       and (name() != 'hint')
                       and (name() != 'label')">
                <xsl:attribute name="{name()}">
                    <xsl:sequence select="."/>
                </xsl:attribute>
            </xsl:if>
        </xsl:for-each>
        <xsl:if test="name() = 'bebop:formWidget' and (not(@id) and @name)">
            <xsl:attribute name="id">
                <xsl:sequence select="@name"/>
            </xsl:attribute>
        </xsl:if>
    </xsl:template>-->
    
    <foundry:doc section="devel" type="function">
        <xsl:doc-desc>
            <p>
                A helper function for reading the current category from the <code>datatree</code>. The 
                function joins the titles of all categories in <code>nav:categoryPath</code> to a 
                string. The tokens a separated by a slash (<code>/</code>).
            </p>
        </xsl:doc-desc>
        <foundry:doc-result>
            <p>
                The path of the current category.
            </p>
        </foundry:doc-result>
    </foundry:doc>
    <xsl:function name="foundry:read-current-category">
        <xsl:sequence select="string-join($data-tree/nav:categoryPath/nav:category/@name, '/')"/>
    </xsl:function>
    
    <xsl:function name="foundry:shying" as="xs:string">
        <xsl:param name="text" as="xs:string"/>
        
        <xsl:sequence select="translate($text, '\-', '\-&shy;')"/>
    </xsl:function>
    
    <xsl:function name="foundry:title" as="xs:string">
        <xsl:param name="useCategoryMenu"/>
        <xsl:param name="useRootCategoryIndexItemTitle"/>

        <xsl:choose>
            <!-- Use fixed title for some special content items -->
            <xsl:when test="$data-tree//cms:contentPanel">
                <xsl:choose>
                    <!-- Glossary -->
                    <xsl:when test="$data-tree//cms:contentPanel/cms:item/type/label = 'Glossary Item'">
                        <xsl:sequence select="foundry:get-static-text('', 'layout/page/title/glossary')"/>
                    </xsl:when>
                    <!-- FAQ -->
                    <xsl:when test="$data-tree//cms:contentPanel/cms:item/type/label = 'FAQ Item'">
                        <xsl:sequence select="foundry:get-static-text('', 'layout/page/title/faq')"/>
                    </xsl:when>
                    <!-- Else use title of CI -->
                    <xsl:otherwise>
                        <xsl:sequence select="foundry:shying($data-tree//cms:contentPanel/cms:item/title[1])"/>
                    </xsl:otherwise>
                </xsl:choose>
            </xsl:when>
            <!-- Localised title for A-Z list -->
            <xsl:when test="$data-tree/bebop:title = 'AtoZ'">
                <xsl:sequence select="foundry:get-static-text('', 'layout/page/title/atoz')"/>
            </xsl:when>
            <!-- Localised title for search -->
            <xsl:when test="$data-tree/bebop:title = 'Search'">
                <xsl:sequence select="foundry:get-static-text('', 'layout/page/title/search')"/>
            </xsl:when>
            <!-- Localised title for log in -->
            <xsl:when test="$data-tree/@application = 'login'">
                <xsl:sequence select="foundry:get-static-text('', 'layout/page/title/login')"/>
            </xsl:when>
            <!-- Localised title for sitemap -->
            <xsl:when test="$data-tree/@id = 'sitemapPage'">
                <xsl:sequence select="foundry:get-static-text('', 'layout/page/title/sitemap')"/>
            </xsl:when>
            <!-- Title for content section-->
            <xsl:otherwise>
                <xsl:choose>
                    <xsl:when test="$data-tree/nav:categoryMenu[@id=$useCategoryMenu]//nav:category[@isSelected='true']">
                        <xsl:for-each select="$data-tree/nav:categoryMenu[@id=$useCategoryMenu]//nav:category[@isSelected='true']">
                            <xsl:choose>
                                <!-- Special rule: Use content item title for root-page in navigation -->
                                <xsl:when test="position() = last() and position() = 1 and $useRootCategoryIndexItemTitle">
                                    <xsl:choose>
                                        <xsl:when test="$data-tree//title">
                                            <xsl:variable name="page-title">
                                                <xsl:value-of select="$data-tree/*/cms:item/title[1]"/>
                                            </xsl:variable>
                                            <!--<xsl:sequence select="foundry:shying($data-tree//title[1])"/>-->
                                            <xsl:sequence select="foundry:shying($page-title)"/>
                                        </xsl:when>
                                        <xsl:otherwise>
                                            <xsl:sequence select="''"/>
                                        </xsl:otherwise>
                                    </xsl:choose>
                                </xsl:when>
                                <!-- Else use the name of the category -->
                                <xsl:when test="position() = last()">
                                    <xsl:sequence select="foundry:shying(./@title)"/>
                                </xsl:when>
                            </xsl:choose>
                        </xsl:for-each>
                    </xsl:when>
                    <xsl:otherwise>
                        <xsl:sequence select="''"/>
                    </xsl:otherwise>
                </xsl:choose>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:function>
    
    <xsl:function name="foundry:truncate-text">
        <xsl:param name="text" as="xs:string"/>
        <xsl:param name="limit" as="xs:integer"/>
        <xsl:param name="min-length" as="xs:integer" />
        
        <xsl:value-of select="foundry:truncate-text($text, $limit, $min-length, 'center')"/>
    </xsl:function>
    
    <xsl:function name="foundry:truncate-text">
        <xsl:param name="text" as="xs:string"/>
        <xsl:param name="limit" as="xs:integer"/>
        <xsl:param name="min-length" as="xs:integer" />
        <xsl:param name="mode" as="xs:string"/>
        
        <xsl:variable name="length" select="string-length($text)"/>
            
        <xsl:choose>
            <!-- Truncate text at first punctation mark -->
            <xsl:when test="$mode = 'mark'">
                <xsl:choose>
                    <xsl:when test="$length &gt; 2 * $limit">
                        <xsl:variable name="mark-dot">
                            <xsl:choose>
                                <xsl:when test="contains($text, '.')">
                                    <xsl:value-of select="string-length(substring-before($text, '.'))"/>
                                </xsl:when>
                                <xsl:otherwise>
                                    <xsl:value-of select="$length"/>
                                </xsl:otherwise>
                            </xsl:choose>
                        </xsl:variable>
                        <xsl:variable name="mark-quest">
                            <xsl:choose>
                                <xsl:when test="contains($text, '?')">
                                    <xsl:value-of select="string-length(substring-before($text, '?'))"/>
                                </xsl:when>
                                <xsl:otherwise>
                                    <xsl:value-of select="$length"/>
                                </xsl:otherwise>
                            </xsl:choose>
                        </xsl:variable>
                        <xsl:variable name="mark-exclam">
                            <xsl:choose>
                                <xsl:when test="contains($text, '!')">
                                    <xsl:value-of select="string-length(substring-before($text, '!'))"/>
                                </xsl:when>
                                <xsl:otherwise>
                                    <xsl:value-of select="$length"/>
                                </xsl:otherwise>
                            </xsl:choose>
                        </xsl:variable>
                        <xsl:variable name="mark-dash">
                            <xsl:choose>
                                <xsl:when test="contains($text, ' - ')">
                                    <xsl:value-of select="string-length(substring-before($text, ' - '))"/>
                                </xsl:when>
                                <xsl:otherwise>
                                    <xsl:value-of select="$length"/>
                                </xsl:otherwise>
                            </xsl:choose>
                        </xsl:variable>
                        <xsl:variable name="mark-longdash">
                            <xsl:choose>
                                <xsl:when test="contains($text, ' – ')">
                                    <xsl:value-of select="string-length(substring-before($text, ' – '))"/>
                                </xsl:when>
                                <xsl:otherwise>
                                    <xsl:value-of select="$length"/>
                                </xsl:otherwise>
                            </xsl:choose>
                        </xsl:variable>
                        <xsl:variable name="mark-colon">
                            <xsl:choose>
                                <xsl:when test="contains($text, ': ')">
                                    <xsl:value-of select="string-length(substring-before($text, ': '))"/>
                                </xsl:when>
                                <xsl:otherwise>
                                    <xsl:value-of select="$length"/>
                                </xsl:otherwise>
                            </xsl:choose>
                        </xsl:variable>
                        
                        <xsl:variable name="mark" 
                                      select="min((min((min((min((min(($mark-dot, 
                                                                  $mark-quest)), 
                                                              $mark-exclam)), 
                                                          $mark-dash)), 
                                                      $mark-longdash)), 
                                                  $mark-colon))"/>
                        
                        <xsl:choose>
                            <xsl:when test="$mark &lt; 2 * $limit">
                                <xsl:value-of select="substring($text, 1, $mark)"/>
                            </xsl:when>
                            <xsl:otherwise>
                                <xsl:value-of select="concat(substring($text, 
                                                                       1, 
                                                                       (2 * $limit) - 3), 
                                                             '...')"/>
                            </xsl:otherwise>
                        </xsl:choose>
                    </xsl:when>
                    <xsl:otherwise>
                        <xsl:value-of select="$text"/>
                    </xsl:otherwise>
                </xsl:choose>
            </xsl:when>
            <xsl:when test="$mode = 'center'">
                <!-- Truncate in the middle of the string -->
                <xsl:choose>
                    <xsl:when test="($length > $limit) and ($length - $limit &lt; $min-length)">
                        <!-- Truncate string to length - min-length-->
                        <xsl:variable name="part-length">
                            <xsl:value-of select="(($length - $min-length) div 2) - 1"/>
                        </xsl:variable>
                        <xsl:value-of select="concat(substring($text, 1, ceiling($part-length)),
                                             '...',
                                             substring($text, $length - floor($part-length)))"/>
                    </xsl:when>
                    <xsl:when test="$length - $limit > $min-length">
                        <!-- Truncate to length - limit -->
                        <xsl:variable name="part-length">
                            <xsl:value-of select="(($limit - 3) div 2) - 1"/>
                        </xsl:variable>
                        
                        <xsl:value-of select="concat(substring($text, 1, floor($part-length)), 
                                                     '...', 
                                                     substring($text, 
                                                               $length - ceiling($part-length)))"/>
                    </xsl:when>
                    <xsl:otherwise>
                        <!-- No need to truncate text -->
                        <xsl:value-of select="$text"/>
                    </xsl:otherwise>
                </xsl:choose>
            </xsl:when>
            <xsl:otherwise>
                <!-- Truncate at end of string -->
                <xsl:choose>
                    <xsl:when test="($length > $limit) and ($length - $limit &lt; $min-length)">
                        <!-- Truncate text to length - min-length -->
                        <xsl:value-of select="concat(substring($text, 1, $length - $min-length), 
                                                     '...')"/>
                    </xsl:when>
                    <xsl:when test="$length - $limit > $min-length">
                        <!-- truncate text to limit -->
                        <xsl:value-of select="concat(substring($text, 1, $limit), '...')"/>
                    </xsl:when>
                    <xsl:otherwise>
                        <!-- Do not truncate -->
                        <xsl:value-of select="$text"/>
                    </xsl:otherwise>
                </xsl:choose>
            </xsl:otherwise>
        </xsl:choose>
        
    </xsl:function>
    
</xsl:stylesheet>
