<?xml version="1.0"?>
<!DOCTYPE stylesheet [<!ENTITY nbsp '&#160;'>]>

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
    This file is the main entry point for Foundry base theme. 

    If want to use a different theme than /themes/foundry as your
    parent theme you must change the path the <xsl:import> element
    to point to the start.xsl of your parent theme. Also you have to 
    set the base theme in the conf/global.xml file.
-->

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:foundry="http://foundry.libreccm.org"
                version="2.0">
    
    <!-- Import the Foundry main templates all other things are defined there -->
    <xsl:import href="/themes/foundry/start.xsl"/>
    
    <!-- Import custom extensions -->
    <xsl:import href="user/user.xsl"/>
    
</xsl:stylesheet>
