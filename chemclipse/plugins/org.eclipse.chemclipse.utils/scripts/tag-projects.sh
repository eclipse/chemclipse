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
#	Dr. Janos Binder - initial API and implementation
#*******************************************************************************

function tag_project {

  #
  # Please add the appropriate identifier:
  #
  # CC - ChemClipse
  # OC - OpenChrom
  # XR - XReport
  #
  # REL-1.0.0-OC
  # PREV-1.0.0.1-OC
  # PREV-1.0.0.0-OC
  # REL-1.2.0.2-XR
  #
  echo 'git tag project: '$1
  cd $1
  # DELETE
  #git tag -d OC-CODE-MIGRATE-ECLIPSE
  #git push origin :refs/tags/OC-CODE-MIGRATE-ECLIPSE
  # ADD
  #git tag -a "REL-0.7.0.0-CC" -m "Release 0.7.0.1 CC"
  #git push --tags
  cd $active
}

function tag_projects {
  
  while [ $1 ]; do
    tag_project $1
    shift
  done

  echo "done"
}

echo "Start git project tagging"
  active=$(pwd)
  # Go to the workspace area.
  tag_projects $(find ../../../../../ -maxdepth 1 -type d)
echo "finished"
