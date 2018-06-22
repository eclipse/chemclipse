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

import org.eclipse.chemclipse.swt.ui.support.Colors;

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

		int value = getColor();
		return Colors.getColorRgba(value);
	}

	default String getColorRgbaHtml() {

		int value = getColor();
		return Colors.getColorRgbaHtml(value);
	}

	default String getColorRgbHtml() {

		int value = getColor();
		return Colors.getColorRgbHtml(value);
	}

	void setColor(int color);

	default void setColorRgba(int r, int g, int b, double alpha) {

		int value = Colors.getColorRgba(r, g, b, alpha);
		setColor(value);
	}
}
