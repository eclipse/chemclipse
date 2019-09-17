/*******************************************************************************
 * Copyright (c) 2008, 2019 Lablicate GmbH.
 *
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 * Alexander Kerner - Generics
 * Christoph Läubrich - provide default methods and add an lower bound for generic
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.msd.identifier.peak;

import java.util.List;

import org.eclipse.chemclipse.chromatogram.msd.identifier.settings.IPeakIdentifierSettingsMSD;
import org.eclipse.chemclipse.model.identifier.IIdentificationResults;
import org.eclipse.chemclipse.msd.model.core.IPeakMSD;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.core.runtime.IProgressMonitor;

public interface IPeakIdentifierMSD<T extends IIdentificationResults> {

	// /**
	// * Identifies a peak.
	// *
	// * @param peak
	// * @param peakIdentifierSettings
	// * @param monitor
	// * @return {@link IProcessingInfo}
	// */
	// default IProcessingInfo<T> identify(IPeakMSD peak, IPeakIdentifierSettingsMSD peakIdentifierSettings, IProgressMonitor monitor) {
	//
	// return identify(Collections.singletonList(peak), peakIdentifierSettings, monitor);
	// }
	//
	// /**
	// * Identifies the peak.
	// *
	// * @param peak
	// * @param monitor
	// * @return {@link IProcessingInfo}
	// */
	// default IProcessingInfo<T> identify(IPeakMSD peak, IProgressMonitor monitor) {
	//
	// return identify(peak, null, monitor);
	// }
	/**
	 * Identifies a list of peaks.
	 *
	 * @param peaks
	 * @param peakIdentifierSettings
	 * @param monitor
	 * @return {@link IProcessingInfo}
	 */
	IProcessingInfo<T> identify(List<? extends IPeakMSD> peaks, IPeakIdentifierSettingsMSD peakIdentifierSettings, IProgressMonitor monitor);
	// /**
	// * The same as the other method but without settings.
	// *
	// * @param peaks
	// * @param monitor
	// * @return {@link IProcessingInfo}
	// */
	// default IProcessingInfo<T> identify(List<? extends IPeakMSD> peaks, IProgressMonitor monitor) {
	//
	// return identify(peaks, null, monitor);
	// }
	//
	// default IProcessingInfo<T> identify(IChromatogramSelectionMSD chromatogramSelectionMSD, IProgressMonitor monitor) {
	//
	// return identify(chromatogramSelectionMSD, null, monitor);
	// }
	//
	// default IProcessingInfo<T> identify(IChromatogramSelectionMSD chromatogramSelectionMSD, IPeakIdentifierSettingsMSD peakIdentifierSettings, IProgressMonitor monitor) {
	//
	// return identify(chromatogramSelectionMSD.getChromatogram().getPeaks(chromatogramSelectionMSD), peakIdentifierSettings, monitor);
	// }
}
