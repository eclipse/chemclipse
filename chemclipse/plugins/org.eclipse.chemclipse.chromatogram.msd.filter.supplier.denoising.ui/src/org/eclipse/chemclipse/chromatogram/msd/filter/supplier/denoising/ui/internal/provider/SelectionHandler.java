/*******************************************************************************
 * Copyright (c) 2018 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.msd.filter.supplier.denoising.ui.internal.provider;

import org.eclipse.chemclipse.msd.model.core.ICombinedMassSpectrum;
import org.eclipse.chemclipse.support.events.IChemClipseEvents;
import org.eclipse.chemclipse.support.ui.addons.ModelSupportAddon;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;

public class SelectionHandler extends SelectionAdapter implements SelectionListener {

	@Override
	public void widgetSelected(SelectionEvent e) {

		try {
			Object object = e.getSource();
			if(object instanceof Table) {
				Table table = (Table)object;
				int index = table.getSelectionIndex();
				TableItem tableItem = table.getItem(index);
				Object data = tableItem.getData();
				if(data instanceof ICombinedMassSpectrum) {
					ICombinedMassSpectrum combinedMassSpectrum = (ICombinedMassSpectrum)data;
					IEventBroker eventBroker = ModelSupportAddon.getEventBroker();
					eventBroker.send(IChemClipseEvents.TOPIC_SCAN_XXD_UPDATE_SELECTION, combinedMassSpectrum);
				}
			}
		} catch(Exception e1) {
			//
		}
	}
}
