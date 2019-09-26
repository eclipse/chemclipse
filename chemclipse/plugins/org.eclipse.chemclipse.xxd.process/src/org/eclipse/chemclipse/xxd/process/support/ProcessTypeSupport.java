/*******************************************************************************
 * Copyright (c) 2011, 2019 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 * Alexander Kerner - Generics
 * Christoph Läubrich - improve logging output, propagate processor messages, enhance for usage in editors, preference support for processors
 *******************************************************************************/
package org.eclipse.chemclipse.xxd.process.support;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiConsumer;

import org.eclipse.chemclipse.csd.model.core.IChromatogramCSD;
import org.eclipse.chemclipse.csd.model.core.selection.ChromatogramSelectionCSD;
import org.eclipse.chemclipse.model.core.IChromatogram;
import org.eclipse.chemclipse.model.core.IMeasurement;
import org.eclipse.chemclipse.model.methods.IProcessEntry;
import org.eclipse.chemclipse.model.methods.IProcessMethod;
import org.eclipse.chemclipse.model.methods.ProcessEntry;
import org.eclipse.chemclipse.model.selection.IChromatogramSelection;
import org.eclipse.chemclipse.model.supplier.IChromatogramSelectionProcessSupplier;
import org.eclipse.chemclipse.model.supplier.IMeasurementProcessSupplier;
import org.eclipse.chemclipse.msd.model.core.IChromatogramMSD;
import org.eclipse.chemclipse.msd.model.core.selection.ChromatogramSelectionMSD;
import org.eclipse.chemclipse.processing.DataCategory;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.chemclipse.processing.core.MessageConsumer;
import org.eclipse.chemclipse.processing.core.ProcessingInfo;
import org.eclipse.chemclipse.processing.supplier.IProcessSupplier;
import org.eclipse.chemclipse.processing.supplier.IProcessTypeSupplier;
import org.eclipse.chemclipse.wsd.model.core.IChromatogramWSD;
import org.eclipse.chemclipse.wsd.model.core.selection.ChromatogramSelectionWSD;
import org.eclipse.chemclipse.xxd.process.Activator;
import org.eclipse.core.databinding.validation.ValidationStatus;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.osgi.service.prefs.BackingStoreException;
import org.osgi.service.prefs.Preferences;

public class ProcessTypeSupport {

	private static final String KEY_USE_SYSTEM_DEFAULTS = "useSystemDefaults";
	private static final String KEY_USER_SETTINGS = "userSettings";
	private static final String KEY_ASK_FOR_SETTINGS = "askForSettings";
	//
	private List<IProcessTypeSupplier> localProcessSupplier = new ArrayList<>();
	private static IEclipsePreferences preferences;

