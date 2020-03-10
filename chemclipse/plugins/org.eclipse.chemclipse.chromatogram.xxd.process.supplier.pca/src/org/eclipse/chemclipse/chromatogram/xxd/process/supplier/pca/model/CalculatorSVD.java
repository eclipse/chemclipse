/*******************************************************************************
 * Copyright (c) 2018, 2019 Lablicate GmbH.
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
import org.ejml.data.DMatrixRMaj;
import org.ejml.dense.row.CommonOps_DDRM;
import org.ejml.dense.row.SingularOps_DDRM;
import org.ejml.dense.row.factory.DecompositionFactory_DDRM;
import org.ejml.interfaces.decomposition.SingularValueDecomposition;


public class CalculatorSVD extends AbstractMultivariateCalculator {

	public CalculatorSVD(int numObs, int numVars, int numComps) throws MathIllegalArgumentException {	
		super(numObs, numVars, numComps);
		DMatrixRMaj emptyLoadings = new DMatrixRMaj(1,numVars);
		setLoadings(emptyLoadings);
	}

	@Override
	public void compute() {

		computeLoadings();
		computeScores();
		setComputeSuccess();
	}

	private void computeLoadings() {

		SingularValueDecomposition<DMatrixRMaj> svd = DecompositionFactory_DDRM.svd(getSampleData().getNumRows(), getSampleData().getNumCols(), false, true, false);
		svd.decompose(getSampleData());
		setLoadings(svd.getV(null, true));
		DMatrixRMaj W = svd.getW(null);
		SingularOps_DDRM.descendingOrder(null, false, W, getLoadings(), true);
		getLoadings().reshape(getNumComps(), getMean().length, true);
	}

	private void computeScores() {

		double concatMeans[] = new double[getSampleData().getNumRows() * getSampleData().getNumCols()];
		for(int i = 0; i < getSampleData().getNumRows(); i++) {
			System.arraycopy(getMean(), 0, concatMeans, i * getSampleData().getNumCols(), getSampleData().getNumCols());
		}
		DMatrixRMaj means = DMatrixRMaj.wrap(getSampleData().getNumRows(), getSampleData().getNumCols(), concatMeans);
		// DMatrixRMaj sample = DMatrixRMaj.wrap(getSampleData().getNumRows(), getSampleData().getNumCols(), getSampleData().data);
		DMatrixRMaj sample = getSampleData().copy();
		DMatrixRMaj rotated = new DMatrixRMaj(getNumComps(), getSampleData().getNumRows());
		CommonOps_DDRM.subtract(sample, means, sample);
		DMatrixRMaj loadings = new DMatrixRMaj(getLoadings());
		CommonOps_DDRM.transpose(sample);
		CommonOps_DDRM.mult(loadings, sample, rotated);
		CommonOps_DDRM.transpose(rotated);
		setScores(rotated);
	}
}
