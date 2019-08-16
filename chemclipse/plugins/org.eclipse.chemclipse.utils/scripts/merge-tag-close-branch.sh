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
#*******************************************************************************

function merge_project {

	echo 'git merge/branch/tag project: '$1
	cd $1
	#devbranch='sr-1.0.1-oc'
	#git checkout master
	#git merge --no-ff $devbranch
	#git push origin master
	#git branch -d $devbranch
	#git push origin --delete $devbranch
	#git tag -a "REL-1.0.0.1-OC" -m "1.0.0.1 (Aston) GA."
  	#git push --tags
	cd $active
}

function merge_projects {

	while [ $1 ]; do
		merge_project $1
		shift
	done

	echo "done"
}

echo "Start"
	active=$(pwd)
	# ../../../ go to workspace area.
	merge_projects $(find ../../../../../ -type d -maxdepth 1)
echo "Finished"
