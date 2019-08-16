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
COMMIT_MESSAGE=""

function read_commit_comment {
  
  read COMMIT_MESSAGE
  if [ "$COMMIT_MESSAGE" == "" ];then
    echo "No commit comment typed in."
    exit 0
  fi
}

function commit_project {

  #
  # Apply on a valid Git repository only.
  #
  if [ -e "$1/.git" ]; then
    echo -e "git commit project: \033[1m$1\033[0m"
    cd $1
    git commit$GIT_FLAGS -m "$COMMIT_MESSAGE" -a
    cd $active
  fi
}

function commit_projects {
  
  while [ $1 ]; do
    commit_project $1
    shift
  done
}

while getopts "nqvhm:" opt; do
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
    m)
      COMMIT_MESSAGE="$OPTARG"
      ;;
    h)
      echo "Commit all project updates" >&2
      echo "Flags: -n -- calls git commit without any argument" >&2
      echo "       -q -- calls git commit in '--quiet' mode (default)" >&2
      echo "       -v -- calls git commit in '--verbose' mode" >&2
      echo "       -c -- (required) the 'commit comment'" >&2
      echo "       -h -- shows this help" >&2
      exit 1
      ;;
    :)
      echo "Option -$OPTARG was not provied, please provide a commit comment:" >&2
      read_commit_comment
      ;;
    \?)
      echo "Invalid option: -$OPTARG" >&2
      exit 0
      ;;
  esac
done

if [ "$COMMIT_MESSAGE" == "" ];then
  echo "No commit comment was provided, please provide one now:"
  read_commit_comment
fi

echo "Start git project commit"
  active=$(pwd)
    # Go to the workspace area.
  commit_projects $(find ../../../../../ -type d -maxdepth 1)
echo "finished"
