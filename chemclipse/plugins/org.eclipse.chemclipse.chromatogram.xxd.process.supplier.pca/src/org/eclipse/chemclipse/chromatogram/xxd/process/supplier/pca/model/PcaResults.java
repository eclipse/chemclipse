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

import java.util.ArrayList;
import java.util.List;

public class PcaResults implements IPcaResults {

	private List<double[]> basisVectors;
	private List<IDataInputEntry> dataInputEntries;
	//
	private List<Integer> extractedRetentionTimes;
	private int extractionType;
	private int numberOfPrincipleComponents;
	private List<ISample> pcaResultList;
	private int retentionTimeWindow;

	public PcaResults() {
		this(new ArrayList<IDataInputEntry>());
	}

	public PcaResults(List<IDataInputEntry> dataInputEntries) {
		this.dataInputEntries = dataInputEntries;
		pcaResultList = new ArrayList<ISample>();
	}

	@Override
	public List<double[]> getBasisVectors() {

		return basisVectors;
	}

	@Override
	public List<IDataInputEntry> getDataInputEntries() {

		return dataInputEntries;
	}

	@Override
	public List<Integer> getExtractedRetentionTimes() {

		return extractedRetentionTimes;
	}

	@Override
	public int getExtractionType() {

		return extractionType;
	}

	@Override
	public int getNumberOfPrincipleComponents() {

		return numberOfPrincipleComponents;
	}

	@Override
	public int getRetentionTimeWindow() {

		return retentionTimeWindow;
	}

	@Override
	public List<ISample> getSampleList() {

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
	public void setExtractionType(int extractionType) {

		this.extractionType = extractionType;
	}

	@Override
	public void setNumberOfPrincipleComponents(int numberOfPrincipleComponents) {

		this.numberOfPrincipleComponents = numberOfPrincipleComponents;
	}

	@Override
	public void setRetentionTimeWindow(int retentionTimeWindow) {

		this.retentionTimeWindow = retentionTimeWindow;
	}
}
