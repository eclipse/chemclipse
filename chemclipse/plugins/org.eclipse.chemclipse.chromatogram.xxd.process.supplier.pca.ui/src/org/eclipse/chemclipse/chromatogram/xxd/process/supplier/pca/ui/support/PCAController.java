/*******************************************************************************
 * Copyright (c) 2018 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Jan Holy - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.support;

import java.util.Optional;

import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.managers.SelectionManagerSamples;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.IPcaSettings;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.ISample;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.ISampleData;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.IVariable;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.PcaSettings;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.visualization.IPcaResultsVisualization;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.visualization.IPcaSettingsVisualization;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.visualization.ISamplesVisualization;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.visualization.PcaSettingsVisualization;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.visualization.SamplesVisualization;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Spinner;

import javafx.collections.ObservableList;

public class PCAController {

	private Spinner numerPrincipalComponents;
	private Optional<IPcaResultsVisualization> pcaResults;
	private Spinner pcx;
	private Spinner pcy;
	private Spinner pcz;
	private Button runAnalysis;
	private Optional<SamplesVisualization> samples;

	public PCAController(Composite parent, Object layoutData) {
		samples = Optional.empty();
		pcaResults = Optional.empty();
		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayoutData(layoutData);
		composite.setLayout(new GridLayout(10, false));
		runAnalysis = new Button(composite, SWT.PUSH);
		runAnalysis.setText("RUN");
		runAnalysis.addListener(SWT.Selection, e -> {
			evaluatePCA();
		});
		numerPrincipalComponents = new Spinner(composite, SWT.None);
		numerPrincipalComponents.setMinimum(1);
		numerPrincipalComponents.setIncrement(1);
		numerPrincipalComponents.setSelection(3);
		numerPrincipalComponents.setMaximum(100);
		Label label = new Label(composite, SWT.None);
		label.setText("PCX:");
		pcx = new Spinner(composite, SWT.None);
		pcx.setMinimum(1);
		pcx.setIncrement(1);
		pcx.setSelection(1);
		pcx.setMaximum(3);
		pcx.addModifyListener(e -> {
			if(pcaResults.isPresent()) {
				pcaResults.get().getPcaSettingsVisualization().setPcX(pcx.getSelection());
			}
		});
		label = new Label(composite, SWT.None);
		label.setText("PCY:");
		pcy = new Spinner(composite, SWT.None);
		pcy.setMinimum(1);
		pcy.setMaximum(3);
		pcy.setIncrement(1);
		pcy.setSelection(2);
		pcy.addModifyListener(e -> {
			if(pcaResults.isPresent()) {
				pcaResults.get().getPcaSettingsVisualization().setPcY(pcy.getSelection());
			}
		});
		label = new Label(composite, SWT.None);
		label.setText("PCZ:");
		pcz = new Spinner(composite, SWT.None);
		pcz.setMinimum(1);
		pcz.setIncrement(1);
		pcz.setSelection(3);
		pcz.setMaximum(3);
		pcz.addModifyListener(e -> {
			if(pcaResults.isPresent()) {
				pcaResults.get().getPcaSettingsVisualization().setPcZ(pcz.getSelection());
			}
		});
	}

	private void evaluatePCA() {

		if(samples.isPresent()) {
			ISamplesVisualization<? extends IVariable, ? extends ISample<? extends ISampleData>> s = samples.get();
			ObservableList<ISamplesVisualization<? extends IVariable, ? extends ISample<? extends ISampleData>>> el = SelectionManagerSamples.getInstance().getElements();
			if(!el.contains(s)) {
				el.add(s);
			}
			int maxPC = numerPrincipalComponents.getSelection();
			pcx.setMaximum(maxPC);
			pcy.setMaximum(maxPC);
			pcz.setMaximum(maxPC);
			IPcaSettings pcaSettings = new PcaSettings(maxPC);
			IPcaSettingsVisualization pcaSettingsVisualization = new PcaSettingsVisualization(pcx.getSelection(), pcy.getSelection(), pcz.getSelection());
			IPcaResultsVisualization results = SelectionManagerSamples.getInstance().evaluatePca(s, pcaSettings, pcaSettingsVisualization, new NullProgressMonitor(), true);
			pcaResults = Optional.of(results);
		}
	}

	public Optional<IPcaResultsVisualization> getPcaResults() {

		return pcaResults;
	}

	public Optional<SamplesVisualization> getSamples() {

		return samples;
	}

	public void setSamples(SamplesVisualization samples) {

		this.samples = Optional.of(samples);
	}
}
