/*******************************************************************************
 * Copyright (c) 2017, 2018 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Jan Holy - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.untility;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;

public class PcaColorGroup {

	private static float brightness = 1.0f;
	private static float offsetAngle = 0;
	private static float saturation = 1.0f;

	public static java.awt.Color getActualSelectedColor(java.awt.Color color) {

		return Color.DARK_GRAY;
	}

	public static javafx.scene.paint.Color getActualSelectedColor(javafx.scene.paint.Color color) {

		return javafx.scene.paint.Color.DARKGRAY;
	}

	public static org.eclipse.swt.graphics.Color getActualSelectedColor(org.eclipse.swt.graphics.Color color) {

		return Display.getDefault().getSystemColor(SWT.COLOR_DARK_GRAY);
	}

	private static Map<String, java.awt.Color> getColor(Collection<String> names) {

		Map<String, java.awt.Color> colors = new LinkedHashMap<>();
		List<String> sortName = new ArrayList<>(names);
		sortName.sort(Comparator.nullsFirst(Comparator.naturalOrder()));
		Iterator<String> it = sortName.iterator();
		float angle = 120.0f;
		float actualAngle = offsetAngle;
		while(it.hasNext() && actualAngle < 360.0f) {
			String name = it.next();
			java.awt.Color c = java.awt.Color.getHSBColor(actualAngle / 360.0f, saturation, brightness);
			colors.put(name, c);
			actualAngle = actualAngle + angle;
		}
		while(it.hasNext()) {
			actualAngle = angle / 2 + offsetAngle;
			while(it.hasNext() && actualAngle < 360.0f) {
				String name = it.next();
				java.awt.Color c = java.awt.Color.getHSBColor(actualAngle / 360.0f, saturation, brightness);
				colors.put(name, c);
				actualAngle = actualAngle + angle;
			}
			angle = angle / 2;
		}
		return colors;
	}

	public static Map<String, java.awt.Color> getColorAWT(Collection<String> groupNames) {

		return getColor(groupNames);
	}

	public static Map<String, javafx.scene.paint.Color> getColorJavaFx(Collection<String> groupNames) {

		Map<String, java.awt.Color> colors = getColor(groupNames);
		Map<String, javafx.scene.paint.Color> javaFx = new LinkedHashMap<>();
		Iterator<Entry<String, java.awt.Color>> it = colors.entrySet().iterator();
		while(it.hasNext()) {
			Map.Entry<String, java.awt.Color> entry = it.next();
			int red = entry.getValue().getRed();
			int green = entry.getValue().getGreen();
			int blue = entry.getValue().getBlue();
			javaFx.put(entry.getKey(), javafx.scene.paint.Color.rgb(red, green, blue));
		}
		return javaFx;
	}

	public static Map<String, org.eclipse.swt.graphics.Color> getColorSWT(Collection<String> groupNames) {

		Map<String, java.awt.Color> colors = getColor(groupNames);
		Map<String, org.eclipse.swt.graphics.Color> colorsSWT = new LinkedHashMap<>();
		Iterator<Entry<String, Color>> it = colors.entrySet().iterator();
		while(it.hasNext()) {
			Map.Entry<String, java.awt.Color> entry = it.next();
			int red = entry.getValue().getRed();
			int green = entry.getValue().getGreen();
			int blue = entry.getValue().getBlue();
			colorsSWT.put(entry.getKey(), new org.eclipse.swt.graphics.Color(Display.getDefault(), red, green, blue));
		}
		return colorsSWT;
	}

	public static java.awt.Color getUnselectedColor(java.awt.Color color) {

		return new Color(color.getRed(), color.getGreen(), color.getBlue(), (int)0.5 * 255);
	}

	public static javafx.scene.paint.Color getUnselectedColor(javafx.scene.paint.Color color) {

		return javafx.scene.paint.Color.color(color.getRed(), color.getGreen(), color.getBlue(), 0.5);
	}

	public static org.eclipse.swt.graphics.Color getUnselectedColor(org.eclipse.swt.graphics.Color color) {

		return Display.getDefault().getSystemColor(SWT.COLOR_GRAY);
	}
}
