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
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiConsumer;

import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.model.core.IMeasurement;
import org.eclipse.chemclipse.model.methods.IProcessEntry;
import org.eclipse.chemclipse.model.methods.IProcessMethod;
import org.eclipse.chemclipse.model.methods.ProcessEntry;
import org.eclipse.chemclipse.model.selection.IChromatogramSelection;
import org.eclipse.chemclipse.model.settings.IProcessSettings;
import org.eclipse.chemclipse.model.types.DataType;
import org.eclipse.chemclipse.processing.ProcessorFactory;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.chemclipse.processing.core.MessageConsumer;
import org.eclipse.chemclipse.processing.core.ProcessingInfo;
import org.eclipse.chemclipse.support.settings.serialization.JSONSerialization;
import org.eclipse.chemclipse.support.settings.serialization.SettingsSerialization;
import org.eclipse.chemclipse.xxd.process.Activator;
import org.eclipse.chemclipse.xxd.process.comparators.CategoryComparator;
import org.eclipse.chemclipse.xxd.process.supplier.BaselineDetectorTypeSupplier;
import org.eclipse.chemclipse.xxd.process.supplier.ChromatogramCalculatorTypeSupplier;
import org.eclipse.chemclipse.xxd.process.supplier.ChromatogramExportTypeSupplier;
import org.eclipse.chemclipse.xxd.process.supplier.ChromatogramFilterTypeSupplier;
import org.eclipse.chemclipse.xxd.process.supplier.ChromatogramIdentifierTypeSupplier;
import org.eclipse.chemclipse.xxd.process.supplier.ChromatogramIntegratorTypeSupplier;
import org.eclipse.chemclipse.xxd.process.supplier.ChromatogramReportTypeSupplier;
import org.eclipse.chemclipse.xxd.process.supplier.ClassifierTypeSupplier;
import org.eclipse.chemclipse.xxd.process.supplier.CombinedIntegratorTypeSupplier;
import org.eclipse.chemclipse.xxd.process.supplier.IMeasurementFilterProcessTypeSupplier;
import org.eclipse.chemclipse.xxd.process.supplier.MassspectrumProcessTypeSupplier;
import org.eclipse.chemclipse.xxd.process.supplier.PeakDetectorTypeSupplier;
import org.eclipse.chemclipse.xxd.process.supplier.PeakFilterTypeSupplierMSD;
import org.eclipse.chemclipse.xxd.process.supplier.PeakIdentifierTypeSupplier;
import org.eclipse.chemclipse.xxd.process.supplier.PeakIntegratorTypeSupplier;
import org.eclipse.chemclipse.xxd.process.supplier.PeakQuantitationTypeSupplier;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.osgi.service.prefs.BackingStoreException;
import org.osgi.service.prefs.Preferences;

public class ProcessTypeSupport {

	private static final SettingsSerialization SETTINGS_SERIALIZATION = new JSONSerialization();
	private static final String KEY_USE_SYSTEM_DEFAULTS = "useSystemDefaults";
	private static final String KEY_USER_SETTINGS = "userSettings";
	private static final String KEY_ASK_FOR_SETTINGS = "askForSettings";
	private static final CategoryComparator CATEGORY_COMPARATOR = new CategoryComparator();
	private static final Logger logger = Logger.getLogger(ProcessTypeSupport.class);
	//
	private Map<String, IProcessTypeSupplier> localProcessSupplier = new HashMap<>();
	private static IEclipsePreferences preferences;

	public ProcessTypeSupport() {
		this(null);
	}

	public ProcessTypeSupport(ProcessorFactory filterFactory) {
		/*
		 * Add all available process supplier here. TODO: Test native settings
		 * composite via extension point resolution
		 */
		addProcessSupplier(new BaselineDetectorTypeSupplier()); // OK
		addProcessSupplier(new ChromatogramIdentifierTypeSupplier()); // OK -
																		// Improve
																		// settings
		addProcessSupplier(new ChromatogramIntegratorTypeSupplier()); // OK
		addProcessSupplier(new ClassifierTypeSupplier()); // OK - Improve
															// settings
		addProcessSupplier(new CombinedIntegratorTypeSupplier()); // OK - Nested
																	// Settings
		addProcessSupplier(new ChromatogramFilterTypeSupplier()); // OK
		addProcessSupplier(new PeakFilterTypeSupplierMSD()); // OK
		addProcessSupplier(new PeakDetectorTypeSupplier()); // OK
		addProcessSupplier(new PeakIdentifierTypeSupplier()); // OK
		addProcessSupplier(new PeakIntegratorTypeSupplier()); // OK - Improve
																// settings
		addProcessSupplier(new PeakQuantitationTypeSupplier()); // OK
		addProcessSupplier(new ChromatogramCalculatorTypeSupplier()); // OK
		addProcessSupplier(new ChromatogramReportTypeSupplier()); // OK
		addProcessSupplier(new ChromatogramExportTypeSupplier()); // OK -
																	// Improve
																	// settings
		addProcessSupplier(MassspectrumProcessTypeSupplier.createPeakFilterSupplier());
		addProcessSupplier(MassspectrumProcessTypeSupplier.createScanFilterSupplier());
		// NoiseCalculator?
		if(filterFactory != null) {
			// TODO
			addProcessSupplier(new IMeasurementFilterProcessTypeSupplier(filterFactory));
		}
	}

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

