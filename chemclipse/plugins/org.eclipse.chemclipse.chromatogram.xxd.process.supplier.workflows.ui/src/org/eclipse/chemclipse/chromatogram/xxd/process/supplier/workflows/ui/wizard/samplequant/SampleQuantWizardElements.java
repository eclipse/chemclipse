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
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.workflows.model.SampleQuantReport;
import org.eclipse.chemclipse.support.ui.wizards.ChromatogramWizardElements;

public class SampleQuantWizardElements extends ChromatogramWizardElements implements ISampleQuantWizardElements {

	private String areaPercentReport = "";
	private String quantitationReport = "";
	private String additionalReportData = "";
	private ISampleQuantReport sampleQuantReport;
	private boolean isDataVerified = false;

	public SampleQuantWizardElements() {
		sampleQuantReport = new SampleQuantReport();
	}

	@Override
	public String getAreaPercentReport() {

		return areaPercentReport;
	}

	@Override
	public void setAreaPercentReport(String areaPercentReport) {

		this.areaPercentReport = areaPercentReport;
	}

	@Override
	public String getQuantitationReport() {

		return quantitationReport;
	}

	@Override
	public void setQuantitationReport(String quantitationReport) {

		this.quantitationReport = quantitationReport;
	}

	@Override
	public String getAdditionalReportData() {

		return additionalReportData;
	}

	@Override
	public void setAdditionalReportData(String additionalReportData) {

		this.additionalReportData = additionalReportData;
	}

	@Override
	public ISampleQuantReport getSampleQuantReport() {

		return sampleQuantReport;
	}

	@Override
	public boolean isDataVerified() {

		return isDataVerified;
	}

	@Override
	public void setDataVerified(boolean isDataVerified) {

		this.isDataVerified = isDataVerified;
	}
}
