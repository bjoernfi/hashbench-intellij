#!/bin/bash

set -e

if [ "$#" -lt 1 ]; then
    echo "Usage: $0 <url>"
    exit 1
fi

URL=$1

echo "$(cat <<EOF
<?xml version="1.0"?>
<!--
The <plugins> element (required) contains the description of the plugins
available at this repository.
-->
<plugins>
  <!--
  Each <plugin> element (required) describes one plugin in the repository.
  Attributes:
   - "id" (required) - used by JetBrains IDEs to uniquely identify
     a plugin. Must match <id> in the plugin.xml file.
   - "url" (required) - URL to download the plugin JAR/ZIP file.
     Must be HTTPS.
   - "version" (required) - version of this plugin. Must match <version>
     in the plugin.xml file.
  -->
  <plugin version="1.0.0"
    id="hashbench"
    url="$URL">
    <!--
    The <idea-version> element (required) must match the same element
    in the plugin.xml file.
    -->
    <name>hashbench</name>
    <idea-version since-build="232"/>
  </plugin>
</plugins>
EOF
)"