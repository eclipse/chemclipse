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

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Optional;

import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.IPcaSettings;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.ISample;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.ISampleData;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.PcaSettings;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.preferences.PreferenceSupplier;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.Activator;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.managers.SelectionManagerSamples;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.model.IPcaResultsVisualization;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.model.IPcaSettingsVisualization;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.model.ISampleVisualization;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.model.ISamplesVisualization;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.model.IVariableVisualization;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.model.PcaSettingsVisualization;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.preferences.PreferencePage;
import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.rcp.ui.icons.core.ApplicationImageFactory;
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImage;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.preference.IPreferencePage;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferenceDialog;
import org.eclipse.jface.preference.PreferenceManager;
import org.eclipse.jface.preference.PreferenceNode;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Spinner;

import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

public class PCAController {

	private static final Logger logger = Logger.getLogger(PCAController.class);
	private Spinner numerPrincipalComponents;
	private CCombo pcaAlgo;
	private Optional<IPcaResultsVisualization> pcaResults;
	private Spinner pcx;
	private Spinner pcy;
	private Spinner pcz;
	private Button runAnalysis;
	private Optional<ISamplesVisualization<? extends IVariableVisualization, ? extends ISampleVisualization<? extends ISampleData>>> samples;
	private IPreferenceStore preferenceStore = Activator.getDefault().getPreferenceStore();
	private Button autoReevaluate;
	private Button settings;
	private Runnable autoreevaluete = () -> {
		if(samples.isPresent() && autoReevaluate.getSelection()) {
			evaluatePCA();
		}
	};
	private ListChangeListener<? super ISampleVisualization<? extends ISampleData>> reevaluationSamplesChangeListener = e -> {
		Display.getDefault().timerExec(100, autoreevaluete);
	};
	private ListChangeListener<? super IVariableVisualization> reevaluationRetentionTimeChangeListener = e -> {
		Display.getDefault().timerExec(100, autoreevaluete);
	};

