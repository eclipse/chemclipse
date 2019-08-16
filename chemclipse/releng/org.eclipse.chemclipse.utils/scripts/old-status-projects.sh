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

GIT_FLAGS=" --short"
MASTER_BRANCH="origin/develop"

while getopts "nsvh" opt; do
  case $opt in
    n)
      GIT_FLAGS=""
      ;;
    s)
      GIT_FLAGS=" --short"
      ;;
    v)
      GIT_FLAGS=" --verbose"
      ;;
    h)
      echo "Gets all project statuses" >&2
      echo "Flags: -n -- calls git status without any argument" >&2
      echo "       -s -- calls git status in '--short' mode (default)" >&2
      echo "       -v -- calls git status in '--verbose' mode" >&2
      echo "       -h -- shows this help" >&2
      exit 1
      ;;
    \?)
      echo "Invalid option: -$OPTARG" >&2
      exit 0
      ;;
  esac
done


function status_project {

  #
  # Apply on a valid Git repository only.
  #
  if [ -e "$1/.git" ]; then
    echo -e "git status$GIT_FLAGS project: \033[1m$1\033[0m"
    cd $1
    git status$GIT_FLAGS
    if [ "$GIT_FLAGS"==" --short" ];then
      git rev-list --left-right `git branch -a | grep "^\*" | cut -c 3-`...$MASTER_BRANCH -- 2>/dev/null >/tmp/git_status_delta
      RIGHT_AHEAD=$(grep -c '^>' /tmp/git_status_delta)
      LEFT_AHEAD=$(grep -c '^<' /tmp/git_status_delta)
      if [ "$RIGHT_AHEAD" -ne "0" ];then
        echo -e "\033[1m$1\033[0m is \E[31mbehind $RIGHT_AHEAD commits\033[0m to $MASTER_BRANCH." 
      fi
      if [ "$LEFT_AHEAD" -ne "0" ];then
        echo -e "\033[1m$1\033[0m is \E[31mahead $LEFT_AHEAD commits\033[0m to $MASTER_BRANCH."
      fi
    fi
    cd $active
  fi
}

function status_projects {
  
  while [ $1 ]; do
    status_project $1
    shift
  done
}

echo "Start git project status"
  active=$(pwd)
    # Go to the workspace area.
  status_projects $(find ../../../../../ -maxdepth 1 -type d)
  rm /tmp/git_status_delta
echo "finished"
