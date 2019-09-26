/*******************************************************************************
 * Copyright (c) 2019 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Christoph Läubrich - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.model.supplier;

import org.eclipse.chemclipse.model.selection.IChromatogramSelection;
import org.eclipse.chemclipse.processing.core.MessageConsumer;
import org.eclipse.chemclipse.processing.supplier.IProcessSupplier;
import org.eclipse.core.runtime.IProgressMonitor;

public interface IChromatogramSelectionProcessSupplier<SettingType> extends IProcessSupplier<SettingType> {

	/**
	 * Apply this processor to the given chromatogram selection
	 * 
	 * @param chromatogramSelection
	 *            the {@link IChromatogramSelection} to process
	 * @param processSettings
	 *            settings to use
	 * @param monitor
	 *            the monitor to use for reporting progress or <code>null</code> if no progress is desired
	 * @return the processed {@link IChromatogramSelection}
	 */
	IChromatogramSelection<?, ?> apply(IChromatogramSelection<?, ?> chromatogramSelection, SettingType processSettings, MessageConsumer messageConsumer, IProgressMonitor monitor);
}
