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
import org.ejml.ops.NormOps;

public class PcaCalculatorNipals extends AbstractPcaCalculator {

	@Override
	public void compute(int numComps) {

		// Prepare data, E, p, t, threshold, scores, loadings
		int numberOfSamples = getSampleData().getNumRows();
		int numberOfVariables = getSampleData().getNumCols();
		final double threshold = 0.00001;
		setNumComps(numComps);
		DenseMatrix64F E = getSampleData();
		double scoreDotOld = 0;
		double scoreDotNew = 0;
		DenseMatrix64F p = new DenseMatrix64F(1, numberOfVariables);
		DenseMatrix64F t = CommonOps.extract(E, 0, numberOfSamples, 0, 1);
		DenseMatrix64F pMatrix = new DenseMatrix64F(1, numberOfVariables);
		DenseMatrix64F tMatrix = new DenseMatrix64F(1, numberOfSamples);
		setLoadings(new DenseMatrix64F(numComps, numberOfVariables));
		setScores(new DenseMatrix64F(numberOfSamples, numComps));
		// Iterate over number of components
		for(int i = 0; i < numComps; i++) {
			// do.. iterate until threshold reached
			do {
				scoreDotOld = scoreDotNew;
				double tMultiplied = 1 / CommonOps.dot(t, t);
				tMatrix.reshape(numberOfSamples, 1);
				t.reshape(numberOfSamples, 1);
				CommonOps.extract(t, 0, numberOfSamples, 0, 1, tMatrix, 0, 0);
				tMatrix.reshape(1, numberOfSamples);
				CommonOps.mult(tMultiplied, tMatrix, E, p);
				NormOps.normalizeF(p);
				double pMultiplied = 1 / CommonOps.dot(p, p);
				CommonOps.extract(p, 0, 1, 0, numberOfVariables, pMatrix, 0, 0);
				pMatrix.reshape(numberOfVariables, 1);
				CommonOps.mult(pMultiplied, E, pMatrix, t);
				pMatrix.reshape(1, numberOfVariables);
				scoreDotNew = CommonOps.dot(t, t);
			} while(Math.abs(scoreDotOld - scoreDotNew) > threshold);
			// write scores, loadings of current component
			CommonOps.extract(t, 0, numberOfSamples, 0, 1, getScores(), 0, i);
			CommonOps.extract(p, 0, 1, 0, numberOfVariables, getLoadings(), i, 0);
			DenseMatrix64F E_sub = new DenseMatrix64F(numberOfSamples, numberOfVariables);
			// subtract calculated t*p from E
			tMatrix.reshape(numberOfSamples, 1);
			CommonOps.mult(tMatrix, pMatrix, E_sub);
			CommonOps.subtractEquals(E, E_sub);
		}
	}
}
