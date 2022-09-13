/*******************************************************************************
 * Copyright (c) 2020, 2022 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.support;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.IResultPCA;
import org.eclipse.chemclipse.model.statistics.ISample;
import org.eclipse.chemclipse.swt.ui.support.Colors;
import org.eclipse.chemclipse.swt.ui.support.IColorScheme;
import org.eclipse.swt.graphics.Color;

public class ColorSupport {

	public static final String GROUP_NAME_UNKOWN = "Unknown";
	public static final Color COLOR_FALLBACK = Colors.DARK_RED;

	public static Map<String, Color> getColorMapResults(List<? extends IResultPCA> resultList, String colorScheme) {

		List<String> groupNames = new ArrayList<>();
		for(IResultPCA pcaResult : resultList) {
			String groupName = pcaResult.getGroupName();
			if(groupName != null) {
				groupNames.add(groupName);
			}
		}
		//
		return createColorMap(groupNames, colorScheme);
	}

	public static Map<String, Color> getColorMapSamples(List<ISample> sampleList, String colorScheme) {

		List<String> groupNames = new ArrayList<>();
		for(ISample sample : sampleList) {
			String groupName = sample.getGroupName();
			if(groupName != null) {
				groupNames.add(groupName);
			}
		}
		//
		return createColorMap(groupNames, colorScheme);
	}

	private static Map<String, Color> createColorMap(List<String> groupNames, String colorSchemeName) {

		IColorScheme colorScheme = Colors.getColorScheme(colorSchemeName);
		Map<String, Color> colorMap = new HashMap<>();
		Set<String> groupNameSet = new HashSet<>();
		//
		groupNameSet.add(GROUP_NAME_UNKOWN);
		for(String groupName : groupNames) {
			if(groupName != null) {
				groupNameSet.add(groupName);
			}
		}
		/*
		 * Assign the colors.
		 */
		List<String> groupNameList = new ArrayList<>(groupNameSet);
		Collections.sort(groupNameList);
		//
		for(String groupName : groupNameList) {
			Color color = colorMap.get(groupName);
			if(color == null) {
				colorMap.put(groupName, colorScheme.getColor());
				colorScheme.incrementColor();
			}
		}
		//
		return colorMap;
	}
}