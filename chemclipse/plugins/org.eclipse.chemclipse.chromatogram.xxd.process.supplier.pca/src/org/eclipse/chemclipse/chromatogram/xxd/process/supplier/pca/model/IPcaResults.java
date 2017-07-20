/*******************************************************************************
 * Copyright (c) 2016, 2017 Lablicate GmbH.
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

public interface IPcaResults {

	int EXTRACT_PEAK = 0;
	int EXTRACT_PEAK_CUMULATION = 1;

	List<double[]> getBasisVectors();

	List<IDataInputEntry> getDataInputEntries();

	List<Integer> getExtractedRetentionTimes();

	int getExtractionType();

	List<IGroup> getGroupList();

	int getNumberOfPrincipleComponents();

	int getRetentionTimeWindow();

	List<ISample> getSampleList();

	List<Boolean> isSelectedRetentionTimes();

	void setBasisVectors(List<double[]> basisVectors);

	void setExtractedRetentionTimes(List<Integer> extractedRetentionTimes);

	void setExtractionType(int extractionType);

	void setNumberOfPrincipleComponents(int numberOfPrincipleComponents);

	void setRetentionTimeWindow(int retentionTimeWindow);

	void setSelectedRetentionTimes(List<Boolean> isSelectedReatentionTime);
}