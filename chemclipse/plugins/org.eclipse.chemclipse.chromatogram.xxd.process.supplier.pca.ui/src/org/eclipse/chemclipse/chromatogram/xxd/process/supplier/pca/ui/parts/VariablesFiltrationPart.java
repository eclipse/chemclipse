/*******************************************************************************
 * Copyright (c) 2017 Lablicate GmbH.
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
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.managers.SelectionManagerSamples;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.ISample;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.ISampleData;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.ISamples;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.IVariable;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.Samples;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.support.FiltersTable;
import org.eclipse.core.runtime.NullProgressMonitor;
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

	private static Map<ISamples<? extends IVariable, ? extends ISample<? extends ISampleData>>, PcaFiltrationData> filters;
	private ListChangeListener<ISamples<? extends IVariable, ? extends ISample<? extends ISampleData>>> actualSelectionLisnter;
	private Label countSelectedRow;
	@Inject
	private Display display;
	private FiltersTable filtersTable;
	private ISamples<? extends IVariable, ? extends ISamples<? extends IVariable, ? extends ISample<? extends ISampleData>>> samples;

	public VariablesFiltrationPart() {
		synchronized(VariablesFiltrationPart.class) {
			if(filters == null) {
				filters = new ConcurrentHashMap<>();
				SelectionManagerSamples.getInstance().getElements().addListener(new ListChangeListener<ISamples<? extends IVariable, ? extends ISample<? extends ISampleData>>>() {

					@Override
					public void onChanged(ListChangeListener.Change<? extends ISamples<? extends IVariable, ? extends ISample<? extends ISampleData>>> c) {

						while(c.next()) {
							for(ISamples<? extends IVariable, ? extends ISample<? extends ISampleData>> samples : c.getRemoved()) {
								filters.remove(samples);
							}
						}
					}
				});
			}
		}
		actualSelectionLisnter = new ListChangeListener<ISamples<? extends IVariable, ? extends ISample<? extends ISampleData>>>() {

			@Override
			public void onChanged(ListChangeListener.Change<? extends ISamples<? extends IVariable, ? extends ISample<? extends ISampleData>>> c) {

				PcaFiltrationData pcaFiltrationData = new PcaFiltrationData();
				if(!c.getList().isEmpty()) {
					pcaFiltrationData = getPcaFiltrationData(c.getList().get(0));
				}
				final PcaFiltrationData filtrationData = pcaFiltrationData;
				display.syncExec(() -> {
					filtersTable.setPcaFiltrationData(filtrationData);
					filtersTable.update();
				});
			}
		};
	}

	private void applyFilters() {

		if(samples instanceof Samples) {
			Samples s = (Samples)samples;
			s.getPcaFiltrationData().process(samples, new NullProgressMonitor());
		} else {
			new PcaFiltrationData();
		}
		updateLabelTotalSelection();
		filtersTable.update();
	}

	private void createButton(Composite parent) {

		Composite buttonComposite = new Composite(parent, SWT.None);
		buttonComposite.setLayoutData(new GridData(SWT.CENTER, SWT.BEGINNING, false, true));
		buttonComposite.setLayout(new FillLayout(SWT.VERTICAL));
		Button button = new Button(buttonComposite, SWT.PUSH);
		button.setText("Apply Filters");
		button.addListener(SWT.Selection, e -> {
			applyFilters();
		});
		button = new Button(buttonComposite, SWT.PUSH);
		button.setText("Create New Filter");
		button.addListener(SWT.Selection, e -> {
			filtersTable.createNewFilter();
			applyFilters();
		});
		button = new Button(buttonComposite, SWT.PUSH);
		button.setText("Remove Selected Filters");
		button.addListener(SWT.Selection, e -> {
			filtersTable.removeSelectedFilters();
			applyFilters();
		});
		button = new Button(buttonComposite, SWT.PUSH);
		button.setText("Remove All Filters");
		button.addListener(SWT.Selection, e -> {
			filtersTable.removeAllFilters();
			applyFilters();
		});
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
		/*
		 *
		 */
		countSelectedRow = new Label(parent, SWT.None);
		if(!SelectionManagerSamples.getInstance().getSelection().isEmpty()) {
			filtersTable.setPcaFiltrationData(getPcaFiltrationData(SelectionManagerSamples.getInstance().getSelection().get(0)));
		}
		updateLabelTotalSelection();
		SelectionManagerSamples.getInstance().getSelection().addListener(actualSelectionLisnter);
	}

	private PcaFiltrationData getPcaFiltrationData(ISamples<? extends IVariable, ? extends ISample<? extends ISampleData>> samples) {

		if(samples instanceof Samples) {
			Samples s = (Samples)samples;
			return s.getPcaFiltrationData();
		} else {
			return filters.getOrDefault(samples, new PcaFiltrationData());
		}
	}

	@PreDestroy
	public void preDestroy() {

		SelectionManagerSamples.getInstance().getSelection().removeListener(actualSelectionLisnter);
	}

	private void updateLabelTotalSelection() {

		long count = 0;
		if(samples != null) {
			count = samples.getVariables().stream().filter(IVariable::isSelected).count();
		}
		countSelectedRow.setText("It will be selected " + count + " rows");
		countSelectedRow.getParent().layout();
	}
}
