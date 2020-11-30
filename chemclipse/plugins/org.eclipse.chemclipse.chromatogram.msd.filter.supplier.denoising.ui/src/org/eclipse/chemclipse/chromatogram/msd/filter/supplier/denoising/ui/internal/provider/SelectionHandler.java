/*******************************************************************************
 * Copyright (c) 2018, 2020 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 * Christoph Läubrich - change to new API
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.msd.filter.supplier.denoising.ui.internal.provider;

import org.eclipse.chemclipse.chromatogram.msd.filter.supplier.denoising.ui.Activator;
import org.eclipse.chemclipse.msd.model.core.ICombinedMassSpectrum;
import org.eclipse.chemclipse.support.events.IChemClipseEvents;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;

public class SelectionHandler implements ISelectionChangedListener {

	@Override
	public void selectionChanged(SelectionChangedEvent event) {

		ISelection selection = event.getSelection();
		if(selection instanceof IStructuredSelection) {
			Object element = ((IStructuredSelection)selection).getFirstElement();
			if(element instanceof ICombinedMassSpectrum) {
				ICombinedMassSpectrum combinedMassSpectrum = (ICombinedMassSpectrum)element;
				IEventBroker eventBroker = Activator.getDefault().getEventBroker();
				if(eventBroker != null) {
					eventBroker.send(IChemClipseEvents.TOPIC_SCAN_XXD_UPDATE_SELECTION, combinedMassSpectrum);
				}
			}
		}
	}
}
