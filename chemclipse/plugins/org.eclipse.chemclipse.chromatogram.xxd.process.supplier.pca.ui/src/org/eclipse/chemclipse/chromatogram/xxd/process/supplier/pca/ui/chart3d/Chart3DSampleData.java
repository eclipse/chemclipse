/*******************************************************************************
 * Copyright (c) 2017 Jan Holy.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Jan Holy - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.chart3d;

import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.ISample;

import javafx.scene.paint.Color;

public class Chart3DSampleData {

	private Color color;
	private int pcaX;
	private int pcaY;
	private int pcaZ;
	private ISample sample;
	private double scale;

	public Chart3DSampleData(ISample sample, int pcaX, int pcaY, int pcaZ, Color color) {
		this.sample = sample;
		this.pcaX = pcaX;
		this.pcaY = pcaY;
		this.pcaZ = pcaZ;
		this.color = color;
	}

	public Color getColor() {

		return color;
	}

	public int getPcaX() {

		return pcaX;
	}

	public double getPcaXData(boolean isScaled) {

		double d = ((pcaX > 0) ? new Double(sample.getPcaResult().getEigenSpace()[pcaX - 1]) : new Double(0));
		return (isScaled ? d * scale : d);
	}

	public int getPcaY() {

		return pcaY;
	}

	public double getPcaYData(boolean isScaled) {

		double d = ((pcaY > 0) ? new Double(sample.getPcaResult().getEigenSpace()[pcaY - 1]) : new Double(0));
		return (isScaled ? d * scale : d);
	}

	public int getPcaZ() {

		return pcaZ;
	}

	public double getPcaZData(boolean isScaled) {

		double d = ((pcaZ > 0) ? new Double(sample.getPcaResult().getEigenSpace()[pcaZ - 1]) : new Double(0));
		return (isScaled ? d * scale : d);
	}

	public ISample getSample() {

		return sample;
	}

	public double getScale() {

		return scale;
	}

	public void setScale(double scale) {

		this.scale = scale;
	}
}
