/*******************************************************************************
 * Copyright (c) 2015, 2018 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Florian Ernst - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.msd.peak.detector.supplier.deconvolution.support;

public class ArrayView implements IArrayView {

	private double yScale;

	public ArrayView(double point) {
		yScale = point;
	}

	@Override
	public double getScale() {

		return yScale;
	}

	@Override
	public void setyScale(double yScale) {

		this.yScale = yScale;
	}
}
