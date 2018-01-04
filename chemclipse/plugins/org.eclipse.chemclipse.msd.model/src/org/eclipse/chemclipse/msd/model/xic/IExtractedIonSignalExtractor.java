/*******************************************************************************
 * Copyright (c) 2012, 2018 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.model.xic;

import org.eclipse.chemclipse.msd.model.core.selection.IChromatogramSelectionMSD;

public interface IExtractedIonSignalExtractor {

	/**
	 * Use this methods if you want to edit a selected ion range and want to
	 * modify the extracted ion signals afterwards.<br/>
	 * This method could be important if you try to deconvolute a chromatogram.<br/>
	 * If you want to get the extracted ion signals in a range of e.g. ion
	 * 28-600 back for each scan, use:<br/>
	 * getExtractedIonSignals(28, 600);<br/>
	 * So each IExtractedIonSignal contains an abundance array for the range ion
	 * 28-600. You can now modify each ion in it's abundance.<br/>
	 * This method is much faster as "getExtractedIonSignals()" because the
	 * min-max ion values are not calculated by each mass spectrum.
	 * 
	 * @param startIon
	 * @param stopIon
	 * @return IExtractedIonSignals
	 */
	IExtractedIonSignals getExtractedIonSignals(float startIon, float stopIon);

	/**
	 * Returns a list of all extracted ion signals for each scan.<br/>
	 * The list {@link IExtractedIonSignal}can be accessed via the method
	 * "getAbundance(int ion)" which will return the abundance for the
	 * given ion or null if the ion does not exists.<br/>
	 * The list stores for each scan a IExtractedIonSignal object so it is
	 * possible to get an extracted ion chromatogram for the whole chromatogram.<br/>
	 * You can only edit the stored ion range.<br/>
	 * This could be different for each scan. The one scan could have stored a
	 * ion range from 28-104, the next e.g. in a range of ion 56-382.<br/>
	 * You can only edit the abundance values for the stored scan range.<br/>
	 * If you need a predefined scan range, use getExtractedIonSignals(float
	 * startIon, float stopIon).
	 * 
	 * @return IExtractedIonSignals
	 */
	IExtractedIonSignals getExtractedIonSignals();

	/**
	 * Returns an {@link IExtractedIonSignals} object in the range of the given
	 * chromatogram selection.<br/>
	 * It means, that the object covers only the retention time range
	 * (respectively scan range) given in the chromatogram selection.<br/>
	 * The stored chromatogram in the chromatogram selection must be the same a
	 * the called one, otherwise an empty object will be returned.<br/>
	 * Why such a restriction? If another chromatogram was used and the
	 * retention time is wider, it would cause a out of bounds exception.
	 * 
	 * @param chromatogramSelection
	 * @return {@link IExtractedIonSignals}
	 */
	IExtractedIonSignals getExtractedIonSignals(IChromatogramSelectionMSD chromatogramSelection);

	/**
	 * Calculates the extracted ion signals object in the range of the given
	 * start and stop scan.<br/>
	 * If the start and stop scan are not valid, an empty {@link IExtractedIonSignals} object will be returned.
	 * 
	 * @param startScan
	 * @param stopScan
	 * @return {@link IExtractedIonSignals}
	 */
	IExtractedIonSignals getExtractedIonSignals(int startScan, int stopScan);
}
