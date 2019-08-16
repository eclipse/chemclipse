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
package org.eclipse.chemclipse.chromatogram.xxd.process.supplier.workflows.ui.wizard.samplequant;

import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.workflows.model.ISampleQuantReport;
import org.eclipse.chemclipse.support.ui.wizards.IChromatogramWizardElements;

public interface ISampleQuantWizardElements extends IChromatogramWizardElements {

	String getAreaPercentReport();

	void setAreaPercentReport(String areaPercentReport);

	String getQuantitationReport();

	void setQuantitationReport(String quantitationReport);

	String getAdditionalReportData();

	void setAdditionalReportData(String additionalReportData);

	ISampleQuantReport getSampleQuantReport();

	boolean isDataVerified();

	void setDataVerified(boolean isDataVerified);
}
