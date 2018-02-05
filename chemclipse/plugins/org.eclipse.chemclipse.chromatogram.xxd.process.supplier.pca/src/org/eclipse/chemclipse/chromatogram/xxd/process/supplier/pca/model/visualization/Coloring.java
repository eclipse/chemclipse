/*******************************************************************************
 * Copyright (c) 2018 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Jan Holy - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.visualization;

import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

public class Coloring {

	private static final float brightness = 1.0f;
	private static final float offsetAngle = 0;
	private static final float saturation = 1.0f;

	public static Map<String, Integer> getColor(Collection<String> names) {

		Map<String, Integer> colors = new LinkedHashMap<>();
		SortedSet<String> sortName = new TreeSet<>(Comparator.nullsFirst(Comparator.naturalOrder()));
		sortName.addAll(names);
		Iterator<String> it = sortName.iterator();
		float angle = 120.0f;
		float actualAngle = offsetAngle;
		while(it.hasNext() && actualAngle < 360.0f) {
			String name = it.next();
			int c = HSBtoRGB(actualAngle / 360.0f, saturation, brightness);
			colors.put(name, c);
			actualAngle = actualAngle + angle;
		}
		while(it.hasNext()) {
			actualAngle = angle / 2 + offsetAngle;
			while(it.hasNext() && actualAngle < 360.0f) {
				String name = it.next();
				int c = HSBtoRGB(actualAngle / 360.0f, saturation, brightness);
				colors.put(name, c);
				actualAngle = actualAngle + angle;
			}
			angle = angle / 2;
		}
		return colors;
	}

	private static int HSBtoRGB(float hue, float saturation, float brightness) {

		int r = 0, g = 0, b = 0;
		if(saturation == 0) {
			r = g = b = (int)(brightness * 255.0f + 0.5f);
		} else {
			float h = (hue - (float)Math.floor(hue)) * 6.0f;
			float f = h - (float)java.lang.Math.floor(h);
			float p = brightness * (1.0f - saturation);
			float q = brightness * (1.0f - saturation * f);
			float t = brightness * (1.0f - (saturation * (1.0f - f)));
			switch((int)h) {
				case 0:
					r = (int)(brightness * 255.0f + 0.5f);
					g = (int)(t * 255.0f + 0.5f);
					b = (int)(p * 255.0f + 0.5f);
					break;
				case 1:
					r = (int)(q * 255.0f + 0.5f);
					g = (int)(brightness * 255.0f + 0.5f);
					b = (int)(p * 255.0f + 0.5f);
					break;
				case 2:
					r = (int)(p * 255.0f + 0.5f);
					g = (int)(brightness * 255.0f + 0.5f);
					b = (int)(t * 255.0f + 0.5f);
					break;
				case 3:
					r = (int)(p * 255.0f + 0.5f);
					g = (int)(q * 255.0f + 0.5f);
					b = (int)(brightness * 255.0f + 0.5f);
					break;
				case 4:
					r = (int)(t * 255.0f + 0.5f);
					g = (int)(p * 255.0f + 0.5f);
					b = (int)(brightness * 255.0f + 0.5f);
					break;
				case 5:
					r = (int)(brightness * 255.0f + 0.5f);
					g = (int)(p * 255.0f + 0.5f);
					b = (int)(q * 255.0f + 0.5f);
					break;
			}
		}
		return 0xff000000 | (r << 16) | (g << 8) | (b << 0);
	}
}
