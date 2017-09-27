/*******************************************************************************
 * Copyright (c) 2017 Jan Holy.
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
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.core.filters.RetentionTime2Filter;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.IRetentionTime;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CellLabelProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;

public class FilterRetentionTime2WizardPage extends WizardPage implements IFilterWizardPage {

	private NumberFormat nf = NumberFormat.getNumberInstance(Locale.US);
	private RetentionTime2Filter retentionTimeFilter;
	private List<IRetentionTime> retentionTimes;
	private TableViewer tableViewer;

	public FilterRetentionTime2WizardPage(RetentionTime2Filter retentionTimeFilter) {
		super("Retention time filter");
		setTitle("Retention Time Filter");
		retentionTimes = IRetentionTime.copy(retentionTimeFilter.getRetentionTimes());
		this.retentionTimeFilter = retentionTimeFilter;
	}

	private void createColumns() {

		String[] titles = {"Retention Time (Minutes)", "Description"};
		int[] bounds = {150, 150};
		TableViewerColumn col = createTableViewerColumn(titles[0], bounds[0]);
		col.setLabelProvider(new CellLabelProvider() {

			@Override
			public void update(ViewerCell cell) {

				IRetentionTime retentionTime = (IRetentionTime)cell.getElement();
				cell.setText(nf.format(retentionTime.getRetentionTimeMinutes()));
			}
		});
		col = createTableViewerColumn(titles[1], bounds[1]);
		col.setLabelProvider(new CellLabelProvider() {

			@Override
			public void update(ViewerCell cell) {

				IRetentionTime retentionTime = (IRetentionTime)cell.getElement();
				String description = retentionTime.getDescription();
				if(description != null) {
					cell.setText(retentionTime.getDescription());
				} else {
					cell.setText("");
				}
			}
		});
	}

	@Override
	public void createControl(Composite parent) {

		Composite composite = new Composite(parent, SWT.None);
		composite.setLayout(new GridLayout(1, false));
		Table table = new Table(composite, SWT.MULTI | SWT.FULL_SELECTION);
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		GridDataFactory.fillDefaults().grab(true, true).applyTo(table);
		tableViewer = new TableViewer(table);
		tableViewer.setContentProvider(new ArrayContentProvider());
		createColumns();
		tableViewer.setInput(retentionTimes);
		Button button = new Button(composite, SWT.PUSH);
		button.setText("Remove Selected Retention Times");
		button.addListener(SWT.Selection, e -> remove());
		GridDataFactory.fillDefaults().grab(true, false).applyTo(button);
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

	private void remove() {

		IStructuredSelection selection = (IStructuredSelection)tableViewer.getSelection();
		Iterator<?> it = selection.iterator();
		while(it.hasNext()) {
			retentionTimes.remove(it.next());
		}
		updateTable();
	}

	@Override
	public void update() {

		List<IRetentionTime> retentionTimes = retentionTimeFilter.getRetentionTimes();
		retentionTimes.clear();
		retentionTimes.addAll(this.retentionTimes);
	}

	private void updateTable() {

		tableViewer.refresh();
		for(TableColumn column : tableViewer.getTable().getColumns()) {
			column.pack();
		}
	}
}
