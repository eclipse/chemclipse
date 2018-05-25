/*******************************************************************************
 * Copyright (c) 2018 Lablicate GmbH.
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
import org.eclipse.jface.layout.GridDataFactory;
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
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;

public abstract class TableViewerFieldEditor<Value> extends FieldEditor {

	private TableViewer tableViewer;
	private Composite buttonBox;
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
		init(name, labelText);
		this.columnNames = columnNames;
		this.columnWidth = columnWidth;
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

		GridLayout layout = new GridLayout();
		layout.numColumns = getNumberOfControls();
		layout.marginWidth = 0;
		layout.marginHeight = 0;
		layout.horizontalSpacing = HORIZONTAL_GAP;
		parent.setLayout(layout);
		Label control = getLabelControl(parent);
		GridDataFactory.swtDefaults().span(numColumns, 1).applyTo(control);
		tableViewer = getTableControl(parent);
		for(int i = 0; i < columnNames.length; i++) {
			TableViewerColumn column = createColumn(columnNames[i], columnWidth[i], i);
			column.getColumn().addSelectionListener(getSelectionAdapter(column.getColumn(), i));
		}
		GridData gd = new GridData();
		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.verticalAlignment = GridData.FILL;
		gd.horizontalSpan = numColumns - 1;
		gd.grabExcessHorizontalSpace = true;
		tableViewer.getTable().setLayoutData(gd);
		tableViewer.getTable().addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				update();
			}
		});
		buttonBox = getButtonControl(parent);
		tableViewer.setComparator(new ViewerComparator() {

			@Override
			public int compare(Viewer viewer, Object e1, Object e2) {

				return sortDirection * compareValue((Value)e1, (Value)e2, sortColumn);
			}
		});
		GridDataFactory.fillDefaults().align(SWT.FILL, SWT.BEGINNING).applyTo(buttonBox);
	}

	private SelectionAdapter getSelectionAdapter(final TableColumn column, final int index) {

		SelectionAdapter selectionAdapter = new SelectionAdapter() {

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
		return selectionAdapter;
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
		List<Value> selections = selection.toList();
		for(Value value : selections) {
			getInput().remove(value);
		}
		update();
	}

	private Composite getButtonControl(Composite parent) {

		Composite box = new Composite(parent, SWT.NONE);
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
		button.setEnabled(false);
		int widthHint = Math.max(convertHorizontalDLUsToPixels(button, IDialogConstants.BUTTON_WIDTH), button.computeSize(SWT.DEFAULT, SWT.DEFAULT, true).x);
		GridDataFactory.swtDefaults().align(SWT.FILL, SWT.CENTER).hint(widthHint, SWT.DEFAULT).applyTo(button);
		return button;
	}

	private TableViewerColumn createColumn(String name, int width, int order) {

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
		return tableViewer;
	}

	@Override
	protected void doLoad() {

		String preferences = getPreferenceStore().getDefaultString(getPreferenceName());
		List<Value> v = parseSavePreferences(preferences);
		tableViewer.setInput(v);
		update();
	}

	@Override
	protected void doLoadDefault() {

		String preferences = getPreferenceStore().getString(getPreferenceName());
		List<Value> v = parseSavePreferences(preferences);
		tableViewer.setInput(v);
		update();
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