	public <SettingType> IProcessSupplier<SettingType> getSupplier(String processorId) {

		for(IProcessTypeSupplier supplier : localProcessSupplier.values()) {
			IProcessSupplier<SettingType> processSupplier = supplier.getProcessorSupplier(processorId);
			if(processSupplier != null) {
				return processSupplier;
			}
		}
		IProcessTypeSupplier[] dynamic = Activator.geIProcessTypeSuppliers();
		for(IProcessTypeSupplier typeSupplier : dynamic) {
			IProcessSupplier<SettingType> supplier = typeSupplier.getProcessorSupplier(processorId);
			if(supplier != null) {
				return supplier;
			}
		}
		return null;
	}

	/**
	 * 
	 * @param processorId
	 * @return the preferences for this processor id
	 */
	public static <T> ProcessorPreferences<T> getWorkspacePreferences(IProcessSupplier<T> supplier) {

		return new NodeProcessorPreferences<T>(supplier);
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

		try {
			for(IProcessSupplier<?> supplier : processTypeSupplier.getProcessorSuppliers()) {
				String processorId = supplier.getId();
				IProcessTypeSupplier typeSupplier = localProcessSupplier.get(processorId);
				if(typeSupplier != null) {
					logger.warn("The processor id " + processorId + " is already defined by " + typeSupplier.getClass().getSimpleName() + " and is ignored for redefining supplier " + processTypeSupplier.getClass().getSimpleName());
				} else {
					localProcessSupplier.put(processorId, processTypeSupplier);
				}
			}
		} catch(Exception e) {
			logger.warn(e);
		}
	}

	/**
	 * 
	 * @param dataTypes
	 * @return the matching {@link IProcessTypeSupplier} order by category name
	 */
	public List<IProcessTypeSupplier> getProcessorTypeSuppliers(Collection<? extends DataType> dataTypes) {

		List<IProcessTypeSupplier> supplier = new ArrayList<>();
		addMatchingSupplier(dataTypes, supplier, localProcessSupplier.values());
		addMatchingSupplier(dataTypes, supplier, Arrays.asList(Activator.geIProcessTypeSuppliers()));
		Collections.sort(supplier, CATEGORY_COMPARATOR);
		return supplier;
	}

	private void addMatchingSupplier(Collection<? extends DataType> dataTypes, List<IProcessTypeSupplier> supplier, Iterable<IProcessTypeSupplier> candidates) {

		for(IProcessTypeSupplier processTypeSupplier : candidates) {
			if(supplier.contains(processTypeSupplier)) {
				continue;
			}
			outer:
			for(IProcessSupplier<?> processSupplier : processTypeSupplier.getProcessorSuppliers()) {
				for(DataType dataType : dataTypes) {
					if(processSupplier.getSupportedDataTypes().contains(dataType)) {
						supplier.add(processTypeSupplier);
						break outer;
					}
				}
			}
		}
	}

	public <T> IProcessingInfo<T> applyProcessor(IChromatogramSelection<?, ?> chromatogramSelection, IProcessMethod processMethod, IProgressMonitor monitor) {

		List<IChromatogramSelection<?, ?>> chromatogramSelections = new ArrayList<>();
		if(chromatogramSelection != null) {
			chromatogramSelections.add(chromatogramSelection);
		}
		return applyProcessor(chromatogramSelections, processMethod, monitor);
	}

