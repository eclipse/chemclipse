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
package org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.internal.wizards;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.core.filters.RetentionTimeFilter;
import org.eclipse.chemclipse.model.core.IChromatogramOverview;
import org.eclipse.core.databinding.AggregateValidationStatus;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.conversion.IConverter;
import org.eclipse.core.databinding.observable.value.ComputedValue;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.observable.value.WritableValue;
import org.eclipse.core.databinding.validation.MultiValidator;
import org.eclipse.core.databinding.validation.ValidationStatus;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.databinding.swt.ISWTObservableValue;
import org.eclipse.jface.databinding.swt.WidgetProperties;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CellLabelProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;

public class FilterRetentionTimeWizardPage extends WizardPage implements IFilterWizardPage {

	private DataBindingContext dbc = new DataBindingContext();
	private int filterType;
	private List<int[]> intervals;
	private NumberFormat nf = NumberFormat.getNumberInstance(Locale.US);
	private IObservableValue<Double> observeBegin = new WritableValue<>();
	private IObservableValue<Double> observeFinish = new WritableValue<>();
	private RetentionTimeFilter retentionTimeFilter;
	private TableViewer tableViewer;

	public FilterRetentionTimeWizardPage(RetentionTimeFilter retentionTimeFilter) {
		super("Retention time interval filter");
		setTitle("Retention Time Interval Filter");
		this.filterType = retentionTimeFilter.getFiltrationType();
		this.intervals = retentionTimeFilter.copyInterval();
		this.retentionTimeFilter = retentionTimeFilter;
		observeBegin.setValue(0.0);
		observeFinish.setValue(10.0);
	}

	private void addFilterRetentionTimeInterval() {

		int begin = (int)(observeBegin.getValue() * IChromatogramOverview.MINUTE_CORRELATION_FACTOR);
		int finish = (int)(observeFinish.getValue() * IChromatogramOverview.MINUTE_CORRELATION_FACTOR);
		intervals.add(new int[]{begin, finish});
		setPageComplete(!intervals.isEmpty());
		updateTable();
	}

	private void createColumns() {

		String[] titles = {"Start Time (Mintes)", "End Time (Mintes)"};
		int[] bounds = {50, 50};
		TableViewerColumn col = createTableViewerColumn(titles[0], bounds[0]);
		col.setLabelProvider(new CellLabelProvider() {

			@Override
			public void update(ViewerCell cell) {

				int[] i = (int[])cell.getElement();
				double v = i[0] / IChromatogramOverview.MINUTE_CORRELATION_FACTOR;
				cell.setText(nf.format(v));
			}
		});
		col = createTableViewerColumn(titles[1], bounds[1]);
		col.setLabelProvider(new CellLabelProvider() {

			@Override
			public void update(ViewerCell cell) {

				int[] i = (int[])cell.getElement();
				double v = i[1] / IChromatogramOverview.MINUTE_CORRELATION_FACTOR;
				cell.setText(nf.format(v));
			}
		});
	}

