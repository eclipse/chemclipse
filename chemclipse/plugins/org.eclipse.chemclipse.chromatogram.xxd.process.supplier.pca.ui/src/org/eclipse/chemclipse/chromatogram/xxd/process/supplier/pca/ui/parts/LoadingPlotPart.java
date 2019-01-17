/*******************************************************************************
 * Copyright (c) 2017, 2018 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Jan Holy - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.parts;

import java.util.function.Consumer;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;

import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.chart2d.LoadingPlot;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.managers.SelectionManagerSamples;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.model.IPcaResultsVisualization;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.model.IPcaSettingsVisualization;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.model.IVariableVisualization;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.preferences.PreferenceLoadingPlot2DPage;
import org.eclipse.chemclipse.rcp.ui.icons.core.ApplicationImageFactory;
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImage;
import org.eclipse.jface.preference.IPreferencePage;
import org.eclipse.jface.preference.PreferenceDialog;
import org.eclipse.jface.preference.PreferenceManager;
import org.eclipse.jface.preference.PreferenceNode;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;

import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;

public class LoadingPlotPart {

	private LoadingPlot loadingPlot;
	private ChangeListener<IPcaResultsVisualization> pcaResultChangeLisnter;
	private IPcaResultsVisualization pcaResults;
	private boolean partHasBeenDestroy;
	private Runnable updateSelection = () -> {
		if(partHasBeenDestroy)
			return;
		if(pcaResults != null) {
			loadingPlot.update(pcaResults);
		}
	};
	private ListChangeListener<IVariableVisualization> variableChanger;
	private Consumer<IPcaSettingsVisualization> settingUpdateListener;
	@Inject
	@org.eclipse.e4.core.di.annotations.Optional
	private SelectionManagerSamples managerSamples;

	public LoadingPlotPart() {

		settingUpdateListener = new Consumer<IPcaSettingsVisualization>() {

			@Override
			public void accept(IPcaSettingsVisualization t) {

				Display.getDefault().timerExec(100, updateSelection);
			}
		};
		variableChanger = new ListChangeListener<IVariableVisualization>() {

			@Override
			public void onChanged(ListChangeListener.Change<? extends IVariableVisualization> c) {

				Display.getDefault().timerExec(100, updateSelection);
			}
		};
		pcaResultChangeLisnter = new ChangeListener<IPcaResultsVisualization>() {

			@Override
			public void changed(ObservableValue<? extends IPcaResultsVisualization> observable, IPcaResultsVisualization oldValue, IPcaResultsVisualization newValue) {

				Display.getDefault().syncExec(() -> {
					if(partHasBeenDestroy)
						return;
					if(oldValue != null) {
						oldValue.getExtractedVariables().removeListener(variableChanger);
						oldValue.getPcaSettingsVisualization().removeChangeListener(settingUpdateListener);
					}
					if(newValue != null) {
						pcaResults = newValue;
						pcaResults.getExtractedVariables().addListener(variableChanger);
						pcaResults.getPcaSettingsVisualization().addChangeListener(settingUpdateListener);
						loadingPlot.update(newValue);
					} else {
						pcaResults = null;
						loadingPlot.deleteSeries();
					}
				});
			}
		};
	}

	@PostConstruct
	public void createComposite(Composite parent) {

		partHasBeenDestroy = false;
		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new GridLayout(1, false));
		Composite buttonsComposite = new Composite(composite, SWT.None);
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.horizontalAlignment = SWT.END;
		buttonsComposite.setLayoutData(gridData);
		buttonsComposite.setLayout(new GridLayout(1, false));
		createSettingsButtons(buttonsComposite);
		Composite loadingPlotComposite = new Composite(composite, SWT.None);
		loadingPlotComposite.setLayoutData(new GridData(GridData.FILL_BOTH));
		loadingPlotComposite.setLayout(new FillLayout());
		loadingPlot = new LoadingPlot(loadingPlotComposite);
		ReadOnlyObjectProperty<IPcaResultsVisualization> pcaResults = getSelectionManagerSamples().getActualSelectedPcaResults();
		pcaResults.addListener(pcaResultChangeLisnter);
		if(pcaResults.isNotNull().get()) {
			this.pcaResults = pcaResults.get();
			loadingPlot.update(this.pcaResults);
			this.pcaResults.getExtractedVariables().addListener(variableChanger);
			this.pcaResults.getPcaSettingsVisualization().addChangeListener(settingUpdateListener);
		}
	}

	private SelectionManagerSamples getSelectionManagerSamples() {

		if(managerSamples != null) {
			return managerSamples;
		}
		return SelectionManagerSamples.getInstance();
	}

	private void createSettingsButtons(Composite buttonsComposite) {

		Button button = new Button(buttonsComposite, SWT.PUSH);
		button.setToolTipText("Open the Settings");
		button.setText("");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_CONFIGURE, IApplicationImage.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				IPreferencePage preferenceLoadingPlot2DPage = new PreferenceLoadingPlot2DPage();
				preferenceLoadingPlot2DPage.setTitle("Loaing Plot 2D Settings ");
				//
				PreferenceManager preferenceManager = new PreferenceManager();
				preferenceManager.addToRoot(new PreferenceNode("1", preferenceLoadingPlot2DPage));
				//
				PreferenceDialog preferenceDialog = new PreferenceDialog(Display.getDefault().getActiveShell(), preferenceManager);
				preferenceDialog.create();
				preferenceDialog.setMessage("Settings");
				if(preferenceDialog.open() == Window.OK) {
					if(partHasBeenDestroy)
						return;
					if(pcaResults != null) {
						loadingPlot.update(pcaResults);
					}
				}
			}
		});
	}

	@PreDestroy
	public void preDestroy() {

		partHasBeenDestroy = true;
		getSelectionManagerSamples().getActualSelectedPcaResults().removeListener(pcaResultChangeLisnter);
		if(pcaResults != null) {
			pcaResults.getExtractedVariables().removeListener(variableChanger);
			pcaResults.getPcaSettingsVisualization().removeChangeListener(settingUpdateListener);
		}
	}
}
