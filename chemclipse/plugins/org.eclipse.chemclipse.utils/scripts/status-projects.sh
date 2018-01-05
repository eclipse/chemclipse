#!/bin/bash

#*******************************************************************************
#
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

STATUS_SCRIPT_GIT_FLAGS=" --short"
MASTER_BRANCH="origin/develop"
VERY_QUIET=false

while getopts "nsqvh" opt; do
  case $opt in
    n)
      VERY_QUIET=false
      STATUS_SCRIPT_GIT_FLAGS=""
      ;;
    s)
      VERY_QUIET=false
      STATUS_SCRIPT_GIT_FLAGS=" --short"
      ;;
    q)
      VERY_QUIET=true
      STATUS_SCRIPT_GIT_FLAGS=" --short"
      ;;

    v)
      VERY_QUIET=false
      STATUS_SCRIPT_GIT_FLAGS=" --verbose"
      ;;
    h)
      echo "Gets all project statuses" >&2
      echo "Flags: -n -- calls git status without any argument" >&2
      echo "       -q -- calls git status very quiet in '--short' mode (prints only if something has changed)" >&2
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
  seed=$RANDOM
  if [ -e "$1/.git" ]; then
    cd $1
    git_output=`git -c color.status=always status$STATUS_SCRIPT_GIT_FLAGS`
    if [[ "$VERY_QUIET" == false ]]; then
      echo -e "git status$STATUS_SCRIPT_GIT_FLAGS project: \033[1m$1\033[0m"
      if [[ -n $git_output ]]; then
        echo -e "$git_output"
      fi
    elif [[ "$VERY_QUIET" == true && -n $git_output ]]; then
      echo -e "git status$STATUS_SCRIPT_GIT_FLAGS project: \033[1m$1\033[0m"
      echo -e "$git_output"
    fi
    if [[ "$STATUS_SCRIPT_GIT_FLAGS"==" --short" ]];then
      # TODO: a cleaner version could be created by "git for-each-ref --format='%(refname:short)%(upstream:track)' refs/"
      # then it should be grepped by regex for ahead or behind
      # code taken from:
      # https://github.com/kortina/bakpak/blob/965e651f26f34d1d8912a559bc9812d00f2a36d4/bin/git-current-branch-is-behind-origin-master.sh
      # https://github.com/kortina/bakpak/blob/965e651f26f34d1d8912a559bc9812d00f2a36d4/bin/git-branches-vs-origin-master
      git rev-list --left-right `git branch -a | grep "^\*" | cut -c 3-`...$MASTER_BRANCH -- 2>/dev/null >/tmp/git_status_delta_$seed
      RIGHT_AHEAD=$(grep -c '^>' /tmp/git_status_delta_$seed)
      LEFT_AHEAD=$(grep -c '^<' /tmp/git_status_delta_$seed)
      if [[ "$RIGHT_AHEAD" -ne "0" ]];then
        echo -e "\033[1m$1\033[0m is \E[31mbehind $RIGHT_AHEAD commits\033[0m to $MASTER_BRANCH." 
      fi
      if [[ "$LEFT_AHEAD" -ne "0" ]];then
        echo -e "\033[1m$1\033[0m is \E[31mahead $LEFT_AHEAD commits\033[0m to $MASTER_BRANCH."
      fi
      rm /tmp/git_status_delta_$seed 
    fi
    cd $status_script_active
  fi
}


echo "Start git project status"
  status_script_active=$(pwd)
  # ../../../ go to workspace area. test
  export -f status_project
  export STATUS_SCRIPT_GIT_FLAGS
  export MASTER_BRANCH
  export VERY_QUIET
  export status_script_active
  val=$(command -v parallel)
  if [ -z "$val" ]; then
    echo "INFO: Please consider installing 'parallel' to make this script superfast."
    for git_project in $(find ../../.. -maxdepth 1 -type d); do
      status_project $git_project
    done
  else
    # one can play with parallel --bar or --progress but it looks ugly
    find ../../../../../ -maxdepth 1 -type d | parallel status_project :::: -
  fi
echo "finished"
