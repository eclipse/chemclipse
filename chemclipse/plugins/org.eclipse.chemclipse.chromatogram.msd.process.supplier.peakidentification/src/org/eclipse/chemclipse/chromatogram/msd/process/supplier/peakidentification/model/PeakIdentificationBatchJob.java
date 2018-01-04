/*******************************************************************************
 * Copyright (c) 2011, 2018 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.msd.process.supplier.peakidentification.model;

import java.util.ArrayList;
import java.util.List;

public class PeakIdentificationBatchJob implements IPeakIdentificationBatchJob {

	private List<IPeakInputEntry> peakInputEntries;
	private List<IPeakOutputEntry> peakOutputEntries;
	private IPeakIntegrationEntry peakIntegrationEntry;
	private IPeakIdentificationEntry peakIdentificationEntry;
	private String reportFolder;
	private boolean overrideReport = false;
	private String name;

	public PeakIdentificationBatchJob(String name) {
		if(name != null) {
			this.name = name;
		}
		peakInputEntries = new ArrayList<IPeakInputEntry>();
		peakOutputEntries = new ArrayList<IPeakOutputEntry>();
		peakIntegrationEntry = new PeakIntegrationEntry("");
		peakIdentificationEntry = new PeakIdentificationEntry("");
	}

	@Override
	public String getName() {

		String fileName = "PeakIdentification";
		if(name != "" && name != null) {
			if(name.endsWith(".opi")) {
				fileName = name.substring(0, name.length() - 4);
			} else {
				fileName = name;
			}
		}
		return fileName;
	}

	@Override
	public List<IPeakInputEntry> getPeakInputEntries() {

		return peakInputEntries;
	}

	@Override
	public List<IPeakOutputEntry> getPeakOutputEntries() {

		return peakOutputEntries;
	}

	@Override
	public IPeakIntegrationEntry getPeakIntegrationEntry() {

		return peakIntegrationEntry;
	}

	@Override
	public void setPeakIntegrationEntry(IPeakIntegrationEntry peakIntegrationEntry) {

		this.peakIntegrationEntry = peakIntegrationEntry;
	}

	@Override
	public IPeakIdentificationEntry getPeakIdentificationEntry() {

		return peakIdentificationEntry;
	}

	@Override
	public void setPeakIdentificationEntry(IPeakIdentificationEntry peakIdentificationEntry) {

		this.peakIdentificationEntry = peakIdentificationEntry;
	}

	@Override
	public String getReportFolder() {

		return reportFolder;
	}

	@Override
	public void setReportFolder(String reportFolder) {

		this.reportFolder = reportFolder;
	}

	@Override
	public boolean isOverrideReport() {

		return overrideReport;
	}

	@Override
	public void setOverrideReport(boolean overrideReport) {

		this.overrideReport = overrideReport;
	}
}
