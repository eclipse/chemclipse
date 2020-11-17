/*******************************************************************************
 * Copyright (c) 2020 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.swt.ui.components;

import java.util.Map;

import org.eclipse.chemclipse.rcp.ui.icons.core.ApplicationImageFactory;
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImage;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class DataMapSupportUI extends Composite {

	public static final String HEADER_ENTRY = "Header Entry";
	//
	private Text textKey;
	private Text textValue;
	private Button buttonAdd;
	//
	private IHeaderListener headerListener;
	private Map<String, String> dataMap;

	public DataMapSupportUI(Composite parent, int style) {

		super(parent, style);
		createControl();
	}

	public void reset() {

		textKey.setText("");
		textValue.setText("");
		buttonAdd.setEnabled(false);
	}

	public void setHeaderListener(IHeaderListener headerListener) {

		this.headerListener = headerListener;
	}

	public void setInput(Map<String, String> dataMap) {

		this.dataMap = dataMap;
		updateWidget();
	}

	private void createControl() {

		setLayout(new FillLayout());
		//
		Composite composite = new Composite(this, SWT.NONE);
		GridLayout gridLayout = new GridLayout(3, false);
		gridLayout.marginLeft = 0;
		gridLayout.marginRight = 0;
		composite.setLayout(gridLayout);
		//
		textKey = createTextKey(composite);
		textValue = createTextValue(composite);
		buttonAdd = createButtonAdd(composite);
		//
		updateWidget();
	}

	private Text createTextKey(Composite parent) {

		Text text = new Text(parent, SWT.BORDER);
		text.setText("");
		text.setToolTipText("Type in a header key.");
		text.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		text.addKeyListener(new KeyAdapter() {

			@Override
			public void keyReleased(KeyEvent e) {

				updateWidget();
			}
		});
		//
		return text;
	}

	private Text createTextValue(Composite parent) {

		Text text = new Text(parent, SWT.BORDER);
		text.setText("");
		text.setToolTipText("Type in a header value.");
		text.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		text.addKeyListener(new KeyAdapter() {

			@Override
			public void keyReleased(KeyEvent e) {

				updateWidget();
			}
		});
		//
		return text;
	}

	private Button createButtonAdd(Composite parent) {

		Button button = new Button(parent, SWT.PUSH);
		button.setText("");
		button.setToolTipText("Add the entry.");
		button.setEnabled(false);
		button.setImage(ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_ADD, IApplicationImage.SIZE_16x16));
		button.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				addEntry(e.display.getActiveShell());
			}
		});
		//
		return button;
	}

	private void addEntry(Shell shell) {

		if(dataMap != null) {
			/*
			 * Get the values.
			 */
			String key = textKey.getText().trim();
			String value = textValue.getText().trim();
			//
			if("".equals(key)) {
				MessageDialog.openError(shell, HEADER_ENTRY, "The header key must be not empty.");
			} else if(dataMap.containsKey(key)) {
				MessageDialog.openError(shell, HEADER_ENTRY, "The header key already exists.");
			} else if("".equals(value)) {
				MessageDialog.openError(shell, HEADER_ENTRY, "The header value must be not empty.");
			} else {
				dataMap.put(key, value);
				reset();
				fireUpdate();
			}
		}
	}

	private void updateWidget() {

		textKey.setEnabled(dataMap != null);
		textValue.setEnabled(dataMap != null);
		//
		boolean enabled = false;
		if(dataMap != null) {
			String key = textKey.getText().trim();
			if(!key.isEmpty()) {
				String value = textValue.getText().trim();
				if(!value.isEmpty()) {
					enabled = true;
				}
			}
		}
		//
		buttonAdd.setEnabled(enabled);
	}

	private void fireUpdate() {

		if(dataMap != null) {
			if(headerListener != null) {
				headerListener.update();
			}
		}
	}
}