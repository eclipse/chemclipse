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
package org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.core.algorithms;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.OptionalDouble;

import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.exception.MathIllegalArgumentException;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.AbstractMultivariateCalculator;
import org.ejml.data.DMatrixRMaj;
import org.ejml.dense.row.CommonOps_DDRM;


public class CalculatorOPLS extends AbstractMultivariateCalculator {

	public CalculatorOPLS(int numObs, int numVars, int numComps) throws MathIllegalArgumentException {
		super(numObs, numVars, numComps);
	}

	private DMatrixRMaj getYVector() {

		HashSet<String> groupNamesSet = new HashSet<>();
		ArrayList<String> groupNames = getGroupNames();
		double[] vector = new double[groupNames.size()];
		groupNamesSet.addAll(groupNames);
		List<String> uniqueGroupNames = Arrays.asList(groupNamesSet.toArray(new String[groupNamesSet.size()]));
		int yIterator = 0;
		for(String myString : groupNames) {
			vector[yIterator] = uniqueGroupNames.indexOf(myString);
			yIterator++;
		}
		DMatrixRMaj yVector = new DMatrixRMaj(groupNames.size(), 1, true, vector);
		return yVector;
	}

	private DMatrixRMaj getAvgYVector() {

		double[] yVector = getYVector().data;
		double[] avgYData = new double[yVector.length];
		OptionalDouble avgValue = Arrays.stream(yVector).average();
		if(avgValue.isPresent()) {
			Arrays.fill(avgYData, avgValue.getAsDouble());
		}
		DMatrixRMaj avgYVector = new DMatrixRMaj(yVector.length, 1, true, avgYData);
		return avgYVector;
	}

	private DMatrixRMaj getAvgXVector() {

		DMatrixRMaj X = getSampleData();
		DMatrixRMaj avgOfCols = new DMatrixRMaj(1, getSampleData().getNumCols());
		CommonOps_DDRM.sumCols(X, avgOfCols);
		CommonOps_DDRM.divide(avgOfCols, getSampleData().getNumRows());
		return avgOfCols;
	}

	private DMatrixRMaj getSDXVector() {

		DMatrixRMaj X = getSampleData().copy();
		DMatrixRMaj avgOfCols = getAvgXVector();
		DMatrixRMaj sdXVector = new DMatrixRMaj(1, getSampleData().getNumCols());
		for(int i = 0; i < getSampleData().getNumCols(); i++) {
			for(int j = 0; j < getSampleData().getNumRows(); j++) {
				X.set(j, i, X.get(j, i) - avgOfCols.get(0, i));
				X.set(j, i, X.get(j, i) * X.get(j, i));
			}
		}
		CommonOps_DDRM.sumCols(X, sdXVector);
		for(int i = 0; i < getSampleData().getNumCols(); i++) {
			sdXVector.set(0, i, Math.sqrt(sdXVector.get(0, i)));
		}
		return sdXVector;
	}

