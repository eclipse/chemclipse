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
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.LabelOptionPCA;
import org.eclipse.chemclipse.model.statistics.ISample;
import org.eclipse.chemclipse.swt.ui.support.Colors;
import org.eclipse.chemclipse.swt.ui.support.IColorScheme;
import org.eclipse.swt.graphics.Color;

public class ColorSupport {

	public static final String COLOR_GROUP_DEFAULT = "Unknown";
	public static final Color COLOR_FALLBACK = Colors.DARK_RED;

	public static Map<String, Color> getColorMapResults(List<? extends IResultPCA> resultList, LabelOptionPCA labelOptionPCA, String colorScheme) {

		Set<String> colorGroups = new HashSet<>();
		for(IResultPCA pcaResult : resultList) {
			String colorGroup = getColorGroup(pcaResult.getSample(), labelOptionPCA);
			if(colorGroup != null) {
				colorGroups.add(colorGroup);
			}
		}
		//
		return createColorMap(colorGroups, colorScheme);
	}

	public static Map<String, Color> getColorMapSamples(List<ISample> sampleList, LabelOptionPCA labelOptionPCA, String colorScheme) {

		Set<String> colorGroups = new HashSet<>();
		for(ISample sample : sampleList) {
			String colorGroup = getColorGroup(sample, labelOptionPCA);
			if(colorGroup != null) {
				colorGroups.add(colorGroup);
			}
		}
		//
		return createColorMap(colorGroups, colorScheme);
	}

	public static String getColorGroup(ISample sample, LabelOptionPCA labelOptionPCA) {

		String colorGroup;
		switch(labelOptionPCA) {
			case SAMPLE_NAME:
				colorGroup = sample.getSampleName();
				break;
			case CLASSIFICATION:
				colorGroup = sample.getClassification();
				break;
			case DESCRIPTION:
				colorGroup = sample.getDescription();
				break;
			default:
				/*
				 * Group Name is the default.
				 */
				colorGroup = sample.getGroupName();
				break;
		}
		//
		return validateColorGroup(colorGroup);
	}

	private static String validateColorGroup(String colorGroup) {

		/*
		 * Validate the group.
		 */
		if(colorGroup == null) {
			colorGroup = ColorSupport.COLOR_GROUP_DEFAULT;
		} else {
			colorGroup = colorGroup.trim().replace(" ", "_");
			if(colorGroup.isEmpty()) {
				colorGroup = ColorSupport.COLOR_GROUP_DEFAULT;
			}
		}
		//
		return colorGroup;
	}

	private static Map<String, Color> createColorMap(Set<String> colorGroups, String colorSchemeName) {

		IColorScheme colorScheme = Colors.getColorScheme(colorSchemeName);
		Map<String, Color> colorMap = new HashMap<>();
		/*
		 * Assign the colors.
		 * Add the default marker at the end of the sorted list.
		 */
		colorGroups.remove(COLOR_GROUP_DEFAULT);
		List<String> colorGroupList = new ArrayList<>(colorGroups);
		Collections.sort(colorGroupList);
		colorGroupList.add(COLOR_GROUP_DEFAULT);
		//
		for(String colorGroup : colorGroupList) {
			Color color = colorMap.get(colorGroup);
			if(color == null) {
				colorMap.put(colorGroup, colorScheme.getColor());
				colorScheme.incrementColor();
			}
		}
		//
		return colorMap;
	}
}