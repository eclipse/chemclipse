/*******************************************************************************
 * Copyright (c) 2024 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.model.ui.swt;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import org.eclipse.chemclipse.model.core.FileHeaderData;
import org.eclipse.chemclipse.model.core.support.HeaderField;
import org.eclipse.chemclipse.model.support.FileHeaderDataSupport;
import org.eclipse.chemclipse.support.ui.provider.AbstractLabelProvider;
import org.eclipse.chemclipse.support.ui.swt.EnhancedComboViewer;
import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.core.databinding.validation.ValidationStatus;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.jface.fieldassist.FieldDecorationRegistry;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Text;

public class FileHeaderDataEditor extends Composite {

	private AtomicReference<ComboViewer> comboViewerControl = new AtomicReference<>();
	private AtomicReference<Text> textControl = new AtomicReference<>();
	private AtomicReference<Spinner> spinnerControl = new AtomicReference<>();
	//
	private FileHeaderData fileHeaderData = new FileHeaderData();
	private List<Listener> listeners = new ArrayList<>();
	private String message = null;

	public FileHeaderDataEditor(Composite parent, int style) {

		super(parent, style);
		createControl();
	}

	public void load(String items) {

		FileHeaderDataSupport.load(fileHeaderData, items);
		updateInput();
	}

	public String save() {

		return FileHeaderDataSupport.save(fileHeaderData);
	}

	public String getMessage() {

		return message;
	}

	public void addChangeListener(Listener listener) {

		Control control = textControl.get();
		control.addListener(SWT.Selection, listener);
		control.addListener(SWT.KeyUp, listener);
		control.addListener(SWT.MouseUp, listener);
	}

	private void createControl() {

		GridLayout gridLayout = new GridLayout(3, false);
		gridLayout.marginWidth = 0;
		gridLayout.marginHeight = 0;
		setLayout(gridLayout);
		//
		createComboViewerHeaderField(this);
		createTextRegularExpression(this);
		createSpinnerGroupIndex(this);
		//
		initialize();
	}

	private void initialize() {

		updateInput();
	}

	private void createComboViewerHeaderField(Composite parent) {

		ComboViewer comboViewer = new EnhancedComboViewer(parent, SWT.READ_ONLY);
		Combo combo = comboViewer.getCombo();
		comboViewer.setContentProvider(ArrayContentProvider.getInstance());
		comboViewer.setLabelProvider(new AbstractLabelProvider() {

			@Override
			public String getText(Object element) {

				if(element instanceof HeaderField headerField) {
					return headerField.label();
				} else {
					return null;
				}
			}
		});
		/*
		 * Select the item.
		 */
		combo.setToolTipText("Header Field");
		GridData gridData = new GridData();
		gridData.widthHint = 150;
		combo.setLayoutData(gridData);
		combo.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				Object object = comboViewer.getStructuredSelection().getFirstElement();
				if(object instanceof HeaderField headerField) {
					fileHeaderData.setHeaderField(headerField);
				}
			}
		});
		//
		comboViewer.setInput(HeaderField.values());
		//
		comboViewerControl.set(comboViewer);
	}

	private void createTextRegularExpression(Composite parent) {

		Text text = new Text(parent, SWT.BORDER);
		text.setText("");
		text.setToolTipText("Regular Expression");
		text.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		//
		IValidator<String> validator = new IValidator<>() {

			@Override
			public IStatus validate(String value) {

				String message = null;
				if(value.isBlank()) {
					message = "The regular expression must not be empty.";
				} else {
					String delimiter = FileHeaderDataSupport.VALUE_DELIMITER;
					if(value.contains(delimiter)) {
						message = "The regular expression must not contain: '" + delimiter + "'.";
					}
				}
				//
				if(message != null) {
					return ValidationStatus.error(message);
				} else {
					return ValidationStatus.ok();
				}
			}
		};
		ControlDecoration controlDecoration = new ControlDecoration(text, SWT.LEFT | SWT.TOP);
		text.addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(ModifyEvent e) {

				if(validate(validator, controlDecoration, text)) {
					fileHeaderData.setRegularExpression(text.getText().trim());
				}
			}
		});
		//
		textControl.set(text);
	}

	private void createSpinnerGroupIndex(Composite parent) {

		Spinner spinner = new Spinner(parent, SWT.BORDER);
		spinner.setMinimum(1);
		spinner.setMaximum(Integer.MAX_VALUE);
		spinner.setPageIncrement(1);
		spinner.setSelection(1);
		spinner.setToolTipText("Select the group index of the value to be extracted.");
		GridData gridData = new GridData();
		gridData.widthHint = 80;
		spinner.setLayoutData(gridData);
		//
		spinner.addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(ModifyEvent e) {

				fileHeaderData.setGroupIndex(spinner.getSelection());
			}
		});
		//
		spinnerControl.set(spinner);
	}

	private boolean validate(IValidator<String> validator, ControlDecoration controlDecoration, Text text) {

		IStatus status = validator.validate(text.getText().trim());
		if(status.isOK()) {
			message = null;
			controlDecoration.hide();
			return true;
		} else {
			message = status.getMessage();
			controlDecoration.setImage(FieldDecorationRegistry.getDefault().getFieldDecoration(FieldDecorationRegistry.DEC_CONTENT_PROPOSAL).getImage());
			controlDecoration.showHoverText(message);
			controlDecoration.show();
			return false;
		}
	}

	private void updateInput() {

		comboViewerControl.get().setSelection(new StructuredSelection(fileHeaderData.getHeaderField()));
		textControl.get().setText(fileHeaderData.getRegularExpression());
		spinnerControl.get().setSelection(fileHeaderData.getGroupIndex());
		//
		for(Listener listener : listeners) {
			listener.handleEvent(new Event());
		}
	}
}