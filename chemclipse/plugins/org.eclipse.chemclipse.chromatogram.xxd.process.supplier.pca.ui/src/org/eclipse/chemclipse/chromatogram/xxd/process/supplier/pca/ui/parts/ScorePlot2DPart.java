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

import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.IPcaResult;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.chart2d.ScorePlot;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.managers.SelectionManagerSample;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.managers.SelectionManagerSamples;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.model.IPcaResultVisualization;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.model.IPcaResultsVisualization;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.model.IPcaVisualization;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.preferences.PreferenceScorePlot2DPage;
import org.eclipse.chemclipse.model.statistics.ISample;
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

public class ScorePlot2DPart {

	@SuppressWarnings("restriction")
	@Inject
	private EHandlerService handlerService;
	private static String ID_COMMAND_SETTINGS = "org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.command.settings2dscoreplot";
	//
	private ListChangeListener<ISample> actualSelectionChangeListener;
	private ChangeListener<IPcaResultsVisualization> pcaResultChangeLisnter;
	private IPcaResultsVisualization pcaResults;
	private ScorePlot scorePlot;
	private ListChangeListener<IPcaResultVisualization> selectionChangeListener;
	private Consumer<IPcaVisualization> settingUpdateListener;
	private boolean partHasBeenDestroy;
	private Runnable updateSelection = () -> {
		if(partHasBeenDestroy)
			return;
		if(pcaResults != null) {
			scorePlot.update(pcaResults);
		}
	};
	@Inject
	@org.eclipse.e4.core.di.annotations.Optional
	private SelectionManagerSamples managerSamples;

	public ScorePlot2DPart() {

		//
		settingUpdateListener = new Consumer<IPcaVisualization>() {

			@Override
			public void accept(IPcaVisualization t) {

				Display.getDefault().timerExec(100, updateSelection);
			}
		};
		actualSelectionChangeListener = new ListChangeListener<ISample>() {

			@Override
			public void onChanged(javafx.collections.ListChangeListener.Change<? extends ISample> c) {

				Display.getDefault().asyncExec(() -> {
					if(partHasBeenDestroy)
						return;
					scorePlot.getBaseChart().resetSeriesSettings();
					if(!c.getList().isEmpty()) {
						for(Entry<String, IPcaResult> entry : scorePlot.getExtractedResults().entrySet()) {
							if(c.getList().contains(entry.getValue().getSample())) {
								scorePlot.getBaseChart().selectSeries(entry.getKey());
							}
						}
					}
				});
			}
		};
		selectionChangeListener = new ListChangeListener<IPcaResultVisualization>() {

			@Override
			public void onChanged(javafx.collections.ListChangeListener.Change<? extends IPcaResultVisualization> c) {

				Display.getDefault().timerExec(100, updateSelection);
			}
		};
		pcaResultChangeLisnter = new ChangeListener<IPcaResultsVisualization>() {

			@Override
			public void changed(ObservableValue<? extends IPcaResultsVisualization> observable, IPcaResultsVisualization oldValue, IPcaResultsVisualization newValue) {

				Display.getDefault().asyncExec(() -> {
					if(partHasBeenDestroy)
						return;
					pcaResults = newValue;
					if(oldValue != null) {
						oldValue.getPcaResultList().removeListener(selectionChangeListener);
						oldValue.getPcaVisualization().removeChangeListener(settingUpdateListener);
					}
					if(newValue != null) {
						scorePlot.update(newValue);
						newValue.getPcaResultList().addListener(selectionChangeListener);
						newValue.getPcaVisualization().addChangeListener(settingUpdateListener);
					} else {
						scorePlot.deleteSeries();
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
		Composite scorePlotComposite = new Composite(composite, SWT.None);
		scorePlotComposite.setLayoutData(new GridData(GridData.FILL_BOTH));
		scorePlotComposite.setLayout(new FillLayout());
		scorePlot = new ScorePlot(scorePlotComposite, getSelectionManagerSample());
		ReadOnlyObjectProperty<IPcaResultsVisualization> pcaresults = getSelectionManagerSamples().getActualSelectedPcaResults();
		pcaresults.addListener(pcaResultChangeLisnter);
		this.pcaResults = pcaresults.get();
		if(this.pcaResults != null) {
			scorePlot.update(pcaresults.getValue());
			this.pcaResults.getPcaResultList().addListener(selectionChangeListener);
			this.pcaResults.getPcaVisualization().addChangeListener(settingUpdateListener);
		}
		getSelectionManagerSample().getSelection().addListener(actualSelectionChangeListener);
		//
		handlerService.activateHandler(ID_COMMAND_SETTINGS, new Object() {

			@Execute
			private void execute(Display display) {

				IPreferencePage preferenceScorePlot2DPage = new PreferenceScorePlot2DPage();
				preferenceScorePlot2DPage.setTitle("Score Plot 2D Settings ");
				//
				PreferenceManager preferenceManager = new PreferenceManager();
				preferenceManager.addToRoot(new PreferenceNode("1", preferenceScorePlot2DPage));
				//
				PreferenceDialog preferenceDialog = new PreferenceDialog(display.getActiveShell(), preferenceManager);
				preferenceDialog.create();
				preferenceDialog.setMessage("Settings");
				if(preferenceDialog.open() == Window.OK) {
					if(partHasBeenDestroy)
						return;
					if(pcaResults != null) {
						scorePlot.update(pcaResults);
					}
				}
			}
		});
	}

	private SelectionManagerSample getSelectionManagerSample() {

		return getSelectionManagerSamples().getSelectionManagerSample();
	}

	private SelectionManagerSamples getSelectionManagerSamples() {

		if(managerSamples != null) {
			return managerSamples;
		}
		return SelectionManagerSamples.getInstance();
	}

	@PreDestroy
	public void preDestroy() {

		partHasBeenDestroy = true;
		Display.getDefault().timerExec(-1, updateSelection);
		getSelectionManagerSample().getSelection().removeListener(actualSelectionChangeListener);
		getSelectionManagerSamples().getActualSelectedPcaResults().removeListener(pcaResultChangeLisnter);
		if(pcaResults != null) {
			this.pcaResults.getPcaResultList().removeListener(selectionChangeListener);
			this.pcaResults.getPcaVisualization().removeChangeListener(settingUpdateListener);
		}
	}
}
