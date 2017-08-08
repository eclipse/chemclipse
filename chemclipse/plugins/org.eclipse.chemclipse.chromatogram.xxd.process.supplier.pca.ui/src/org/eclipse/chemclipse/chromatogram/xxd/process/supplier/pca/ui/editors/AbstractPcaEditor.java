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
import java.util.Optional;

import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.core.PcaExtractionData;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.core.PcaFiltrationData;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.core.PcaNormalizationData;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.core.PcaUtils;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.IPcaResults;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.internal.runnable.PcaPeaksInputRunnable;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.internal.runnable.ReEvaluateRunnable;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.internal.wizards.BatchProcessWizardDialog;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.internal.wizards.PcaPeaksInputWizard;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Display;

public abstract class AbstractPcaEditor {

	private Optional<Integer> numberOfPrincipleComponents = Optional.of(3);
	private Optional<PcaFiltrationData> pcaFiltrationData = Optional.empty();
	private Optional<PcaNormalizationData> pcaNormalizationData = Optional.empty();
	private Optional<IPcaResults> pcaResults = Optional.empty();

	public AbstractPcaEditor() {
	}

	public Optional<Integer> getNumberOfPrincipleComponents() {

		return numberOfPrincipleComponents;
	}

	public Optional<PcaFiltrationData> getPcaFiltrationData() {

		return pcaFiltrationData;
	}

	public Optional<PcaNormalizationData> getPcaNormalizationData() {

		return pcaNormalizationData;
	}

	public Optional<IPcaResults> getPcaResults() {

		return pcaResults;
	}

	protected void openWizardPcaPeakInputs() throws InvocationTargetException, InterruptedException {

		PcaPeaksInputWizard wizard = new PcaPeaksInputWizard();
		BatchProcessWizardDialog wizardDialog = new BatchProcessWizardDialog(Display.getCurrent().getActiveShell(), wizard);
		if(wizardDialog.open() == Window.OK) {
			PcaFiltrationData pcaFiltrationData = wizard.getPcaFiltrationData();
			PcaNormalizationData pcaNormalizationData = wizard.getPcaNormalizationData();
			PcaExtractionData pcaExtractionData = wizard.getPcaExtractionData();
			int numberOfPrincipleComponents = wizard.getNumerOfComponents();
			/*
			 * Run the process.
			 */
			PcaPeaksInputRunnable runnable = new PcaPeaksInputRunnable(pcaExtractionData, pcaFiltrationData, pcaNormalizationData, numberOfPrincipleComponents);
			ProgressMonitorDialog monitor = new ProgressMonitorDialog(Display.getCurrent().getActiveShell());
			/*
			 * Calculate the results and show the score plot page.
			 */
			monitor.run(true, true, runnable);
			this.pcaFiltrationData = Optional.of(pcaFiltrationData);
			this.pcaNormalizationData = Optional.of(pcaNormalizationData);
			this.pcaResults = Optional.of(runnable.getPcaResults());
			this.numberOfPrincipleComponents = Optional.of(numberOfPrincipleComponents);
		}
	}

	protected void reEvaluatePcaCalculation() throws InvocationTargetException, InterruptedException {

		/*
		 * Run the process.
		 */
		IPcaResults results = pcaResults.get();
		int numberOfPrincComp = numberOfPrincipleComponents.get();
		ReEvaluateRunnable runnable = new ReEvaluateRunnable(results, numberOfPrincComp);
		ProgressMonitorDialog monitor = new ProgressMonitorDialog(Display.getCurrent().getActiveShell());
		monitor.run(true, true, runnable);
	}

	protected void reFiltrationData() {

		pcaFiltrationData.get().process(pcaResults.get(), true, new NullProgressMonitor());
	}

	protected void reNormalizationData() {

		pcaNormalizationData.get().process(pcaResults.get(), new NullProgressMonitor());
	}

	protected void selectAllData() {

		pcaFiltrationData.get().selectAll(pcaResults.get());
	}

	public void setNumberOfPrincipleComponents(int numberOfPrincipleComponents) {

		this.numberOfPrincipleComponents = Optional.of(numberOfPrincipleComponents);
	}

	protected void updataGroupNames() {

		PcaUtils.setGroups(getPcaResults().get(), true);
	}
}
