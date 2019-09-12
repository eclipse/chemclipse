/*******************************************************************************
 * Copyright (c) 2019 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Christoph Läubrich - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.methods;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.chemclipse.support.settings.parser.InputValue;
import org.eclipse.chemclipse.xxd.process.support.ProcessorPreferences;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;

public class SettingsUI<T> extends Composite {

	private List<WidgetItem> widgetItems = new ArrayList<>();
	private List<Label> labels = new ArrayList<>();
	private ProcessorPreferences<T> preferences;

	public SettingsUI(Composite parent, ProcessorPreferences<T> preferences) throws IOException {
		super(parent, SWT.NONE);
		this.preferences = preferences;
		setLayout(new GridLayout(2, false));
		Map<InputValue, Object> valuesMap = preferences.getSerialization().fromString(preferences.getSupplier().getSettingsParser().getInputValues(), preferences.getUserSettingsAsString());
		if(valuesMap != null) {
			for(Entry<InputValue, ?> entry : valuesMap.entrySet()) {
				widgetItems.add(new WidgetItem(entry.getKey(), entry.getValue()));
			}
		}
		if(widgetItems.size() > 0) {
			createOptionWidgets(this);
		} else {
			createNoOptionsMessage(this);
		}
	}

	private void createOptionWidgets(Composite parent) {

		for(WidgetItem widgetItem : widgetItems) {
			Label label = new Label(parent, SWT.NONE);
			label.setText(widgetItem.getInputValue().getName());
			label.setToolTipText(widgetItem.getInputValue().getDescription());
			labels.add(label);
			widgetItem.initializeControl(parent);
		}
	}

	@Override
	public void setEnabled(boolean enabled) {

		for(WidgetItem widgetItem : widgetItems) {
			widgetItem.getControl().setEnabled(enabled);
		}
		for(Label label : labels) {
			label.setEnabled(enabled);
		}
		super.setEnabled(enabled);
	}

	public void addWidgetListener(Listener listener) {

		for(WidgetItem widgetItem : widgetItems) {
			Control control = widgetItem.getControl();
			control.addListener(SWT.Selection, listener);
			control.addListener(SWT.KeyUp, listener);
			control.addListener(SWT.MouseUp, listener);
			control.addListener(SWT.MouseDoubleClick, listener);
		}
		Event event = new Event();
		event.display = getShell().getDisplay();
		event.widget = this;
		listener.handleEvent(event);
	}

	private void createNoOptionsMessage(Composite parent) {

		Label label = new Label(parent, SWT.NONE);
		label.setText("This processor offers no options.");
		label.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
	}

	public String validate() {

		for(WidgetItem widgetItem : widgetItems) {
			String message = widgetItem.validate();
			if(message != null) {
				return message;
			}
		}
		return null;
	}

	public String getSettings() throws IOException {

		Map<InputValue, Object> values = new HashMap<>();
		for(WidgetItem widgetItem : widgetItems) {
			InputValue inputValue = widgetItem.getInputValue();
			values.put(inputValue, widgetItem.getValue());
		}
		return preferences.getSerialization().toString(values);
	}
}
