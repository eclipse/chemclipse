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

DIFF_SCRIPT_GIT_FLAGS=""

function diff_project {

  #
  # Apply on a valid Git repository only.
  #
  if [ -e "$1/.git" ]; then
    echo -e "git diff$DIFF_SCRIPT_GIT_FLAGS project: \033[1m$1\033[0m"
    cd $1
    git -c color.status=always diff$DIFF_SCRIPT_GIT_FLAGS
    cd $diff_script_active
  fi
}

while getopts "nh" opt; do
  case $opt in
    n)
      DIFF_SCRIPT_GIT_FLAGS=""
      ;;
    h)
      echo "Pulls all project updates" >&2
      echo "Flags: -n -- calls git diff without any argument" >&2
      echo "       -h -- shows this help" >&2
      exit 1
      ;;
    \?)
      echo "Invalid option: -$OPTARG" >&2
      exit 0
      ;;
  esac
done

echo "Start git project diff"
  diff_script_active=$(pwd)
  # ../../../ go to workspace area.
  export -f diff_project
  export DIFF_SCRIPT_GIT_FLAGS
  export diff_script_active
  for git_project in $(find ../../../../../ -maxdepth 1 -type d); do
    diff_project $git_project
  done
echo "finished"
