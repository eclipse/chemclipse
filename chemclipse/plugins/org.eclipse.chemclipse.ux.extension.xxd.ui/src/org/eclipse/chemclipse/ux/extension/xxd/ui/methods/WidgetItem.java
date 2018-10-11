/*******************************************************************************
 * Copyright (c) 2018 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.methods;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.chemclipse.support.ui.provider.AbstractLabelProvider;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.jface.fieldassist.FieldDecorationRegistry;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;

public class WidgetItem {

	private InputValue inputValue;
	private InputValidator inputValidator;
	private ControlDecoration controlDecoration;
	private Control control;

	public WidgetItem(InputValue inputValue) {
		this.inputValue = inputValue;
	}

	public InputValue getInputValue() {

		return inputValue;
	}

	public InputValidator getInputValidator() {

		return inputValidator;
	}

	public ControlDecoration getControlDecoration() {

		return controlDecoration;
	}

	public Control getControl() {

		return control;
	}

	public void initializeControl(Composite parent) throws Exception {

		control = createControl(parent);
		inputValidator = new InputValidator(inputValue);
		controlDecoration = new ControlDecoration(control, SWT.LEFT | SWT.TOP);
	}

	public String validate() {

		String message = null;
		/*
		 * Get the input.
		 */
		String input = "";
		if(control instanceof Text) {
			input = ((Text)control).getText().trim();
		} else if(control instanceof Button) {
			input = Boolean.toString(((Button)control).getSelection());
		} else if(control instanceof Combo) {
			input = ((Combo)control).getText().trim();
		}
		/*
		 * Validate
		 */
		IStatus status = inputValidator.validate(input);
		if(status.isOK()) {
			controlDecoration.hide();
		} else {
			message = status.getMessage();
			controlDecoration.setImage(FieldDecorationRegistry.getDefault().getFieldDecoration(FieldDecorationRegistry.DEC_CONTENT_PROPOSAL).getImage());
			controlDecoration.showHoverText(status.getMessage());
			controlDecoration.show();
		}
		//
		return message;
	}

	public Object getValue() {

		Object value = null;
		Class<?> rawType = inputValue.getRawType();
		if(rawType != null) {
			if(control instanceof Text) {
				/*
				 * Text
				 */
				Text text = (Text)control;
				String textValue = text.getText().trim();
				//
				if(rawType == int.class || rawType == Integer.class) {
					value = Integer.parseInt(textValue);
				} else if(rawType == float.class || rawType == Float.class) {
					value = Float.parseFloat(textValue);
				} else if(rawType == double.class || rawType == Double.class) {
					value = Double.parseDouble(textValue);
				} else if(rawType == String.class) {
					value = textValue;
				}
			} else if(control instanceof Button) {
				/*
				 * Checkbox
				 */
				Button button = (Button)control;
				if(rawType == boolean.class || rawType == Boolean.class) {
					value = Boolean.toString(button.getSelection());
				}
			} else if(control instanceof Combo) {
				/*
				 * Combo
				 */
				Combo combo = (Combo)control;
				if(rawType.isEnum()) {
					value = combo.getText().trim();
				}
			}
		}
		return value;
	}

	@SuppressWarnings("rawtypes")
	private Control createControl(Composite parent) throws Exception {

		Control control = null;
		Class<?> rawType = inputValue.getRawType();
		if(rawType != null) {
			if(rawType == int.class || rawType == Integer.class) {
				control = createTextWidget(parent);
			} else if(rawType == float.class || rawType == Float.class) {
				control = createTextWidget(parent);
			} else if(rawType == double.class || rawType == Double.class) {
				control = createTextWidget(parent);
			} else if(rawType == String.class) {
				control = createTextWidget(parent);
			} else if(rawType == boolean.class || rawType == Boolean.class) {
				control = createCheckboxWidget(parent);
			} else if(rawType.isEnum()) {
				List<String> input = new ArrayList<>();
				Enum[] enums = (Enum[])rawType.getEnumConstants();
				for(int i = 0; i < enums.length; i++) {
					input.add(enums[i].toString());
				}
				control = createComboViewerWidget(parent, input);
			} else {
				throw new Exception("Unknown Raw Type: " + rawType);
			}
		}
		return control;
	}

	private Control createTextWidget(Composite parent) {

		Text text = new Text(parent, SWT.BORDER);
		text.setText(inputValue.getValue());
		text.setToolTipText(inputValue.getDescription());
		text.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		return text;
	}

	private Control createCheckboxWidget(Composite parent) {

		Button button = new Button(parent, SWT.CHECK);
		button.setText("");
		button.setSelection(Boolean.parseBoolean(inputValue.getValue()));
		button.setToolTipText(inputValue.getDescription());
		button.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		return button;
	}

	private Control createComboViewerWidget(Composite parent, List<String> input) {

		ComboViewer comboViewer = new ComboViewer(parent, SWT.READ_ONLY);
		//
		Combo combo = comboViewer.getCombo();
		comboViewer.setContentProvider(ArrayContentProvider.getInstance());
		comboViewer.setLabelProvider(new AbstractLabelProvider() {

			@Override
			public String getText(Object element) {

				return element.toString();
			}
		});
		combo.setToolTipText(inputValue.getDescription());
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.widthHint = 150;
		combo.setLayoutData(gridData);
		//
		comboViewer.setInput(input);
		combo.setText(inputValue.getValue());
		//
		return combo;
	}
}
