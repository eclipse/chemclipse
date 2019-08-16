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

GIT_FLAGS=" --quiet"

function push_project {

  #
  # Apply on a valid Git repository only.
  #
  if [ -e "$1/.git" ]; then
    echo -e "git push$GIT_FLAGS project: \033[1m$1\033[0m"
    cd $1
    git push$GIT_FLAGS
    #git push origin sr-0.7.0
    cd $active
  fi
}

function push_projects {
  
  while [ $1 ]; do
    push_project $1
    shift
  done
}

while getopts "nqvh" opt; do
  case $opt in
    n)
      GIT_FLAGS=""
      ;;
    q)
      GIT_FLAGS=" --quiet"
      ;;
    v)
      GIT_FLAGS=" --verbose"
      ;;
    h)
      echo "Pushes all project updates into the repository" >&2
      echo "Flags: -n -- calls git push without any argument" >&2
      echo "       -q -- calls git push in '--quiet' mode (default)" >&2
      echo "       -v -- calls git push in '--verbose' mode" >&2
      echo "       -h -- shows this help" >&2
      exit 1
      ;;
    \?)
      echo "Invalid option: -$OPTARG" >&2
      exit 0
      ;;
  esac
done

echo "Start git project push"
  active=$(pwd)
    # Go to the workspace area.
  push_projects $(find ../../../../../ -maxdepth 1 -type d)
echo "finished"
