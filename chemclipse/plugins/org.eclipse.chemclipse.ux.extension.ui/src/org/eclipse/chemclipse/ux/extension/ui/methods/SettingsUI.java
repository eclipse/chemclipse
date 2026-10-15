/*******************************************************************************
 * Copyright (c) 2019, 2026 Lablicate GmbH.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 * Christoph Läubrich - initial API and implementation
 * Philip Wenig - enable profiles
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.ui.methods;

import static org.eclipse.chemclipse.support.ui.swt.ControlBuilder.createContainer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicReference;

import org.eclipse.chemclipse.processing.supplier.IProcessorPreferences;
import org.eclipse.chemclipse.support.l10n.TranslationSupport;
import org.eclipse.chemclipse.support.settings.ISettingsDependency;
import org.eclipse.chemclipse.support.settings.SettingsPropertyState;
import org.eclipse.chemclipse.support.settings.parser.InputValue;
import org.eclipse.chemclipse.ux.extension.ui.l10n.ExtensionMessages;
import org.eclipse.chemclipse.ux.extension.ui.methods.SettingsUIProvider.SettingsUIControl;
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

	private AtomicReference<SettingsUIControl> control = new AtomicReference<>();

	public SettingsUI(Composite parent, IProcessorPreferences<T> preferences, boolean showProfileToolbar) throws IOException {

		super(parent, SWT.NONE);
		createControl(preferences, showProfileToolbar);
	}

	@Override
	public void setEnabled(boolean enabled) {

		control.get().setEnabled(enabled);
		super.setEnabled(enabled);
	}

	public SettingsUIControl getControl() {

		return control.get();
	}

	private void createControl(IProcessorPreferences<T> preferences, boolean showProfileToolbar) throws IOException {

		setLayout(new FillLayout());

		SettingsUIProvider<T> settingsUIProvider = createSettingsUIProvider(preferences);
		control.set(settingsUIProvider.createUI(this, preferences, showProfileToolbar));
	}

	private SettingsUIProvider<T> createSettingsUIProvider(IProcessorPreferences<T> preferences) {

		T settings = preferences.getUserSettings();
		if(settings == null) {
			settings = preferences.getSupplier().getSettingsParser().createDefaultInstance();
		}

		@SuppressWarnings("unchecked")
		SettingsUIProvider<T> settingsUIProvider = Adapters.adapt(settings, SettingsUIProvider.class);
		if(settingsUIProvider == null) {
			settingsUIProvider = new DefaultSettingsUIProvider<>();
		}

		return settingsUIProvider;
	}

	private static final class DefaultSettingsUIProvider<T> implements SettingsUIProvider<T> {

		@Override
		public SettingsUIControl createUI(Composite parent, IProcessorPreferences<T> preferences, boolean showProfileToolbar) throws IOException {

			return new SettingsUIControlImplementation<>(parent, preferences);
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

			List<InputValue> inputValues = preferences.getSupplier().getSettingsParser().getInputValues();
			Map<InputValue, Object> valuesMap = preferences.getSerialization().fromObject(inputValues, preferences.getSettings());
			if(valuesMap != null) {
				for(Entry<InputValue, ?> entry : valuesMap.entrySet()) {
					widgetItems.add(new WidgetItem(entry.getKey(), entry.getValue()));
				}
			}

			if(!widgetItems.isEmpty()) {
				createOptionWidgets(container);
				initializeSettingsDependencies();
			} else {
				createNoOptionsMessage(container);
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

			if(enabled) {
				updateSettingsDependencies();
			}
		}

		private void initializeSettingsDependencies() {

			for(WidgetItem widgetItem : widgetItems) {
				if(widgetItem.getInputValue().getSettingsDependency() != null) {
					Control control = widgetItem.getControl();
					if(control != null) {
						control.addListener(SWT.Selection, _ -> updateSettingsDependencies());
						control.addListener(SWT.Modify, _ -> updateSettingsDependencies());
					}
				}
			}
			updateSettingsDependencies();
		}

		private void updateSettingsDependencies() {

			for(WidgetItem widgetItem : widgetItems) {
				ISettingsDependency settingsDependency = widgetItem.getInputValue().getSettingsDependency();
				if(settingsDependency != null) {
					Map<String, SettingsPropertyState> states = settingsDependency.getFieldStates(widgetItem.getDependencyValue());
					if(states != null) {
						for(Entry<String, SettingsPropertyState> entry : states.entrySet()) {
							WidgetItem dependentItem = getWidgetItem(entry.getKey());
							if(dependentItem != null) {
								dependentItem.applySettingsPropertyState(entry.getValue());
							}
						}
					}
				}
			}
		}

		private WidgetItem getWidgetItem(String name) {

			for(WidgetItem widgetItem : widgetItems) {
				InputValue inputValue = widgetItem.getInputValue();
				if(inputValue.getFieldName().equals(name) || inputValue.getName().equals(name)) {
					return widgetItem;
				}
			}
			return null;
		}

		private void createOptionWidgets(Composite parent) {

			TranslationService translationService = TranslationSupport.getTranslationService();
			for(WidgetItem widgetItem : widgetItems) {
				Label label = new Label(parent, SWT.NONE);
				label.setText(getLabelText(widgetItem, translationService));
				label.setToolTipText(getLabelToolTipText(widgetItem, translationService));
				GridData data = new GridData(SWT.LEFT, SWT.TOP, false, false);
				data.verticalIndent = 5;
				data.horizontalIndent = 5;
				label.setLayoutData(data);
				labels.add(label);
				widgetItem.setLabel(label);
				widgetItem.initializeControl(parent);
			}
		}

		private String getLabelText(WidgetItem widgetItem, TranslationService translationService) {

			if(widgetItem.getInputValue().getLabel() != null && !widgetItem.getInputValue().getLabel().isEmpty()) {
				String contributorURI = widgetItem.getInputValue().getContributorURI();
				return translationService.translate(widgetItem.getInputValue().getLabel(), contributorURI);
			} else {
				return widgetItem.getInputValue().getName();
			}
		}

		private String getLabelToolTipText(WidgetItem widgetItem, TranslationService translationService) {

			if(widgetItem.getInputValue().getTooltip() != null && !widgetItem.getInputValue().getTooltip().isEmpty()) {
				String contributorURI = widgetItem.getInputValue().getContributorURI();
				return translationService.translate(widgetItem.getInputValue().getTooltip(), contributorURI);
			} else {
				return widgetItem.getInputValue().getDescription();
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

			return preferences.getSerialization().toString(values);
		}

		@Override
		public void restoreDefaults() {

			for(WidgetItem widgetItem : widgetItems) {
				widgetItem.restoreDefaults();
			}
			updateSettingsDependencies();
		}

		@Override
		public void addChangeListener(Listener listener) {

			for(WidgetItem widgetItem : widgetItems) {
				/*
				 * Listen to changes
				 */
				Control control = widgetItem.getControl();
				control.addListener(SWT.Selection, listener);
				control.addListener(SWT.KeyUp, listener);
				control.addListener(SWT.MouseUp, listener);
				control.addListener(SWT.MouseDoubleClick, listener);
				control.addListener(SWT.Modify, listener);

				if(control instanceof IChangeListener changeListener) {
					changeListener.addChangeListener(listener);
				}
			}

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