/*******************************************************************************
 * Copyright (c) 2019, 2021 Lablicate GmbH.
 *
 * All rights reserved.
 * 
 * Contributors:
 * Christoph Läubrich - initial API and implementation
 * Philip Wenig - refactoring
 *******************************************************************************/
package org.eclipse.chemclipse.converter.ui.adapters;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.BiFunction;

import org.eclipse.chemclipse.converter.methods.MetaProcessorSettings;
import org.eclipse.chemclipse.converter.methods.MethodConverter;
import org.eclipse.chemclipse.converter.ui.Activator;
import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.model.handler.IModificationHandler;
import org.eclipse.chemclipse.processing.DataCategory;
import org.eclipse.chemclipse.processing.core.ProcessingInfo;
import org.eclipse.chemclipse.processing.methods.IProcessEntry;
import org.eclipse.chemclipse.processing.methods.IProcessMethod;
import org.eclipse.chemclipse.processing.supplier.ProcessSupplierContext;
import org.eclipse.chemclipse.processing.supplier.ProcessorPreferences;
import org.eclipse.chemclipse.ux.extension.xxd.ui.methods.SettingsUIProvider;
import org.eclipse.chemclipse.ux.extension.xxd.ui.swt.editors.ExtendedMethodUI;
import org.eclipse.core.databinding.validation.ValidationStatus;
import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

public class MetaProcessorSettingsAdapterFactory implements IAdapterFactory, SettingsUIProvider<MetaProcessorSettings> {

	private static final Logger logger = Logger.getLogger(MetaProcessorSettingsAdapterFactory.class);

	private final class SettingsUIControlImplementation implements SettingsUIControl, IModificationHandler {

		private final MetaProcessorSettings settings;
		private final ExtendedMethodUI extendedMethodUI;
		//
		private ProcessorPreferences<MetaProcessorSettings> preferences;
		private List<Listener> listeners = new ArrayList<>();
		//
		boolean enableEditProfiles = false;
		private int sizeProfiles = 1; // Default 1 profile

		public SettingsUIControlImplementation(Composite parent, ProcessorPreferences<MetaProcessorSettings> preferences, boolean showProfileToolbar) throws IOException {

			this.preferences = preferences;
			settings = preferences.getSettings();
			extendedMethodUI = new ExtendedMethodUI(parent, SWT.READ_ONLY, Activator.getProcessSupplierContext(), new BiFunction<IProcessEntry, ProcessSupplierContext, ProcessorPreferences<?>>() {

				@Override
				public ProcessorPreferences<?> apply(IProcessEntry entry, ProcessSupplierContext context) {

					return settings.getProcessorPreferences(entry, entry.getPreferences(context));
				}
			}, settings.getMethod().getDataCategories().toArray(new DataCategory[0]));
			/*
			 * Process Method and Settings
			 */
			IProcessMethod processMethod = settings.getMethod();
			sizeProfiles = processMethod.getProfiles().size();
			enableEditProfiles = isEnableEditProfiles(processMethod, showProfileToolbar);
			/*
			 * Modify the method editor.
			 */
			extendedMethodUI.setToolbarMainVisible(false);
			extendedMethodUI.setToolbarProfileVisible(showProfileToolbar);
			extendedMethodUI.setToolbarProfileEnableEdit(enableEditProfiles);
			extendedMethodUI.setInputs(processMethod, null);
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

			return preferences.getSerialization().toString(settings);
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
			/*
			 * The meta process method is created on the fly.
			 * Hence, transfer the active profile status to apply
			 * the selected profile.
			 */
			IProcessMethod processMethodEditor = extendedMethodUI.getProcessMethod();
			IProcessMethod processMethodSettings = settings.getMethod();
			processMethodSettings.setActiveProfile(processMethodEditor.getActiveProfile());
			/*
			 * Save the method on demand, if the profiles have been edited.
			 */
			if(enableEditProfiles) {
				int sizeProfilesChanged = processMethodEditor.getProfiles().size();
				if(sizeProfiles != sizeProfilesChanged) {
					/*
					 * Remove profiles if they have been deleted.
					 */
					Set<String> profilesEditor = processMethodEditor.getProfiles();
					Set<String> profilesSettings = new HashSet<>(processMethodSettings.getProfiles());
					for(String profile : profilesSettings) {
						if(!profilesEditor.contains(profile)) {
							processMethodSettings.deleteProfile(profile);
						}
					}
					/*
					 * Save
					 */
					logger.info("Profiles have been edited. Request save method (UUID): " + processMethodSettings.getUUID());
					saveMethod(processMethodSettings);
					sizeProfiles = sizeProfilesChanged;
				}
			}
			//
			for(Listener listener : listeners) {
				listener.handleEvent(event);
			}
		}

		/**
		 * This methods checks if the process method source file is available.
		 * If yes, additional checks are performed to tests if it is writable.
		 * ---
		 * To enable modifying a contained process method, the OSGi bundle itself should
		 * be shipped with the following shape option:
		 * ---
		 * Eclipse-BundleShape: dir
		 * 
		 * @param processMethod
		 * @param showProfileToolbar
		 * @return boolean
		 */
		private boolean isEnableEditProfiles(IProcessMethod processMethod, boolean showProfileToolbar) {

			boolean enableEditProfiles = showProfileToolbar;
			if(enableEditProfiles) {
				if(processMethod != null) {
					if(processMethod.isFinal()) {
						enableEditProfiles = false;
					} else {
						File file = processMethod.getSourceFile();
						if(file == null || !file.exists() || !file.canWrite()) {
							enableEditProfiles = false;
						}
					}
				}
			}
			//
			return enableEditProfiles;
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
	public SettingsUIControl createUI(Composite parent, ProcessorPreferences<MetaProcessorSettings> preferences, boolean showProfileToolbar) throws IOException {

		return new SettingsUIControlImplementation(parent, preferences, showProfileToolbar);
	}

	private void saveMethod(IProcessMethod processMethod) {

		File file = processMethod.getSourceFile();
		if(file != null) {
			if(file.exists()) {
				if(file.getName().endsWith("." + MethodConverter.DEFAULT_METHOD_FILE_NAME_EXTENSION)) {
					try (FileOutputStream fileOutputStream = new FileOutputStream(file)) {
						try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
							MethodConverter.store(byteArrayOutputStream, "", processMethod, new ProcessingInfo<>(), new NullProgressMonitor());
							fileOutputStream.write(byteArrayOutputStream.toByteArray());
							fileOutputStream.flush();
							logger.info("Method saved (UUID): " + processMethod.getUUID() + " => " + file.getAbsolutePath());
						}
					} catch(FileNotFoundException e) {
						logger.warn(e);
					} catch(IOException e) {
						logger.warn(e);
					}
				}
			}
		}
	}
}