	@Override
	public void compute() {

		int numberOfSamples = getSampleData().getNumRows();
		int numberOfVariables = getSampleData().getNumCols();
		DMatrixRMaj T_ortho = new DMatrixRMaj(numberOfSamples, getNumComps() - 1);
		DMatrixRMaj P_ortho = new DMatrixRMaj(getNumComps() - 1, numberOfVariables);
		DMatrixRMaj W_ortho = new DMatrixRMaj(getNumComps() - 1, numberOfVariables);
		DMatrixRMaj t_ortho = new DMatrixRMaj(numberOfSamples, 1);
		DMatrixRMaj p_ortho = new DMatrixRMaj(1, numberOfVariables);
		DMatrixRMaj w_ortho = new DMatrixRMaj(1, numberOfVariables);
		DMatrixRMaj X = new DMatrixRMaj(1, 1);
		X.set(getSampleData());
		DMatrixRMaj y = new DMatrixRMaj(1, 1);
		y.set(getYVector());
		// DenseMatrix64F y_avg = getAvgYVector();
		// DenseMatrix64F x_avg = getAvgXVector();
		// DenseMatrix64F x_sd = getSDXVector();
		DMatrixRMaj te = new DMatrixRMaj(numberOfSamples, 1);
		DMatrixRMaj p = new DMatrixRMaj(1, numberOfVariables);
		DMatrixRMaj w = new DMatrixRMaj(1, numberOfVariables);
		DMatrixRMaj u = new DMatrixRMaj(numberOfSamples, 1);
		DMatrixRMaj ce = new DMatrixRMaj(1, 1);
		DMatrixRMaj b = new DMatrixRMaj(numberOfVariables, 1);
		// #1
		DMatrixRMaj yy = new DMatrixRMaj(1, 1);
		CommonOps_DDRM.multInner(y, yy);
		CommonOps_DDRM.multTransA(y, X, w);
		CommonOps_DDRM.divide(w, yy.get(0));
		// #2
		DMatrixRMaj ww = new DMatrixRMaj(1, 1);
		CommonOps_DDRM.transpose(w);
		CommonOps_DDRM.multInner(w, ww);
		double absW = Math.sqrt(ww.get(0));
		CommonOps_DDRM.divide(w, absW);
		// #3
		for(int i = 0; i < getNumComps(); i++) {
			DMatrixRMaj wTemp = new DMatrixRMaj(1, 1);
			CommonOps_DDRM.multInner(w, wTemp);
			CommonOps_DDRM.mult(X, w, te);
			CommonOps_DDRM.divide(te, wTemp.get(0));
			// #4
			DMatrixRMaj tTemp = new DMatrixRMaj(1, 1);
			CommonOps_DDRM.multInner(te, tTemp);
			CommonOps_DDRM.multTransA(te, y, ce);
			CommonOps_DDRM.divide(ce, tTemp.get(0));
			// #5
			DMatrixRMaj cTemp = new DMatrixRMaj(1, 1);
			CommonOps_DDRM.multInner(ce, cTemp);
			CommonOps_DDRM.mult(y, ce, u);
			CommonOps_DDRM.divide(u, cTemp.get(0));
			// #6
			CommonOps_DDRM.multTransA(te, X, p);
			CommonOps_DDRM.divide(p, tTemp.get(0));
			if(i < getNumComps() - 1) {
				// #7
				DMatrixRMaj wTemp2 = new DMatrixRMaj(1, 1);
				DMatrixRMaj w_ortho_temp = new DMatrixRMaj(numberOfVariables, 1);
				DMatrixRMaj wTemp3 = new DMatrixRMaj(1, numberOfVariables);
				CommonOps_DDRM.multTransAB(w, p, wTemp2);
				CommonOps_DDRM.divide(wTemp2, wTemp.get(0));
				CommonOps_DDRM.mult(w, wTemp2, w_ortho_temp);
				CommonOps_DDRM.transpose(w_ortho_temp);
				CommonOps_DDRM.subtract(p, w_ortho_temp, w_ortho);
				// #8
				DMatrixRMaj ww_ortho = new DMatrixRMaj(1, 1);
				CommonOps_DDRM.transpose(w_ortho);
				CommonOps_DDRM.multInner(w_ortho, ww_ortho);
				double absW_ortho = Math.sqrt(ww_ortho.get(0));
				CommonOps_DDRM.divide(w_ortho, absW_ortho);
				// #9
				CommonOps_DDRM.transpose(w_ortho_temp);
				CommonOps_DDRM.multInner(w_ortho_temp, ww_ortho);
				CommonOps_DDRM.mult(X, w_ortho, t_ortho);
				CommonOps_DDRM.divide(t_ortho, ww_ortho.get(0));
				// #10
				DMatrixRMaj tt_temp = new DMatrixRMaj(1, 1);
				CommonOps_DDRM.multInner(t_ortho, tt_temp);
				CommonOps_DDRM.multTransA(t_ortho, X, p_ortho);
				CommonOps_DDRM.divide(p_ortho, tt_temp.get(0));
				// #11
				DMatrixRMaj X_temp = new DMatrixRMaj(numberOfSamples, numberOfVariables);
				CommonOps_DDRM.mult(t_ortho, p_ortho, X_temp);
				CommonOps_DDRM.subtract(X, X_temp, X);
				// #12
				for(int j = 0; j < numberOfSamples; j++) {
					T_ortho.set(j, i, t_ortho.get(j));
				}
				for(int k = 0; k < numberOfVariables; k++) {
					P_ortho.set(i, k, p_ortho.get(k));
					W_ortho.set(i, k, w_ortho.get(k));
				}
				CommonOps_DDRM.transpose(w_ortho);
			}
		}
		CommonOps_DDRM.mult(w, ce, b);
		double combinedScores[] = new double[numberOfSamples * getNumComps()];
		System.arraycopy(te.getData(), 0, combinedScores, 0, numberOfSamples);
		System.arraycopy(T_ortho.getData(), 0, combinedScores, numberOfSamples, numberOfSamples * (getNumComps() - 1));
		DMatrixRMaj scores = new DMatrixRMaj(numberOfSamples, getNumComps(), false, combinedScores);
		setScores(scores);
		double combinedLoadings[] = new double[getNumComps() * numberOfVariables];
		System.arraycopy(p.getData(), 0, combinedLoadings, 0, numberOfVariables);
		System.arraycopy(P_ortho.getData(), 0, combinedLoadings, numberOfVariables, (getNumComps() - 1) * numberOfVariables);
		DMatrixRMaj loadings = new DMatrixRMaj(getNumComps(), numberOfVariables, true, combinedLoadings);
		setLoadings(loadings);
		this.setComputeSuccess();
	}
}
