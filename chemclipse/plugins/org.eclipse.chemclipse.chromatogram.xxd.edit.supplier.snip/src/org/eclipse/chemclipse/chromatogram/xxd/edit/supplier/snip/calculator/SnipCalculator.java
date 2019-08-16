/*******************************************************************************
 * Copyright (c) 2014, 2018 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.edit.supplier.snip.calculator;

import org.eclipse.core.runtime.IProgressMonitor;

public class SnipCalculator {

	/**
	 * Calculates the intensity values.
	 * C.G. Ryan, E. Clayton, W.L. Griffin, S.H. Sie, D.R. Cousens
	 * SNIP, a statistics-sensitive background treatment for the quantitative analysis of PIXE spectra in geoscience applications
	 * http://dx.doi.org/10.1016/0168-583X(88)90063-8
	 * 
	 * @param intensityValues
	 * @param iterations
	 * @return float[]
	 */
	public float[] calculateBaselineIntensityValues(float[] intensityValues, int iterations, IProgressMonitor monitor) {

		int size = intensityValues.length;
		float[] tmp = new float[size];
		for(int i = 1; i <= iterations; ++i) {
			monitor.subTask("Iteration: " + i);
			for(int j = i; j < size - i; ++j) {
				float a = intensityValues[j];
				float b = (intensityValues[j - i] + intensityValues[j + i]) / 2;
				if(b < a) {
					a = b;
				}
				tmp[j] = a;
			}
			for(int j = i; j < size - i; ++j) {
				intensityValues[j] = tmp[j];
			}
		}
		return intensityValues;
	}
}
