/*******************************************************************************
 * Copyright (c) 2018, 2022 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 * Christoph Läubrich - improve data handling
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.swt;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import org.eclipse.chemclipse.model.core.IMeasurementResult;
import org.eclipse.chemclipse.support.ui.provider.AbstractLabelProvider;
import org.eclipse.chemclipse.swt.ui.components.InformationUI;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;

public class ExtendedMeasurementResultUI extends Composite implements IExtendedPartUI {

	private static final String NO_SELECTION = "--";
	//
	private Button buttonToolbarInfo;
	private AtomicReference<InformationUI> toolbarInfo = new AtomicReference<>();
	private Button buttonToolbarResults;
	private AtomicReference<InformationUI> toolbarResults = new AtomicReference<>();
	private ComboViewer comboMeasurementResults;
	private MeasurementResultUI measurementResultsUI;

	public ExtendedMeasurementResultUI(Composite parent, int style) {

		super(parent, style);
		createControl();
	}

	/**
	 * Update the UI with the given data
	 * 
	 * @param results
	 *            the results to show, must not be <code>null</code>
	 * @param infoLabel
	 *            the infolabel to show must not be <code>null</code>
	 */
	public void setInput(Collection<IMeasurementResult<?>> results, String infoLabel) {

		toolbarInfo.get().setText(infoLabel);
		/*
		 * Update the combo viewer.
		 */
		List<Object> measurementResults = new ArrayList<>();
		measurementResults.add(NO_SELECTION);
		List<IMeasurementResult<?>> resultsSorted = new ArrayList<>(results.stream().filter(m -> m.isVisible()).collect(Collectors.toList()));
		Collections.sort(resultsSorted, (r1, r2) -> r1.getName().compareTo(r2.getName()));
		measurementResults.addAll(resultsSorted);
		//
		IStructuredSelection structuredSelection = comboMeasurementResults.getStructuredSelection();
		Object object = structuredSelection.getFirstElement();
		comboMeasurementResults.setSelection(StructuredSelection.EMPTY);
		comboMeasurementResults.setInput(measurementResults);
		/*
		 * Get the selection
		 */
		IMeasurementResult<?> selection = null;
		int index = 0;
		if(object instanceof IMeasurementResult<?>) {
			/*
			 * Validation
			 */
			IMeasurementResult<?> result = (IMeasurementResult<?>)object;
			exitloop:
			for(int i = 0; i < resultsSorted.size(); i++) {
				IMeasurementResult<?> measurementResult = resultsSorted.get(i);
				if(measurementResult.getName().equals(result.getName())) {
					selection = measurementResult;
					index = i + 1;
					break exitloop;
				}
			}
		}
		/*
		 * Set the selection
		 */
		comboMeasurementResults.setSelection(selection == null ? StructuredSelection.EMPTY : new StructuredSelection(selection));
		comboMeasurementResults.getCombo().select(index);
	}

	private void createControl() {

		setLayout(new GridLayout(1, true));
		//
		createToolbarMain(this);
		createToolbarInfo(this);
		createTable(this);
		createToolbarResults(this);
		//
		initialize();
	}

	private void initialize() {

		enableToolbar(toolbarInfo, buttonToolbarInfo, IMAGE_INFO, TOOLTIP_INFO, true);
		enableToolbar(toolbarResults, buttonToolbarResults, IMAGE_RESULTS, TOOLTIP_RESULTS, false);
	}

	private void createToolbarMain(Composite parent) {

		Composite composite = new Composite(parent, SWT.NONE);
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		composite.setLayoutData(gridData);
		composite.setLayout(new GridLayout(3, false));
		//
		buttonToolbarInfo = createButtonToggleToolbar(composite, toolbarInfo, IMAGE_INFO, TOOLTIP_INFO);
		comboMeasurementResults = createResultCombo(composite);
		buttonToolbarResults = createButtonToggleToolbar(composite, toolbarResults, IMAGE_RESULTS, TOOLTIP_RESULTS);
	}

	public ComboViewer getComboMeasurementResults() {

		return comboMeasurementResults;
	}

	private void createToolbarInfo(Composite parent) {

		toolbarInfo.set(createInformationUI(parent));
	}

	private void createTable(Composite parent) {

		measurementResultsUI = new MeasurementResultUI(parent, SWT.VIRTUAL | SWT.BORDER | SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION);
		measurementResultsUI.getTable().setLayoutData(new GridData(GridData.FILL_BOTH));
	}

	private void createToolbarResults(Composite parent) {

		toolbarResults.set(createInformationUI(parent));
	}

	private InformationUI createInformationUI(Composite parent) {

		InformationUI informationUI = new InformationUI(parent, SWT.NONE);
		informationUI.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		//
		return informationUI;
	}

	private ComboViewer createResultCombo(Composite parent) {

		ComboViewer comboViewer = new ComboViewer(parent, SWT.PUSH);
		Combo combo = comboViewer.getCombo();
		comboViewer.setContentProvider(ArrayContentProvider.getInstance());
		comboViewer.setLabelProvider(new AbstractLabelProvider() {

			@Override
			public String getText(Object element) {

				if(element instanceof IMeasurementResult<?>) {
					IMeasurementResult<?> measurementResult = (IMeasurementResult<?>)element;
					return measurementResult.getName();
				} else if(element instanceof String) {
					return (String)element;
				}
				//
				return super.getText(element);
			}
		});
		//
		combo.setToolTipText("Show the available measurement results.");
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.grabExcessHorizontalSpace = true;
		combo.setLayoutData(gridData);
		comboViewer.addSelectionChangedListener(new ISelectionChangedListener() {

			@Override
			public void selectionChanged(SelectionChangedEvent event) {

				Object object = comboViewer.getStructuredSelection().getFirstElement();
				if(object instanceof IMeasurementResult<?>) {
					update((IMeasurementResult<?>)object);
				} else {
					update(null);
				}
			}
		});
		//
		return comboViewer;
	}

	private void update(IMeasurementResult<?> measurementResult) {

		updateLabel(measurementResult);
		measurementResultsUI.update(measurementResult);
	}

	private void updateLabel(IMeasurementResult<?> measurementResult) {

		toolbarResults.get().setText(measurementResult != null ? measurementResult.getDescription() : "");
	}
}