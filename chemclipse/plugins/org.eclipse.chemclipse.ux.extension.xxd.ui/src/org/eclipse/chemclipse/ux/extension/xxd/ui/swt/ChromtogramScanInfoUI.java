/*******************************************************************************
 * Copyright (c) 2016, 2018 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.swt;

import org.eclipse.chemclipse.msd.model.core.IScanMSD;
import org.eclipse.chemclipse.msd.model.core.selection.IChromatogramSelectionMSD;
import org.eclipse.chemclipse.support.events.IChemClipseEvents;
import org.eclipse.chemclipse.support.ui.addons.ModelSupportAddon;
import org.eclipse.chemclipse.support.ui.swt.ExtendedTableViewer;
import org.eclipse.chemclipse.ux.extension.xxd.ui.internal.provider.ChromatogramScanInfoLabelProvider;
import org.eclipse.chemclipse.ux.extension.xxd.ui.internal.provider.ChromtogramScanInfoContentProvider;
import org.eclipse.chemclipse.ux.extension.xxd.ui.internal.provider.ChromtogramScanInfoTableComparator;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Composite;

public class ChromtogramScanInfoUI extends ExtendedTableViewer {

	private String[] titles = {"Scan#", "RT (Minutes)", "Number Of Ions", "SIM/SCAN", "m/z..."};
	private int bounds[] = {120, 120, 120, 120, 120};

	public ChromtogramScanInfoUI(Composite parent, int style) {
		super(parent, style);
		createColumns();
	}

	public void update(IChromatogramSelectionMSD chromatogramSelection) {

		if(chromatogramSelection != null) {
			setInput(chromatogramSelection);
		} else {
			setInput(null);
		}
	}

	private void createColumns() {

		createColumns(titles, bounds);
		//
		setLabelProvider(new ChromatogramScanInfoLabelProvider());
		setContentProvider(new ChromtogramScanInfoContentProvider());
		setComparator(new ChromtogramScanInfoTableComparator());
		//
		getTable().addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {

				IStructuredSelection structuredSelection = getStructuredSelection();
				Object object = structuredSelection.getFirstElement();
				if(object instanceof IScanMSD) {
					IEventBroker eventBroker = ModelSupportAddon.getEventBroker();
					eventBroker.send(IChemClipseEvents.TOPIC_SCAN_XXD_UPDATE_SELECTION, object);
				}
			}
		});
	}
}
