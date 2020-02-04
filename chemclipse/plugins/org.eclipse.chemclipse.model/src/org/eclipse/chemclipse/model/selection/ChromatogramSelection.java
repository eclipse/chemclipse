/*******************************************************************************
 * Copyright (c) 2013, 2020 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 * Alexander Kerner - Generics
 *******************************************************************************/
package org.eclipse.chemclipse.model.selection;

import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.model.core.IChromatogram;
import org.eclipse.chemclipse.model.core.IChromatogramPeak;
import org.eclipse.chemclipse.model.core.IScan;
import org.eclipse.chemclipse.model.exceptions.ChromatogramIsNullException;

/**
 * ONLY USE THIS CLASS WHEN NO UPDATES SHALL BE FIRED.
 *
 * This implementation shall be used as a proxy only.
 * It will not fire update changes, hence it is not aware of the
 * contained chromatogram type. It could be a chromatogram detected
 * by a mass selective, a flame ionization or another detector.
 *
 */
public class ChromatogramSelection<T extends IChromatogramPeak, C extends IChromatogram<T>> extends AbstractChromatogramSelection<T, C> implements IChromatogramSelection<T, C> {

	private static final Logger logger = Logger.getLogger(ChromatogramSelection.class);

	public ChromatogramSelection(C chromatogram, boolean fireUpdate) throws ChromatogramIsNullException {
		super(chromatogram, fireUpdate);
		/*
		 * Set all members to default values.<br/> This includes also to set a
		 * valid scan and if exists a valid peak.
		 */
		reset(fireUpdate);
	}

	public ChromatogramSelection(C chromatogram) throws ChromatogramIsNullException {
		this(chromatogram, true);
	}

	@Override
	public void reset() {

		super.reset(false);
	}

	@Override
	public void fireUpdateChange(boolean forceReload) {

		logger.warn("Bad boy - fireUpdateChange(boolean forceReload): don't use the ChromatogramSelection implementation");
	}

	@Override
	public IScan getSelectedScan() {

		logger.warn("Bad boy - getSelectedScan(): don't use the ChromatogramSelection implementation");
		return null;
	}

	@Override
	public void setSelectedScan(IScan selectedScan) {

		logger.warn("Bad boy - setSelectedScan(IScan selectedScan): don't use the ChromatogramSelection implementation");
	}

	@Override
	public void setSelectedScan(IScan selectedScan, boolean update) {

		logger.warn("Bad boy - setSelectedScan(IScan selectedScan, boolean update): don't use the ChromatogramSelection implementation");
	}

	@Override
	public T getSelectedPeak() {

		logger.warn("Bad boy - getSelectedPeak(): don't use the ChromatogramSelection implementation");
		return null;
	}

	@Override
	public void setSelectedPeak(T selectedPeak) {

		logger.warn("Bad boy - setSelectedPeak(IPeak selectedPeak): don't use the ChromatogramSelection implementation");
	}

	@Override
	public void setSelectedPeak(T selectedPeak, boolean update) {

		logger.warn("Bad boy - setSelectedPeak(IPeak selectedPeak): don't use the ChromatogramSelection implementation");
	}

	@Override
	public IScan getSelectedIdentifiedScan() {

		logger.warn("Bad boy - getSelectedIdentifiedScan(): don't use the ChromatogramSelection implementation");
		return null;
	}

	@Override
	public void setSelectedIdentifiedScan(IScan identifiedScan) {

		logger.warn("Bad boy - setSelectedIdentifiedScan: don't use the ChromatogramSelection implementation");
	}

	@Override
	public void removeSelectedIdentifiedScan() {

		logger.warn("Bad boy - removeSelectedIdentifiedScan: don't use the ChromatogramSelection implementation");
	}
}
