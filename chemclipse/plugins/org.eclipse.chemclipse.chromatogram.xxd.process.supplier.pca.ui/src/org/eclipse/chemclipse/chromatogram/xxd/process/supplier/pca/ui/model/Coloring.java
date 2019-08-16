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
package org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.model;

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

	public static Map<String, Integer> getColorsForGroup(Collection<String> groupNames) {

		Map<String, Integer> colors = new LinkedHashMap<>();
		SortedSet<String> sortName = new TreeSet<>(Comparator.nullsFirst(Comparator.naturalOrder()));
		sortName.addAll(groupNames);
		Iterator<String> it = sortName.iterator();
		float angle = 120.0f;
		float actualAngle = offsetAngle;
		float saturOffset = 0f;
		float brightOffset = 0f;
		int numCycle = 0;
		while(it.hasNext() && actualAngle < 360.0f) {
			String name = it.next();
			int c = HSBtoRGB(actualAngle / 360.0f, saturation, brightness);
			colors.put(name, c);
			actualAngle = actualAngle + angle;
		}
		numCycle++;
		while(it.hasNext()) {
			actualAngle = angle / 2 + offsetAngle;
			for(int k = 0; k < (120f / angle) && it.hasNext(); k++) {
				actualAngle = k * angle + angle / 2 + offsetAngle;
				for(int j = 0; j < 3 && it.hasNext(); j++) {
					if(numCycle == 1) {
						if(k % 2 == 0) {
							saturOffset = 0f;
							brightOffset = 0f;
						} else {
							saturOffset = 0;
							brightOffset = 0f;
						}
					} else if(numCycle == 2) {
						if(k % 2 == 0) {
							saturOffset = -0.2f;
							brightOffset = 0f;
						} else {
							saturOffset = 0f;
							brightOffset = 0f;
						}
					} else if(numCycle == 3) {
						if(k % 2 == 0) {
							saturOffset = -0.4f;
							brightOffset = 0f;
						} else {
							saturOffset = 0;
							brightOffset = -0.4f;
						}
					} else if(numCycle > 3) {
						if(k % 3 == 0) {
							saturOffset = -0.6f;
							brightOffset = 0f;
						} else if(k % 3 == 1) {
							saturOffset = 0;
							brightOffset = -0.6f;
						} else if(k % 3 == 2) {
							saturOffset = -0.4f;
							brightOffset = -0.4f;
						}
					}
					String name = it.next();
					int c = HSBtoRGB(actualAngle / 360.0f, saturation + saturOffset, brightness + brightOffset);
					colors.put(name, c);
					actualAngle = actualAngle + 120f;
				}
			}
			angle = angle / 2;
			numCycle++;
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
