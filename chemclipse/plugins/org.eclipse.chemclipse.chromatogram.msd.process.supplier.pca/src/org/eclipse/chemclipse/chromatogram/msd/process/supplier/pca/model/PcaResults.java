/*******************************************************************************
 * Copyright (c) 2013, 2015 Dr. Philip Wenig.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.msd.process.supplier.pca.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PcaResults {

	private int retentionTimeWindow;
	private int numberOfPrincipleComponents;
	private List<Integer> extractedRetentionTimes;
	private List<double[]> basisVectors;
	private Map<String, PcaResult> pcaResultMap;

	public PcaResults() {

		pcaResultMap = new HashMap<String, PcaResult>();
	}

	public int getRetentionTimeWindow() {

		return retentionTimeWindow;
	}

	public void setRetentionTimeWindow(int retentionTimeWindow) {

		this.retentionTimeWindow = retentionTimeWindow;
	}

	public int getNumberOfPrincipleComponents() {

		return numberOfPrincipleComponents;
	}

	public void setNumberOfPrincipleComponents(int numberOfPrincipleComponents) {

		this.numberOfPrincipleComponents = numberOfPrincipleComponents;
	}

	public List<Integer> getExtractedRetentionTimes() {

		return extractedRetentionTimes;
	}

	public void setExtractedRetentionTimes(List<Integer> extractedRetentionTimes) {

		this.extractedRetentionTimes = extractedRetentionTimes;
	}

	public List<double[]> getBasisVectors() {

		return basisVectors;
	}

	public void setBasisVectors(List<double[]> basisVectors) {

		this.basisVectors = basisVectors;
	}

	public Map<String, PcaResult> getPcaResultMap() {

		return pcaResultMap;
	}

	public void setPcaResultMap(Map<String, PcaResult> pcaResultMap) {

		this.pcaResultMap = pcaResultMap;
	}
}
