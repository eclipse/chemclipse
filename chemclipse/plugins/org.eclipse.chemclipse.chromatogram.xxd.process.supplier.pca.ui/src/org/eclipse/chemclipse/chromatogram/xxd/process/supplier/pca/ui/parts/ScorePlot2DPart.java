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

import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.managers.SelectionManagerSample;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.managers.SelectionManagerSamples;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.IPcaResult;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.ISample;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.ISampleData;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.visualization.IPcaResultVisualization;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.visualization.IPcaResultsVisualization;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.visualization.IPcaSettingsVisualization;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.chart2d.ScorePlot;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;

import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;

public class ScorePlot2DPart {

	private ListChangeListener<ISample<? extends ISampleData>> actualSelectionChangeListener;
	private ChangeListener<IPcaResultsVisualization> pcaResultChangeLisnter;
	private IPcaResultsVisualization pcaResults;
	private ScorePlot scorePlot;
	private ListChangeListener<IPcaResultVisualization> selectionChangeListener;
	private Consumer<IPcaSettingsVisualization> settingUpdateListener;
	private Runnable updateSelection = () -> {
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
		actualSelectionChangeListener = new ListChangeListener<ISample<? extends ISampleData>>() {

			@Override
			public void onChanged(javafx.collections.ListChangeListener.Change<? extends ISample<? extends ISampleData>> c) {

				Display.getDefault().syncExec(() -> {
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
			}
		};
	}

	@PostConstruct
	public void createComposite(Composite parent) {

		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new FillLayout());
		scorePlot = new ScorePlot(composite);
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

	@PreDestroy
	public void preDestroy() {

		Display.getDefault().timerExec(-1, updateSelection);
		SelectionManagerSample.getInstance().getSelection().removeListener(actualSelectionChangeListener);
		SelectionManagerSamples.getInstance().getActualSelectedPcaResults().removeListener(pcaResultChangeLisnter);
		if(pcaResults != null) {
			this.pcaResults.getPcaResultList().removeListener(selectionChangeListener);
			this.pcaResults.getPcaSettingsVisualization().removeChangeListener(settingUpdateListener);
		}
	}
}
