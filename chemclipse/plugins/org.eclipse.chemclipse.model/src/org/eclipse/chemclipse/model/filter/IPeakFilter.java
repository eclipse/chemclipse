/*******************************************************************************
 * Copyright (c) 2019, 2023 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Christoph Läubrich - initial API and implementation
 * Philip Wenig - refactor menu categories
 *******************************************************************************/
package org.eclipse.chemclipse.model.filter;

import java.util.Collection;

import org.eclipse.chemclipse.model.core.IPeak;
import org.eclipse.chemclipse.model.selection.IChromatogramSelection;
import org.eclipse.chemclipse.processing.DataCategory;
import org.eclipse.chemclipse.processing.filter.Filter;
import org.eclipse.chemclipse.processing.supplier.ProcessExecutionContext;
import org.eclipse.core.runtime.IProgressMonitor;

/**
 * A {@link Filter} Extension interface for filters that can work on {@link IPeak}s.
 * This is part of the ChemClipse FilterFramework, to make the Filter available simply register it with the OSGi Service factory under the {@link Filter} interface, implementors are encouraged to also register each filter under all sub(filter) interface.
 * 
 * @author Christoph Läubrich
 *
 * @param <ConfigType>
 */
public interface IPeakFilter<ConfigType> extends Filter<ConfigType> {

	/**
	 * Filters the given Collection of {@link IPeak}s with this filter and returns the result.
	 * The resulting Collection could either be the same or a new collection, might have more or less items
	 * 
	 * @param configuration
	 *            the configuration to apply or <code>null</code> if no special configuration is desired
	 * 
	 * @param context
	 *            to be used for access to progress monitor and messaging
	 * @param monitor
	 *            a {@link IProgressMonitor} to report progress of the filtering or <code>null</code> if no progress is desired
	 * @throws IllegalArgumentException
	 *             if the given {@link IPeak}s are incompatible with this filter ({@link #acceptsPeaks(IPeak)} returns <code>false</code>)
	 */
	void filterPeaks(IChromatogramSelection<?, ?> chromatogramSelection, ConfigType configuration, ProcessExecutionContext context) throws IllegalArgumentException;

	/**
	 * Creates a new configuration that is specially suited for the given {@link IPeak} types
	 * 
	 * @param items
	 * @return a new configuration for this items or the default configuration if items is empty or no suitable configuration can be created
	 * 
	 */
	default ConfigType createConfiguration(Collection<IPeak> items) {

		return createNewConfiguration();
	}

	/**
	 * The default implementation returns all data categories, implementors might override if the can only work on a certain sub category
	 */
	@Override
	default DataCategory[] getDataCategories() {

		return new DataCategory[]{DataCategory.CSD, DataCategory.MSD, DataCategory.WSD, DataCategory.VSD};
	}
}