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

import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.IPcaResult;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.ISample;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.chart2d.ScorePlot;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.managers.SelectionManagerSample;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.managers.SelectionManagerSamples;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.model.IPcaResultVisualization;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.model.IPcaResultsVisualization;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.model.IPcaSettingsVisualization;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.preferences.PreferenceScorePlot2DPage;
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

public class ScorePlot2DPart {

	private ListChangeListener<ISample> actualSelectionChangeListener;
	private ChangeListener<IPcaResultsVisualization> pcaResultChangeLisnter;
	private IPcaResultsVisualization pcaResults;
	private ScorePlot scorePlot;
	private ListChangeListener<IPcaResultVisualization> selectionChangeListener;
	private Consumer<IPcaSettingsVisualization> settingUpdateListener;
	private boolean partHasBeenDestroy;
	private Runnable updateSelection = () -> {
		if(partHasBeenDestroy)
			return;
		if(pcaResults != null) {
			scorePlot.update(pcaResults);
		}
	};

	public ScorePlot2DPart() {

		settingUpdateListener = new Consumer<IPcaSettingsVisualization>() {

			@Override
			public void accept(IPcaSettingsVisualization t) {

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
						oldValue.getPcaSettingsVisualization().removeChangeListener(settingUpdateListener);
					}
					if(newValue != null) {
						scorePlot.update(newValue);
						newValue.getPcaResultList().addListener(selectionChangeListener);
						newValue.getPcaSettingsVisualization().addChangeListener(settingUpdateListener);
					} else {
						scorePlot.deleteSeries();
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
		Composite scorePlotComposite = new Composite(composite, SWT.None);
		scorePlotComposite.setLayoutData(new GridData(GridData.FILL_BOTH));
		scorePlotComposite.setLayout(new FillLayout());
		scorePlot = new ScorePlot(scorePlotComposite);
		ReadOnlyObjectProperty<IPcaResultsVisualization> pcaresults = SelectionManagerSamples.getInstance().getActualSelectedPcaResults();
		pcaresults.addListener(pcaResultChangeLisnter);
		this.pcaResults = pcaresults.get();
		if(this.pcaResults != null) {
			scorePlot.update(pcaresults.getValue());
			this.pcaResults.getPcaResultList().addListener(selectionChangeListener);
			this.pcaResults.getPcaSettingsVisualization().addChangeListener(settingUpdateListener);
		}
		SelectionManagerSample.getInstance().getSelection().addListener(actualSelectionChangeListener);
	}

	private void createSettingsButtons(Composite buttonsComposite) {

		Button button = new Button(buttonsComposite, SWT.PUSH);
		button.setToolTipText("Open the Settings");
		button.setText("");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_CONFIGURE, IApplicationImage.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				IPreferencePage preferenceScorePlot2DPage = new PreferenceScorePlot2DPage();
				preferenceScorePlot2DPage.setTitle("Score Plot 2D Settings ");
				//
				PreferenceManager preferenceManager = new PreferenceManager();
				preferenceManager.addToRoot(new PreferenceNode("1", preferenceScorePlot2DPage));
				//
				PreferenceDialog preferenceDialog = new PreferenceDialog(Display.getDefault().getActiveShell(), preferenceManager);
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

	@PreDestroy
	public void preDestroy() {

		partHasBeenDestroy = true;
		Display.getDefault().timerExec(-1, updateSelection);
		SelectionManagerSample.getInstance().getSelection().removeListener(actualSelectionChangeListener);
		SelectionManagerSamples.getInstance().getActualSelectedPcaResults().removeListener(pcaResultChangeLisnter);
		if(pcaResults != null) {
			this.pcaResults.getPcaResultList().removeListener(selectionChangeListener);
			this.pcaResults.getPcaSettingsVisualization().removeChangeListener(settingUpdateListener);
		}
	}
}
