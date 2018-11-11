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
import org.eclipse.chemclipse.model.methods.IProcessEntry;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class SettingsWizardPage extends WizardPage {

	private static final Logger logger = Logger.getLogger(SettingsWizardPage.class);
	//
	private List<WidgetItem> widgetItems = new ArrayList<>();

	protected SettingsWizardPage(List<InputValue> inputValues) {
		super("SettingsWizardPage");
		setTitle("Process Settings");
		setDescription("Modify the process settings.");
		//
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
		if(widgetItems.size() > 0) {
			createOptionWidgets(composite);
		} else {
			createNoOptionsMessage(composite);
		}
		//
		setControl(composite);
		validate();
	}

	private void createOptionWidgets(Composite parent) {

		for(WidgetItem widgetItem : widgetItems) {
			//
			Label label = new Label(parent, SWT.NONE);
			label.setText(widgetItem.getInputValue().getName());
			//
			try {
				widgetItem.initializeControl(parent);
				Control control = widgetItem.getControl();
				addKeyListener(control);
				addMouseListener(control);
			} catch(Exception e) {
				logger.warn(e);
				Label info = new Label(parent, SWT.NONE);
				info.setText("Error to create the widget.");
			}
		}
	}

	private void addKeyListener(Control control) {

		control.addKeyListener(new KeyAdapter() {

			@Override
			public void keyReleased(KeyEvent e) {

				validate();
			}
		});
	}

	private void addMouseListener(Control control) {

		control.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseDoubleClick(MouseEvent e) {

				validate();
			}

			@Override
			public void mouseUp(MouseEvent e) {

				validate();
			}
		});
	}

	private void createNoOptionsMessage(Composite parent) {

		Label label = new Label(parent, SWT.NONE);
		label.setText("This processor offers no options.");
		label.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
	}

	private void validate() {

		String message = null;
		exitloop:
		for(WidgetItem widgetItem : widgetItems) {
			message = widgetItem.validate();
			if(message != null) {
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

		String settings = IProcessEntry.EMPTY_JSON_SETTINGS;
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
