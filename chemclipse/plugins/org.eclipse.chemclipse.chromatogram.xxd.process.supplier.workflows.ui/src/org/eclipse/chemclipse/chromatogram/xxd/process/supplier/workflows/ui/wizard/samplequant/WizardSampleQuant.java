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

import java.io.File;
import java.util.Date;

import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.workflows.core.SampleQuantProcessor;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.workflows.model.ISampleQuantReport;
import org.eclipse.chemclipse.support.ui.wizards.AbstractFileWizard;
import org.eclipse.chemclipse.ux.extension.msd.ui.wizards.ChromatogramInputEntriesWizardPage;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;

public class WizardSampleQuant extends AbstractFileWizard {

	public static final int PREFERRED_WIDTH = 350;
	public static final int PREFERRED_HEIGHT = 500;
	//
	private ISampleQuantWizardElements wizardElements = new SampleQuantWizardElements();
	//
	private ChromatogramInputEntriesWizardPage pageChromatogramInputEntries;
	private PageReportDataSelection pageReportDataSelection;
	private PageDataVerification pageDataVerification;

	public WizardSampleQuant() {
		super("SampleQuantReport_" + new Date().getTime(), SampleQuantProcessor.REPORT_FILE_EXTENSION);
	}

	@Override
	public void addPages() {

		super.addPages();
		/*
		 * Pages must implement IExtendedWizardPage / extend AbstractExtendedWizardPage
		 */
		pageChromatogramInputEntries = new ChromatogramInputEntriesWizardPage(wizardElements);
		pageReportDataSelection = new PageReportDataSelection(wizardElements);
		pageDataVerification = new PageDataVerification(wizardElements);
		//
		addPage(pageChromatogramInputEntries);
		addPage(pageReportDataSelection);
		addPage(pageDataVerification);
	}

	@Override
	public boolean canFinish() {

		boolean canFinish = (wizardElements.getSelectedChromatograms().size() > 0) ? true : false;
		if(canFinish) {
			canFinish = wizardElements.isDataVerified();
		}
		return canFinish;
	}

	@Override
	public void doFinish(IProgressMonitor monitor) throws CoreException {

		monitor.beginTask("Create Sample Quantitation", IProgressMonitor.UNKNOWN);
		final IFile file = super.prepareProject(monitor);
		//
		ISampleQuantReport sampleQuantReport = wizardElements.getSampleQuantReport();
		String chromatogram = wizardElements.getSelectedChromatograms().get(0);
		File sampleQuantReportFile = file.getLocation().toFile();
		SampleQuantProcessor sampleQuantProcessor = new SampleQuantProcessor();
		sampleQuantProcessor.createSampleQuantReport(sampleQuantReportFile, sampleQuantReport, chromatogram, monitor);
		/*
		 * Refresh
		 */
		super.refreshWorkspace(monitor);
		super.runOpenEditor(file, monitor);
	}
}
