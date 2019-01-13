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

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;

import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.core.PcaFiltrationData;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.IVariablesFiltration;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.managers.SelectionManagerSamples;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.model.ISampleVisualization;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.model.ISamplesVisualization;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.model.IVariableVisualization;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.support.FiltersTable;
import org.eclipse.chemclipse.model.statistics.ISample;
import org.eclipse.chemclipse.model.statistics.ISamples;
import org.eclipse.chemclipse.model.statistics.IVariable;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;

import javafx.collections.ListChangeListener;

public class VariablesFiltrationPart {

	private static Map<ISamples<? extends IVariable, ? extends ISample>, PcaFiltrationData> filters;
	private ListChangeListener<ISamplesVisualization<? extends IVariableVisualization, ? extends ISampleVisualization>> actualSelectionLisnter;
	private Label countSelectedRow;
	@Inject
	private Display display;
	private FiltersTable filtersTable;
	private Runnable changeSelectionVariables;
	private ListChangeListener<IVariableVisualization> changeVariablesChangeListener;
	private ISamplesVisualization<? extends IVariableVisualization, ? extends ISampleVisualization> samples;

	public VariablesFiltrationPart() {

		synchronized(VariablesFiltrationPart.class) {
			if(filters == null) {
				filters = new ConcurrentHashMap<>();
				SelectionManagerSamples.getInstance().getElements().addListener(new ListChangeListener<ISamples<? extends IVariable, ? extends ISample>>() {

					@Override
					public void onChanged(ListChangeListener.Change<? extends ISamples<? extends IVariable, ? extends ISample>> c) {

						while(c.next()) {
							for(ISamples<? extends IVariable, ? extends ISample> samples : c.getRemoved()) {
								filters.remove(samples);
							}
						}
					}
				});
			}
		}
		changeSelectionVariables = () -> updateLabelTotalSelection();
		changeVariablesChangeListener = new ListChangeListener<IVariableVisualization>() {

			@Override
			public void onChanged(javafx.collections.ListChangeListener.Change<? extends IVariableVisualization> c) {

				Display.getDefault().timerExec(100, changeSelectionVariables);
			}
		};
		actualSelectionLisnter = new ListChangeListener<ISamplesVisualization<? extends IVariableVisualization, ? extends ISampleVisualization>>() {

			@Override
			public void onChanged(ListChangeListener.Change<? extends ISamplesVisualization<? extends IVariableVisualization, ? extends ISampleVisualization>> c) {

				PcaFiltrationData pcaFiltrationData = new PcaFiltrationData();
				if(samples != null) {
					samples.getVariables().removeListener(changeVariablesChangeListener);
				}
				if(!c.getList().isEmpty()) {
					samples = c.getList().get(0);
					samples.getVariables().addListener(changeVariablesChangeListener);
					filtersTable.setSamples(samples);
					pcaFiltrationData = getPcaFiltrationData(c.getList().get(0));
				} else {
					samples = null;
				}
				final PcaFiltrationData filtrationData = pcaFiltrationData;
				display.syncExec(() -> {
					filtersTable.setPcaFiltrationData(filtrationData);
					filtersTable.update();
					updateLabelTotalSelection();
				});
			}
		};
	}

	private void createButton(Composite parent) {

		Composite buttonComposite = new Composite(parent, SWT.None);
		buttonComposite.setLayoutData(new GridData(SWT.CENTER, SWT.BEGINNING, false, true));
		buttonComposite.setLayout(new FillLayout(SWT.VERTICAL));
		Button button = new Button(buttonComposite, SWT.PUSH);
		button.setText("Apply Filters");
		button.addListener(SWT.Selection, e -> {
			filtersTable.updateVariables();
		});
		button = new Button(buttonComposite, SWT.PUSH);
		button.setText("Create New Filter");
		button.addListener(SWT.Selection, e -> {
			filtersTable.createNewFilter();
		});
		button = new Button(buttonComposite, SWT.PUSH);
		button.setText("Remove Selected Filters");
		button.addListener(SWT.Selection, e -> {
			filtersTable.removeSelectedFilters();
		});
		button = new Button(buttonComposite, SWT.PUSH);
		button.setText("Remove All Filters");
		button.addListener(SWT.Selection, e -> {
			filtersTable.removeAllFilters();
		});
		button = new Button(buttonComposite, SWT.CHECK);
		button.setText("Autoupdate");
		button.addListener(SWT.Selection, e -> {
			filtersTable.setAutoUpdate(((Button)e.widget).getSelection());
		});
		button.setSelection(filtersTable.isAutoUpdate());
	}

	@PostConstruct
	public void createComposite(Composite parent) {

		parent.setLayout(new GridLayout(1, false));
		Composite composite = new Composite(parent, SWT.None);
		composite.setLayoutData(new GridData(GridData.FILL_BOTH));
		composite.setLayout(new GridLayout(2, false));
		/*
		 * Create the section.
		 */
		filtersTable = new FiltersTable(composite, new GridData(GridData.FILL_BOTH));
		/*
		 * create button area
		 */
		createButton(composite);
		countSelectedRow = new Label(parent, SWT.None);
		if(!SelectionManagerSamples.getInstance().getSelection().isEmpty()) {
			samples = SelectionManagerSamples.getInstance().getSelection().get(0);
			samples.getVariables().addListener(changeVariablesChangeListener);
			filtersTable.setPcaFiltrationData(getPcaFiltrationData(samples));
			filtersTable.setSamples(samples);
			filtersTable.update();
		}
		updateLabelTotalSelection();
		SelectionManagerSamples.getInstance().getSelection().addListener(actualSelectionLisnter);
	}

	private PcaFiltrationData getPcaFiltrationData(ISamples<? extends IVariable, ? extends ISample> samples) {

		if(samples instanceof IVariablesFiltration) {
			IVariablesFiltration variablesFiltration = (IVariablesFiltration)samples;
			return variablesFiltration.getPcaFiltrationData();
		} else {
			if(filters.containsKey(samples)) {
				return filters.get(samples);
			} else {
				PcaFiltrationData pcaFiltrationData = new PcaFiltrationData();
				filters.put(samples, pcaFiltrationData);
				return pcaFiltrationData;
			}
		}
	}

	@PreDestroy
	public void preDestroy() {

		SelectionManagerSamples.getInstance().getSelection().removeListener(actualSelectionLisnter);
		if(samples != null) {
			samples.getVariables().removeListener(changeVariablesChangeListener);
		}
		Display.getDefault().timerExec(-1, changeSelectionVariables);
	}

	private void updateLabelTotalSelection() {

		long count = 0;
		int totalCount = 0;
		if(samples != null) {
			count = samples.getVariables().stream().filter(IVariable::isSelected).count();
			totalCount = samples.getVariables().size();
		}
		countSelectedRow.setText("It will be selected " + count + " variables from " + totalCount);
		countSelectedRow.getParent().layout();
	}
}
