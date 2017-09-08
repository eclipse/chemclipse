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
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.core.IDataModification;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.core.PcaFiltrationData;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.core.PcaScalingData;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.IDataInputEntry;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.IPcaResults;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.ISamples;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.internal.runnable.PcaInputRunnable;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.internal.runnable.ReEvaluateRunnable;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.internal.wizards.BatchProcessWizardDialog;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.internal.wizards.IPcaInputWizard;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.internal.wizards.PcaPeaksInputWizard;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.internal.wizards.PcaScansInputWizard;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Display;

public abstract class AbstractPcaEditor {

	private List<IDataInputEntry> dataInputEntries;
	private Optional<Integer> numberOfPrincipleComponents = Optional.of(3);
	private Optional<PcaFiltrationData> pcaFiltrationData = Optional.empty();
	private Optional<IPcaResults> pcaResults = Optional.empty();
	private Optional<PcaScalingData> pcaScalingData = Optional.empty();
	private Optional<ISamples> samples = Optional.empty();

	public AbstractPcaEditor() {
		dataInputEntries = new ArrayList<>();
	}

	public List<IDataInputEntry> getDataInputEntries() {

		return dataInputEntries;
	}

	public Optional<Integer> getNumberOfPrincipleComponents() {

		return numberOfPrincipleComponents;
	}

	public Optional<PcaFiltrationData> getPcaFiltrationData() {

		return pcaFiltrationData;
	}

	public Optional<IPcaResults> getPcaResults() {

		return pcaResults;
	}

	public Optional<PcaScalingData> getPcaScalingData() {

		return pcaScalingData;
	}

	public Optional<ISamples> getSamples() {

		return samples;
	}
	
	protected void evaluateSamples(ISamples samples) throws InvocationTargetException, InterruptedException{
		
		
		int numberOfPrincComp = numberOfPrincipleComponents.get();
		ReEvaluateRunnable runnable = new ReEvaluateRunnable(samples, numberOfPrincComp);
		ProgressMonitorDialog monitor = new ProgressMonitorDialog(Display.getCurrent().getActiveShell());
		monitor.run(true, false, runnable);
		this.evaluateSamples(samples);
		this.pcaResults = Optional.of(runnable.getPcaResults());
		
	}

	private int openWizardPcaInput(IPcaInputWizard wizard) throws InvocationTargetException, InterruptedException {

		BatchProcessWizardDialog wizardDialog = new BatchProcessWizardDialog(Display.getCurrent().getActiveShell(), wizard);
		int status = wizardDialog.open();
		if(status == Window.OK) {
			PcaFiltrationData pcaFiltrationData = wizard.getPcaFiltrationData();
			PcaScalingData pcaScalingData = wizard.getPcaScalingData();
			IDataExtraction pcaExtractionData = wizard.getPcaExtractionData();
			int numberOfPrincipleComponents = wizard.getNumerOfComponents();
			/*
			 * Run the process.
			 */
			PcaInputRunnable runnable = new PcaInputRunnable(pcaExtractionData, pcaFiltrationData, pcaScalingData, numberOfPrincipleComponents);
			ProgressMonitorDialog monitor = new ProgressMonitorDialog(Display.getCurrent().getActiveShell());
			/*
			 * Calculate the results and show the score plot page.
			 */
			monitor.run(true, true, runnable);
			this.pcaFiltrationData = Optional.of(pcaFiltrationData);
			this.pcaScalingData = Optional.of(pcaScalingData);
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
	}

	protected void reFiltrationData() {

		pcaFiltrationData.get().process(samples.get(), true, new NullProgressMonitor());
	}

	protected void modifyData() {

		IDataModification.resetData(samples.get());
		pcaScalingData.get().process(samples.get(), new NullProgressMonitor());
	}

	public void setNumberOfPrincipleComponents(int numberOfPrincipleComponents) {

		this.numberOfPrincipleComponents = Optional.of(numberOfPrincipleComponents);
	}

	protected void setSelectAllData(boolean selection) {

		pcaFiltrationData.get().setSelectAllRow(samples.get(), selection);
	}

	protected void updataGroupNames() {

		samples.get().createGroups();
	}
}
