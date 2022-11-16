/*******************************************************************************
 * Copyright (c) 2018, 2022 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Lorenz Gerber - initial API and implementation
 * Christoph Läubrich - fix pre-checks
 * Philip Wenig - refactoring
 *******************************************************************************/
package org.eclipse.chemclipse.xxd.process.supplier.pca.model;

import java.util.ArrayList;

import org.eclipse.chemclipse.model.statistics.ISample;
import org.eclipse.chemclipse.xxd.process.supplier.pca.exception.MathIllegalArgumentException;
import org.ejml.data.DMatrixRMaj;
import org.ejml.dense.row.CommonOps_DDRM;

public abstract class AbstractMultivariateCalculator implements IMultivariateCalculator {

	private DMatrixRMaj loadings;
	private DMatrixRMaj scores;
	private double mean[];
	private int numComps;
	private DMatrixRMaj sampleData;
	private ArrayList<ISample> sampleKeys = new ArrayList<>();
	private ArrayList<String> groupNames = new ArrayList<>();
	private int sampleIndex;
	private boolean computeSuccess;

	public AbstractMultivariateCalculator(int numSamples, int numVars, int numComponents) throws MathIllegalArgumentException {

		if(numComponents > numVars) {
			throw new MathIllegalArgumentException("Number of components must be smaller than number of variables.");
		}
		if(numVars <= 0) {
			throw new MathIllegalArgumentException("Number of variables must be larger than zero");
		}
		if(numSamples <= 0) {
			throw new MathIllegalArgumentException("Number of samples must be larger than zero.");
		}
		if(numComponents <= 0) {
			throw new MathIllegalArgumentException("Number of components must be larger than zero.");
		}
		sampleData = new DMatrixRMaj(numSamples, numVars);
		this.mean = new double[numVars];
		sampleIndex = 0;
		this.numComps = numComponents;
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

	public DMatrixRMaj getScores() {

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

		DMatrixRMaj mean = DMatrixRMaj.wrap(sampleData.getNumCols(), 1, this.mean);
		DMatrixRMaj sample = new DMatrixRMaj(sampleData.getNumCols(), 1, true, obs);
		DMatrixRMaj rotated = new DMatrixRMaj(numComps, 1);
		CommonOps_DDRM.subtract(sample, mean, sample);
		CommonOps_DDRM.mult(loadings, sample, rotated);
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

	public DMatrixRMaj getLoadings() {

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
		DMatrixRMaj loadingVector = new DMatrixRMaj(1, sampleData.numCols);
		CommonOps_DDRM.extract(loadings, var, var + 1, 0, sampleData.numCols, loadingVector, 0, 0);
		return loadingVector.data;
	}

	@Override
	public double getSummedVariance() {

		// calculate means of variables
		DMatrixRMaj colMeans = new DMatrixRMaj(1, sampleData.numCols);
		CommonOps_DDRM.sumCols(sampleData, colMeans);
		CommonOps_DDRM.divide(colMeans, sampleData.numRows);
		// subtract col means from col values and square them
		DMatrixRMaj varTemp = sampleData.copy();
		DMatrixRMaj colTemp = new DMatrixRMaj(varTemp.numRows, 1);
		for(int i = 0; i < varTemp.numCols; i++) {
			CommonOps_DDRM.extractColumn(varTemp, i, colTemp);
			CommonOps_DDRM.add(colTemp, colMeans.get(i) * -1);
			for(int j = 0; j < varTemp.numRows; j++) {
				varTemp.set(j, i, Math.pow(colTemp.get(j), 2));
			}
		}
		// sum along Columns and divide by 1-N
		DMatrixRMaj colSums = new DMatrixRMaj(1, sampleData.numCols);
		CommonOps_DDRM.sumCols(varTemp, colSums);
		CommonOps_DDRM.divide(colSums, (sampleData.numRows - 1));
		// sum all row variances
		double summedVariance = CommonOps_DDRM.elementSum(colSums);
		return summedVariance;
	}

	@Override
	public double getExplainedVariance(int var) {

		DMatrixRMaj component = new DMatrixRMaj(sampleData.getNumRows(), 1);
		CommonOps_DDRM.extractColumn(getScores(), var, component);
		double colMean = CommonOps_DDRM.elementSum(component) / sampleData.getNumRows();
		CommonOps_DDRM.add(component, colMean * -1);
		for(int i = 0; i < component.numRows; i++) {
			component.set(i, 0, Math.pow(component.get(i), 2));
		}
		CommonOps_DDRM.divide(component, (sampleData.numRows - 1));
		double explainedVariance = CommonOps_DDRM.elementSum(component) / 100;
		return explainedVariance;
	}

	protected double[] getMean() {

		return mean;
	}

	protected int getNumComps() {

		return numComps;
	}

	protected DMatrixRMaj getSampleData() {

		return sampleData;
	}

	@Override
	public double[] getScoreVector(ISample sampleId) {

		int obs = sampleKeys.indexOf(sampleId);
		DMatrixRMaj scoreVector = new DMatrixRMaj(1, numComps);
		CommonOps_DDRM.extract(scores, obs, obs + 1, 0, numComps, scoreVector, 0, 0);
		return scoreVector.data;
	}

	protected double[] reproject(double[] scoreVector) {

		DMatrixRMaj sample = new DMatrixRMaj(sampleData.getNumCols(), 1);
		DMatrixRMaj rotated = DMatrixRMaj.wrap(numComps, 1, scoreVector);
		CommonOps_DDRM.multTransA(loadings, rotated, sample);
		DMatrixRMaj mean = DMatrixRMaj.wrap(sampleData.getNumCols(), 1, this.mean);
		CommonOps_DDRM.add(sample, mean, sample);
		return sample.data;
	}

	protected void setLoadings(DMatrixRMaj loadings) {

		this.loadings = loadings;
	}

	protected void setScores(DMatrixRMaj scores) {

		this.scores = scores;
	}
}
