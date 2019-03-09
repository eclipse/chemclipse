/*******************************************************************************
 * Copyright (c) 2008, 2018, 2019 Lablicate GmbH.
 *
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 * Alexander Kerner - Generics
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.msd.filter.core.chromatogram;

import org.eclipse.chemclipse.chromatogram.filter.settings.IChromatogramFilterSettings;
import org.eclipse.chemclipse.msd.model.core.selection.IChromatogramSelectionMSD;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;

public interface IChromatogramFilterMSD<R> {

	/**
	 * Apply the filter in the given chromatogram selection and take care of the
	 * filter settings.<br/>
	 * Return an {@link IProcessingInfo} instance.<br/>
	 * If there is no monitor instance, use a {@link NullProgressMonitor}.
	 *
	 * @param chromatogramSelection
	 * @param chromatogramFilterSettings
	 * @param monitor
	 * @return {@link IProcessingInfo}
	 */
	IProcessingInfo<R> applyFilter(IChromatogramSelectionMSD chromatogramSelection, IChromatogramFilterSettings chromatogramFilterSettings, IProgressMonitor monitor);

	/**
	 * Apply the filter in the given chromatogram selection.
	 * Return an {@link IProcessingInfo} instance.<br/>
	 * If there is no monitor instance, use a {@link NullProgressMonitor}.
	 *
	 * @param chromatogramSelection
	 * @param monitor
	 * @return {@link IProcessingInfo}
	 */
	IProcessingInfo<R> applyFilter(IChromatogramSelectionMSD chromatogramSelection, IProgressMonitor monitor);

	/**
	 * Validates the selection and settings and returns a process info instance.
	 *
	 * @param chromatogramSelection
	 * @param chromatogramFilterSettings
	 * @return {@link IProcessingInfo}
	 */
	IProcessingInfo<R> validate(IChromatogramSelectionMSD chromatogramSelection, IChromatogramFilterSettings chromatogramFilterSettings);
}
