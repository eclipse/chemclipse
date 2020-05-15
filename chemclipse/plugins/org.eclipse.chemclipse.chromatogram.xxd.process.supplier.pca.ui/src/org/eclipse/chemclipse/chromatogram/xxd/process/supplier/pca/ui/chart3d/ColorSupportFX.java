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
package org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.chart3d;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.IResultPCA;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.support.ColorSupport;
import org.eclipse.swt.graphics.Color;

public class ColorSupportFX {

	public static Map<String, javafx.scene.paint.Color> getGroupNameColorMap(List<IResultPCA> resultList) {

		Map<String, Color> colorMap = ColorSupport.getColorMapResults(resultList);
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
