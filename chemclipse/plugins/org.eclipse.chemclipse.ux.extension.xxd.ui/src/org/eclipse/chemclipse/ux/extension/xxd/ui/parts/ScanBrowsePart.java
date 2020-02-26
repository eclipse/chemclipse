/*******************************************************************************
 * Copyright (c) 2020 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.parts;

import java.util.List;

import javax.inject.Inject;

import org.eclipse.chemclipse.model.core.IScan;
import org.eclipse.chemclipse.support.events.IChemClipseEvents;
import org.eclipse.chemclipse.ux.extension.xxd.ui.Activator;
import org.eclipse.chemclipse.ux.extension.xxd.ui.part.support.EnhancedUpdateSupport;
import org.eclipse.chemclipse.ux.extension.xxd.ui.part.support.IUpdateSupport;
import org.eclipse.chemclipse.ux.extension.xxd.ui.swt.ExtendedScanBrowseUI;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.swt.widgets.Composite;

public class ScanBrowsePart extends EnhancedUpdateSupport implements IUpdateSupport {

	private ExtendedScanBrowseUI extendedScanBrowseUI;

	@Inject
	public ScanBrowsePart(Composite parent, MPart part, IEventBroker eventBroker) {
		super(parent, Activator.getDefault().getDataUpdateSupport(), IChemClipseEvents.TOPIC_SCAN_XXD_UPDATE_SELECTION, part);
	}

	@Override
	public void createControl(Composite parent) {

		extendedScanBrowseUI = new ExtendedScanBrowseUI(parent);
	}

	@Override
	public void updateSelection(List<Object> objects, String topic) {

		/*
		 * 0 => because only one property was used to register the event.
		 */
		if(objects.size() == 1) {
			Object object = objects.get(0);
			if(isScanTopic(topic)) {
				IScan scan = null;
				if(object instanceof IScan) {
					scan = (IScan)object;
				}
				extendedScanBrowseUI.update(scan);
			}
		}
	}

	private boolean isScanTopic(String topic) {

		if(topic.equals(IChemClipseEvents.TOPIC_SCAN_XXD_UPDATE_SELECTION)) {
			return true;
		}
		return false;
	}
}
