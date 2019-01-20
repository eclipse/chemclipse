/*******************************************************************************
 * Copyright (c) 2018 Lablicate GmbH.
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

import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.exception.MathIllegalArgumentException;
import org.ejml.data.DenseMatrix64F;
import org.ejml.factory.DecompositionFactory;
import org.ejml.interfaces.decomposition.SingularValueDecomposition;
import org.ejml.ops.CommonOps;
import org.ejml.ops.SingularOps;

public class PcaCalculatorSvd extends AbstractMultivariateCalculator {

	public PcaCalculatorSvd(int numObs, int numVars, int numComps) throws MathIllegalArgumentException {

		super(numObs, numVars, numComps);
	}

	@Override
	public void compute() {

		computeLoadings();
		computeScores();
		setComputeSuccess();
	}

	private void computeLoadings() {

		SingularValueDecomposition<DenseMatrix64F> svd = DecompositionFactory.svd(getSampleData().getNumRows(), getSampleData().getNumCols(), false, true, false);
		svd.decompose(getSampleData());
		setLoadings(svd.getV(null, true));
		DenseMatrix64F W = svd.getW(null);
		SingularOps.descendingOrder(null, false, W, getLoadings(), true);
		getLoadings().reshape(getNumComps(), getMean().length, true);
	}

	private void computeScores() {

		double concatMeans[] = new double[getSampleData().getNumRows() * getSampleData().getNumCols()];
		for(int i = 0; i < getSampleData().getNumRows(); i++) {
			System.arraycopy(getMean(), 0, concatMeans, i * getSampleData().getNumCols(), getSampleData().getNumCols());
		}
		DenseMatrix64F means = DenseMatrix64F.wrap(getSampleData().getNumRows(), getSampleData().getNumCols(), concatMeans);
		// DenseMatrix64F sample = DenseMatrix64F.wrap(getSampleData().getNumRows(), getSampleData().getNumCols(), getSampleData().data);
		DenseMatrix64F sample = getSampleData().copy();
		DenseMatrix64F rotated = new DenseMatrix64F(getNumComps(), getSampleData().getNumRows());
		CommonOps.subtract(sample, means, sample);
		DenseMatrix64F loadings = new DenseMatrix64F(getLoadings());
		CommonOps.transpose(sample);
		CommonOps.mult(loadings, sample, rotated);
		CommonOps.transpose(rotated);
		setScores(rotated);
	}
}
