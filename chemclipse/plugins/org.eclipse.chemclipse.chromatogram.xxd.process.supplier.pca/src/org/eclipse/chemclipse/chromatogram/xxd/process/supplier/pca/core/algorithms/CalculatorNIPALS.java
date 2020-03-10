/*******************************************************************************
 * Copyright (c) 2018, 2019 Lablicate Gmbh.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Lorenz Gerber - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.core.algorithms;

import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.exception.MathIllegalArgumentException;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.AbstractMultivariateCalculator;
import org.ejml.data.DMatrixRMaj;
import org.ejml.dense.row.CommonOps_DDRM;
import org.ejml.dense.row.NormOps_DDRM;

public class CalculatorNIPALS extends AbstractMultivariateCalculator {

	public CalculatorNIPALS(int numObs, int numVars, int numComps) throws MathIllegalArgumentException {
		super(numObs, numVars, numComps);
	}

	@Override
	public void compute() {

		// Prepare data, E, p, t, threshold, scores, loadings
		int numberOfSamples = getSampleData().getNumRows();
		int numberOfVariables = getSampleData().getNumCols();
		final double threshold = 0.00001;
		DMatrixRMaj E = getSampleData().copy();
		double scoreDotOld = 0;
		double scoreDotNew = 0;
		DMatrixRMaj p = new DMatrixRMaj(1, numberOfVariables);
		DMatrixRMaj t = CommonOps_DDRM.extract(E, 0, numberOfSamples, 0, 1);
		DMatrixRMaj pMatrix = new DMatrixRMaj(1, numberOfVariables);
		DMatrixRMaj tMatrix = new DMatrixRMaj(1, numberOfSamples);
		setLoadings(new DMatrixRMaj(getNumComps(), numberOfVariables));
		setScores(new DMatrixRMaj(numberOfSamples, getNumComps()));
		// Iterate over number of components
		for(int i = 0; i < getNumComps(); i++) {
			// do.. iterate until threshold reached
			int bailOut = 10000;
			do {
				scoreDotOld = scoreDotNew;
				double tMultiplied = 1 / CommonOps_DDRM.dot(t, t);
				tMatrix.reshape(numberOfSamples, 1);
				t.reshape(numberOfSamples, 1);
				CommonOps_DDRM.extract(t, 0, numberOfSamples, 0, 1, tMatrix, 0, 0);
				tMatrix.reshape(1, numberOfSamples);
				CommonOps_DDRM.mult(tMultiplied, tMatrix, E, p);
				NormOps_DDRM.normalizeF(p);
				double pMultiplied = 1 / CommonOps_DDRM.dot(p, p);
				CommonOps_DDRM.extract(p, 0, 1, 0, numberOfVariables, pMatrix, 0, 0);
				pMatrix.reshape(numberOfVariables, 1);
				CommonOps_DDRM.mult(pMultiplied, E, pMatrix, t);
				pMatrix.reshape(1, numberOfVariables);
				scoreDotNew = CommonOps_DDRM.dot(t, t);
				bailOut--;
			} while(Math.abs(scoreDotOld - scoreDotNew) > threshold && bailOut != 0);
			// write scores, loadings of current component
			CommonOps_DDRM.extract(t, 0, numberOfSamples, 0, 1, getScores(), 0, i);
			CommonOps_DDRM.extract(p, 0, 1, 0, numberOfVariables, getLoadings(), i, 0);
			DMatrixRMaj E_sub = new DMatrixRMaj(numberOfSamples, numberOfVariables);
			// subtract calculated t*p from E
			tMatrix.reshape(numberOfSamples, 1);
			CommonOps_DDRM.mult(tMatrix, pMatrix, E_sub);
			CommonOps_DDRM.subtractEquals(E, E_sub);
		}
		this.setComputeSuccess();
	}
}
