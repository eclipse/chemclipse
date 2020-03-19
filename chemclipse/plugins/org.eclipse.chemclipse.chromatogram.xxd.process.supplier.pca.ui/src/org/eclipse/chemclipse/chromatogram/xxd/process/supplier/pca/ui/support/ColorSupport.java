/*******************************************************************************
 * Copyright (c) 2020 Lablicate GmbH.
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
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.preferences.PreferenceSupplier;
import org.eclipse.chemclipse.swt.ui.support.Colors;
import org.eclipse.chemclipse.swt.ui.support.IColorScheme;
import org.eclipse.swt.graphics.Color;

public class ColorSupport {

	public static Map<String, Color> getGroupNameColorMap(List<IResultPCA> resultList) {

		IColorScheme colorScheme = Colors.getColorScheme(PreferenceSupplier.getColorScheme());
		Map<String, Color> colorMap = new HashMap<>();
		Set<String> groupNameSet = new HashSet<>();
		//
		for(IResultPCA pcaResult : resultList) {
			String groupName = pcaResult.getGroupName();
			if(groupName != null) {
				groupNameSet.add(groupName);
			}
		}
		//
		List<String> groupNameList = new ArrayList<>(groupNameSet);
		Collections.sort(groupNameList);
		groupNameList.add(0, null); // Group Name could be null.
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

	public static Map<String, javafx.scene.paint.Color> getGroupNameColorMapFX(List<IResultPCA> resultList) {

		Map<String, Color> colorMap = getGroupNameColorMap(resultList);
		Map<String, javafx.scene.paint.Color> colorMapFX = new HashMap<>();
		//
		for(Map.Entry<String, Color> colorEntry : colorMap.entrySet()) {
			Color color = colorEntry.getValue();
			double red = calculateChannelFX(color.getRed());
			double green = calculateChannelFX(color.getGreen());
			double blue = calculateChannelFX(color.getBlue());
			double alpha = calculateChannelFX(color.getAlpha());
			javafx.scene.paint.Color colorFX = new javafx.scene.paint.Color(red, green, blue, alpha);
			colorMapFX.put(colorEntry.getKey(), colorFX);
		}
		//
		return colorMapFX;
	}

	private static double calculateChannelFX(int channel) {

		return 1.0d / 255.0d * channel;
	}
}
