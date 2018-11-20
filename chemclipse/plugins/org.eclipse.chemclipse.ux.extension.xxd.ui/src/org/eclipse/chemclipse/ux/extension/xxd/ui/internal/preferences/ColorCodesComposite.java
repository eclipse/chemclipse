/*******************************************************************************
 * Copyright (c) 2018 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.internal.preferences;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.chemclipse.support.ui.provider.ListContentProvider;
import org.eclipse.chemclipse.ux.extension.xxd.ui.internal.provider.ColorCodeLabelProvider;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.FontMetrics;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;;

public class ColorCodesComposite {

	private ColorCodes colorCodes = new ColorCodes();
	private Button addButton;
	private Button editButton;
	private Button removeButton;
	private Label label;
	private TableViewer tableViewer;
	private String[] titles = {"Name", "Color"};
	private int bounds[] = {100, 100};
	private FontMetrics fontMetrics;
	private static final int WIDTH_HINT = 400;

	public ColorCodesComposite() {
		colorCodes.load();
	}

	public Control createContents(Composite parent) {

		setFontMetrics(parent);
		Composite composite = new Composite(parent, SWT.NONE);
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 2;
		gridLayout.marginWidth = 0;
		gridLayout.marginHeight = 0;
		composite.setLayout(gridLayout);
		GridData gridData = new GridData(GridData.FILL_BOTH);
		gridData.widthHint = WIDTH_HINT;
		composite.setLayoutData(gridData);
		//
		label = new Label(composite, SWT.LEFT);
		label.setText("Add/Remove Color Codes");
		gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.horizontalSpan = 2;
		label.setLayoutData(gridData);
		//
		Composite tableComposite = new Composite(composite, SWT.NONE);
		tableComposite.setLayout(new FillLayout());
		gridData = new GridData(SWT.FILL, SWT.FILL, true, true);
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = true;
		tableComposite.setLayoutData(gridData);
		tableViewer = new TableViewer(tableComposite, SWT.BORDER | SWT.FULL_SELECTION | SWT.MULTI);
		createColumns(tableViewer);
		tableViewer.setContentProvider(new ListContentProvider());
		tableViewer.setLabelProvider(new ColorCodeLabelProvider());
		setTableViewerInput();
		/*
		 * Create the buttons
		 */
		createButtonGroup(composite);
		return composite;
	}

	/**
	 * Creates the columns for the peak viewer table.
	 * 
	 * @param tableViewer
	 */
	private void createColumns(final TableViewer tableViewer) {

		/*
		 * Set the titles and bounds.
		 */
		for(int i = 0; i < titles.length; i++) {
			/*
			 * Column sort.
			 */
			final TableViewerColumn tableViewerColumn = new TableViewerColumn(tableViewer, SWT.NONE);
			final TableColumn tableColumn = tableViewerColumn.getColumn();
			tableColumn.setText(titles[i]);
			tableColumn.setWidth(bounds[i]);
			tableColumn.setResizable(true);
			tableColumn.setMoveable(true);
		}
		/*
		 * Set header and lines visible.
		 */
		Table table = tableViewer.getTable();
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
	}

	private void createButtonGroup(Composite parent) {

		Composite buttonComposite = new Composite(parent, SWT.NULL);
		GridLayout gridLayout = new GridLayout();
		gridLayout.marginWidth = 0;
		gridLayout.marginHeight = 0;
		buttonComposite.setLayout(gridLayout);
		GridData gridData = new GridData();
		gridData.verticalAlignment = GridData.FILL;
		gridData.horizontalAlignment = GridData.FILL;
		buttonComposite.setLayoutData(gridData);
		/*
		 * Add
		 */
		addButton = new Button(buttonComposite, SWT.PUSH);
		addButton.setText("Add");
		addButton.addSelectionListener(new SelectionAdapter() {

			public void widgetSelected(SelectionEvent e) {

				Shell shell = Display.getCurrent().getActiveShell();
				ColorCodeDialog dialog = new ColorCodeDialog(shell);
				if(IDialogConstants.OK_ID == dialog.open()) {
					ColorCode colorCode = dialog.getColorCode();
					if(colorCode != null) {
						colorCodes.add(colorCode);
						setTableViewerInput();
					}
				}
			}
		});
		setButtonLayoutData(addButton);
		/*
		 * Edit
		 */
		editButton = new Button(buttonComposite, SWT.PUSH);
		editButton.setText("Edit");
		editButton.addSelectionListener(new SelectionAdapter() {

			public void widgetSelected(SelectionEvent e) {

				IStructuredSelection structuredSelection = (IStructuredSelection)tableViewer.getSelection();
				Object object = structuredSelection.getFirstElement();
				if(object instanceof ColorCode) {
					ColorCode colorCode = (ColorCode)object;
					Shell shell = Display.getCurrent().getActiveShell();
					ColorCodeDialog dialog = new ColorCodeDialog(shell, colorCode);
					if(IDialogConstants.OK_ID == dialog.open()) {
						colorCode = dialog.getColorCode();
						if(colorCode != null) {
							colorCodes.add(colorCode);
							setTableViewerInput();
						}
					}
				}
			}
		});
		setButtonLayoutData(editButton);
		/*
		 * Remove
		 */
		removeButton = new Button(buttonComposite, SWT.PUSH);
		removeButton.setText("Remove");
		removeButton.addSelectionListener(new SelectionAdapter() {

			public void widgetSelected(SelectionEvent e) {

				List<String> removeKeys = new ArrayList<>();
				IStructuredSelection structuredSelection = (IStructuredSelection)tableViewer.getSelection();
				for(Object object : structuredSelection.toArray()) {
					if(object instanceof ColorCode) {
						ColorCode colorCode = (ColorCode)object;
						removeKeys.add(colorCode.getName());
					}
				}
				/*
				 * Remove the objects.
				 */
				for(String key : removeKeys) {
					colorCodes.remove(key);
				}
				setTableViewerInput();
			}
		});
		setButtonLayoutData(removeButton);
	}

	private void setTableViewerInput() {

		tableViewer.setInput(colorCodes);
	}

	private GridData setButtonLayoutData(Button button) {

		GridData data = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		int widthHint = Dialog.convertHorizontalDLUsToPixels(fontMetrics, IDialogConstants.BUTTON_WIDTH);
		data.widthHint = Math.max(widthHint, button.computeSize(SWT.DEFAULT, SWT.DEFAULT, true).x);
		button.setLayoutData(data);
		return data;
	}

	private void setFontMetrics(Composite composite) {

		GC gc = new GC(composite);
		gc.setFont(composite.getFont());
		fontMetrics = gc.getFontMetrics();
		gc.dispose();
	}

	public boolean performOk() {

		return colorCodes.save();
	}
}
