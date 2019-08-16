#!/bin/bash

#*******************************************************************************
# Copyright (c) 2015, 2018 Lablicate GmbH.
# 
# All rights reserved. This program and the accompanying materials
# are made available under the terms of the Eclipse Public License v1.0
# which accompanies this distribution, and is available at
# http://www.eclipse.org/legal/epl-v10.html
# 
# Contributors:
# 	Dr. Philip Wenig - initial API and implementation
#*******************************************************************************

#
# The actual version, e.g. 0.8.0
#
echo -n "Please enter the actual version (0.8.0): "
read actual_version
if [ -z $actual_version ]; then
  echo "No valid actual version."
  exit
fi

#
# The new version, e.g. 0.8.0
#
echo -n "Please enter a new version (0.8.0): "
read new_version
if [ -z $new_version ]; then
  echo "No valid new version."
  exit
fi

#
# The workspace path, use the eclipse workspace path, e.g. /home/openchrom/www.openchrom.net/OpenChrom/trunk
# Use "$workspace" as the actual workspace could contains white spaces.
#
echo -n "Please enter the workspace path (/home/openchrom/www.openchrom.net/Development/OpenChrom/0.6.0/workspace): "
read workspace
if [ -z "$workspace" ]; then
  echo "No valid workspace."
  exit
fi

#
# pom.xml
#
# Replaces a version tag, commonly in pom.xml files.
# <version>0.8.0-SNAPSHOT</version>
#
function replace_version_tag {

  actual='<version>'$actual_version'-SNAPSHOT<\/version>'
  new='<version>'$new_version'-SNAPSHOT<\/version>'

  sed -i s/"$actual"/"$new"/g $1
}

#
# MANIFEST.MF
#
# Replaces the bundle version.
# bundle-version="0.8.0"
#
function replace_bundle_version {

  actual='bundle-version=\"'$actual_version'\"'
  new='bundle-version=\"'$new_version'\"'

  sed -i s/"$actual"/"$new"/g $1
}

#
# MANIFEST.MF
#
# Replaces the manifest bundle version.
# Bundle-Version: 0.8.0.qualifier
#
function replace_manifest_bundle_version {

  actual='Bundle-Version: '$actual_version'.qualifier'
  new='Bundle-Version: '$new_version'.qualifier'

  sed -i s/"$actual"/"$new"/g $1
}

#
# feature.xml
#
# Replaces the version.
# version="0.8.0.qualifier"
#
function replace_feature_version {

  actual='version=\"'$actual_version'.qualifier\"'
  new='version=\"'$new_version'.qualifier\"'

  sed -i s/"$actual"/"$new"/g $1
}

#
# *.product
#
# Replaces the version.
# version="0.8.0"
#
function replace_version {

  actual='version=\"'$actual_version'\"'
  new='version=\"'$new_version'\"'

  sed -i s/"$actual"/"$new"/g $1
}

#
# Calls the corresponding replace functions for different file types.
# MANIFEST.MF, pom.xml, feature.xml, site.xml, *.product
#
function change_version {
  
  while [ $1 ]; do
    echo -n "."
      case $1 in
        */MANIFEST.MF ) replace_bundle_version $1; replace_manifest_bundle_version $1;;
        */pom.xml ) replace_version_tag $1;;
        */feature.xml ) replace_feature_version $1;;
        */*.product ) replace_version $1; replace_feature_version $1;;
      esac
    shift
  done

  echo "done"
}


echo "Actual version: " $actual_version
echo "New version: " $new_version
echo "Update on workspace: " $workspace

#
# Start processing.
#
echo "Start changing the version."
echo ""

#
# Change MANIFEST.MF files.
#
echo "Change MANIFEST.MF files"
  change_version $(find "$workspace" -type f -name MANIFEST.MF)
echo ""

#
# Change pom.xml files.
#
echo "Change pom.xml files"
  change_version $(find "$workspace" -type f -name pom.xml)
echo ""

#
# Change feature.xml files.
#
echo "Change feature.xml files"
  change_version $(find "$workspace" -type f -name feature.xml)
echo ""

#
# Change *.product files.
#
echo "Change *.product files"
  change_version $(find "$workspace" -type f -name *product)
echo ""

echo "finished"
echo "bye"
