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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.OptionalDouble;

import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.exception.MathIllegalArgumentException;
import org.ejml.data.DenseMatrix64F;
import org.ejml.ops.CommonOps;

@SuppressWarnings("restriction")
public class OplsCalculatorNipals extends AbstractMultivariateCalculator {

	public OplsCalculatorNipals(int numObs, int numVars, int numComps) throws MathIllegalArgumentException {
		super(numObs, numVars, numComps);
	}

	private DenseMatrix64F getYVector() {

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
		DenseMatrix64F yVector = new DenseMatrix64F(groupNames.size(), 1, true, vector);
		return yVector;
	}

	private DenseMatrix64F getAvgYVector() {

		double[] yVector = getYVector().data;
		double[] avgYData = new double[yVector.length];
		OptionalDouble avgValue = Arrays.stream(yVector).average();
		if(avgValue.isPresent()) {
			Arrays.fill(avgYData, avgValue.getAsDouble());
		}
		DenseMatrix64F avgYVector = new DenseMatrix64F(yVector.length, 1, true, avgYData);
		return avgYVector;
	}

	private DenseMatrix64F getAvgXVector() {

		DenseMatrix64F X = getSampleData();
		DenseMatrix64F avgOfCols = new DenseMatrix64F(1, getSampleData().getNumCols());
		CommonOps.sumCols(X, avgOfCols);
		CommonOps.divide(avgOfCols, getSampleData().getNumRows());
		return avgOfCols;
	}

	private DenseMatrix64F getSDXVector() {

		DenseMatrix64F X = getSampleData().copy();
		DenseMatrix64F avgOfCols = getAvgXVector();
		DenseMatrix64F sdXVector = new DenseMatrix64F(1, getSampleData().getNumCols());
		for(int i = 0; i < getSampleData().getNumCols(); i++) {
			for(int j = 0; j < getSampleData().getNumRows(); j++) {
				X.set(j, i, X.get(j, i) - avgOfCols.get(0, i));
				X.set(j, i, X.get(j, i) * X.get(j, i));
			}
		}
		CommonOps.sumCols(X, sdXVector);
		for(int i = 0; i < getSampleData().getNumCols(); i++) {
			sdXVector.set(0, i, Math.sqrt(sdXVector.get(0, i)));
		}
		return sdXVector;
	}