	@Override
	public void createControl(Composite parent) {

		Composite composite = new Composite(parent, SWT.None);
		composite.setLayout(new GridLayout(1, false));
		Button button = new Button(composite, SWT.RADIO);
		GridDataFactory.fillDefaults().grab(true, false).applyTo(button);
		button.setText("Select intervals");
		button.addListener(SWT.Selection, e -> filterType = RetentionTimeFilter.SELECT_INTERVAL);
		button.setSelection(filterType == RetentionTimeFilter.SELECT_INTERVAL);
		button = new Button(composite, SWT.RADIO);
		GridDataFactory.fillDefaults().grab(true, false).applyTo(button);
		button.setText("Deselect intervals");
		button.addListener(SWT.Selection, e -> filterType = RetentionTimeFilter.DESELECT_INTERVAL);
		button.setSelection(filterType == RetentionTimeFilter.DESELECT_INTERVAL);
		Label label = new Label(composite, SWT.None);
		label.setText("Set time interval parametrs:");
		GridDataFactory.fillDefaults().grab(true, false).applyTo(label);
		label = new Label(composite, SWT.None);
		label.setText("Start Time of Interval (minutes)");
		GridDataFactory.fillDefaults().grab(true, false).applyTo(label);
		Text text = new Text(composite, SWT.BORDER);
		GridDataFactory.fillDefaults().grab(true, false).applyTo(text);
		ISWTObservableValue targetObservableValue = WidgetProperties.text(SWT.Modify).observe(text);
		UpdateValueStrategy targetToModel = UpdateValueStrategy.create(IConverter.create(String.class, Double.class, o1 -> {
			try {
				return nf.parse((String)o1).doubleValue();
			} catch(ParseException e1) {
			}
			return null;
		}));
		targetToModel.setBeforeSetValidator(o1 -> {
			if(o1 instanceof Double) {
				Double d = (Double)o1;
				if(d >= 0) {
					return ValidationStatus.ok();
				}
			}
			return ValidationStatus.error("error");
		});
		UpdateValueStrategy modelToTarget = UpdateValueStrategy.create(IConverter.create(Double.class, String.class, o1 -> {
			try {
				return nf.format((o1));
			} catch(NumberFormatException e) {
			}
			return null;
		}));
		dbc.bindValue(targetObservableValue, observeBegin, targetToModel, modelToTarget);
		label = new Label(composite, SWT.None);
		label.setText("End Time of Interval (minutes)");
		GridDataFactory.fillDefaults().grab(true, false).applyTo(label);
		text = new Text(composite, SWT.BORDER);
		GridDataFactory.fillDefaults().grab(true, false).applyTo(text);
		targetObservableValue = WidgetProperties.text(SWT.Modify).observe(text);
		targetToModel = UpdateValueStrategy.create(IConverter.create(String.class, Double.class, o1 -> {
			try {
				return nf.parse((String)o1).doubleValue();
			} catch(ParseException e1) {
			}
			return null;
		}));
		targetToModel.setBeforeSetValidator(o1 -> {
			if(o1 instanceof Double) {
				Double d = (Double)o1;
				if(d >= 0) {
					return ValidationStatus.ok();
				}
			}
			return ValidationStatus.error("error");
		});
		modelToTarget = UpdateValueStrategy.create(IConverter.create(Double.class, String.class, o1 -> {
			try {
				return nf.format((o1));
			} catch(NumberFormatException e) {
			}
			return null;
		}));
		dbc.bindValue(targetObservableValue, observeFinish, targetToModel, modelToTarget);
		dbc.addValidationStatusProvider(new MultiValidator() {

			@Override
			protected IStatus validate() {

				if(observeBegin.getValue() < observeFinish.getValue()) {
					return ValidationStatus.ok();
				} else {
					return ValidationStatus.error("error");
				}
			}
		});
		Composite buttonComposite = new Composite(composite, SWT.None);
		GridDataFactory.fillDefaults().grab(false, false).align(SWT.BEGINNING, SWT.BEGINNING).applyTo(buttonComposite);
		buttonComposite.setLayout(new FillLayout());
		button = new Button(buttonComposite, SWT.PUSH);
		button.setText("Add Interval to Table");
		button.addListener(SWT.Selection, e -> addFilterRetentionTimeInterval());
		// Create a boolean observable, which depends on the ok status
		AggregateValidationStatus aggregateValidationStatus = new AggregateValidationStatus(dbc.getBindings(), AggregateValidationStatus.MAX_SEVERITY);
		IObservableValue<Boolean> isValidationOk = ComputedValue.create(() -> aggregateValidationStatus.getValue().isOK());
		ISWTObservableValue buttonEnabledObservable = WidgetProperties.enabled().observe(button);
		// bind the enablement of the finish button to the validation
		dbc.bindValue(buttonEnabledObservable, isValidationOk);
		button = new Button(buttonComposite, SWT.PUSH);
		button.setText("Remove Selected Intervals");
		button.addListener(SWT.Selection, e -> removeFilterRetentionTimeInterval());
		Table table = new Table(composite, SWT.MULTI | SWT.FULL_SELECTION);
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		GridDataFactory.fillDefaults().grab(true, true).minSize(200, 300).applyTo(table);
		tableViewer = new TableViewer(table);
		createColumns();
		tableViewer.setContentProvider(new ArrayContentProvider());
		tableViewer.setInput(intervals);
		updateTable();
		setControl(composite);
	}

	private TableViewerColumn createTableViewerColumn(String title, int bound) {

		final TableViewerColumn viewerColumn = new TableViewerColumn(tableViewer, SWT.NONE);
		final TableColumn column = viewerColumn.getColumn();
		column.setText(title);
		column.setWidth(bound);
		column.setResizable(true);
		column.setMoveable(true);
		return viewerColumn;
	}

	private void removeFilterRetentionTimeInterval() {

		IStructuredSelection selection = (IStructuredSelection)tableViewer.getSelection();
		Iterator<?> it = selection.iterator();
		while(it.hasNext()) {
			int[] sel = (int[])it.next();
			intervals.remove(sel);
		}
		setPageComplete(!intervals.isEmpty());
		updateTable();
	}

	@Override
	public void update() {

		List<int[]> intervals = this.retentionTimeFilter.getIntervals();
		intervals.clear();
		intervals.addAll(this.intervals);
		retentionTimeFilter.setFiltrationType(filterType);
	}

	private void updateTable() {

		tableViewer.refresh();
		for(TableColumn column : tableViewer.getTable().getColumns()) {
			column.pack();
		}
	}
}
