/*******************************************************************************
 * Copyright (c) 2018, 2019 Lablicate GmbH.
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

import java.util.Collection;

import javax.inject.Inject;

import org.eclipse.chemclipse.model.core.IMeasurementResult;
import org.eclipse.chemclipse.rcp.ui.icons.core.ApplicationImageFactory;
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImage;
import org.eclipse.chemclipse.support.ui.provider.AbstractLabelProvider;
import org.eclipse.chemclipse.support.ui.swt.ExtendedTableViewer;
import org.eclipse.chemclipse.support.ui.swt.columns.ColumnDefinitionProvider;
import org.eclipse.chemclipse.ux.extension.ui.support.PartSupport;
import org.eclipse.core.runtime.Adapters;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;

public class ExtendedMeasurementResultUI {

	private Label labelChromatogramInfo;
	private Label labelMeasurementResultInfo;
	private Composite toolbarChromatogramInfo;
	private Composite toolbarMeasurementResultInfo;
	private ComboViewer comboMeasurementResults;
	private ExtendedTableViewer resultTable;
	private ProxySelectionChangedListener selectionChangedListener;
	private ProxyTableLabelProvider labelProvider;
	private ProxyStructuredContentProvider contentProvider;

	@Inject
	public ExtendedMeasurementResultUI(Composite parent) {
		initialize(parent);
	}

	/**
	 * Update the UI with the given data
	 * 
	 * @param results
	 *            the results to show, must not be <code>null</code>
	 * @param infoLabel
	 *            the infolabel to show must not be <code>null</code>
	 */
	public void update(Collection<IMeasurementResult<?>> results, String infoLabel) {

		if(!labelChromatogramInfo.isDisposed()) {
			labelChromatogramInfo.setText(infoLabel);
		}
		comboMeasurementResults.setInput(results);
		comboMeasurementResults.refresh();
		IMeasurementResult<?> element = (IMeasurementResult<?>)comboMeasurementResults.getStructuredSelection().getFirstElement();
		if(element == null && results.size() == 1) {
			element = results.iterator().next();
			comboMeasurementResults.setSelection(new StructuredSelection(element));
		}
		updateMeasurementResult(element);
	}

	private void initialize(Composite parent) {

		parent.setLayout(new GridLayout(1, true));
		//
		createToolbarMain(parent);
		toolbarChromatogramInfo = createToolbarChromatogramInfo(parent);
		toolbarMeasurementResultInfo = createToolbarMeasurementResultInfo(parent);
		resultTable = createResultSection(parent);
		//
		PartSupport.setCompositeVisibility(toolbarChromatogramInfo, true);
		PartSupport.setCompositeVisibility(toolbarMeasurementResultInfo, false);
	}

	private void createToolbarMain(Composite parent) {

		Composite composite = new Composite(parent, SWT.NONE);
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		composite.setLayoutData(gridData);
		composite.setLayout(new GridLayout(3, false));
		//
		createButtonToggleChromatogramToolbarInfo(composite);
		comboMeasurementResults = createResultCombo(composite);
		createButtonToggleMeasurementResultToolbarInfo(composite);
	}

	private Composite createToolbarChromatogramInfo(Composite parent) {

		Composite composite = new Composite(parent, SWT.NONE);
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		composite.setLayoutData(gridData);
		composite.setLayout(new GridLayout(1, false));
		//
		labelChromatogramInfo = new Label(composite, SWT.NONE);
		labelChromatogramInfo.setText("");
		labelChromatogramInfo.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		//
		return composite;
	}

	private Composite createToolbarMeasurementResultInfo(Composite parent) {

		Composite composite = new Composite(parent, SWT.NONE);
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		composite.setLayoutData(gridData);
		composite.setLayout(new GridLayout(1, false));
		//
		labelMeasurementResultInfo = new Label(composite, SWT.NONE);
		labelMeasurementResultInfo.setText("");
		labelMeasurementResultInfo.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		//
		return composite;
	}

	private ExtendedTableViewer createResultSection(Composite parent) {

		ExtendedTableViewer tableViewer = new ExtendedTableViewer(parent, SWT.BORDER | SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION);
		Table table = tableViewer.getTable();
		table.setLayoutData(new GridData(GridData.FILL_BOTH));
		contentProvider = new ProxyStructuredContentProvider();
		tableViewer.setContentProvider(contentProvider);
		labelProvider = new ProxyTableLabelProvider();
		tableViewer.setLabelProvider(labelProvider);
		selectionChangedListener = new ProxySelectionChangedListener();
		tableViewer.addSelectionChangedListener(selectionChangedListener);
		return tableViewer;
	}

	private Button createButtonToggleChromatogramToolbarInfo(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setToolTipText("Toggle info toolbar.");
		button.setText("");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_INFO, IApplicationImage.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				boolean visible = PartSupport.toggleCompositeVisibility(toolbarChromatogramInfo);
				if(visible) {
					button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_INFO, IApplicationImage.SIZE_16x16));
				} else {
					button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_INFO, IApplicationImage.SIZE_16x16));
				}
			}
		});
		//
		return button;
	}

	private Button createButtonToggleMeasurementResultToolbarInfo(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setToolTipText("Toggle measurement results description toolbar.");
		button.setText("");
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_MEASUREMENT_RESULTS_DEFAULT, IApplicationImage.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				boolean visible = PartSupport.toggleCompositeVisibility(toolbarMeasurementResultInfo);
				if(visible) {
					button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_MEASUREMENT_RESULTS_DEFAULT, IApplicationImage.SIZE_16x16));
				} else {
					button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_MEASUREMENT_RESULTS_DEFAULT, IApplicationImage.SIZE_16x16));
				}
			}
		});
		//
		return button;
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
				}
				return super.getText(element);
			}
		});
		combo.setToolTipText("Show the available measurement results.");
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.grabExcessHorizontalSpace = true;
		combo.setLayoutData(gridData);
		comboViewer.addSelectionChangedListener(new ISelectionChangedListener() {

			@Override
			public void selectionChanged(SelectionChangedEvent event) {

				Object object = comboViewer.getStructuredSelection().getFirstElement();
				if(object instanceof IMeasurementResult<?>) {
					updateMeasurementResult((IMeasurementResult<?>)object);
				} else {
					updateMeasurementResult(null);
				}
			}
		});
		return comboViewer;
	}

	private void updateMeasurementResult(IMeasurementResult<?> measurementResult) {

		updateLabel(measurementResult);
		contentProvider.setProxy(adaptTo(measurementResult, IStructuredContentProvider.class));
		selectionChangedListener.setProxy(adaptTo(measurementResult, ISelectionChangedListener.class));
		resultTable.clearColumns();
		resultTable.addColumns(adaptTo(measurementResult, ColumnDefinitionProvider.class));
		ITableLabelProvider tableLabelProvider = adaptTo(measurementResult, ITableLabelProvider.class);
		labelProvider.setProxy(tableLabelProvider);
		if(tableLabelProvider != null) {
			resultTable.setLabelProvider(tableLabelProvider);
		}
		resultTable.setInput(measurementResult);
		resultTable.refresh();
	}

	private static <T> T adaptTo(IMeasurementResult<?> measurementResult, Class<T> desiredType) {

		if(measurementResult == null) {
			return null;
		}
		T resultAdapted = Adapters.adapt(measurementResult, desiredType);
		if(resultAdapted != null) {
			return resultAdapted;
		} else {
			return Adapters.adapt(measurementResult.getResult(), desiredType);
		}
	}

	public void updateLabel(IMeasurementResult<?> measurementResult) {

		if(!labelMeasurementResultInfo.isDisposed()) {
			if(measurementResult != null) {
				labelMeasurementResultInfo.setText(measurementResult.getDescription());
			} else {
				labelMeasurementResultInfo.setText("");
			}
		}
	}
}
