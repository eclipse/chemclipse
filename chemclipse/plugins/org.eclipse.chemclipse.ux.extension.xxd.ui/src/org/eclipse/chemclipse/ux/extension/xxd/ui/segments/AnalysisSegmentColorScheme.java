/*******************************************************************************
 * Copyright (c) 2019 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Christoph Läubrich - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.segments;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.RGB;

public class AnalysisSegmentColorScheme {

	public enum Type {
		SEGMENT_EVEN, SEGMENT_ODD, LINE, SELECTION;
	}

	public static final AnalysisSegmentColorScheme ANALYSIS = new AnalysisSegmentColorScheme(new RGB(215, 244, 227), new RGB(175, 233, 198), new RGB(135, 222, 170), new RGB(95, 211, 141));
	public static final AnalysisSegmentColorScheme NOISE = new AnalysisSegmentColorScheme(new RGB(246, 213, 255), new RGB(238, 170, 255), new RGB(229, 128, 255), new RGB(170, 0, 212));
	private final RGB[] rgb;

	private AnalysisSegmentColorScheme(RGB... colours) {
		this.rgb = colours;
	}

	public AnalysisSegmentColors create(GC gc) {

		return new AnalysisSegmentColors(gc);
	}

	public final class AnalysisSegmentColors {

		private final Color[] colors;

		public AnalysisSegmentColors(GC gc) {
			colors = new Color[rgb.length];
			for(int i = 0; i < rgb.length; i++) {
				colors[i] = new Color(gc.getDevice(), rgb[i]);
			}
		}

		public void dispose() {

			for(Color color : colors) {
				color.dispose();
			}
		}

		public Color get(Type type) {

			return colors[type.ordinal()];
		}
	}
}
