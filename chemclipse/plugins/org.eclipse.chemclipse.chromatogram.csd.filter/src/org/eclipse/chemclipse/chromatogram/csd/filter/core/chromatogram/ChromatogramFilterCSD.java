/*******************************************************************************
 * Copyright (c) 2015, 2023 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.csd.filter.core.chromatogram;

import org.eclipse.chemclipse.chromatogram.csd.filter.l10n.Messages;
import org.eclipse.chemclipse.chromatogram.filter.result.IChromatogramFilterResult;
import org.eclipse.chemclipse.chromatogram.filter.settings.IChromatogramFilterSettings;
import org.eclipse.chemclipse.csd.model.core.selection.IChromatogramSelectionCSD;
import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.chemclipse.processing.core.ProcessingInfo;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Platform;

public class ChromatogramFilterCSD {

	private static final Logger logger = Logger.getLogger(ChromatogramFilterCSD.class);
	private static final String EXTENSION_POINT = "org.eclipse.chemclipse.chromatogram.csd.filter.chromatogramFilterSupplier"; //$NON-NLS-1$
	/*
	 * These are the attributes of the extension point elements.
	 */
	private static final String ID = "id"; //$NON-NLS-1$
	private static final String DESCRIPTION = "description"; //$NON-NLS-1$
	private static final String FILTER_NAME = "filterName"; //$NON-NLS-1$
	private static final String FILTER = "filter"; //$NON-NLS-1$
	private static final String FILTER_SETTINGS = "filterSettings"; //$NON-NLS-1$

	/**
	 * This class is a singleton. Use only static methods.
	 */
	private ChromatogramFilterCSD() {

	}

	/**
	 * Applies the specified filter (filterID) with the given {@link IChromatogramFilterSettings} on the {@link IChromatogramSelectionMSD} .<br/>
	 * The filter can be supported as a plugin through the extension point
	 * mechanism.<br/>
	 * You could think of filters that for example remove background
	 * automatically or mean normalize the chromatogram.
	 * 
	 * @param chromatogramSelection
	 * @param chromatogramFilterSettings
	 * @param filterId
	 * @return {@link IProcessingInfo}
	 */
	public static IProcessingInfo<IChromatogramFilterResult> applyFilter(IChromatogramSelectionCSD chromatogramSelection, IChromatogramFilterSettings chromatogramFilterSettings, String filterId, IProgressMonitor monitor) {

		IProcessingInfo<IChromatogramFilterResult> processingInfo;
		IChromatogramFilterCSD chromatogramFilter = getChromatogramFilter(filterId);
		if(chromatogramFilter != null) {
			processingInfo = chromatogramFilter.applyFilter(chromatogramSelection, chromatogramFilterSettings, monitor);
		} else {
			processingInfo = new ProcessingInfo<>();
			processingInfo.addErrorMessage(Messages.chromatogramFilter, Messages.noChromatogramFilterAvailable);
		}
		return processingInfo;
	}

	// TODO JUnit
	/**
	 * Applies the specified filter, but retrieves the IChromatogramFilterSettings dynamically.<br/>
	 * See also method: applyFilter(IChromatogramSelection<?, ?> chromatogramSelection, IChromatogramFilterSettings chromatogramFilterSettings, String filterId, IProgressMonitor monitor)
	 * 
	 * @param chromatogramSelection
	 * @param filterId
	 * @param monitor
	 * @return {@link IProcessingInfo}
	 */
	public static IProcessingInfo<IChromatogramFilterResult> applyFilter(IChromatogramSelectionCSD chromatogramSelection, String filterId, IProgressMonitor monitor) {

		IProcessingInfo<IChromatogramFilterResult> processingInfo;
		IChromatogramFilterCSD chromatogramFilter = getChromatogramFilter(filterId);
		if(chromatogramFilter != null) {
			processingInfo = chromatogramFilter.applyFilter(chromatogramSelection, monitor);
		} else {
			processingInfo = new ProcessingInfo<>();
			processingInfo.addErrorMessage(Messages.chromatogramFilter, Messages.noChromatogramFilterAvailable);
		}
		return processingInfo;
	}

	public static IChromatogramFilterSupportCSD getChromatogramFilterSupport() {

		ChromatogramFilterSupplierCSD supplier;
		ChromatogramFilterSupportCSD filterSupport = new ChromatogramFilterSupportCSD();
		/*
		 * Search in the extension registry and fill the comparison support
		 * object with supplier information.
		 */
		IExtensionRegistry registry = Platform.getExtensionRegistry();
		IConfigurationElement[] extensions = registry.getConfigurationElementsFor(EXTENSION_POINT);
		for(IConfigurationElement element : extensions) {
			supplier = new ChromatogramFilterSupplierCSD();
			supplier.setId(element.getAttribute(ID));
			supplier.setDescription(element.getAttribute(DESCRIPTION));
			supplier.setFilterName(element.getAttribute(FILTER_NAME));
			if(element.getAttribute(FILTER_SETTINGS) != null) {
				try {
					IChromatogramFilterSettings instance = (IChromatogramFilterSettings)element.createExecutableExtension(FILTER_SETTINGS);
					supplier.setFilterSettingsClass(instance.getClass());
				} catch(CoreException e) {
					logger.warn(e);
					// settings class is optional, set null instead
					supplier.setFilterSettingsClass(null);
				}
			}
			filterSupport.add(supplier);
		}
		return filterSupport;
	}

	// --------------------------------------------private methods
	/**
	 * Returns a {@link IChromatogramFilterCSD} instance given by the filterId or
	 * null, if none is available.
	 */
	private static IChromatogramFilterCSD getChromatogramFilter(final String filterId) {

		IConfigurationElement element;
		element = getConfigurationElement(filterId);
		IChromatogramFilterCSD instance = null;
		if(element != null) {
			try {
				instance = (IChromatogramFilterCSD)element.createExecutableExtension(FILTER);
			} catch(CoreException e) {
				logger.warn(e);
			}
		}
		return instance;
	}

	/**
	 * Returns an {@link IChromatogramFilterCSD} instance or null if none is
	 * available.
	 * 
	 * @param filterId
	 * @return IConfigurationElement
	 */
	private static IConfigurationElement getConfigurationElement(final String filterId) {

		if(filterId.isEmpty()) {
			return null;
		}
		IExtensionRegistry registry = Platform.getExtensionRegistry();
		IConfigurationElement[] elements = registry.getConfigurationElementsFor(EXTENSION_POINT);
		for(IConfigurationElement element : elements) {
			if(element.getAttribute(ID).equals(filterId)) {
				return element;
			}
		}
		return null;
	}
	// --------------------------------------------private methods
}
