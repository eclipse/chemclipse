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
package org.eclipse.chemclipse.chromatogram.xxd.process.supplier.workflows.ui.wizard.evaluation;

import java.io.File;
import java.util.Date;
import java.util.List;

import org.eclipse.chemclipse.chromatogram.msd.process.model.IChromatogramProcessEntry;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.workflows.core.EvaluationProcessor;
import org.eclipse.chemclipse.support.ui.wizards.AbstractFileWizard;
import org.eclipse.chemclipse.ux.extension.msd.ui.wizards.ChromatogramInputEntriesWizardPage;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;

public class WizardSingleChromatogramEvaluation extends AbstractFileWizard {

	private IEvaluationWizardElements wizardElements = new EvaluationWizardElements();
	//
	private PageDescription pageDescription;
	private ChromatogramInputEntriesWizardPage pageChromatogramInputEntries;
	private PageNotes pageNotes;
	private PageProcessing pageProcessing;

	public WizardSingleChromatogramEvaluation() {
		super("SingleChromatogramEvaluation_" + new Date().getTime(), EvaluationProcessor.REPORT_FILE_EXTENSION);
	}

	@Override
	public void addPages() {

		super.addPages();
		/*
		 * Pages must implement IExtendedWizardPage / extend AbstractExtendedWizardPage
		 */
		pageDescription = new PageDescription(wizardElements);
		pageChromatogramInputEntries = new ChromatogramInputEntriesWizardPage(wizardElements);
		pageProcessing = new PageProcessing(wizardElements);
		pageNotes = new PageNotes(wizardElements);
		//
		addPage(pageDescription);
		addPage(pageChromatogramInputEntries);
		addPage(pageProcessing);
		addPage(pageNotes);
	}

	@Override
	public boolean canFinish() {

		boolean canFinish = pageDescription.canFinish();
		if(canFinish) {
			canFinish = (wizardElements.getSelectedChromatograms().size() > 0) ? true : false;
		}
		if(canFinish) {
			canFinish = pageProcessing.canFinish();
		}
		if(canFinish) {
			canFinish = pageNotes.canFinish();
		}
		return canFinish;
	}

	@Override
	public void doFinish(IProgressMonitor monitor) throws CoreException {

		monitor.beginTask("Create Chromatogram Evaluation", IProgressMonitor.UNKNOWN);
		final IFile file = super.prepareProject(monitor);
		//
		EvaluationProcessor evaluationProcessor = new EvaluationProcessor();
		File chromatogramEvaluationReportFile = file.getLocation().toFile();
		List<IChromatogramProcessEntry> chromatogramProcessingEntries = wizardElements.getProcessingEntries();
		String description = wizardElements.getDescription();
		String notes = wizardElements.getNotes();
		String pathChromatogramOriginal = wizardElements.getSelectedChromatograms().get(0);
		evaluationProcessor.processAndWriteReport(chromatogramEvaluationReportFile, chromatogramProcessingEntries, description, notes, pathChromatogramOriginal, monitor);
		/*
		 * Refresh
		 */
		super.refreshWorkspace(monitor);
		super.runOpenEditor(file, monitor);
	}
}
