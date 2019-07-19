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
package org.eclipse.chemclipse.xxd.process.support;

import org.eclipse.chemclipse.model.selection.IChromatogramSelection;
import org.eclipse.chemclipse.model.settings.IProcessSettings;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.core.runtime.IProgressMonitor;

public interface IChromatogramSelectionProcessTypeSupplier extends IProcessTypeSupplier {

	/**
	 * Apply the given processor to the given chromatogram selection
	 * 
	 * @param chromatogramSelection
	 *            the {@link IChromatogramSelection} to process
	 * @param processorId
	 *            the processor to call
	 * @param processSettings
	 *            settings to use
	 * @param monitor
	 *            the monitor to use for reporting progress or <code>null</code> if no progress is desired
	 * @return <code>null</code> if the given processor id can't be found or the processing result containing the processed {@link IChromatogramSelection}
	 */
	IProcessingInfo<IChromatogramSelection<?, ?>> applyProcessor(IChromatogramSelection<?, ?> chromatogramSelection, String processorId, IProcessSettings processSettings, IProgressMonitor monitor);
}
