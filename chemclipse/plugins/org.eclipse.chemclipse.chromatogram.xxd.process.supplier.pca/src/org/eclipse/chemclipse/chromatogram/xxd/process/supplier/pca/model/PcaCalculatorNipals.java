/*******************************************************************************
 * Copyright (c) 2018 Lablicate Gmbh.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Lorenz Gerber - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model;

import org.ejml.data.DenseMatrix64F;
import org.ejml.ops.CommonOps;

public class PcaCalculatorNipals extends AbstractPcaCalculator {

	@Override
	public void compute(int numComps) {

		// Prepare data, E, p, t, threshold
		int numberOfSamples = getSampleData().getNumRows();
		int numberOfVariables = getSampleData().getNumCols();
		final double threshold = 0.00001;
		setNumComps(numComps);
		DenseMatrix64F E = getSampleData();
		double scoreDotOld = 0;
		double scoreDotNew = 0;
		DenseMatrix64F p = new DenseMatrix64F(numberOfVariables, 1);
		DenseMatrix64F t = CommonOps.extract(E, 0, numberOfVariables, 0, 1);
		DenseMatrix64F pMatrix = new DenseMatrix64F(numberOfVariables);
		DenseMatrix64F tMatrix = new DenseMatrix64F(numberOfSamples);
		// Iterate over number of components
		for(int i = 0; i < numComps; i++) {
			// do.. iterate until threshold reached
			do {
				scoreDotOld = scoreDotNew;
				double tMultiplied = 1 / CommonOps.dot(t, t);
			} while(Math.abs(scoreDotOld - scoreDotNew) > threshold);
			// write scores, loadings of current component
		}
		// subtract calculated t*p from E
	}
}
