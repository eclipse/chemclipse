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

import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.swt.widgets.Text;

public class WidgetItem {

	private static final Logger logger = Logger.getLogger(WidgetItem.class);
	//
	private InputValue inputValue;
	private InputValidator inputValidator;
	private ControlDecoration controlDecoration;
	private Text text;

	public WidgetItem(InputValue inputValue) {
		this.inputValue = inputValue;
	}

	public InputValue getInputValue() {

		return inputValue;
	}

	public InputValidator getInputValidator() {

		return inputValidator;
	}

	public void setInputValidator(InputValidator inputValidator) {

		this.inputValidator = inputValidator;
	}

	public ControlDecoration getControlDecoration() {

		return controlDecoration;
	}

	public void setControlDecoration(ControlDecoration controlDecoration) {

		this.controlDecoration = controlDecoration;
	}

	public Text getText() {

		return text;
	}

	public void setText(Text text) {

		this.text = text;
	}

	public Object getValue() {

		Object value = null;
		Class<?> rawType = inputValue.getRawType();
		if(rawType != null) {
			String textValue = text.getText().trim();
			if(rawType == int.class || rawType == Integer.class) {
				value = Integer.parseInt(textValue);
			} else if(rawType == float.class || rawType == Float.class) {
				value = Float.parseFloat(textValue);
			} else if(rawType == double.class || rawType == Double.class) {
				value = Double.parseDouble(textValue);
			} else if(rawType == String.class) {
				value = textValue;
			} else if(rawType == boolean.class || rawType == Boolean.class) {
				value = Boolean.parseBoolean(textValue);
			} else if(rawType.isEnum()) {
				value = textValue;
			} else {
				logger.info("Unknown Raw Type: " + rawType);
			}
		}
		return value;
	}
}
