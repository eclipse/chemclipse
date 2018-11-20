/*******************************************************************************
 * Copyright (c) 2008, 2018 Lablicate GmbH.
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.chemclipse.support.ui.workbench.DisplayUtils;
import org.eclipse.chemclipse.swt.ui.preferences.PreferenceSupplier;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;

public class Colors {

	public static final Color WHITE = DisplayUtils.getDisplay().getSystemColor(SWT.COLOR_WHITE);
	public static final Color RED = DisplayUtils.getDisplay().getSystemColor(SWT.COLOR_RED);
	public static final Color BLACK = DisplayUtils.getDisplay().getSystemColor(SWT.COLOR_BLACK);
	public static final Color DARK_RED = DisplayUtils.getDisplay().getSystemColor(SWT.COLOR_DARK_RED);
	public static final Color GREEN = DisplayUtils.getDisplay().getSystemColor(SWT.COLOR_GREEN);
	public static final Color GRAY = DisplayUtils.getDisplay().getSystemColor(SWT.COLOR_GRAY);
	public static final Color DARK_GRAY = DisplayUtils.getDisplay().getSystemColor(SWT.COLOR_DARK_GRAY);
	public static final Color CYAN = DisplayUtils.getDisplay().getSystemColor(SWT.COLOR_CYAN);
	public static final Color DARK_CYAN = DisplayUtils.getDisplay().getSystemColor(SWT.COLOR_DARK_CYAN);
	public static final Color DARK_YELLOW = DisplayUtils.getDisplay().getSystemColor(SWT.COLOR_DARK_YELLOW);
	public static final Color DARK_GREEN = DisplayUtils.getDisplay().getSystemColor(SWT.COLOR_DARK_GREEN);
	public static final Color BLUE = DisplayUtils.getDisplay().getSystemColor(SWT.COLOR_BLUE);
	public static final Color MAGENTA = DisplayUtils.getDisplay().getSystemColor(SWT.COLOR_MAGENTA);
	public static final Color YELLOW = DisplayUtils.getDisplay().getSystemColor(SWT.COLOR_YELLOW);
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
	private static final int[] colorIdsGradientHighContrast = new int[]{ //
			SWT.COLOR_BLUE, //
			SWT.COLOR_CYAN, //
			SWT.COLOR_GRAY, //
			SWT.COLOR_MAGENTA, //
			SWT.COLOR_GREEN, //
			SWT.COLOR_BLACK, //
			SWT.COLOR_DARK_YELLOW, //
			SWT.COLOR_RED //
	};
	private static List<Color> colorsGradientHighContrast;
	/*
	 * Publication
	 * Red, Green, Blue, Dark Red, Dark Green, Dark Blue
	 */
	public static final String COLOR_SCHEME_PUBLICATION = "Publication";
	private static final RGB[] colorIdsGradientPublication = new RGB[]{ //
			new RGB(255, 0, 0), //
			new RGB(0, 255, 0), //
			new RGB(0, 0, 255), //
			new RGB(150, 0, 0), //
			new RGB(0, 150, 0), //
			new RGB(0, 0, 150) //
	};
	private static List<Color> colorsGradientPublication;
	/*
	 * Print
	 */
	public static final String COLOR_SCHEME_PRINT = "Print";
	private static final RGB[] colorIdsGradientPrint = new RGB[]{ //
			new RGB(0, 69, 134), //
			new RGB(255, 66, 14), //
			new RGB(255, 211, 32), //
			new RGB(87, 157, 28), //
			new RGB(126, 0, 33), //
			new RGB(131, 202, 255), //
			new RGB(49, 64, 4), //
			new RGB(174, 207, 0), //
			new RGB(75, 31, 111), //
			new RGB(255, 149, 14) //
	};
	private static List<Color> colorsGradientPrint;
	/*
	 * 
	 */
	public static final String COLOR_SCHEME_UNLIMITED = "Unlimited";
	/*
	 * Variable Colors
	 */
	public static final int ALPHA_OPAQUE = 255;
	public static final int ALPHA_TRANSPARENT = 0;
	private static Map<Integer, Map<RGB, Color>> colorMap; // Alpha and Colors
	/**
	 * Creates a color array.
	 */
	static {
		initializeColors();
	}

	public static String[][] getAvailableColorSchemes() {

		String[][] elements = new String[7][2];
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
		elements[5][0] = COLOR_SCHEME_PRINT;
		elements[5][1] = COLOR_SCHEME_PRINT;
		//
		elements[6][0] = COLOR_SCHEME_UNLIMITED;
		elements[6][1] = COLOR_SCHEME_UNLIMITED;
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
		} else if(COLOR_SCHEME_PRINT.equals(colorScheme)) {
			return new ColorScheme(colorsGradientPrint);
		} else if(COLOR_SCHEME_UNLIMITED.equals(colorScheme)) {
			return new UnlimitedColorSchema();
		} else {
			// DEFAULT SCHEME_RED
			return new ColorScheme(colorsGradientRed);
		}
	}

	public static Color getColor(RGB rgb) {

		return getColor(rgb, ALPHA_OPAQUE);
	}

	public static Color getColor(RGB rgb, int alpha) {

		/*
		 * Get the alpha color map.
		 */
		Map<RGB, Color> alphaColors = colorMap.get(alpha);
		if(alphaColors == null) {
			alphaColors = new HashMap<RGB, Color>();
			colorMap.put(alpha, alphaColors);
		}
		/*
		 * Get the color.
		 */
		Color color = alphaColors.get(rgb);
		if(color == null) {
			Display display = DisplayUtils.getDisplay();
			color = new Color(display, rgb, alpha);
			alphaColors.put(rgb, color);
		}
		//
		return color;
	}

	public static Color getColor(int red, int green, int blue) {

		return getColor(red, green, blue, ALPHA_OPAQUE);
	}

	public static Color getColor(int red, int green, int blue, int alpha) {

		RGB rgb = new RGB(red, green, blue);
		return getColor(rgb, alpha);
	}

	public static Color getColor(String rgb) {

		return getColor(rgb, ALPHA_OPAQUE);
	}

	public static Color getColor(int color) {

		return getColor(getColorRGB(color), ALPHA_OPAQUE);
	}

	/*
	 * E.g.:
	 * 0,0,0 and alpha
	 * or
	 * 255,255,255 and alpha
	 * ...
	 * Returns WHITE on exception.
	 */
	public static Color getColor(String rgb, int alpha) {

		try {
			/*
			 * Assume that there are 3 values.
			 */
			String[] values = rgb.split(",");
			int red = Integer.parseInt(values[0]);
			int green = Integer.parseInt(values[1]);
			int blue = Integer.parseInt(values[2]);
			return getColor(new RGB(red, green, blue), alpha);
		} catch(Exception e) {
			return WHITE;
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
		Display display = DisplayUtils.getDisplay();
		colorMap = new HashMap<Integer, Map<RGB, Color>>();
		/*
		 * RED GRADIENT
		 */
		colorsGradientRed = new ArrayList<Color>();
		for(int colorId : colorIdsGradientRed) {
			Color color = getColor(colorId, 0, 0);
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
			Color color = getColor(colorId, 0, 0);
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
			Color color = getColor(rgb);
			colorsGradientPublication.add(color);
		}
		/*
		 * GRADIENT PRINT
		 */
		colorsGradientPrint = new ArrayList<Color>();
		for(RGB rgb : colorIdsGradientPrint) {
			Color color = getColor(rgb);
			colorsGradientPrint.add(color);
		}
	}

	public static int[] getColorRgba(int color) {

		int[] rgba = new int[4];
		int value = color;
		int r = (value >> 16) & 0xFF;
		int g = (value >> 8) & 0xFF;
		int b = (value >> 0) & 0xFF;
		int alpha = ((value >> 24) & 0xff);
		rgba[0] = r;
		rgba[1] = g;
		rgba[2] = b;
		rgba[3] = alpha;
		return rgba;
	}

	public static RGB getColorRGB(int color) {

		int[] rgba = getColorRgba(color);
		return new RGB(rgba[0], rgba[1], rgba[2]);
	}

	public static int getColorRgba(int r, int g, int b, double alpha) {

		int a = (int)(alpha * 255);
		int value = ((a & 0xFF) << 24) | ((r & 0xFF) << 16) | ((g & 0xFF) << 8) | ((b & 0xFF) << 0);
		return value;
	}

	public static String getColorRgbaHtml(int color) {

		int value = color;
		int r = (value >> 16) & 0xFF;
		int g = (value >> 8) & 0xFF;
		int b = (value >> 0) & 0xFF;
		double alpha = ((value >> 24) & 0xff) / 255;
		return "rgba(" + r + " ," + g + ", " + b + ", " + alpha + ")";
	}

	public static String getColorRgbHtml(int color) {

		int value = color;
		int r = (value >> 16) & 0xFF;
		int g = (value >> 8) & 0xFF;
		int b = (value >> 0) & 0xFF;
		return "rgb(" + r + " ," + g + ", " + b + ")";
	}
}
