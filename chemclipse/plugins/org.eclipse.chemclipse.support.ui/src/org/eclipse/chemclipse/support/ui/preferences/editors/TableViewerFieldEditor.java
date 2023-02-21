/*******************************************************************************
 * Copyright (c) 2018, 2023 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Jan Holy - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.support.ui.preferences.editors;

import java.util.Collections;
import java.util.List;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.preference.FieldEditor;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;

public abstract class TableViewerFieldEditor<Value> extends FieldEditor {

	private TableViewer tableViewer;
	private Button upButton;
	private Button downButton;
	private Button newButton;
	private Button removeButton;
	private String[] columnNames;
	private int[] columnWidth;
	// sorting
	private int sortColumn = 0;
	private int sortDirection = 0;
	//

	protected TableViewerFieldEditor(String name, String labelText, String[] columnNames, int[] columnWidth, Composite parent) {

		this.columnNames = columnNames;
		this.columnWidth = columnWidth;
		init(name, labelText);
		createControl(parent);
	}

	protected abstract String createSavePreferences(List<Value> values);

	protected abstract List<Value> parseSavePreferences(String data);

	protected abstract String convertColumnValue(Value value, int indexColumn);

	protected abstract List<Value> getNewInputObject();

	protected abstract int compareValue(Value value1, Value value2, int indexColumn);

	@Override
	protected void adjustForNumColumns(int numColumns) {

		Control control = getLabelControl();
		((GridData)control.getLayoutData()).horizontalSpan = numColumns;
		((GridData)tableViewer.getTable().getLayoutData()).horizontalSpan = numColumns - 1;
	}

	@Override
	protected void doFillIntoGrid(Composite parent, int numColumns) {

		Control control = getLabelControl(parent);
		GridData gd = new GridData();
		gd.horizontalSpan = numColumns;
		control.setLayoutData(gd);
		Composite composite = new Composite(parent, SWT.NONE);
		GridData gridData = new GridData(SWT.FILL, SWT.BEGINNING, true, false);
		gridData.horizontalSpan = 2;
		gridData.widthHint = 500;
		gridData.heightHint = 200;
		composite.setLayoutData(gridData);
		composite.setLayout(new GridLayout(2, false));
		tableViewer = getTableControl(composite);
		Table table = tableViewer.getTable();
		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.verticalAlignment = GridData.FILL;
		gd.horizontalSpan = numColumns - 1;
		gd.grabExcessHorizontalSpace = true;
		gd.grabExcessVerticalSpace = true;
		table.setLayoutData(gd);
		Composite buttonBox = getButtonControl(composite);
		gd = new GridData();
		gd.verticalAlignment = GridData.BEGINNING;
		buttonBox.setLayoutData(gd);
	}

	private SelectionAdapter getSelectionAdapter(final TableColumn column, final int index) {

		return new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				if(sortColumn == index) {
					if(tableViewer.getTable().getSortDirection() == SWT.NONE) {
						tableViewer.getTable().setSortDirection(SWT.DOWN);
						sortDirection = 1;
					} else {
						if(tableViewer.getTable().getSortDirection() == SWT.DOWN) {
							tableViewer.getTable().setSortDirection(SWT.UP);
							sortDirection = -1;
						} else {
							tableViewer.getTable().setSortDirection(SWT.NONE);
							sortDirection = 0;
						}
					}
				} else {
					tableViewer.getTable().setSortDirection(SWT.DOWN);
				}
				sortColumn = index;
				tableViewer.getTable().setSortColumn(column);
				update();
			}
		};
	}

	private void moveUp() {

		int index = tableViewer.getTable().getSelectionIndex();
		Value movedElement = getInput().remove(index);
		int newIndex = Math.min(index - 1, 0);
		getInput().add(newIndex, movedElement);
		update();
	}

	private void moveDown() {

		int index = tableViewer.getTable().getSelectionIndex();
		Value movedElement = getInput().remove(index);
		int newIndex = Math.max(index + 1, getInput().size());
		getInput().add(newIndex, movedElement);
		update();
	}

	private void addItem() {

		List<Value> values = getNewInputObject();
		for(Value value : values) {
			/*
			 * can not insert duplicate value because selection does not work
			 */
			if(!getInput().contains(value)) {
				getInput().add(value);
				update();
			}
		}
	}

	protected Shell getShell() {

		return newButton.getShell();
	}

	private void removeItem() {

		IStructuredSelection selection = (IStructuredSelection)tableViewer.getSelection();
		@SuppressWarnings("unchecked")
		List<Value> selections = selection.toList();
		for(Value value : selections) {
			getInput().remove(value);
		}
		update();
	}

	private Composite getButtonControl(Composite parent) {

		Composite box = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout();
		layout.marginWidth = 0;
		box.setLayout(layout);
		GridLayoutFactory.fillDefaults().applyTo(box);
		newButton = createButton(box, "New");
		newButton.addListener(SWT.Selection, e -> addItem());
		removeButton = createButton(box, "Remove");
		removeButton.addListener(SWT.Selection, e -> removeItem());
		upButton = createButton(box, "Up");
		upButton.addListener(SWT.Selection, e -> moveUp());
		downButton = createButton(box, "Down");
		downButton.addListener(SWT.Selection, e -> moveDown());
		return box;
	}

	private Button createButton(Composite box, String text) {

		Button button = new Button(box, SWT.PUSH);
		button.setText(text);
		GridData data = new GridData(GridData.FILL_HORIZONTAL);
		int widthHint = convertHorizontalDLUsToPixels(button, IDialogConstants.BUTTON_WIDTH);
		data.widthHint = Math.max(widthHint, button.computeSize(SWT.DEFAULT, SWT.DEFAULT, true).x);
		button.setLayoutData(data);
		return button;
	}

	private TableViewerColumn createColumn(TableViewer tableViewer, String name, int width, int order) {

		TableViewerColumn culumn = new TableViewerColumn(tableViewer, SWT.NONE);
		culumn.getColumn().setWidth(width);
		culumn.getColumn().setText(name);
		culumn.setLabelProvider(new ColumnLabelProvider() {

			@SuppressWarnings("unchecked")
			@Override
			public String getText(Object element) {

				return convertColumnValue((Value)element, order);
			}
		});
		return culumn;
	}

	private TableViewer getTableControl(Composite parent) {

		TableViewer tableViewer = new TableViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.BORDER);
		tableViewer.setContentProvider(ArrayContentProvider.getInstance());
		Table table = tableViewer.getTable();
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		for(int i = 0; i < columnNames.length; i++) {
			TableViewerColumn column = createColumn(tableViewer, columnNames[i], columnWidth[i], i);
			column.getColumn().addSelectionListener(getSelectionAdapter(column.getColumn(), i));
		}
		tableViewer.getTable().addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				update();
			}
		});
		tableViewer.setComparator(new ViewerComparator() {

			@SuppressWarnings("unchecked")
			@Override
			public int compare(Viewer viewer, Object e1, Object e2) {

				return sortDirection * compareValue((Value)e1, (Value)e2, sortColumn);
			}
		});
		return tableViewer;
	}

	@Override
	protected void doLoad() {

		String preferences = getPreferenceStore().getString(getPreferenceName());
		List<Value> v = parseSavePreferences(preferences);
		tableViewer.setInput(v);
		update();
	}

	@Override
	protected void doLoadDefault() {

		doLoad();
	}

	private void update() {

		int selectionIndex = tableViewer.getTable().getSelectionIndex();
		newButton.setEnabled(true);
		upButton.setEnabled(selectionIndex != -1 && selectionIndex > 0 && sortDirection == 0);
		downButton.setEnabled(selectionIndex != -1 && selectionIndex < tableViewer.getTable().getItemCount() - 1 && sortDirection == 0);
		removeButton.setEnabled(selectionIndex != -1);
		tableViewer.getTable().update();
		tableViewer.refresh();
	}

	@Override
	protected void doStore() {

		List<Value> v = getInput();
		String store = createSavePreferences(v);
		getPreferenceStore().setValue(getPreferenceName(), store);
	}

	public List<Value> getDataTable() {

		return Collections.unmodifiableList(getInput());
	}

	@SuppressWarnings("unchecked")
	private List<Value> getInput() {

		return (List<Value>)tableViewer.getInput();
	}

	@Override
	public int getNumberOfControls() {

		return 2;
	}
}
