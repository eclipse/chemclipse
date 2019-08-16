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

import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.exception.MathIllegalArgumentException;
import org.eclipse.chemclipse.model.statistics.ISample;
import org.ejml.data.DenseMatrix64F;
import org.ejml.ops.CommonOps;

public abstract class AbstractMultivariateCalculator implements IMultivariateCalculator {

	private DenseMatrix64F loadings;
	private DenseMatrix64F scores;
	private double mean[];
	private int numComps;
	private DenseMatrix64F sampleData;
	private ArrayList<ISample> sampleKeys = new ArrayList<>();
	private ArrayList<String> groupNames = new ArrayList<>();
	private int sampleIndex;
	private boolean computeSuccess;

	public AbstractMultivariateCalculator(int numObs, int numVars, int numComps) throws MathIllegalArgumentException {
		if(numComps > numObs) {
			throw new MathIllegalArgumentException("Number of samples has to be equle or bigger than number of componets.");
		}
		if(numVars <= 0) {
			throw new MathIllegalArgumentException("Number of variables has to be equle or bigger than one.");
		}
		if(numObs <= 0) {
			throw new MathIllegalArgumentException("Number of samples has to be equle or bigger than one.");
		}
		if(numComps <= 0) {
			throw new MathIllegalArgumentException("Number of samples has to be equle or bigger than one.");
		}
		sampleData = new DenseMatrix64F(numObs, numVars);
		this.mean = new double[numVars];
		sampleIndex = 0;
		this.numComps = numComps;
		computeSuccess = false;
	}

	@Override
	public void setComputeSuccess() {

		computeSuccess = true;
	}

	@Override
	public boolean getComputeStatus() {

		return computeSuccess;
	}

	@Override
	public void addObservation(double[] obsData, ISample sampleKey, String groupName) {
		/*
		 * if(obsData.length < sampleData.getNumCols()) {
		 * this.invalidatePca();
		 * }
		 */

		for(int i = 0; i < obsData.length; i++) {
			sampleData.set(sampleIndex, i, obsData[i]);
		}
		sampleKeys.add(sampleKey);
		groupNames.add(groupName);
		sampleIndex++;
	}

	protected ArrayList<String> getGroupNames() {

		return groupNames;
	}

	public DenseMatrix64F getScores() {

		return scores;
	}

	/**
	 * appplyLoadings
	 * 
	 * Observation(/Sample)-wise calculation of score vectors.
	 * 
	 * @param obs
	 *            one observation / sample
	 */
	private double[] applyLoadings(double[] obs) {

		DenseMatrix64F mean = DenseMatrix64F.wrap(sampleData.getNumCols(), 1, this.mean);
		DenseMatrix64F sample = new DenseMatrix64F(sampleData.getNumCols(), 1, true, obs);
		DenseMatrix64F rotated = new DenseMatrix64F(numComps, 1);
		CommonOps.subtract(sample, mean, sample);
		CommonOps.mult(loadings, sample, rotated);
		return rotated.data;
	}

	/**
	 * getErrorMetric
	 * 
	 * This is currently the implementation for DmodX.
	 * 
	 * @param obs
	 *            observation
	 */
	@Override
	public double getErrorMetric(double[] obs) {

		if(!getComputeStatus()) {
			return 0.0;
		}
		double[] eig = applyLoadings(obs);
		double[] reproj = reproject(eig);
		double total = 0;
		for(int i = 0; i < reproj.length; i++) {
			double d = obs[i] - reproj[i];
			total += d * d;
		}
		return Math.sqrt(total);
	}

	public DenseMatrix64F getLoadings() {

		return loadings;
	}

	/**
	 * getLoadingVector
	 * 
	 * Convenience accessor to extract the loading
	 * vector of a specific component.
	 * 
	 * @param var
	 *            component to extract the loading from
	 */
	@Override
	public double[] getLoadingVector(int var) {

		if(var < 0 || var >= numComps) {
			throw new IllegalArgumentException("Invalid component");
		}
		DenseMatrix64F loadingVector = new DenseMatrix64F(1, sampleData.numCols);
		CommonOps.extract(loadings, var, var + 1, 0, sampleData.numCols, loadingVector, 0, 0);
		return loadingVector.data;
	}

	@Override
	public double getSummedVariance() {

		// calculate means of variables
		DenseMatrix64F colMeans = new DenseMatrix64F(1, sampleData.numCols);
		CommonOps.sumCols(sampleData, colMeans);
		CommonOps.divide(colMeans, sampleData.numRows);
		// subtract col means from col values and square them
		DenseMatrix64F varTemp = sampleData.copy();
		DenseMatrix64F colTemp = new DenseMatrix64F(varTemp.numRows, 1);
		for(int i = 0; i < varTemp.numCols; i++) {
			CommonOps.extractColumn(varTemp, i, colTemp);
			CommonOps.add(colTemp, colMeans.get(i) * -1);
			for(int j = 0; j < varTemp.numRows; j++) {
				varTemp.set(j, i, Math.pow(colTemp.get(j), 2));
			}
		}
		// sum along Columns and divide by 1-N
		DenseMatrix64F colSums = new DenseMatrix64F(1, sampleData.numCols);
		CommonOps.sumCols(varTemp, colSums);
		CommonOps.divide(colSums, (sampleData.numRows - 1));
		// sum all row variances
		double summedVariance = CommonOps.elementSum(colSums);
		return summedVariance;
	}

	@Override
	public double getExplainedVariance(int var) {

		DenseMatrix64F component = new DenseMatrix64F(sampleData.getNumRows(), 1);
		CommonOps.extractColumn(getScores(), var, component);
		double colMean = CommonOps.elementSum(component) / sampleData.getNumRows();
		CommonOps.add(component, colMean * -1);
		for(int i = 0; i < component.numRows; i++) {
			component.set(i, 0, Math.pow(component.get(i), 2));
		}
		CommonOps.divide(component, (sampleData.numRows - 1));
		double explainedVariance = CommonOps.elementSum(component) / 100;
		return explainedVariance;
	}

	protected double[] getMean() {

		return mean;
	}

	protected int getNumComps() {

		return numComps;
	}

	protected DenseMatrix64F getSampleData() {

		return sampleData;
	}

	@Override
	public double[] getScoreVector(ISample sampleId) {

		int obs = sampleKeys.indexOf(sampleId);
		DenseMatrix64F scoreVector = new DenseMatrix64F(1, numComps);
		CommonOps.extract(scores, obs, obs + 1, 0, numComps, scoreVector, 0, 0);
		return scoreVector.data;
	}

	protected double[] reproject(double[] scoreVector) {

		DenseMatrix64F sample = new DenseMatrix64F(sampleData.getNumCols(), 1);
		DenseMatrix64F rotated = DenseMatrix64F.wrap(numComps, 1, scoreVector);
		CommonOps.multTransA(loadings, rotated, sample);
		DenseMatrix64F mean = DenseMatrix64F.wrap(sampleData.getNumCols(), 1, this.mean);
		CommonOps.add(sample, mean, sample);
		return sample.data;
	}

	protected void setLoadings(DenseMatrix64F loadings) {

		this.loadings = loadings;
	}

	protected void setScores(DenseMatrix64F scores) {

		this.scores = scores;
	}
}
