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

import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.managers.SelectionManagerSample;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.managers.SelectionManagerSamples;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.IPcaResult;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.ISample;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.ISampleData;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.visualization.IPcaResultsVisualization;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.visualization.IPcaSettingsVisualization;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.chart3d.ScorePlot3d;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;

import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;

public class ScorePlot3DPart {

	private ListChangeListener<ISample<? extends ISampleData>> actualSelectionChangeListener;
	private ChangeListener<IPcaResultsVisualization> pcaResultChangeLisnter;
	private IPcaResultsVisualization pcaResults;
	private ScorePlot3d scorePlot3d;
	private ListChangeListener<IPcaResult> selectionChangeListener;
	private Consumer<IPcaSettingsVisualization> settingUpdateListener;

	public ScorePlot3DPart() {
		settingUpdateListener = new Consumer<IPcaSettingsVisualization>() {

			@Override
			public void accept(IPcaSettingsVisualization t) {

				scorePlot3d.update(pcaResults);
			}
		};
		selectionChangeListener = new ListChangeListener<IPcaResult>() {

			@Override
			public void onChanged(javafx.collections.ListChangeListener.Change<? extends IPcaResult> c) {

				scorePlot3d.updateSelection();
			}
		};
		actualSelectionChangeListener = new ListChangeListener<ISample<? extends ISampleData>>() {

			@Override
			public void onChanged(ListChangeListener.Change<? extends ISample<? extends ISampleData>> c) {

				scorePlot3d.updateSelection();
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
					scorePlot3d.update(newValue);
					pcaResults.getPcaResultList().addListener(selectionChangeListener);
					pcaResults.getPcaSettingsVisualization().addChangeListener(settingUpdateListener);
				} else {
					scorePlot3d.removeData();
				}
			}
		};
	}

	@PostConstruct
	public void createComposite(Composite parent) {

		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new FillLayout());
		scorePlot3d = new ScorePlot3d(composite, null);
		ReadOnlyObjectProperty<IPcaResultsVisualization> pcaresults = SelectionManagerSamples.getInstance().getActualSelectedPcaResults();
		pcaresults.addListener(pcaResultChangeLisnter);
		this.pcaResults = pcaresults.get();
		if(this.pcaResults != null) {
			scorePlot3d.update(pcaresults.getValue());
			this.pcaResults.getPcaResultList().addListener(selectionChangeListener);
			this.pcaResults.getPcaSettingsVisualization().addChangeListener(settingUpdateListener);
		}
		SelectionManagerSample.getInstance().getSelection().addListener(actualSelectionChangeListener);
	}

	@PreDestroy
	public void preDestroy() {

		SelectionManagerSamples.getInstance().getActualSelectedPcaResults().removeListener(pcaResultChangeLisnter);
		SelectionManagerSample.getInstance().getSelection().removeListener(actualSelectionChangeListener);
		if(pcaResults != null) {
			this.pcaResults.getPcaResultList().removeListener(selectionChangeListener);
			this.pcaResults.getPcaSettingsVisualization().removeChangeListener(settingUpdateListener);
		}
	}

	public void updateSelection() {

		scorePlot3d.updateSelection();
	}
}
