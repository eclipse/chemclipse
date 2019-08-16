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

function checkout_project {

  echo 'git checkout project: '$1
  cd $1
  #git checkout develop-trustytahr
  #git checkout release-0.9.0
  cd $active
}

function checkout_projects {
  
  while [ $1 ]; do
    checkout_project $1
    shift
  done

  echo "done"
}

echo "Start git project checkout"
  active=$(pwd)
  # Go to the workspace area.
  checkout_projects $(find ../../../../../ -maxdepth 1 -type d)
echo "finished"
