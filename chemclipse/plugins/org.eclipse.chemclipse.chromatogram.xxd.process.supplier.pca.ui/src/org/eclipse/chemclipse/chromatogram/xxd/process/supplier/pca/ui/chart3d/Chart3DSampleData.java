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

	public double getPcaXData() {

		return ((pcaX > 0) ? new Double(sample.getPcaResult().getEigenSpace()[pcaX - 1]) : new Double(0));
	}

	public int getPcaY() {

		return pcaY;
	}

	public double getPcaYData() {

		return ((pcaY > 0) ? new Double(sample.getPcaResult().getEigenSpace()[pcaY - 1]) : new Double(0));
	}

	public int getPcaZ() {

		return pcaZ;
	}

	public double getPcaZData() {

		return ((pcaZ > 0) ? new Double(sample.getPcaResult().getEigenSpace()[pcaZ - 1]) : new Double(0));
	}

	public ISample getSample() {

		return sample;
	}
}
