#!/bin/bash

saxon9-transform -s:doc/foundry-documentation.xml  -xsl:start.xsl -o:doc/foundry-documentation.html theme-prefix=`pwd`
