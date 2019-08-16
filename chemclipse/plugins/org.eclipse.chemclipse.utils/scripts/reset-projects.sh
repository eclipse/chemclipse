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

function reset_project {

  echo 'git reset project: '$1
  cd $1
	echo "git reset activate ... !"  
	#git reset --hard
  cd $active
}

function reset_projects {
  
  while [ $1 ]; do
    reset_project $1
    shift
  done

  echo "done"
}

echo "Start git project reset"
  active=$(pwd)
    # Go to the workspace area.
  reset_projects $(find ../../../../../ -type d -maxdepth 1)
echo "finished"
