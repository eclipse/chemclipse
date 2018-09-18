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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.jface.fieldassist.FieldDecorationRegistry;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class SettingsWizardPage extends WizardPage {

	private static final Logger logger = Logger.getLogger(SettingsWizardPage.class);
	//
	private List<WidgetItem> widgetItems = new ArrayList<>();

	public SettingsWizardPage(List<InputValue> inputValues) {
		super("SettingsWizardPage");
		setMessage("Modify the process settings.");
		if(inputValues != null) {
			for(InputValue inputValue : inputValues) {
				widgetItems.add(new WidgetItem(inputValue));
			}
		}
	}

	@Override
	public void createControl(Composite parent) {

		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new GridLayout(2, false));
		//
		for(WidgetItem widgetItem : widgetItems) {
			//
			InputValue inputValue = widgetItem.getInputValue();
			//
			Label label = new Label(composite, SWT.NONE);
			label.setText(inputValue.getName());
			//
			Text text = new Text(composite, SWT.BORDER);
			text.setText(inputValue.getDefaultValue());
			text.setToolTipText(inputValue.getDescription());
			text.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			//
			widgetItem.setText(text);
			InputValidator inputValidator = new InputValidator(inputValue);
			widgetItem.setInputValidator(inputValidator);
			ControlDecoration controlDecoration = new ControlDecoration(text, SWT.LEFT | SWT.TOP);
			widgetItem.setControlDecoration(controlDecoration);
			//
			text.addModifyListener(new ModifyListener() {

				@Override
				public void modifyText(ModifyEvent e) {

					validate();
				}
			});
		}
		//
		setControl(composite);
		validate();
	}

	private void validate() {

		String message = null;
		exitloop:
		for(WidgetItem widgetItem : widgetItems) {
			/*
			 * Validation
			 */
			InputValidator inputValidator = widgetItem.getInputValidator();
			ControlDecoration controlDecoration = widgetItem.getControlDecoration();
			Text text = widgetItem.getText();
			//
			IStatus status = inputValidator.validate(text.getText().trim());
			if(status.isOK()) {
				controlDecoration.hide();
			} else {
				message = status.getMessage();
				controlDecoration.setImage(FieldDecorationRegistry.getDefault().getFieldDecoration(FieldDecorationRegistry.DEC_CONTENT_PROPOSAL).getImage());
				controlDecoration.showHoverText(status.getMessage());
				controlDecoration.show();
				break exitloop;
			}
		}
		//
		setErrorMessage(message);
		setPageComplete((message == null));
		if(message == null) {
			getWizard().getDialogSettings().put(SettingsWizard.JSON_SETTINGS, getJsonSettings());
		}
	}

	private String getJsonSettings() {

		String settings = "{}";
		try {
			Map<String, Object> values = new HashMap<>();
			for(WidgetItem widgetItem : widgetItems) {
				InputValue inputValue = widgetItem.getInputValue();
				values.put(inputValue.getName(), widgetItem.getValue());
			}
			ObjectMapper mapper = new ObjectMapper();
			settings = mapper.writeValueAsString(values);
		} catch(JsonProcessingException e) {
			logger.warn(e);
		}
		//
		return settings;
	}
}
