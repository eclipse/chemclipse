/*******************************************************************************
 * Copyright (c) 2013, 2016 Dr. Philip Wenig.
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PcaResults implements IPcaResults {

	private List<IDataInputEntry> dataInputEntries;
	private int retentionTimeWindow;
	private int numberOfPrincipleComponents;
	private int extractionType;
	private Map<ISample, IPcaResult> pcaResultMap;
	//
	private List<Integer> extractedRetentionTimes;
	private List<double[]> basisVectors;

	public PcaResults() {
		this(new ArrayList<IDataInputEntry>());
	}

	public PcaResults(List<IDataInputEntry> dataInputEntries) {
		this.dataInputEntries = dataInputEntries;
		pcaResultMap = new HashMap<ISample, IPcaResult>();
	}

	@Override
	public List<IDataInputEntry> getDataInputEntries() {

		return dataInputEntries;
	}

	@Override
	public int getRetentionTimeWindow() {

		return retentionTimeWindow;
	}

	@Override
	public void setRetentionTimeWindow(int retentionTimeWindow) {

		this.retentionTimeWindow = retentionTimeWindow;
	}

	@Override
	public int getNumberOfPrincipleComponents() {

		return numberOfPrincipleComponents;
	}

	@Override
	public int getExtractionType() {

		return extractionType;
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
	public Map<ISample, IPcaResult> getPcaResultMap() {

		return pcaResultMap;
	}

	@Override
	public List<Integer> getExtractedRetentionTimes() {

		return extractedRetentionTimes;
	}

	@Override
	public void setExtractedRetentionTimes(List<Integer> extractedRetentionTimes) {

		this.extractedRetentionTimes = extractedRetentionTimes;
	}

	@Override
	public List<double[]> getBasisVectors() {

		return basisVectors;
	}

	@Override
	public void setBasisVectors(List<double[]> basisVectors) {

		this.basisVectors = basisVectors;
	}
}