	/**
	 * 
	 * @return all active preferences for this {@link ProcessTypeSupport}
	 */
	public Collection<ProcessorPreferences<?>> getAllPreferences() {

		List<ProcessorPreferences<?>> result = new ArrayList<>();
		try {
			IEclipsePreferences storage = getStorage();
			String[] childrenNames = storage.childrenNames();
			for(String name : childrenNames) {
				Preferences node = storage.node(name);
				if(node.keys().length == 0) {
					// empty default node
					continue;
				}
				IProcessSupplier<?> processorSupplier = getSupplier(name);
				if(processorSupplier != null) {
					result.add(new NodeProcessorPreferences<>(processorSupplier, node));
				}
			}
		} catch(BackingStoreException e) {
			// can't load it then
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	public <SettingType> IProcessSupplier<SettingType> getSupplier(String processorId) {

		for(IProcessTypeSupplier typeSupplier : localProcessSupplier) {
			for(IProcessSupplier<?> supplier : typeSupplier.getProcessorSuppliers()) {
				if(supplier.matchesId(processorId)) {
					return (IProcessSupplier<SettingType>)supplier;
				}
			}
		}
		IProcessTypeSupplier[] dynamic = Activator.geIProcessTypeSuppliers();
		for(IProcessTypeSupplier typeSupplier : dynamic) {
			for(IProcessSupplier<?> supplier : typeSupplier.getProcessorSuppliers()) {
				if(supplier.matchesId(processorId)) {
					return (IProcessSupplier<SettingType>)supplier;
				}
			}
		}
		return null;
	}

	/**
	 * Get all suppliers matching a given set of datacategories
	 * 
	 * @param dataTypes
	 * @return the matching {@link IProcessSupplier}
	 */
	public Set<IProcessSupplier<?>> getSupplier(EnumSet<DataCategory> dataTypes) {

		Set<IProcessSupplier<?>> supplier = new TreeSet<>((o1, o2) -> o1.getId().compareTo(o2.getId()));
		addMatchingSupplier(dataTypes, supplier, localProcessSupplier.toArray(new IProcessTypeSupplier[0]));
		addMatchingSupplier(dataTypes, supplier, Activator.geIProcessTypeSuppliers());
		return supplier;
	}

	private void addMatchingSupplier(EnumSet<DataCategory> dataTypes, Set<IProcessSupplier<?>> supplier, IProcessTypeSupplier[] processTypeSuppliers) {

		for(IProcessTypeSupplier processTypeSupplier : processTypeSuppliers) {
			for(IProcessSupplier<?> processSupplier : processTypeSupplier.getProcessorSuppliers()) {
				for(DataCategory category : dataTypes) {
					if(processSupplier.getSupportedDataTypes().contains(category)) {
						supplier.add(processSupplier);
						break;
					}
				}
			}
		}
	}

	/**
	 * 
	 * @param processorId
	 * @return the preferences for this processor id
	 */
	public static <T> ProcessorPreferences<T> getWorkspacePreferences(IProcessSupplier<T> supplier) {

		return new NodeProcessorPreferences<T>(supplier);
	}

	public <T> ProcessorPreferences<T> getProcessEntryPreferences(IProcessEntry entry) {

		IProcessSupplier<T> supplier = getSupplier(entry.getProcessorId());
		if(supplier == null) {
			return null;
		}
		return new ProcessEntryProcessorPreferences<>(supplier, entry);
	}

	private static IEclipsePreferences getStorage() {

		if(preferences == null) {
			preferences = InstanceScope.INSTANCE.getNode(ProcessTypeSupport.class.getName());
		}
		return preferences;
	}

	/**
	 * Adds the given {@link IProcessTypeSupplier} to this
	 * {@link ProcessTypeSupport} this allows for adding non standard supplier
	 * 
	 * @param processTypeSupplier
	 */
	public void addProcessSupplier(IProcessTypeSupplier processTypeSupplier) {

		localProcessSupplier.add(processTypeSupplier);
	}

	public <T> IProcessingInfo<T> applyProcessor(IChromatogramSelection<?, ?> chromatogramSelection, IProcessMethod processMethod, IProgressMonitor monitor) {

		List<IChromatogramSelection<?, ?>> chromatogramSelections = new ArrayList<>();
		if(chromatogramSelection != null) {
			chromatogramSelections.add(chromatogramSelection);
		}
		return applyProcessor(chromatogramSelections, processMethod, monitor);
	}

	public <T, X> IProcessingInfo<T> applyProcessor(List<? extends IChromatogramSelection<?, ?>> chromatogramSelections, IProcessMethod processMethod, IProgressMonitor monitor) {

		SubMonitor subMonitor = SubMonitor.convert(monitor, chromatogramSelections.size() * 100);
		IProcessingInfo<T> processingInfo = new ProcessingInfo<>();
		for(IChromatogramSelection<?, ?> chromatogramSelection : chromatogramSelections) {
			SubMonitor split = subMonitor.split(100);
			SubMonitor convert = SubMonitor.convert(split, processMethod.getNumberOfEntries());
			applyProcessor(processMethod, new BiConsumer<IProcessSupplier<X>, X>() {

				@Override
				public void accept(IProcessSupplier<X> processSupplier, X settings) {

					applyProcessor(chromatogramSelection, processSupplier, settings, processingInfo, convert.split(1));
				}
			}, processingInfo);
		}
		return processingInfo;
	}

	public <X> Collection<? extends IMeasurement> applyProcessor(Collection<? extends IMeasurement> measurements, IProcessMethod processMethod, MessageConsumer messageConsumer, IProgressMonitor monitor) {

		SubMonitor subMonitor = SubMonitor.convert(monitor, "Processing files", processMethod.getNumberOfEntries() * 100);
		AtomicReference<Collection<? extends IMeasurement>> result = new AtomicReference<Collection<? extends IMeasurement>>(measurements);
		applyProcessor(processMethod, new BiConsumer<IProcessSupplier<X>, X>() {

			@Override
			public void accept(IProcessSupplier<X> processor, X settings) {

				result.set(applyProcessor(result.get(), processor, settings, messageConsumer, subMonitor.split(100)));
			}
		}, messageConsumer);
		return result.get();
	}

	private <X> void applyProcessor(IProcessMethod processMethod, BiConsumer<IProcessSupplier<X>, X> consumer, MessageConsumer messages) {

		for(IProcessEntry processEntry : processMethod) {
			ProcessorPreferences<X> preferences = getProcessEntryPreferences(processEntry);
			if(preferences == null) {
				messages.addWarnMessage(processEntry.getName(), "processor not found, will be skipped");
				continue;
			}
			try {
				X settings = preferences.getSettings();
				consumer.accept(preferences.getSupplier(), settings);
			} catch(IOException e) {
				messages.addWarnMessage(processEntry.getName(), "the settings can't be read, will be skipped", e);
				continue;
			}
		}
	}

	public IStatus validate(IProcessEntry processEntry) {

		if(processEntry == null) {
			return ValidationStatus.error("Entry is null");
		}
		ProcessorPreferences<?> preferences = getProcessEntryPreferences(processEntry);
		if(preferences == null) {
			return ValidationStatus.error("Processor " + processEntry.getName() + " not avaiable");
		}
		if(preferences.getSupplier().getSettingsClass() == null) {
			return ValidationStatus.warning("Processor " + processEntry.getName() + " has no settingsclass");
		}
		if(preferences.isUseSystemDefaults()) {
			return ValidationStatus.info("Processor " + processEntry.getName() + " uses system default settings");
		} else {
			try {
				preferences.getUserSettings();
			} catch(IOException e) {
				return ValidationStatus.error("Loading settings for Processor " + processEntry.getName() + "failed", e);
			}
			return ValidationStatus.ok();
		}
	}

	public static <T> IChromatogramSelection<?, ?> applyProcessor(IChromatogramSelection<?, ?> chromatogramSelection, IProcessSupplier<T> supplier, T processSettings, MessageConsumer messageConsumer, IProgressMonitor monitor) {

		if(supplier instanceof IChromatogramSelectionProcessSupplier<?>) {
			IChromatogramSelectionProcessSupplier<T> chromatogramSelectionProcessSupplier = (IChromatogramSelectionProcessSupplier<T>)supplier;
			return chromatogramSelectionProcessSupplier.apply(chromatogramSelection, processSettings, messageConsumer, monitor);
		}
		if(supplier instanceof IMeasurementProcessSupplier<?>) {
			IMeasurementProcessSupplier<T> measurementProcessSupplier = (IMeasurementProcessSupplier<T>)supplier;
			IChromatogram<?> chromatogram = chromatogramSelection.getChromatogram();
			Collection<? extends IMeasurement> collection = measurementProcessSupplier.applyProcessor(Collections.singleton(chromatogram), processSettings, messageConsumer, monitor);
			for(IMeasurement measurement : collection) {
				if(measurement == chromatogram) {
					return chromatogramSelection;
				} else if(measurement instanceof IChromatogramMSD) {
					return new ChromatogramSelectionMSD((IChromatogramMSD)measurement);
				} else if(measurement instanceof IChromatogramCSD) {
					return new ChromatogramSelectionCSD((IChromatogramCSD)measurement);
				} else if(measurement instanceof IChromatogramWSD) {
					return new ChromatogramSelectionWSD((IChromatogramWSD)measurement);
				}
			}
		}
		return chromatogramSelection;
	}

	public static <X> Collection<? extends IMeasurement> applyProcessor(Collection<? extends IMeasurement> measurements, IProcessSupplier<X> supplier, X processSettings, MessageConsumer messageConsumer, IProgressMonitor monitor) {

		if(supplier instanceof IMeasurementProcessSupplier<?>) {
			IMeasurementProcessSupplier<X> measurementProcessSupplier = (IMeasurementProcessSupplier<X>)supplier;
			return measurementProcessSupplier.applyProcessor(measurements, processSettings, messageConsumer, monitor);
		}
		return measurements;
	}

	private static final class ProcessEntryProcessorPreferences<T> implements ProcessorPreferences<T> {

		private IProcessEntry processEntry;
		private IProcessSupplier<T> supplier;

		public ProcessEntryProcessorPreferences(IProcessSupplier<T> supplier, IProcessEntry processEntry) {
			this.supplier = supplier;
			this.processEntry = processEntry;
		}

		@Override
		public DialogBehavior getDialogBehaviour() {

			return DialogBehavior.NONE;
		}

		@Override
		public void setAskForSettings(boolean askForSettings) {

			// no-op
		}

		@Override
		public void setUserSettings(String settings) {

			processEntry.setJsonSettings(settings);
		}

		@Override
		public boolean isUseSystemDefaults() {

			if(supplier.getSettingsClass() == null) {
				return true;
			}
			String jsonSettings = processEntry.getJsonSettings();
			return jsonSettings == null || jsonSettings.isEmpty() || ProcessEntry.EMPTY_JSON_SETTINGS.equals(jsonSettings);
		}

		@Override
		public void setUseSystemDefaults(boolean useSystemDefaults) {

			if(useSystemDefaults) {
				processEntry.setJsonSettings(ProcessEntry.EMPTY_JSON_SETTINGS);
			}
		}

		@Override
		public void reset() {

			throw new UnsupportedOperationException();
		}

		@Override
		public IProcessSupplier<T> getSupplier() {

			return supplier;
		}

		@Override
		public String getUserSettingsAsString() {

			return processEntry.getJsonSettings();
		}
	}

	private static final class NodeProcessorPreferences<T> implements ProcessorPreferences<T> {

		private Preferences node;
		private IProcessSupplier<T> supplier;

		public NodeProcessorPreferences(IProcessSupplier<T> supplier) {
			this(supplier, getStorage().node(supplier.getId()));
		}

		public NodeProcessorPreferences(IProcessSupplier<T> supplier, Preferences node) {
			this.supplier = supplier;
			this.node = node;
		}

		@Override
		public DialogBehavior getDialogBehaviour() {

			if(supplier.getSettingsClass() == null) {
				return DialogBehavior.NONE;
			}
			trySync();
			boolean askForSettings = node.getBoolean(KEY_ASK_FOR_SETTINGS, true);
			if(askForSettings) {
				return DialogBehavior.SHOW;
			} else {
				return DialogBehavior.SAVED_DEFAULTS;
			}
		}

		public void trySync() {

			try {
				node.sync();
			} catch(BackingStoreException e) {
			}
		}

		@Override
		public void setAskForSettings(boolean askForSettings) {

			node.putBoolean(KEY_ASK_FOR_SETTINGS, askForSettings);
			tryFlush();
		}

		private void tryFlush() {

			try {
				node.flush();
			} catch(BackingStoreException e) {
			}
		}

		@Override
		public void setUserSettings(String settings) {

			node.put(KEY_USER_SETTINGS, settings);
			tryFlush();
		}

		@Override
		public void reset() {

			try {
				node.clear();
				tryFlush();
			} catch(BackingStoreException e) {
			}
		}

		@Override
		public boolean isUseSystemDefaults() {

			if(supplier.getSettingsClass() == null) {
				return true;
			}
			trySync();
			return node.getBoolean(KEY_USE_SYSTEM_DEFAULTS, true);
		}

		@Override
		public void setUseSystemDefaults(boolean useSystemDefaults) {

			node.putBoolean(KEY_USE_SYSTEM_DEFAULTS, useSystemDefaults);
			tryFlush();
		}

		@Override
		public String getUserSettingsAsString() {

			trySync();
			return node.get(KEY_USER_SETTINGS, ProcessEntry.EMPTY_JSON_SETTINGS);
		}

		@Override
		public IProcessSupplier<T> getSupplier() {

			return supplier;
		}
	}
}
