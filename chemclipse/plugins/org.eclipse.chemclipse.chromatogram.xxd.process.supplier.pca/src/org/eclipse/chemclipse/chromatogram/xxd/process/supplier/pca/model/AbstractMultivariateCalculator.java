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

import java.util.ArrayList;

import org.ejml.data.DenseMatrix64F;
import org.ejml.ops.CommonOps;

public abstract class AbstractMultivariateCalculator implements IMultivariateCalculator {

	private DenseMatrix64F loadings;
	private DenseMatrix64F scores;
	private double mean[];
	private int numComps;
	private DenseMatrix64F sampleData = new DenseMatrix64F(1, 1);
	private ArrayList<ISample<?>> sampleKeys = new ArrayList<>();
	private ArrayList<String> groupNames = new ArrayList<>();
	private int sampleIndex;

	@Override
	public void initialize(int numObs, int numVars) {

		sampleData.reshape(numObs, numVars, false);
		mean = new double[numVars];
		sampleIndex = 0;
		numComps = -1;
	}

	@Override
	public void addObservation(double[] obsData) {

		for(int i = 0; i < obsData.length; i++) {
			sampleData.set(sampleIndex, i, obsData[i]);
		}
		sampleIndex++;
	}

	public void addObservationKey(ISample<?> sampleKey) {

		sampleKeys.add(sampleKey);
	}

	@Override
	public void addGroupName(String groupName) {

		groupNames.add(groupName);
	}

	@Override
	public ArrayList<String> getGroupNames() {

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
	@Override
	public double[] applyLoadings(double[] obs) {

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

	public double[] getMean() {

		return mean;
	}

	public int getNumComps() {

		return numComps;
	}

	public DenseMatrix64F getSampleData() {

		return sampleData;
	}

	@Override
	public double[] getScoreVector(ISample<?> sampleId) {

		int obs = sampleKeys.indexOf(sampleId);
		DenseMatrix64F scoreVector = new DenseMatrix64F(1, numComps);
		CommonOps.extract(scores, obs, obs + 1, 0, numComps, scoreVector, 0, 0);
		return scoreVector.data;
	}

	public double[] reproject(double[] scoreVector) {

		DenseMatrix64F sample = new DenseMatrix64F(sampleData.getNumCols(), 1);
		DenseMatrix64F rotated = DenseMatrix64F.wrap(numComps, 1, scoreVector);
		CommonOps.multTransA(loadings, rotated, sample);
		DenseMatrix64F mean = DenseMatrix64F.wrap(sampleData.getNumCols(), 1, this.mean);
		CommonOps.add(sample, mean, sample);
		return sample.data;
	}

	public void setLoadings(DenseMatrix64F loadings) {

		this.loadings = loadings;
	}

	public void setScores(DenseMatrix64F scores) {

		this.scores = scores;
	}

	public void setNumComps(int numComps) {

		this.numComps = numComps;
	}
}
