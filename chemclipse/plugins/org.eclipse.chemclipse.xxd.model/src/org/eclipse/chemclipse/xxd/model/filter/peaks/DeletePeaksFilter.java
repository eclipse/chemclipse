/*******************************************************************************
 * Copyright (c) 2019, 2022 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Christoph Läubrich - initial API and implementation
 * Philip Wenig - improvement update process
 * Matthias Mailänder - undoable
 *******************************************************************************/
package org.eclipse.chemclipse.xxd.model.filter.peaks;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.model.core.IPeak;
import org.eclipse.chemclipse.model.core.IPeakModel;
import org.eclipse.chemclipse.model.filter.IPeakFilter;
import org.eclipse.chemclipse.model.selection.IChromatogramSelection;
import org.eclipse.chemclipse.processing.Processor;
import org.eclipse.chemclipse.processing.core.MessageConsumer;
import org.eclipse.chemclipse.processing.filter.CRUDListener;
import org.eclipse.chemclipse.processing.filter.Filter;
import org.eclipse.chemclipse.rcp.app.undo.UndoContextFactory;
import org.eclipse.chemclipse.xxd.model.operations.DeletePeaksOperation;
import org.eclipse.chemclipse.xxd.model.settings.peaks.DeletePeaksFilterSettings;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.OperationHistoryFactory;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubMonitor;
import org.osgi.service.component.annotations.Component;

@Component(service = {IPeakFilter.class, Filter.class, Processor.class})
public class DeletePeaksFilter extends AbstractPeakFilter<DeletePeaksFilterSettings> {

	private static final Logger logger = Logger.getLogger(DeletePeaksFilter.class);

	@Override
	public String getName() {

		return "Delete Peaks";
	}

	@Override
	public Class<DeletePeaksFilterSettings> getConfigClass() {

		return DeletePeaksFilterSettings.class;
	}

	@Override
	public <X extends IPeak> void filterIPeaks(CRUDListener<X, IPeakModel> listener, DeletePeaksFilterSettings configuration, MessageConsumer messageConsumer, IProgressMonitor monitor) throws IllegalArgumentException {

		Collection<X> peaks = listener.read();
		/*
		 * Settings
		 */
		if(configuration == null) {
			configuration = createConfiguration(peaks);
		}
		/*
		 * Delete the peaks.
		 */
		List<IPeak> peaksToDelete = new ArrayList<>();
		if(configuration.isDeletePeaks()) {
			SubMonitor subMonitor = SubMonitor.convert(monitor, peaks.size());
			for(X peak : peaks) {
				if(configuration.isDeleteUnidentifiedOnly()) {
					if(peak.getTargets().isEmpty()) {
						peaksToDelete.add(peak);
					}
				} else {
					peaksToDelete.add(peak);
				}
				subMonitor.worked(1);
			}
		}
		IChromatogramSelection<?, ?> chromatogramSelection = (IChromatogramSelection<?, ?>)listener.getDataContainer();
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

	@Override
	public boolean acceptsIPeaks(Collection<? extends IPeak> items) {

		return true;
	}
}
