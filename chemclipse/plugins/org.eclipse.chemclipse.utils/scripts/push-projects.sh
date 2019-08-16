#!/bin/bash

#*******************************************************************************
# Copyright (c) 2014, 2018 Lablicate GmbH.
# 
# All rights reserved. This program and the accompanying materials
# are made available under the terms of the Eclipse Public License v1.0
# which accompanies this distribution, and is available at
# http://www.eclipse.org/legal/epl-v10.html
# 
# Contributors:
#	Dr. Philip Wenig
#       Dr. Janos Binder
#*******************************************************************************/

PUSH_SCRIPT_GIT_FLAGS=" --quiet"

function push_project {

  #
  # Apply on a valid Git repository only.
  #
  if [ -e "$1/.git" ]; then
    echo -e "git push$PUSH_SCRIPT_GIT_FLAGS project: \033[1m$1\033[0m"
    cd $1
    git push$PUSH_SCRIPT_GIT_FLAGS
    #git push origin sr-0.7.0
    cd $push_script_active
  fi
}

while getopts "nqvh" opt; do
  case $opt in
    n)
      PUSH_SCRIPT_GIT_FLAGS=""
      ;;
    q)
      PUSH_SCRIPT_GIT_FLAGS=" --quiet"
      ;;
    v)
      PUSH_SCRIPT_GIT_FLAGS=" --verbose"
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
  push_script_active=$(pwd)
  # ../../../ go to workspace area.
  export -f push_project
  export PUSH_SCRIPT_GIT_FLAGS
  export push_script_active
  val=$(command -v parallel)
  if [ -z "$val" ]; then
    echo "INFO: Please consider installing 'parallel' to make this script superfast."
    for git_project in $(find ../../.. -maxdepth 1 -type d); do
      push_project $git_project
    done
  else
    # one can play with parallel --bar or --progress but it looks ugly
    find ../../../../../ -maxdepth 1 -type d | parallel push_project :::: -
  fi
echo "finished"
