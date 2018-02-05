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

import javafx.beans.property.IntegerProperty;

public interface IColor extends IVisualization {

	IntegerProperty colorProperty();

	@Override
	default void copyVisualizationProperties(IVisualization visualization) {

		if(visualization instanceof IColor) {
			IColor color = (IColor)visualization;
			setColor(color.getColor());
		}
	}

	int getColor();

	default int[] getColorRgba() {

		int[] rgba = new int[4];
		int value = getColor();
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

	default String getColorRgbaHtml() {

		int value = getColor();
		int r = (value >> 16) & 0xFF;
		int g = (value >> 8) & 0xFF;
		int b = (value >> 0) & 0xFF;
		double alpha = ((value >> 24) & 0xff) / 255;
		return "rgba(" + r + " ," + g + ", " + b + ", " + alpha + ")";
	}

	default String getColorRgbHtml() {

		int value = getColor();
		int r = (value >> 16) & 0xFF;
		int g = (value >> 8) & 0xFF;
		int b = (value >> 0) & 0xFF;
		return "rgb(" + r + " ," + g + ", " + b + ")";
	}

	void setColor(int color);

	default void setColorRgba(int r, int g, int b, double alpha) {

		int a = (int)(alpha * 255);
		int value = ((a & 0xFF) << 24) | ((r & 0xFF) << 16) | ((g & 0xFF) << 8) | ((b & 0xFF) << 0);
		setColor(value);
	}
}
