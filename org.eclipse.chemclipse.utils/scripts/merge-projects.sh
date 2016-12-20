#!/bin/bash

#*******************************************************************************
# Copyright (c) 2015, 2016 Lablicate GmbH.
# 
# All rights reserved. This program and the accompanying materials
# are made available under the terms of the Eclipse Public License v1.0
# which accompanies this distribution, and is available at
# http://www.eclipse.org/legal/epl-v10.html
# 
# Contributors:
# 	Dr. Philip Wenig - initial API and implementation
#*******************************************************************************

function merge_project {

  echo 'git merge project: '$1
  cd $1
  #git checkout master
  #git merge --no-ff develop
  #git push origin master
  #git checkout develop
  cd $active
}

function merge_projects {
  
  while [ $1 ]; do
    merge_project $1
    shift
  done

  echo "done"
}

echo "Start git project merge"
  active=$(pwd)
  # ../../../ go to workspace area.
  merge_projects $(find ../../../ -type d -maxdepth 1)
echo "finished"
