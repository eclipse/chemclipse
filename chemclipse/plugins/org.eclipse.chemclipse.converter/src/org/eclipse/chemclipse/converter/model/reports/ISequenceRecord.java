/*******************************************************************************
 * Copyright (c) 2017, 2024 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.converter.model.reports;

import java.util.Map;

public interface ISequenceRecord {

	String getSubstance();

	void setSubstance(String substance);

	String getProcessMethod();

	void setProcessMethod(String processMethod);

	String getReportMethod();

	void setReportMethod(String reportMethod);

	int getVial();

	void setVial(int vial);

	double getInjectionVolume();

	void setInjectionVolume(double injectionVolume);

	String getSampleName();

	void setSampleName(String sampleName);

	double getMultiplier();

	void setMultiplier(double multiplier);

	String getDataPath();

	void setDataPath(String dataPath);

	String getDataFile();

	void setDataFile(String dataFile);

	String getDescription();

	void setDescription(String description);

	SequenceRecordAdvice getAdvice();

	Map<String, String> getMetadata();
}