	public <T, X> IProcessingInfo<T> applyProcessor(List<? extends IChromatogramSelection<?, ?>> chromatogramSelections, IProcessMethod processMethod, IProgressMonitor monitor) {

		SubMonitor subMonitor = SubMonitor.convert(monitor, chromatogramSelections.size());
		IProcessingInfo<T> processingInfo = new ProcessingInfo<>();
		for(IChromatogramSelection<?, ?> chromatogramSelection : chromatogramSelections) {
			/*
			 * Process referenced chromatograms? Will make things more complex.
			 * Discussion needed. Think of RI calculation. Are the RIs of the
			 * master taken or the RIs of the selected reference? What about the
			 * column?
			 */
			applyProcessor(processMethod, new BiConsumer<IProcessSupplier<X>, X>() {

				@Override
				public void accept(IProcessSupplier<X> processSupplier, X settings) {

					IProcessTypeSupplier processTypeSupplier = processSupplier.getTypeSupplier();
					if(processTypeSupplier instanceof IChromatogramSelectionProcessTypeSupplier) {
						IChromatogramSelectionProcessTypeSupplier chromatogramSelectionProcessTypeSupplier = (IChromatogramSelectionProcessTypeSupplier)processTypeSupplier;
						/*
						 * If processEntry.getJsonSettings() == {}
						 * (IProcessEntry.EMPTY_JSON_SETTINGS), the processSettings
						 * class will be null. The applyProcessor method manages how
						 * to handle this situation. By default, the system settings
						 * of the plugin shall be used instead.
						 */
						IProcessSettings processSettings;
						if(settings instanceof IProcessSettings) {
							processSettings = (IProcessSettings)settings;
						} else {
							processSettings = null;
						}
						IProcessingInfo<?> processorResult = chromatogramSelectionProcessTypeSupplier.applyProcessor(chromatogramSelection, processSupplier.getId(), processSettings, monitor);
						processingInfo.addMessages(processorResult);
					}
				}
			}, processingInfo);
			subMonitor.worked(1);
		}
		return processingInfo;
	}

	public <X> Collection<? extends IMeasurement> applyProcessor(Collection<? extends IMeasurement> measurements, IProcessMethod processMethod, MessageConsumer messageConsumer, IProgressMonitor monitor) {

		SubMonitor subMonitor = SubMonitor.convert(monitor, "Processing files", processMethod.size() * 100);
		AtomicReference<Collection<? extends IMeasurement>> result = new AtomicReference<Collection<? extends IMeasurement>>(measurements);
		applyProcessor(processMethod, new BiConsumer<IProcessSupplier<X>, X>() {

			@Override
			public void accept(IProcessSupplier<X> processor, X settings) {

				IProcessTypeSupplier processTypeSupplier = processor.getTypeSupplier();
				if(processTypeSupplier instanceof IMeasurementProcessTypeSupplier) {
					result.set(((IMeasurementProcessTypeSupplier)processTypeSupplier).applyProcessor(result.get(), processor.getId(), settings, messageConsumer, subMonitor.split(100)));
				}
			}
		}, messageConsumer);
		return result.get();
	}

	private <X> void applyProcessor(IProcessMethod processMethod, BiConsumer<IProcessSupplier<X>, X> consumer, MessageConsumer messages) {

		for(IProcessEntry processEntry : processMethod) {
			String processorId = processEntry.getProcessorId();
			IProcessSupplier<X> processSupplier = getSupplier(processorId);
			if(processSupplier == null) {
				messages.addWarnMessage(processEntry.getName(), "processor not found, will be skipped");
				continue;
			}
			X settings;
			try {
				settings = processSupplier.getPreferences().getSettings(processEntry.getJsonSettings());
			} catch(IOException e) {
				messages.addWarnMessage(processorId, "the settings can't be read", e);
				continue;
			}
			consumer.accept(processSupplier, settings);
		}
	}

	public int validate(IProcessEntry processEntry) {

		if(processEntry == null) {
			return IStatus.ERROR;
		}
		IProcessSupplier<?> supplier = getSupplier(processEntry.getProcessorId());
		if(supplier != null) {
			return validateSettings(processEntry, supplier);
		} else {
			return IStatus.ERROR;
		}
	}

	private static int validateSettings(IProcessEntry processEntry, IProcessSupplier<?> supplier) {

		if(supplier.getSettingsClass() == null) {
			return IStatus.WARNING;
		}
		try {
			supplier.getPreferences().getSettings(processEntry.getJsonSettings());
		} catch(IOException e) {
			return IStatus.ERROR;
		}
		if(processEntry.getJsonSettings().equals(IProcessEntry.EMPTY_JSON_SETTINGS)) {
			return IStatus.INFO;
		} else {
			return IStatus.OK;
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
		public boolean isAskForSettings() {

			trySync();
			return node.getBoolean(KEY_ASK_FOR_SETTINGS, true);
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

			trySync();
			return node.getBoolean(KEY_USE_SYSTEM_DEFAULTS, true);
		}

		@Override
		public void setUseSystemDefaults(boolean useSystemDefaults) {

			node.putBoolean(KEY_USE_SYSTEM_DEFAULTS, useSystemDefaults);
			tryFlush();
		}

		@Override
		public SettingsSerialization getSerialization() {

			return SETTINGS_SERIALIZATION;
		}

		@Override
		public T getUserSettings() throws IOException {

			return getSettings(getUserSettingsAsString());
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

		@Override
		public T getSettings(String serializedString) throws IOException {

			Class<T> settingsClass = getSupplier().getSettingsClass();
			if(serializedString == null || settingsClass == null) {
				return null;
			}
			return getSerialization().fromString(settingsClass, serializedString);
		}
	}
}
