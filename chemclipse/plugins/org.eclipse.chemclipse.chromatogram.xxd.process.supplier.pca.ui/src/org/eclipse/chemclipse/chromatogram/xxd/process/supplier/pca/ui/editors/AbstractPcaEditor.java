/*******************************************************************************
 * Copyright (c) 2017 Jan Holy.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Jan Holy - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.editors;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.core.IDataExtraction;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.core.PcaFiltrationData;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.core.PcaPreprocessingData;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.IDataInputEntry;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.IPcaResults;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.ISamples;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.internal.runnable.PcaInputRunnable;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.internal.runnable.ReEvaluateRunnable;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.internal.wizards.BatchProcessWizardDialog;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.internal.wizards.IPcaInputWizard;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.internal.wizards.PcaPeaksInputWizard;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.internal.wizards.PcaScansInputWizard;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Display;

public abstract class AbstractPcaEditor {

	private List<IDataInputEntry> dataInputEntries;
	private Optional<Integer> numberOfPrincipleComponents = Optional.of(3);
	private Optional<PcaFiltrationData> pcaFiltrationData = Optional.empty();
	private Optional<PcaPreprocessingData> pcaPreprocessingData = Optional.empty();
	private Optional<IPcaResults> pcaResults = Optional.empty();
	private Optional<ISamples> samples = Optional.empty();

	public AbstractPcaEditor() {
		dataInputEntries = new ArrayList<>();
	}

	protected abstract void addNewFilter();

	public List<IDataInputEntry> getDataInputEntries() {

		return dataInputEntries;
	}

	public Optional<Integer> getNumberOfPrincipleComponents() {

		return numberOfPrincipleComponents;
	}

	public Optional<PcaFiltrationData> getPcaFiltrationData() {

		return pcaFiltrationData;
	}

	public Optional<PcaPreprocessingData> getPcaPreprocessingData() {

		return pcaPreprocessingData;
	}

	public Optional<IPcaResults> getPcaResults() {

		return pcaResults;
	}

	public Optional<ISamples> getSamples() {

		return samples;
	}

	private int openWizardPcaInput(IPcaInputWizard wizard) throws InvocationTargetException, InterruptedException {

		BatchProcessWizardDialog wizardDialog = new BatchProcessWizardDialog(Display.getCurrent().getActiveShell(), wizard);
		int status = wizardDialog.open();
		if(status == Window.OK) {
			PcaFiltrationData pcaFiltrationData = wizard.getPcaFiltrationData();
			PcaPreprocessingData pcaPreprocessingData = wizard.getPcaPreprocessingData();
			IDataExtraction pcaExtractionData = wizard.getPcaExtractionData();
			int numberOfPrincipleComponents = wizard.getNumerOfComponents();
			/*
			 * Run the process.
			 */
			PcaInputRunnable runnable = new PcaInputRunnable(pcaExtractionData, pcaFiltrationData, pcaPreprocessingData, numberOfPrincipleComponents);
			ProgressMonitorDialog monitor = new ProgressMonitorDialog(Display.getCurrent().getActiveShell());
			/*
			 * Calculate the results and show the score plot page.
			 */
			monitor.run(true, true, runnable);
			this.pcaFiltrationData = Optional.of(pcaFiltrationData);
			this.pcaPreprocessingData = Optional.of(pcaPreprocessingData);
			this.pcaResults = Optional.of(runnable.getPcaResults());
			this.samples = Optional.of(runnable.getSamples());
			this.numberOfPrincipleComponents = Optional.of(numberOfPrincipleComponents);
			this.dataInputEntries.clear();
			this.dataInputEntries.addAll(wizard.getDataInputEntries());
		}
		return status;
	}

	protected int openWizardPcaPeaksInput() throws InvocationTargetException, InterruptedException {

		return openWizardPcaInput(new PcaPeaksInputWizard());
	}

	protected int openWizardPcaScansInput() throws InvocationTargetException, InterruptedException {

		return openWizardPcaInput(new PcaScansInputWizard());
	}

	protected void reEvaluatePcaCalculation() throws InvocationTargetException, InterruptedException {

		/*
		 * Run the process.
		 */
		int numberOfPrincComp = numberOfPrincipleComponents.get();
		ReEvaluateRunnable runnable = new ReEvaluateRunnable(samples.get(), numberOfPrincComp);
		ProgressMonitorDialog monitor = new ProgressMonitorDialog(Display.getCurrent().getActiveShell());
		monitor.run(true, false, runnable);
		this.pcaResults = Optional.of(runnable.getPcaResults());
		updateResults();
	}

	public void setNumberOfPrincipleComponents(int numberOfPrincipleComponents) {

		this.numberOfPrincipleComponents = Optional.of(numberOfPrincipleComponents);
	}

	public void setSamples(ISamples samples) {

		this.samples = Optional.of(samples);
		this.pcaFiltrationData = Optional.of(new PcaFiltrationData());
		this.pcaPreprocessingData = Optional.of(new PcaPreprocessingData());
	}

	protected abstract void updateFilters();

	protected abstract void updatePreprocessoring();

	protected abstract void updateResults();

	protected abstract void updateSamples();
}
