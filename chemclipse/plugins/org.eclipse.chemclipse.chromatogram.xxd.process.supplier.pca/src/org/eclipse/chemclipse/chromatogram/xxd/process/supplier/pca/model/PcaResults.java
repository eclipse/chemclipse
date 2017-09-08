/*******************************************************************************
 * Copyright (c) 2013, 2017 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model;

import java.util.List;

public class PcaResults implements IPcaResults {

	private List<double[]> basisVectors;
	private List<Integer> extractedRetentionTimes;
	//
	private int numberOfPrincipleComponents;
	private List<IPcaResult> pcaResultGroupsList;
	private List<IPcaResult> pcaResultList;

	public PcaResults() {
	}

	@Override
	public List<double[]> getBasisVectors() {

		return basisVectors;
	}

	@Override
	public List<Integer> getExtractedRetentionTimes() {

		return extractedRetentionTimes;
	}

	@Override
	public int getNumberOfPrincipleComponents() {

		return numberOfPrincipleComponents;
	}

	@Override
	public List<IPcaResult> getPcaResultGroupsList() {

		return pcaResultGroupsList;
	}

	@Override
	public List<IPcaResult> getPcaResultList() {

		return pcaResultList;
	}

	@Override
	public void setBasisVectors(List<double[]> basisVectors) {

		this.basisVectors = basisVectors;
	}

	@Override
	public void setExtractedRetentionTimes(List<Integer> extractedRetentionTimes) {

		this.extractedRetentionTimes = extractedRetentionTimes;
	}

	@Override
	public void setNumberOfPrincipleComponents(int numberOfPrincipleComponents) {

		this.numberOfPrincipleComponents = numberOfPrincipleComponents;
	}

	@Override
	public void setPcaResultGroupsList(List<IPcaResult> pcaResultGroupsList) {

		this.pcaResultGroupsList = pcaResultGroupsList;
	}

	@Override
	public void setPcaResultList(List<IPcaResult> pcaResultList) {

		this.pcaResultList = pcaResultList;
	}
}
