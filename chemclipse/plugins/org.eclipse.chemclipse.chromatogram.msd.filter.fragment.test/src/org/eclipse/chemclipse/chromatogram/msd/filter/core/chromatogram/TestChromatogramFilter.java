/*******************************************************************************
 * Copyright (c) 2008, 2016 Philip (eselmeister) Wenig.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip (eselmeister) Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.msd.filter.core.chromatogram;

import org.eclipse.core.runtime.IProgressMonitor;

import org.eclipse.chemclipse.chromatogram.filter.processing.ChromatogramFilterProcessingInfo;
import org.eclipse.chemclipse.chromatogram.filter.processing.IChromatogramFilterProcessingInfo;
import org.eclipse.chemclipse.chromatogram.filter.settings.IChromatogramFilterSettings;
import org.eclipse.chemclipse.chromatogram.msd.filter.core.chromatogram.AbstractChromatogramFilter;
import org.eclipse.chemclipse.msd.model.core.selection.IChromatogramSelectionMSD;

/**
 * THIS IS A TEST CLASS. DO NOT USE IT BUT ONLY FOR TESTING PURPOSE.
 * 
 * @author eselmeister
 */
public class TestChromatogramFilter extends AbstractChromatogramFilter {

	@Override
	public IChromatogramFilterProcessingInfo applyFilter(IChromatogramSelectionMSD chromatogramSelection, IChromatogramFilterSettings chromatogramFilterSettings, IProgressMonitor monitor) {

		return new ChromatogramFilterProcessingInfo();
	}

	@Override
	public IChromatogramFilterProcessingInfo applyFilter(IChromatogramSelectionMSD chromatogramSelection, IProgressMonitor monitor) {

		return new ChromatogramFilterProcessingInfo();
	}
}
