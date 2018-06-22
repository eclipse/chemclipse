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
package org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.utility;

import java.awt.Color;

import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.model.IColor;
import org.eclipse.chemclipse.swt.ui.support.Colors;
import org.eclipse.swt.widgets.Display;

public class PcaColorGroup {

	public static java.awt.Color getActualSelectedColor(java.awt.Color color) {

		return Color.DARK_GRAY;
	}

	public static javafx.scene.paint.Color getActualSelectedColor(javafx.scene.paint.Color color) {

		return javafx.scene.paint.Color.DARKGRAY;
	}

	public static org.eclipse.swt.graphics.Color getActualSelectedColor(org.eclipse.swt.graphics.Color color) {

		return Colors.DARK_GRAY;
	}

	public static java.awt.Color getSampleColorAWT(IColor color) {

		int[] rgba = color.getColorRgba();
		return new Color(rgba[0], rgba[1], rgba[2], rgba[3]);
	}

	public static java.awt.Color getSampleColorAWT(int color) {

		int[] rgba = Colors.getColorRgba(color);
		return new Color(rgba[0], rgba[1], rgba[2], rgba[3]);
	}

	public static javafx.scene.paint.Color getSampleColorFX(IColor color) {

		int[] rgba = color.getColorRgba();
		return javafx.scene.paint.Color.color(rgba[0] / 255.0, rgba[1] / 255.0, rgba[2] / 255.0, rgba[3] / 255.0);
	}

	public static javafx.scene.paint.Color getSampleColorFX(int color) {

		int[] rgba = Colors.getColorRgba(color);
		return javafx.scene.paint.Color.color(rgba[0] / 255.0, rgba[1] / 255.0, rgba[2] / 255.0, rgba[3] / 255.0);
	}

	public static org.eclipse.swt.graphics.Color getSampleColorSWT(IColor color) {

		int[] rgba = color.getColorRgba();
		return new org.eclipse.swt.graphics.Color(Display.getDefault(), rgba[0], rgba[1], rgba[2], rgba[3]);
	}

	public static org.eclipse.swt.graphics.Color getSampleColorSWT(int color) {

		return Colors.getColor(color);
	}

	public static java.awt.Color getUnselectedColor(java.awt.Color color) {

		return new Color(color.getRed(), color.getGreen(), color.getBlue(), (int)0.5 * 255);
	}

	public static javafx.scene.paint.Color getUnselectedColor(javafx.scene.paint.Color color) {

		return javafx.scene.paint.Color.color(color.getRed(), color.getGreen(), color.getBlue(), 0.5);
	}

	public static org.eclipse.swt.graphics.Color getUnselectedColor(org.eclipse.swt.graphics.Color color) {

		return Colors.GRAY;
	}
}
