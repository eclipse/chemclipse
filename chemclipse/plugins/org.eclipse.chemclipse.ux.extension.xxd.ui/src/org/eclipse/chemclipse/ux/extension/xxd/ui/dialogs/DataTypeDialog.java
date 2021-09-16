/*******************************************************************************
 * Copyright (c) 2021 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.dialogs;

import org.eclipse.chemclipse.model.types.DataType;
import org.eclipse.chemclipse.support.ui.provider.AbstractLabelProvider;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

public class DataTypeDialog extends Dialog {

	private static final int WIDTH = 250;
	private static final int HEIGHT = 150;
	//
	private ComboViewer comboViewer;
	//
	private DataType dataType;
	private DataType[] dataTypes;

	public DataTypeDialog(Shell parentShell, DataType[] dataTypes) {

		super(parentShell);
		this.dataTypes = dataTypes;
	}

	public DataType getDataType() {

		return dataType;
	}

	@Override
	protected void configureShell(Shell newShell) {

		super.configureShell(newShell);
		newShell.setText("Data Type");
	}

	@Override
	protected boolean isResizable() {

		return true;
	}

	@Override
	protected Point getInitialSize() {

		return new Point(WIDTH, HEIGHT);
	}

	@Override
	protected void createButtonsForButtonBar(Composite parent) {

		super.createButtonsForButtonBar(parent);
		getButton(IDialogConstants.OK_ID).setEnabled(false); // Users must make a selection.
	}

	@Override
	protected Control createDialogArea(Composite parent) {

		Composite composite = (Composite)super.createDialogArea(parent);
		GridLayout layout = new GridLayout(1, false);
		layout.marginRight = 10;
		layout.marginLeft = 10;
		composite.setLayout(layout);
		//
		createLabel(composite);
		comboViewer = createComboViewer(composite);
		//
		return composite;
	}

	private void createLabel(Composite parent) {

		Label label = new Label(parent, SWT.NONE);
		label.setText("Data Type:");
	}

	private ComboViewer createComboViewer(Composite parent) {

		ComboViewer comboViewer = new ComboViewer(parent, SWT.READ_ONLY);
		Combo combo = comboViewer.getCombo();
		comboViewer.setContentProvider(new ArrayContentProvider());
		comboViewer.setLabelProvider(new AbstractLabelProvider() {

			@Override
			public String getText(Object element) {

				if(element instanceof DataType) {
					return ((DataType)element).name();
				}
				//
				return null;
			}
		});
		//
		combo.setToolTipText("Select a data type.");
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.widthHint = 150;
		combo.setLayoutData(gridData);
		combo.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				Object object = comboViewer.getStructuredSelection().getFirstElement();
				if(object instanceof DataType) {
					dataType = (DataType)object;
					validate();
				}
			}
		});
		//
		comboViewer.setInput(dataTypes);
		return comboViewer;
	}

	private void validate() {

		boolean valid = comboViewer.getStructuredSelection().getFirstElement() != null;
		getButton(IDialogConstants.OK_ID).setEnabled(valid);
	}
}