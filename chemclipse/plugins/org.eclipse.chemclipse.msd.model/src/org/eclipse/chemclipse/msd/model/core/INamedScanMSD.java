/*******************************************************************************
 * Copyright (c) 2015, 2018 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Janos Binder - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.model.core;

public interface INamedScanMSD extends IScanMSD {

	String getSubstanceName();

	void setSubstanceName(String substanceName);

	String getOriginName();

	void setOriginName(String originName);

	Long getOriginalReferenceMassSpectrumId();

	void setOriginalReferenceMassSpectrumId(Long originalReferenceMassSpectrumId);

	Double getPeakArea();

	void setPeakArea(Double peakArea);

	Float getPeakTailing();

	void setPeakTailing(Float peakTailing);

	Float getPeakSN();

	void setPeakSN(Float peakSN);
}
