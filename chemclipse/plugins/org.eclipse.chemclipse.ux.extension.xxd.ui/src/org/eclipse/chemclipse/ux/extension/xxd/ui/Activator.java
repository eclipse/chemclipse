/*******************************************************************************
 * Copyright (c) 2015, 2020 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 * Christoph Läubrich - init DataUpdateSupport on first access
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui;

import java.util.Map;

import org.eclipse.chemclipse.msd.model.preferences.PreferenceSupplier;
import org.eclipse.chemclipse.support.events.IChemClipseEvents;
import org.eclipse.chemclipse.support.preferences.IPreferenceSupplier;
import org.eclipse.chemclipse.support.ui.activator.AbstractActivatorUI;
import org.eclipse.chemclipse.swt.ui.services.IMoleculeImageService;
import org.eclipse.chemclipse.ux.extension.xxd.ui.part.support.DataUpdateSupport;
import org.eclipse.core.runtime.Platform;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.osgi.service.datalocation.Location;
import org.eclipse.ui.preferences.ScopedPreferenceStore;
import org.osgi.framework.BundleContext;
import org.osgi.util.tracker.ServiceTracker;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractActivatorUI {

	private static Activator plugin;
	//
	private ScopedPreferenceStore preferenceStoreSubtract;
	private DataUpdateSupport dataUpdateSupport;
	//
	private ServiceTracker<IMoleculeImageService, IMoleculeImageService> moleculeImageServiceTracker = null;

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	@Override
	public void start(BundleContext context) throws Exception {

		super.start(context);
		plugin = this;
		initializePreferenceStoreSubtract(PreferenceSupplier.INSTANCE());
		//
		moleculeImageServiceTracker = new ServiceTracker<>(context, IMoleculeImageService.class, null);
		moleculeImageServiceTracker.open();
		/*
		 * Don't call here:
		 * ---
		 * getDataUpdateSupport()
		 * getEclipseContext()
		 * ---
		 * The context is initialized, but the application values are not available yet.
		 */
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	@Override
	public void stop(BundleContext context) throws Exception {

		plugin = null;
		dataUpdateSupport = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static Activator getDefault() {

		return plugin;
	}

	public ScopedPreferenceStore getPreferenceStoreSubtract() {

		return preferenceStoreSubtract;
	}

	public String getSettingsPath() {

		Location location = Platform.getUserLocation();
		return location.getURL().getPath().toString();
	}

	public DataUpdateSupport getDataUpdateSupport() {

		if(dataUpdateSupport == null) {
			dataUpdateSupport = new DataUpdateSupport(getEventBroker());
			initialize(dataUpdateSupport);
		}
		return dataUpdateSupport;
	}

	public Object[] getMoleculeImageServices() {

		return moleculeImageServiceTracker.getServices();
	}

	private void initialize(DataUpdateSupport dataUpdateSupport) {

		/*
		 * The specific events will be removed soon.
		 */
		dataUpdateSupport.subscribe(IChemClipseEvents.TOPIC_CHROMATOGRAM_MSD_UPDATE_CHROMATOGRAM_SELECTION, IChemClipseEvents.PROPERTY_CHROMATOGRAM_SELECTION);
		dataUpdateSupport.subscribe(IChemClipseEvents.TOPIC_CHROMATOGRAM_CSD_UPDATE_CHROMATOGRAM_SELECTION, IChemClipseEvents.PROPERTY_CHROMATOGRAM_SELECTION);
		dataUpdateSupport.subscribe(IChemClipseEvents.TOPIC_CHROMATOGRAM_WSD_UPDATE_CHROMATOGRAM_SELECTION, IChemClipseEvents.PROPERTY_CHROMATOGRAM_SELECTION);
		/*
		 * Register the data update support early to get all recent selections.
		 */
		dataUpdateSupport.subscribe(IChemClipseEvents.TOPIC_CHROMATOGRAM_XXD_UPDATE_SELECTION, IChemClipseEvents.PROPERTY_CHROMATOGRAM_SELECTION_XXD);
		dataUpdateSupport.subscribe(IChemClipseEvents.TOPIC_SCAN_XXD_UPDATE_SELECTION, IChemClipseEvents.PROPERTY_SELECTED_SCAN);
		dataUpdateSupport.subscribe(IChemClipseEvents.TOPIC_PEAK_XXD_UPDATE_SELECTION, IChemClipseEvents.PROPERTY_SELECTED_PEAK);
		dataUpdateSupport.subscribe(IChemClipseEvents.TOPIC_IDENTIFICATION_TARGETS_UPDATE_SELECTION, IChemClipseEvents.PROPERTY_SELECTED_IDENTIFICATION_TARGET_SUPPLIER);
		dataUpdateSupport.subscribe(IChemClipseEvents.TOPIC_IDENTIFICATION_TARGET_UPDATE, IChemClipseEvents.PROPERTY_IDENTIFICATION_TARGET);
		/*
		 * Subtract MS
		 */
		dataUpdateSupport.subscribe(IChemClipseEvents.TOPIC_UPDATE_SESSION_SUBTRACT_MASS_SPECTRUM, IChemClipseEvents.PROPERTY_UPDATE_SESSION_SUBTRACT_MASS_SPECTRUM);
		/*
		 * Perspective / Parts
		 */
		dataUpdateSupport.subscribe(IChemClipseEvents.TOPIC_APPLICATION_SELECT_PERSPECTIVE, IChemClipseEvents.IEVENTBROKER_DATA);
		dataUpdateSupport.subscribe(IChemClipseEvents.TOPIC_PART_CLOSED, IChemClipseEvents.IEVENTBROKER_DATA);
		/*
		 * Unload needed?
		 */
		dataUpdateSupport.subscribe(IChemClipseEvents.TOPIC_CHROMATOGRAM_XXD_UNLOAD_SELECTION, IChemClipseEvents.PROPERTY_CHROMATOGRAM_SELECTION_XXD);
		dataUpdateSupport.subscribe(IChemClipseEvents.TOPIC_SCAN_XXD_UNLOAD_SELECTION, IChemClipseEvents.PROPERTY_SELECTED_SCAN);
		dataUpdateSupport.subscribe(IChemClipseEvents.TOPIC_PEAK_XXD_UNLOAD_SELECTION, IChemClipseEvents.PROPERTY_SELECTED_PEAK);
		dataUpdateSupport.subscribe(IChemClipseEvents.TOPIC_IDENTIFICATION_TARGETS_UNLOAD_SELECTION, IChemClipseEvents.PROPERTY_SELECTED_IDENTIFICATION_TARGET_SUPPLIER);
		/*
		 * Raw files
		 */
		dataUpdateSupport.subscribe(IChemClipseEvents.TOPIC_CHROMATOGRAM_MSD_UPDATE_RAWFILE, IChemClipseEvents.PROPERTY_CHROMATOGRAM_MSD_RAWFILE);
		dataUpdateSupport.subscribe(IChemClipseEvents.TOPIC_CHROMATOGRAM_CSD_UPDATE_RAWFILE, IChemClipseEvents.PROPERTY_CHROMATOGRAM_CSD_RAWFILE);
		dataUpdateSupport.subscribe(IChemClipseEvents.TOPIC_CHROMATOGRAM_WSD_UPDATE_RAWFILE, IChemClipseEvents.PROPERTY_CHROMATOGRAM_WSD_RAWFILE);
		dataUpdateSupport.subscribe(IChemClipseEvents.TOPIC_SCAN_NMR_UPDATE_RAWFILE, IChemClipseEvents.PROPERTY_SCAN_NMR_RAWFILE);
		dataUpdateSupport.subscribe(IChemClipseEvents.TOPIC_SCAN_XIR_UPDATE_RAWFILE, IChemClipseEvents.PROPERTY_SCAN_XIR_RAWFILE);
		/*
		 * Overview
		 */
		dataUpdateSupport.subscribe(IChemClipseEvents.TOPIC_CHROMATOGRAM_MSD_UPDATE_OVERVIEW, IChemClipseEvents.PROPERTY_CHROMATOGRAM_MSD_OVERVIEW);
		dataUpdateSupport.subscribe(IChemClipseEvents.TOPIC_CHROMATOGRAM_CSD_UPDATE_OVERVIEW, IChemClipseEvents.PROPERTY_CHROMATOGRAM_CSD_OVERVIEW);
		dataUpdateSupport.subscribe(IChemClipseEvents.TOPIC_CHROMATOGRAM_WSD_UPDATE_OVERVIEW, IChemClipseEvents.PROPERTY_CHROMATOGRAM_WSD_OVERVIEW);
		dataUpdateSupport.subscribe(IChemClipseEvents.TOPIC_SCAN_NMR_UPDATE_OVERVIEW, IChemClipseEvents.PROPERTY_SCAN_NMR_OVERVIEW);
		dataUpdateSupport.subscribe(IChemClipseEvents.TOPIC_SCAN_XIR_UPDATE_OVERVIEW, IChemClipseEvents.PROPERTY_SCAN_XIR_OVERVIEW);
		/*
		 * Quantitation
		 */
		dataUpdateSupport.subscribe(IChemClipseEvents.TOPIC_QUANT_DB_COMPOUND_UPDATE, IChemClipseEvents.PROPERTY_QUANT_DB_COMPOUND);
		/*
		 * PCR
		 */
		dataUpdateSupport.subscribe(IChemClipseEvents.TOPIC_WELL_PCR_UPDATE_SELECTION, IEventBroker.DATA);
		dataUpdateSupport.subscribe(IChemClipseEvents.TOPIC_WELL_PCR_UNLOAD_SELECTION, IEventBroker.DATA);
		dataUpdateSupport.subscribe(IChemClipseEvents.TOPIC_PLATE_PCR_UPDATE_SELECTION, IEventBroker.DATA);
		dataUpdateSupport.subscribe(IChemClipseEvents.TOPIC_PLATE_PCR_UNLOAD_SELECTION, IEventBroker.DATA);
		/*
		 * Unload
		 */
		dataUpdateSupport.subscribe(IChemClipseEvents.TOPIC_CHROMATOGRAM_XXD_UPDATE_NONE, IChemClipseEvents.PROPERTY_CHROMATOGRAM_XXD_RAWFILE);
	}

	private void initializePreferenceStoreSubtract(IPreferenceSupplier preferenceSupplier) {

		if(preferenceSupplier != null) {
			/*
			 * Set the default values.
			 */
			preferenceStoreSubtract = new ScopedPreferenceStore(preferenceSupplier.getScopeContext(), preferenceSupplier.getPreferenceNode());
			Map<String, String> initializationEntries = preferenceSupplier.getDefaultValues();
			for(Map.Entry<String, String> entry : initializationEntries.entrySet()) {
				preferenceStoreSubtract.setDefault(entry.getKey(), entry.getValue());
			}
		}
	}
}
