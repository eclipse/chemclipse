/*******************************************************************************
 * Copyright (c) 2016 Lablicate GmbH.
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

	List<IDataInputEntry> getDataInputEntries();

	int getRetentionTimeWindow();

	void setRetentionTimeWindow(int retentionTimeWindow);

	int getNumberOfPrincipleComponents();

	void setNumberOfPrincipleComponents(int numberOfPrincipleComponents);

	int getExtractionType();

	void setExtractionType(int extractionType);

	Map<ISample, IPcaResult> getPcaResultMap();

	List<Integer> getExtractedRetentionTimes();

	void setExtractedRetentionTimes(List<Integer> extractedRetentionTimes);

	List<double[]> getBasisVectors();

	void setBasisVectors(List<double[]> basisVectors);
}