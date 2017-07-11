/*******************************************************************************
 * Copyright (c) 2008, 2017 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.swt.ui.support;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.chemclipse.swt.ui.preferences.PreferenceSupplier;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;

public class Colors {

	public static final Color WHITE = Display.getCurrent().getSystemColor(SWT.COLOR_WHITE);
	public static final Color RED = Display.getCurrent().getSystemColor(SWT.COLOR_RED);
	public static final Color BLACK = Display.getCurrent().getSystemColor(SWT.COLOR_BLACK);
	public static final Color DARK_RED = Display.getCurrent().getSystemColor(SWT.COLOR_DARK_RED);
	public static final Color GREEN = Display.getCurrent().getSystemColor(SWT.COLOR_GREEN);
	public static final Color GRAY = Display.getCurrent().getSystemColor(SWT.COLOR_GRAY);
	public static final Color DARK_GRAY = Display.getCurrent().getSystemColor(SWT.COLOR_DARK_GRAY);
	public static final Color CYAN = Display.getCurrent().getSystemColor(SWT.COLOR_CYAN);
	public static final Color DARK_CYAN = Display.getCurrent().getSystemColor(SWT.COLOR_DARK_CYAN);
	public static final Color DARK_YELLOW = Display.getCurrent().getSystemColor(SWT.COLOR_DARK_YELLOW);
	public static final Color DARK_GREEN = Display.getCurrent().getSystemColor(SWT.COLOR_DARK_GREEN);
	public static final Color BLUE = Display.getCurrent().getSystemColor(SWT.COLOR_BLUE);
	public static final Color MAGENTA = Display.getCurrent().getSystemColor(SWT.COLOR_MAGENTA);
	public static final Color YELLOW = Display.getCurrent().getSystemColor(SWT.COLOR_YELLOW);
	/*
	 * These are system color ids, defined org.eclipse.swt.SWT. If you use own
	 * color, dispose them, if not needed any more.
	 * The colors are black -> dark red -> red
	 * 255, 0, 0
	 * 150, 0, 0
	 * 0, 0, 0
	 */
	public static final String COLOR_SCHEME_RED = "Red";
	private static final int[] colorIdsGradientRed = new int[]{255, 0, 45, 85, 125, 165, 205};
	private static List<Color> colorsGradientRed;
	/*
	 * Normal gradient
	 * Red, Black, Gray, Green, Cyan, Yellow, Magenta, Blue
	 */
	public static final String COLOR_SCHEME_GRADIENT = "Gradient";
	private static final int[] colorIdsGradient = new int[]{3, 2, 16, 6, 14, 8, 12, 10};
	private static List<Color> colorsGradient;
	/*
	 * Red with higher contrast
	 */
	public static final String COLOR_SCHEME_RED_CONTRAST = "Red Contrast";
	private static final int[] colorIdsGradientRedContrast = new int[]{255, 0, 85, 165};
	private static List<Color> colorsGradientRedContrast;
	/*
	 * High Contrast
	 * Blue, Cyan, Gray, Magenta, Green, Black, Dark Yellow, Red
	 */
	public static final String COLOR_SCHEME_HIGH_CONTRAST = "High Contrast";
	private static final int[] colorIdsGradientHighContrast = new int[]{SWT.COLOR_BLUE, SWT.COLOR_CYAN, SWT.COLOR_GRAY, SWT.COLOR_MAGENTA, SWT.COLOR_GREEN, SWT.COLOR_BLACK, SWT.COLOR_DARK_YELLOW, SWT.COLOR_RED};
	private static List<Color> colorsGradientHighContrast;
	/*
	 * Publication
	 * Red, Green, Blue, Dark Red, Dark Green, Dark Blue
	 */
	public static final String COLOR_SCHEME_PUBLICATION = "Publication";
	private static final RGB[] colorIdsGradientPublication = new RGB[]{new RGB(255, 0, 0), new RGB(0, 255, 0), new RGB(0, 0, 255), new RGB(150, 0, 0), new RGB(0, 150, 0), new RGB(0, 0, 150)};
	private static List<Color> colorsGradientPublication;
	/**
	 * Creates a color array.
	 */
	static {
		initializeColors();
	}

	public static String[][] getAvailableColorSchemes() {

		String[][] elements = new String[5][2];
		//
		elements[0][0] = COLOR_SCHEME_RED;
		elements[0][1] = COLOR_SCHEME_RED;
		//
		elements[1][0] = COLOR_SCHEME_RED_CONTRAST;
		elements[1][1] = COLOR_SCHEME_RED_CONTRAST;
		//
		elements[2][0] = COLOR_SCHEME_GRADIENT;
		elements[2][1] = COLOR_SCHEME_GRADIENT;
		//
		elements[3][0] = COLOR_SCHEME_HIGH_CONTRAST;
		elements[3][1] = COLOR_SCHEME_HIGH_CONTRAST;
		//
		elements[4][0] = COLOR_SCHEME_PUBLICATION;
		elements[4][1] = COLOR_SCHEME_PUBLICATION;
		//
		return elements;
	}

	public static IColorScheme getColorScheme() {

		String colorSchemeOverlay = PreferenceSupplier.getColorSchemeOverlay();
		return getColorScheme(colorSchemeOverlay);
	}

	public static IColorScheme getColorScheme(String colorScheme) {

		/*
		 * ...equals(colorScheme)
		 * to avoid NPEs.
		 */
		if(COLOR_SCHEME_GRADIENT.equals(colorScheme)) {
			return new ColorScheme(colorsGradient);
		} else if(COLOR_SCHEME_RED_CONTRAST.equals(colorScheme)) {
			return new ColorScheme(colorsGradientRedContrast);
		} else if(COLOR_SCHEME_HIGH_CONTRAST.equals(colorScheme)) {
			return new ColorScheme(colorsGradientHighContrast);
		} else if(COLOR_SCHEME_PUBLICATION.equals(colorScheme)) {
			return new ColorScheme(colorsGradientPublication);
		} else {
			// DEFAULT SCHEME_RED
			return new ColorScheme(colorsGradientRed);
		}
	}

	// ----------------------------------------------private methods
	/**
	 * Creates a color array by given size.<br/>
	 * The colors will be repeated if the size is greater than the colors
	 * defined in colorIds.
	 * 
	 * @param size
	 */
	private static void initializeColors() {

		/*
		 * In this case, we use system colors. We do not need to dispose them.
		 * If you use own colors, dispose them, if not needed any more.
		 */
		Display display = Display.getCurrent();
		/*
		 * RED GRADIENT
		 */
		colorsGradientRed = new ArrayList<Color>();
		for(int colorId : colorIdsGradientRed) {
			Color color = new Color(display, colorId, 0, 0);
			colorsGradientRed.add(color);
		}
		/*
		 * GRADIENT
		 */
		colorsGradient = new ArrayList<Color>();
		for(int colorId : colorIdsGradient) {
			Color color = display.getSystemColor(colorId);
			colorsGradient.add(color);
		}
		/*
		 * RED GRADIENT + CONTRAST
		 */
		colorsGradientRedContrast = new ArrayList<Color>();
		for(int colorId : colorIdsGradientRedContrast) {
			Color color = new Color(display, colorId, 0, 0);
			colorsGradientRedContrast.add(color);
		}
		/*
		 * GRADIENT HIGH CONTRAST
		 */
		colorsGradientHighContrast = new ArrayList<Color>();
		for(int colorId : colorIdsGradientHighContrast) {
			Color color = display.getSystemColor(colorId);
			colorsGradientHighContrast.add(color);
		}
		/*
		 * GRADIENT PUBLICATION
		 */
		colorsGradientPublication = new ArrayList<Color>();
		for(RGB rgb : colorIdsGradientPublication) {
			Color color = new Color(display, rgb);
			colorsGradientPublication.add(color);
		}
	}
}
