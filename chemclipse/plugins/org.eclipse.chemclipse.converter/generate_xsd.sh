#!/bin/bash

#********************************************************************************
# * Copyright (c) 2020 Lablicate GmbH.
# * 
# * This program and the accompanying materials are made available under the
# * terms of the Eclipse Public License v. 1.0 which is available at
# * http://www.eclipse.org/legal/epl-v10.html.
# * 
# * SPDX-License-Identifier: EPL-1.0
# ********************************************************************************
#
# Make sure to adjust copyright headers after each generation see 
# http://dev.eclipse.org/ipzilla/show_bug.cgi?id=20940
# https://www.eclipse.org/projects/tools/documentation.php?id=science.chemclipse

export LANG=en_EN
xjc -p org.eclipse.chemclipse.converter.methods.xml.v1 -d xsd -no-header -npa schema/processmethods.xsd
