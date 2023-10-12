/*******************************************************************************
 * Copyright (c) 2017, 2023 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Jan Holy - initial API and implementation
 * Philip Wenig - refactoring
 *******************************************************************************/
package org.eclipse.chemclipse.xxd.process.supplier.pca.ui.internal.wizards;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.chemclipse.model.statistics.IVariable;
import org.eclipse.chemclipse.xxd.process.supplier.pca.core.filters.RetentionTime2Filter;
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

	private RetentionTime2Filter retentionTimeFilter;
	private TableViewer tableViewer;
	private List<IVariable> variables;

	public FilterRetentionTime2WizardPage(RetentionTime2Filter retentionTimeFilter) {

		super("Retention time filter");
		setTitle("Retention Time Filter");
		variables = new ArrayList<>(retentionTimeFilter.getVariables());
		this.retentionTimeFilter = retentionTimeFilter;
	}

	private void createColumns() {

		String[] titles = {"Value", "Description"};
		int[] bounds = {150, 150};
		TableViewerColumn col = createTableViewerColumn(titles[0], bounds[0]);
		col.setLabelProvider(new CellLabelProvider() {

			@Override
			public void update(ViewerCell cell) {

				IVariable value = (IVariable)cell.getElement();
				cell.setText(value.getValue());
			}
		});
		col = createTableViewerColumn(titles[1], bounds[1]);
		col.setLabelProvider(new CellLabelProvider() {

			@Override
			public void update(ViewerCell cell) {

				IVariable retentionTime = (IVariable)cell.getElement();
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
		tableViewer.setInput(variables);
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
			variables.remove(it.next());
		}
		updateTable();
	}

	@Override
	public void update() {

		List<IVariable> variables = retentionTimeFilter.getVariables();
		variables.clear();
		variables.addAll(this.variables);
	}

	private void updateTable() {

		tableViewer.refresh();
		for(TableColumn column : tableViewer.getTable().getColumns()) {
			column.pack();
		}
	}
}