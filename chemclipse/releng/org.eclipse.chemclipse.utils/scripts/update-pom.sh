#!/bin/bash

#*******************************************************************************
# Copyright (c) 2018 Lablicate GmbH.
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
# The workspace path, use the eclipse workspace path, e.g. /home/openchrom/www.openchrom.net/OpenChrom/trunk
# Use "$workspace" as the actual workspace could contains white spaces.
#
echo -n "Please enter the workspace path (/home/openchrom/www.openchrom.net/Development/OpenChrom/0.6.0/workspace): "
read workspace
if [ -z "$workspace" ]; then
  echo "No valid workspace."
  exit
fi

function replace_tag {

  actual='technology\/nebula\/snapshot'
  new='nebula\/snapshot'

  sed -i s/"$actual"/"$new"/g $1
}

function change_version {
  
  while [ $1 ]; do
    echo -n "."
      case $1 in
        */*.xml ) replace_tag $1;;
        */*.target ) replace_tag $1;;
      esac
    shift
  done

  echo "done"
}

echo "Update on workspace: " $workspace

#
# Start processing.
#
echo "Start changing the version."
echo ""

#
# Change pom.xml files.
#
echo "Change pom.xml files"
  change_version $(find "$workspace" -type f -name *.xml)
echo ""

#
# Change *.target files.
#
echo "Change *.target files"
  change_version $(find "$workspace" -type f -name *.target)
echo ""

echo "finished"
echo "bye"
