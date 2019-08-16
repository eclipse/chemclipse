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

import java.util.Map.Entry;
import java.util.function.Consumer;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;

import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.chart2d.LoadingPlot;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.managers.SelectionManagerSamples;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.managers.SelectionManagerVariable;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.model.IPcaResultsVisualization;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.model.IPcaVisualization;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.model.IVariableVisualization;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.preferences.PreferenceLoadingPlot2DPage;
import org.eclipse.chemclipse.model.statistics.IVariable;
import org.eclipse.e4.core.commands.EHandlerService;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.jface.preference.IPreferencePage;
import org.eclipse.jface.preference.PreferenceDialog;
import org.eclipse.jface.preference.PreferenceManager;
import org.eclipse.jface.preference.PreferenceNode;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;

import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;

public class LoadingPlotPart {

	@SuppressWarnings("restriction")
	@Inject
	private EHandlerService handlerService;
	private static final String ID_COMMAND_SETTINGS = "org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.command.settingsloadingplot";
	//
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
	private Consumer<IPcaVisualization> settingUpdateListener;
	@Inject
	@org.eclipse.e4.core.di.annotations.Optional
	private SelectionManagerSamples managerSamples;
	private ListChangeListener<IVariable> actualVariableChangeListener;

	public LoadingPlotPart() {

		actualVariableChangeListener = new ListChangeListener<IVariable>() {

			@Override
			public void onChanged(javafx.collections.ListChangeListener.Change<? extends IVariable> c) {

				Display.getDefault().asyncExec(() -> {
					if(partHasBeenDestroy)
						return;
					loadingPlot.getBaseChart().resetSeriesSettings();
					if(!c.getList().isEmpty()) {
						for(Entry<String, IVariable> entry : loadingPlot.getExtractedValues().entrySet()) {
							if(c.getList().contains(entry.getValue())) {
								loadingPlot.getBaseChart().selectSeries(entry.getKey());
							}
						}
					}
				});
			}
		};
		settingUpdateListener = new Consumer<IPcaVisualization>() {

			@Override
			public void accept(IPcaVisualization t) {

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
						oldValue.getPcaVisualization().removeChangeListener(settingUpdateListener);
					}
					if(newValue != null) {
						pcaResults = newValue;
						pcaResults.getExtractedVariables().addListener(variableChanger);
						pcaResults.getPcaVisualization().addChangeListener(settingUpdateListener);
						loadingPlot.update(newValue);
					} else {
						pcaResults = null;
						loadingPlot.deleteSeries();
					}
				});
			}
		};
	}

	@SuppressWarnings("restriction")
	@PostConstruct
	public void createComposite(Composite parent) {

		partHasBeenDestroy = false;
		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new GridLayout(1, false));
		Composite loadingPlotComposite = new Composite(composite, SWT.None);
		loadingPlotComposite.setLayoutData(new GridData(GridData.FILL_BOTH));
		loadingPlotComposite.setLayout(new FillLayout());
		loadingPlot = new LoadingPlot(loadingPlotComposite, getSelectionVariable());
		ReadOnlyObjectProperty<IPcaResultsVisualization> pcaResults = getSelectionManagerSamples().getActualSelectedPcaResults();
		pcaResults.addListener(pcaResultChangeLisnter);
		if(pcaResults.isNotNull().get()) {
			this.pcaResults = pcaResults.get();
			loadingPlot.update(this.pcaResults);
			this.pcaResults.getExtractedVariables().addListener(variableChanger);
			this.pcaResults.getPcaVisualization().addChangeListener(settingUpdateListener);
		}
		getSelectionVariable().getSelection().addListener(actualVariableChangeListener);
		handlerService.activateHandler(ID_COMMAND_SETTINGS, new Object() {

			@Execute
			private void execute(Display display) {

				IPreferencePage preferenceLoadingPlot2DPage = new PreferenceLoadingPlot2DPage();
				preferenceLoadingPlot2DPage.setTitle("Loaing Plot 2D Settings ");
				//
				PreferenceManager preferenceManager = new PreferenceManager();
				preferenceManager.addToRoot(new PreferenceNode("1", preferenceLoadingPlot2DPage));
				//
				PreferenceDialog preferenceDialog = new PreferenceDialog(display.getActiveShell(), preferenceManager);
				preferenceDialog.create();
				preferenceDialog.setMessage("Settings");
				if(preferenceDialog.open() == Window.OK) {
					if(partHasBeenDestroy)
						return;
					if(LoadingPlotPart.this.pcaResults != null) {
						loadingPlot.update(LoadingPlotPart.this.pcaResults);
					}
				}
			}
		});
	}

	private SelectionManagerSamples getSelectionManagerSamples() {

		if(managerSamples != null) {
			return managerSamples;
		}
		return SelectionManagerSamples.getInstance();
	}

	private SelectionManagerVariable getSelectionVariable() {

		return getSelectionManagerSamples().getSelectionManagerVariable();
	}

	@PreDestroy
	public void preDestroy() {

		partHasBeenDestroy = true;
		getSelectionVariable().getSelection().removeListener(actualVariableChangeListener);
		getSelectionManagerSamples().getActualSelectedPcaResults().removeListener(pcaResultChangeLisnter);
		if(pcaResults != null) {
			pcaResults.getExtractedVariables().removeListener(variableChanger);
			pcaResults.getPcaVisualization().removeChangeListener(settingUpdateListener);
		}
	}
}
