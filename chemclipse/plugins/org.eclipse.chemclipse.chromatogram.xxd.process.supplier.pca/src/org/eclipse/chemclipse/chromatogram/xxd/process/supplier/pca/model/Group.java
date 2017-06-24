/*******************************************************************************
 * Copyright (c) 2017 Jan Holy.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Jan Holy - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model;

import java.util.ArrayList;
import java.util.List;

/**
 * this class contains means of samples which contain same group name
 *
 * @author Jan Holy
 *
 */
public class Group implements ISample {

	private String groupName;
	private boolean isSelected;
	private String name;
	private IPcaResult pcaResult;

	public Group(String name) {
		isSelected = true;
		this.name = name;
		this.pcaResult = new PcaResult();
	}

	@Override
	public String getGroupName() {

		return groupName;
	}

	@Override
	public String getName() {

		return name;
	}

	@Override
	public IPcaResult getPcaResult() {

		return pcaResult;
	}

	@Override
	public boolean isSelected() {

		return isSelected;
	}

	@Override
	public void setGroupName(String groupName) {

		this.groupName = groupName;
	}

	/**
	 * @link {@link #setPcaResult(List)}
	 * @param pcaResults
	 */
	public void setPcaResult(IPcaResults pcaResults) {

		List<ISample> sammples = pcaResults.getSampleList();
		setPcaResult(sammples);
	}

	/**
	 * Set values in PcaResult. Values are set as mean of samples which contain same group name as this object
	 * You have to set group name before calling this method
	 *
	 * @param sammples
	 */
	public void setPcaResult(List<ISample> samples) {

		if(groupName == null) {
			throw new NullPointerException("Group name is null");
		}
		/*
		 * select sample which contains same group name
		 */
		List<ISample> samplesSameGroup = new ArrayList<>();
		for(ISample sample : samples) {
			if(groupName.equals(sample.getGroupName())) {
				samplesSameGroup.add(sample);
			}
		}
		if(samplesSameGroup.isEmpty()) {
			return;
		}
		IPcaResult firstResult = samplesSameGroup.get(0).getPcaResult();
		/*
		 * Calculate mean for eigen space
		 */
		double[] esf = firstResult.getEigenSpace();
		if(esf != null) {
			double[] eigenSpace = new double[esf.length];
			for(ISample sample : samplesSameGroup) {
				double[] es = sample.getPcaResult().getEigenSpace();
				for(int i = 0; i < es.length; i++) {
					eigenSpace[i] += es[i];
				}
				for(int i = 0; i < eigenSpace.length; i++) {
					eigenSpace[i] /= samplesSameGroup.size();
				}
			}
			pcaResult.setEigenSpace(firstResult.getEigenSpace());
		}
		/*
		 * Calculate mean for sample data
		 */
		double[] sdf = firstResult.getSampleData();
		if(sdf != null) {
			double[] sampleData = new double[sdf.length];
			for(ISample sample : samplesSameGroup) {
				for(int i = 0; i < sampleData.length; i++) {
					double[] sd = sample.getPcaResult().getSampleData();
					if(sd != null) {
						sampleData[i] += sd[i];
					}
				}
			}
			for(int i = 0; i < sampleData.length; i++) {
				sampleData[i] /= samplesSameGroup.size();
			}
			pcaResult.setSampleData(sampleData);
		}
		/*
		 * calculate mean for error
		 */
		double error = 0;
		for(ISample sample : samplesSameGroup) {
			error += sample.getPcaResult().getErrorMemberShip();
		}
		error /= samplesSameGroup.size();
		pcaResult.setErrorMemberShip(error);
		pcaResult.setSlopes(new Slopes());
	}

	@Override
	public void setSelected(boolean selected) {

		this.isSelected = selected;
	}
}
