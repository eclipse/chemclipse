/*******************************************************************************
 * Copyright (c) 2018, 2019 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 * Christoph Läubrich - extend event handling, extract UI
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.methods;

import java.io.IOException;
import java.util.Map;

import org.eclipse.chemclipse.support.settings.parser.InputValue;
import org.eclipse.chemclipse.support.settings.serialization.SettingsSerialization;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

public class SettingsWizardPage extends WizardPage {

	private Map<InputValue, ?> inputValues;
	private SettingsSerialization serialization;

	protected SettingsWizardPage(SettingsSerialization serialization, Map<InputValue, ?> inputValues) {
		super("SettingsWizardPage");
		this.serialization = serialization;
		this.inputValues = inputValues;
		setTitle("Process Settings");
		setDescription("Modify the process settings.");
	}

	@Override
	public void createControl(Composite parent) {

		SettingsUI settingsUI = new SettingsUI(parent, inputValues, serialization);
		setControl(settingsUI);
		settingsUI.addWidgetListener(new Listener() {

			@Override
			public void handleEvent(Event event) {

				String validate = settingsUI.validate();
				if(validate == null) {
					try {
						getWizard().getDialogSettings().put(SettingsWizard.JSON_SETTINGS, settingsUI.getJsonSettings());
					} catch(IOException e) {
						validate = e.toString();
					}
				}
				setErrorMessage(validate);
				setPageComplete((validate == null));
			}
		});
	}
}
