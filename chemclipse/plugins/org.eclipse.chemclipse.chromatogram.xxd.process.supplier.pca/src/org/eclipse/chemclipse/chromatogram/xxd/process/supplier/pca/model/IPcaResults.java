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
import java.util.Map;

public interface IPcaResults {

	List<double[]> getBasisVectors();

	List<IDataInputEntry> getDataInputEntries();

	List<Integer> getExtractedRetentionTimes();

	int getExtractionType();

	int getNumberOfPrincipleComponents();

	int getRetentionTimeWindow();

	List<ISample> getSampleList();

	Map<ISample, IPcaResult> getPcaResultMap();

	void setBasisVectors(List<double[]> basisVectors);

	void setExtractedRetentionTimes(List<Integer> extractedRetentionTimes);

	void setExtractionType(int extractionType);

	void setNumberOfPrincipleComponents(int numberOfPrincipleComponents);

	void setRetentionTimeWindow(int retentionTimeWindow);
}