/*******************************************************************************
 * Copyright (c) 2021, 2022 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 * Matthias Mailänder - undo/redo for peak deletions
 *******************************************************************************/
package org.eclipse.chemclipse.xxd.filter.peaks;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.model.core.IChromatogram;
import org.eclipse.chemclipse.model.core.IPeak;
import org.eclipse.chemclipse.model.filter.IPeakFilter;
import org.eclipse.chemclipse.model.selection.IChromatogramSelection;
import org.eclipse.chemclipse.rcp.app.undo.UndoContextFactory;
import org.eclipse.chemclipse.xxd.filter.peaks.operations.DeletePeaksOperation;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.OperationHistoryFactory;

public abstract class AbstractPeakFilter<ConfigType> implements IPeakFilter<ConfigType> {

	private static final Logger logger = Logger.getLogger(AbstractPeakFilter.class);

	protected Collection<IPeak> getReadOnlyPeaks(IChromatogramSelection<?, ?> chromatogramSelection) {

		IChromatogram<?> chromatogram = chromatogramSelection.getChromatogram();
		return Collections.unmodifiableCollection(chromatogram.getPeaks(chromatogramSelection));
	}

	protected void deletePeaks(List<IPeak> peaksToDelete, IChromatogramSelection<?, ?> chromatogramSelection) {

		if(!peaksToDelete.isEmpty()) {
			DeletePeaksOperation deletePeaks = new DeletePeaksOperation(chromatogramSelection, peaksToDelete);
			deletePeaks.addContext(UndoContextFactory.getUndoContext());
			try {
				OperationHistoryFactory.getOperationHistory().execute(deletePeaks, null, null);
			} catch(ExecutionException e) {
				logger.warn(e);
			}
		}
	}

	protected void resetPeakSelection(IChromatogramSelection<?, ?> chromatogramSelection) {

		chromatogramSelection.setSelectedPeak(null);
		chromatogramSelection.getChromatogram().setDirty(true);
	}
}
