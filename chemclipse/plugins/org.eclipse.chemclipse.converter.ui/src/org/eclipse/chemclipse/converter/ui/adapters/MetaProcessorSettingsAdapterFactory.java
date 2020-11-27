/*******************************************************************************
 * Copyright (c) 2019, 2020 Lablicate GmbH.
 *
 * All rights reserved.
 * 
 * Contributors:
 * Christoph Läubrich - initial API and implementation
 * Philip Wenig - refactoring
 *******************************************************************************/
package org.eclipse.chemclipse.converter.ui.adapters;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;

import org.eclipse.chemclipse.converter.methods.MetaProcessorSettings;
import org.eclipse.chemclipse.converter.ui.Activator;
import org.eclipse.chemclipse.model.handler.IModificationHandler;
import org.eclipse.chemclipse.processing.DataCategory;
import org.eclipse.chemclipse.processing.methods.IProcessEntry;
import org.eclipse.chemclipse.processing.supplier.ProcessSupplierContext;
import org.eclipse.chemclipse.processing.supplier.ProcessorPreferences;
import org.eclipse.chemclipse.ux.extension.xxd.ui.methods.SettingsUIProvider;
import org.eclipse.chemclipse.ux.extension.xxd.ui.swt.editors.ExtendedMethodUI;
import org.eclipse.core.databinding.validation.ValidationStatus;
import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

public class MetaProcessorSettingsAdapterFactory implements IAdapterFactory, SettingsUIProvider<MetaProcessorSettings> {

	private final class SettingsUIControlImplementation implements SettingsUIControl, IModificationHandler {

		private final MetaProcessorSettings settings;
		private final ExtendedMethodUI extendedMethodUI;
		private ProcessorPreferences<MetaProcessorSettings> preferences;
		private List<Listener> listeners = new ArrayList<>();

		public SettingsUIControlImplementation(Composite parent, ProcessorPreferences<MetaProcessorSettings> preferences) throws IOException {

			this.preferences = preferences;
			settings = preferences.getSettings();
			extendedMethodUI = new ExtendedMethodUI(parent, SWT.READ_ONLY, Activator.getProcessSupplierContext(), new BiFunction<IProcessEntry, ProcessSupplierContext, ProcessorPreferences<?>>() {

				@Override
				public ProcessorPreferences<?> apply(IProcessEntry entry, ProcessSupplierContext context) {

					return settings.getProcessorPreferences(entry, entry.getPreferences(context));
				}
			}, settings.getMethod().getDataCategories().toArray(new DataCategory[0]));
			//
			extendedMethodUI.setToolbarMainVisible(false);
			extendedMethodUI.setInputs(settings.getMethod(), null);
			extendedMethodUI.setModificationHandler(this);
		}

		@Override
		public IStatus validate() {

			return ValidationStatus.ok();
		}

		@Override
		public void setEnabled(boolean enabled) {

			extendedMethodUI.setEnabled(enabled);
		}

		@Override
		public String getSettings() throws IOException {

			String string = preferences.getSerialization().toString(settings);
			return string;
		}

		@Override
		public void addChangeListener(Listener listener) {

			listeners.add(listener);
			setDirty(true);
		}

		@Override
		public Control getControl() {

			return extendedMethodUI;
		}

		@Override
		public void setDirty(boolean dirty) {

			Event event = new Event();
			event.widget = extendedMethodUI;
			event.display = extendedMethodUI.getDisplay();
			for(Listener listener : listeners) {
				listener.handleEvent(event);
			}
		}
	}

	@Override
	public <T> T getAdapter(Object adaptableObject, Class<T> adapterType) {

		if(adaptableObject instanceof MetaProcessorSettings) {
			if(adapterType.isInstance(this)) {
				return adapterType.cast(this);
			}
		}
		return null;
	}

	@Override
	public Class<?>[] getAdapterList() {

		return new Class<?>[]{SettingsUIProvider.class};
	}

	@Override
	public SettingsUIControl createUI(Composite parent, ProcessorPreferences<MetaProcessorSettings> preferences) throws IOException {

		return new SettingsUIControlImplementation(parent, preferences);
	}
}
