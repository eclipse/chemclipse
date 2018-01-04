/*******************************************************************************
 * Copyright (c) 2016, 2018 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.process.supplier.workflows.model;

import java.util.List;

public interface ISampleQuantReport {

	String getName();

	void setName(String name);

	String getDataName();

	void setDataName(String dataName);

	String getDate();

	void setDate(String date);

	String getOperator();

	void setOperator(String operator);

	String getMiscInfo();

	void setMiscInfo(String miscInfo);

	String getPathChromatogramOriginal();

	void setPathChromatogramOriginal(String pathChromatogramOriginal);

	String getPathChromatogramEdited();

	void setPathChromatogramEdited(String pathChromatogramEdited);

	void setMinMatchQuality(double minMatchQuality);

	List<ISampleQuantSubstance> getSampleQuantSubstances();

	List<ISampleQuantSubstance> getSampleQuantSubstances(String searchTerms);
}
