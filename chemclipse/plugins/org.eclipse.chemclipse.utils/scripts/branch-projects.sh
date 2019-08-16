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

function branch_project {

  echo 'git branch project: '$1
  cd $1
  #git checkout -b develop-luna develop
  #git push origin develop-luna
  #git checkout develop
  ##
  #git branch -d develop-luna
  #git push origin --delete develop-luna
  cd $active
}

function branch_projects {
  
  while [ $1 ]; do
    branch_project $1
    shift
  done

  echo "done"
}

echo "Start git project branch"
  active=$(pwd)
  # Go to the workspace area.
  branch_projects $(find ../../../../../ -maxdepth 1 -type d)
echo "finished"
