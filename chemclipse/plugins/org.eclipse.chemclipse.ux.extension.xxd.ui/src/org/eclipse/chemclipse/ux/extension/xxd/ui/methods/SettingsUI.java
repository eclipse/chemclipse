/*******************************************************************************
 * Copyright (c) 2019, 2023 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Christoph Läubrich - initial API and implementation
 * Philip Wenig - enable profiles
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.methods;

import static org.eclipse.chemclipse.support.ui.swt.ControlBuilder.createContainer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.processing.supplier.IProcessorPreferences;
import org.eclipse.chemclipse.support.l10n.TranslationSupport;
import org.eclipse.chemclipse.support.settings.parser.InputValue;
import org.eclipse.chemclipse.ux.extension.xxd.ui.l10n.ExtensionMessages;
import org.eclipse.chemclipse.ux.extension.xxd.ui.methods.SettingsUIProvider.SettingsUIControl;
import org.eclipse.core.databinding.validation.ValidationStatus;
import org.eclipse.core.runtime.Adapters;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.e4.core.services.translation.TranslationService;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;

public class SettingsUI<T> extends Composite {

	private static final Logger logger = Logger.getLogger(SettingsUI.class);
	private final SettingsUIControl control;

	public SettingsUI(Composite parent, IProcessorPreferences<T> preferences, boolean showProfileToolbar) throws IOException {

		super(parent, SWT.NONE);
		setLayout(new FillLayout());
		control = loadSettingsUIProvider(preferences).createUI(this, preferences, showProfileToolbar);
	}

	@Override
	public void setEnabled(boolean enabled) {

		control.setEnabled(enabled);
		super.setEnabled(enabled);
	}

	public SettingsUIControl getControl() {

		return control;
	}

	public void update(IProcessorPreferences<T> preferences) {

		loadSettingsUIProvider(preferences);
	}

	private SettingsUIProvider<T> loadSettingsUIProvider(IProcessorPreferences<T> preferences) {

		try {
			T settings = preferences.getUserSettings();
			if(settings == null) {
				/*
				 * Check the available system settings.
				 * If it fails, try to create default settings.
				 */
				settings = preferences.getSystemSettings();
				if(settings == null) {
					settings = preferences.getSupplier().getSettingsParser().createDefaultInstance();
				}
			}
			@SuppressWarnings("unchecked")
			SettingsUIProvider<T> uiProvider = Adapters.adapt(settings, SettingsUIProvider.class);
			if(uiProvider != null) {
				return uiProvider;
			}
		} catch(IOException e) {
			/*
			 * The settings provider is not available.
			 */
			logger.error("Can't get user-settings for processor " + preferences.getSupplier().getId() + " with settingsclass " + preferences.getSupplier().getSettingsClass());
		}
		return new DefaultSettingsUIProvider<>();
	}

	private static final class DefaultSettingsUIProvider<T> implements SettingsUIProvider<T> {

		@Override
		public SettingsUIControl createUI(Composite parent, IProcessorPreferences<T> preferences, boolean showProfileToolbar) throws IOException {

			return new SettingsUIControlImplementation<T>(parent, preferences);
		}
	}

	private static final class SettingsUIControlImplementation<T> implements SettingsUIControl {

		private final List<WidgetItem> widgetItems = new ArrayList<>();
		private final List<Label> labels = new ArrayList<>();
		private final IProcessorPreferences<T> preferences;
		private final Composite container;

		public SettingsUIControlImplementation(Composite parent, IProcessorPreferences<T> preferences) throws IOException {

			container = createContainer(parent);
			this.preferences = preferences;
			container.setLayout(new GridLayout(2, false));
			//
			List<InputValue> inputValues = preferences.getSupplier().getSettingsParser().getInputValues();
			Map<InputValue, Object> valuesMap = preferences.getSerialization().fromObject(inputValues, preferences.getSettings());
			if(valuesMap != null) {
				for(Entry<InputValue, ?> entry : valuesMap.entrySet()) {
					widgetItems.add(new WidgetItem(entry.getKey(), entry.getValue()));
				}
			}
			//
			if(!widgetItems.isEmpty()) {
				createOptionWidgets(container);
			} else {
				createNoOptionsMessage(container);
			}
		}

		@Override
		public void setEnabled(boolean enabled) {

			for(WidgetItem widgetItem : widgetItems) {
				widgetItem.getControl().setEnabled(enabled);
			}
			//
			for(Label label : labels) {
				label.setEnabled(enabled);
			}
		}

		private void createOptionWidgets(Composite parent) {

			for(WidgetItem widgetItem : widgetItems) {
				Label label = new Label(parent, SWT.NONE);
				String contributorURI = widgetItem.getInputValue().getContributorURI();
				TranslationService translationService = TranslationSupport.getTranslationService();
				if(widgetItem.getInputValue().getLabel() != null && !widgetItem.getInputValue().getLabel().isEmpty()) {
					label.setText(translationService.translate(widgetItem.getInputValue().getLabel(), contributorURI));
				} else {
					label.setText(widgetItem.getInputValue().getName());
				}
				if(widgetItem.getInputValue().getTooltip() != null && !widgetItem.getInputValue().getTooltip().isEmpty()) {
					label.setToolTipText(translationService.translate(widgetItem.getInputValue().getTooltip(), contributorURI));
				} else {
					label.setToolTipText(widgetItem.getInputValue().getDescription());
				}
				GridData data = new GridData(SWT.LEFT, SWT.TOP, false, false);
				data.verticalIndent = 5;
				data.horizontalIndent = 5;
				label.setLayoutData(data);
				labels.add(label);
				widgetItem.initializeControl(parent);
			}
		}

		private void createNoOptionsMessage(Composite parent) {

			Label label = new Label(parent, SWT.NONE);
			label.setText(ExtensionMessages.processorOffersNoOptions);
			label.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		}

		@Override
		public IStatus validate() {

			for(WidgetItem widgetItem : widgetItems) {
				IStatus status = widgetItem.validate();
				if(!status.isOK()) {
					return status;
				}
			}
			return ValidationStatus.ok();
		}

		@Override
		public String getSettings() throws IOException {

			Map<InputValue, Object> values = new HashMap<>();
			for(WidgetItem widgetItem : widgetItems) {
				InputValue inputValue = widgetItem.getInputValue();
				values.put(inputValue, widgetItem.getValue());
			}
			//
			return preferences.getSerialization().toString(values);
		}

		@Override
		public void restoreDefaults() {

			for(WidgetItem widgetItem : widgetItems) {
				widgetItem.restoreDefaults();
			}
		}

		@Override
		public void addChangeListener(Listener listener) {

			for(WidgetItem widgetItem : widgetItems) {
				Control control = widgetItem.getControl();
				control.addListener(SWT.Selection, listener);
				control.addListener(SWT.KeyUp, listener);
				control.addListener(SWT.MouseUp, listener);
				control.addListener(SWT.MouseDoubleClick, listener);
				//
				if(control instanceof IChangeListener changeListener) {
					changeListener.addChangeListener(listener);
				}
			}
			//
			Event event = new Event();
			event.display = container.getShell().getDisplay();
			event.widget = container;
			listener.handleEvent(event);
		}

		@Override
		public Control getControl() {

			return container;
		}
	}
}