	public PCAController(Composite parent, Object layoutData) {
		samples = Optional.empty();
		pcaResults = Optional.empty();
		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayoutData(layoutData);
		composite.setLayout(new GridLayout(12, false));
		runAnalysis = new Button(composite, SWT.PUSH);
		runAnalysis.setText("RUN");
		runAnalysis.addListener(SWT.Selection, e -> {
			evaluatePCA();
		});
		int maxPC = preferenceStore.getInt(PreferenceSupplier.P_NUMBER_OF_COMPONENTS);
		numerPrincipalComponents = new Spinner(composite, SWT.None);
		numerPrincipalComponents.setMinimum(PreferenceSupplier.MIN_NUMBER_OF_COMPONENTS);
		numerPrincipalComponents.setIncrement(1);
		numerPrincipalComponents.setSelection(maxPC);
		numerPrincipalComponents.setMaximum(PreferenceSupplier.MAX_NUMBER_OF_COMPONENTS);
		numerPrincipalComponents.addListener(SWT.Selection, e -> Display.getDefault().timerExec(100, autoreevaluete));
		// Selection PCA calculation algorithm
		Label label = new Label(composite, SWT.None);
		label.setText("Algorithm:");
		pcaAlgo = new CCombo(composite, SWT.READ_ONLY);
		pcaAlgo.setBounds(50, 50, 150, 65);
		String items[] = {IPcaSettings.PCA_ALGO_SVD, IPcaSettings.PCA_ALGO_NIPALS, IPcaSettings.OPLS_ALGO_NIPALS};
		pcaAlgo.setItems(items);
		pcaAlgo.select(Arrays.asList(items).indexOf(preferenceStore.getString(PreferenceSupplier.P_ALGORITHM_TYPE)));
		pcaAlgo.addListener(SWT.Selection, e -> Display.getDefault().timerExec(100, autoreevaluete));
		// Selection Principal Component for X-axis
		label = new Label(composite, SWT.None);
		label.setText("PCX:");
		pcx = new Spinner(composite, SWT.None);
		pcx.setMinimum(1);
		pcx.setIncrement(1);
		pcx.setSelection(1);
		pcx.setMaximum(maxPC);
		pcx.addModifyListener(e -> {
			if(pcaResults.isPresent()) {
				pcaResults.get().getPcaSettingsVisualization().setPcX(pcx.getSelection());
			}
		});
		label = new Label(composite, SWT.None);
		label.setText("PCY:");
		pcy = new Spinner(composite, SWT.None);
		pcy.setMinimum(1);
		pcy.setIncrement(1);
		pcy.setSelection(2);
		pcy.setMaximum(maxPC);
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
		pcz.setMaximum(maxPC);
		pcz.addModifyListener(e -> {
			if(pcaResults.isPresent()) {
				pcaResults.get().getPcaSettingsVisualization().setPcZ(pcz.getSelection());
			}
		});
		autoReevaluate = new Button(composite, SWT.CHECK);
		autoReevaluate.setText("Auto reevaluete");
		autoReevaluate.setSelection(preferenceStore.getBoolean(PreferenceSupplier.P_AUTO_REEVALUATE));
		settings = new Button(composite, SWT.PUSH);
		GridData gridData = new GridData();
		gridData.horizontalAlignment = GridData.END;
		gridData.verticalAlignment = GridData.FILL;
		settings.setLayoutData(gridData);
		settings.setToolTipText("Open the Settings");
		settings.setText("");
		settings.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_CONFIGURE, IApplicationImage.SIZE_16x16));
		settings.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				IPreferencePage preferencePageChromatogram = new PreferencePage();
				preferencePageChromatogram.setTitle("PCA Settings ");
				//
				PreferenceManager preferenceManager = new PreferenceManager();
				preferenceManager.addToRoot(new PreferenceNode("1", preferencePageChromatogram));
				//
				PreferenceDialog preferenceDialog = new PreferenceDialog(Display.getDefault().getActiveShell(), preferenceManager);
				preferenceDialog.create();
				preferenceDialog.setMessage("Settings");
				preferenceDialog.open();
			}
		});
	}

	private void evaluatePCA() {

		synchronized(this) {
			if(samples.isPresent()) {
				ProgressMonitorDialog monitor = new ProgressMonitorDialog(Display.getDefault().getActiveShell());
				int maxPC = numerPrincipalComponents.getSelection();
				String pcaAlgorithm = pcaAlgo.getText();
				pcx.setMaximum(maxPC);
				pcy.setMaximum(maxPC);
				pcz.setMaximum(maxPC);
				int pcX = pcx.getSelection();
				int pcY = pcy.getSelection();
				int pcZ = pcy.getSelection();
				try {
					monitor.run(false, false, progressMonitor -> {
						progressMonitor.setTaskName("Inicialization");
						ISamplesVisualization<? extends IVariableVisualization, ? extends ISample<? extends ISampleData>> s = samples.get();
						ObservableList<ISamplesVisualization<? extends IVariableVisualization, ? extends ISampleVisualization<? extends ISampleData>>> el = SelectionManagerSamples.getInstance().getElements();
						if(!el.contains(s)) {
							el.add(s);
						}
						IPcaSettings pcaSettings = new PcaSettings(maxPC, pcaAlgorithm);
						IPcaSettingsVisualization pcaSettingsVisualization = new PcaSettingsVisualization(pcX, pcY, pcZ);
						final IPcaResultsVisualization results = SelectionManagerSamples.getInstance().evaluatePca(s, pcaSettings, pcaSettingsVisualization, progressMonitor, true);
						pcaResults = Optional.of(results);
					});
				} catch(InvocationTargetException e) {
					logger.warn(e);
				} catch(InterruptedException e) {
					logger.warn(e);
				}
			}
		}
	}

	public Optional<IPcaResultsVisualization> getPcaResults() {

		return pcaResults;
	}

	public Optional<ISamplesVisualization<? extends IVariableVisualization, ? extends ISampleVisualization<? extends ISampleData>>> getSamples() {

		return samples;
	}

	public void setSamples(ISamplesVisualization<? extends IVariableVisualization, ? extends ISampleVisualization<? extends ISampleData>> samples) {

		if(this.samples.isPresent()) {
			ISamplesVisualization<? extends IVariableVisualization, ? extends ISampleVisualization<? extends ISampleData>> samplesVisualizationOld = this.samples.get();
			samplesVisualizationOld.getSampleList().removeListener(reevaluationSamplesChangeListener);
			samplesVisualizationOld.getVariables().removeListener(reevaluationRetentionTimeChangeListener);
		}
		this.samples = Optional.of(samples);
		if(this.samples.isPresent()) {
			ISamplesVisualization<? extends IVariableVisualization, ? extends ISampleVisualization<? extends ISampleData>> samplesVisualizationNew = this.samples.get();
			samplesVisualizationNew.getSampleList().addListener(reevaluationSamplesChangeListener);
			samplesVisualizationNew.getVariables().addListener(reevaluationRetentionTimeChangeListener);
		}
		Display.getDefault().timerExec(100, autoreevaluete);
	}
}