	@Override
	public void compute() {

		int numberOfSamples = getSampleData().getNumRows();
		int numberOfVariables = getSampleData().getNumCols();
		DenseMatrix64F T_ortho = new DenseMatrix64F(numberOfSamples, getNumComps() - 1);
		DenseMatrix64F P_ortho = new DenseMatrix64F(getNumComps() - 1, numberOfVariables);
		DenseMatrix64F W_ortho = new DenseMatrix64F(getNumComps() - 1, numberOfVariables);
		DenseMatrix64F t_ortho = new DenseMatrix64F(numberOfSamples, 1);
		DenseMatrix64F p_ortho = new DenseMatrix64F(1, numberOfVariables);
		DenseMatrix64F w_ortho = new DenseMatrix64F(1, numberOfVariables);
		DenseMatrix64F X = new DenseMatrix64F(1, 1);
		X.set(getSampleData());
		DenseMatrix64F y = new DenseMatrix64F(1, 1);
		y.set(getYVector());
		// DenseMatrix64F y_avg = getAvgYVector();
		// DenseMatrix64F x_avg = getAvgXVector();
		// DenseMatrix64F x_sd = getSDXVector();
		DenseMatrix64F te = new DenseMatrix64F(numberOfSamples, 1);
		DenseMatrix64F p = new DenseMatrix64F(1, numberOfVariables);
		DenseMatrix64F w = new DenseMatrix64F(1, numberOfVariables);
		DenseMatrix64F u = new DenseMatrix64F(numberOfSamples, 1);
		DenseMatrix64F ce = new DenseMatrix64F(1, 1);
		DenseMatrix64F b = new DenseMatrix64F(numberOfVariables, 1);
		// #1
		DenseMatrix64F yy = new DenseMatrix64F(1, 1);
		CommonOps.multInner(y, yy);
		CommonOps.multTransA(y, X, w);
		CommonOps.divide(w, yy.get(0));
		// #2
		DenseMatrix64F ww = new DenseMatrix64F(1, 1);
		CommonOps.transpose(w);
		CommonOps.multInner(w, ww);
		double absW = Math.sqrt(ww.get(0));
		CommonOps.divide(w, absW);
		// #3
		for(int i = 0; i < getNumComps(); i++) {
			DenseMatrix64F wTemp = new DenseMatrix64F(1, 1);
			CommonOps.multInner(w, wTemp);
			CommonOps.mult(X, w, te);
			CommonOps.divide(te, wTemp.get(0));
			// #4
			DenseMatrix64F tTemp = new DenseMatrix64F(1, 1);
			CommonOps.multInner(te, tTemp);
			CommonOps.multTransA(te, y, ce);
			CommonOps.divide(ce, tTemp.get(0));
			// #5
			DenseMatrix64F cTemp = new DenseMatrix64F(1, 1);
			CommonOps.multInner(ce, cTemp);
			CommonOps.mult(y, ce, u);
			CommonOps.divide(u, cTemp.get(0));
			// #6
			CommonOps.multTransA(te, X, p);
			CommonOps.divide(p, tTemp.get(0));
			if(i < getNumComps() - 1) {
				// #7
				DenseMatrix64F wTemp2 = new DenseMatrix64F(1, 1);
				DenseMatrix64F w_ortho_temp = new DenseMatrix64F(numberOfVariables, 1);
				DenseMatrix64F wTemp3 = new DenseMatrix64F(1, numberOfVariables);
				CommonOps.multTransAB(w, p, wTemp2);
				CommonOps.divide(wTemp2, wTemp.get(0));
				CommonOps.mult(w, wTemp2, w_ortho_temp);
				CommonOps.transpose(w_ortho_temp);
				CommonOps.subtract(p, w_ortho_temp, w_ortho);
				// #8
				DenseMatrix64F ww_ortho = new DenseMatrix64F(1, 1);
				CommonOps.transpose(w_ortho);
				CommonOps.multInner(w_ortho, ww_ortho);
				double absW_ortho = Math.sqrt(ww_ortho.get(0));
				CommonOps.divide(w_ortho, absW_ortho);
				// #9
				CommonOps.transpose(w_ortho_temp);
				CommonOps.multInner(w_ortho_temp, ww_ortho);
				CommonOps.mult(X, w_ortho, t_ortho);
				CommonOps.divide(t_ortho, ww_ortho.get(0));
				// #10
				DenseMatrix64F tt_temp = new DenseMatrix64F(1, 1);
				CommonOps.multInner(t_ortho, tt_temp);
				CommonOps.multTransA(t_ortho, X, p_ortho);
				CommonOps.divide(p_ortho, tt_temp.get(0));
				// #11
				DenseMatrix64F X_temp = new DenseMatrix64F(numberOfSamples, numberOfVariables);
				CommonOps.mult(t_ortho, p_ortho, X_temp);
				CommonOps.subtract(X, X_temp, X);
				// #12
				for(int j = 0; j < numberOfSamples; j++) {
					T_ortho.set(j, i, t_ortho.get(j));
				}
				for(int k = 0; k < numberOfVariables; k++) {
					P_ortho.set(i, k, p_ortho.get(k));
					W_ortho.set(i, k, w_ortho.get(k));
				}
				CommonOps.transpose(w_ortho);
			}
		}
		CommonOps.mult(w, ce, b);
		double combinedScores[] = new double[numberOfSamples * getNumComps()];
		System.arraycopy(te.getData(), 0, combinedScores, 0, numberOfSamples);
		System.arraycopy(T_ortho.getData(), 0, combinedScores, numberOfSamples, numberOfSamples * (getNumComps() - 1));
		DenseMatrix64F scores = new DenseMatrix64F(numberOfSamples, getNumComps(), false, combinedScores);
		setScores(scores);
		double combinedLoadings[] = new double[getNumComps() * numberOfVariables];
		System.arraycopy(p.getData(), 0, combinedLoadings, 0, numberOfVariables);
		System.arraycopy(P_ortho.getData(), 0, combinedLoadings, numberOfVariables, (getNumComps() - 1) * numberOfVariables);
		DenseMatrix64F loadings = new DenseMatrix64F(getNumComps(), numberOfVariables, true, combinedLoadings);
		setLoadings(loadings);
		this.setComputeSuccess();
	}
}
