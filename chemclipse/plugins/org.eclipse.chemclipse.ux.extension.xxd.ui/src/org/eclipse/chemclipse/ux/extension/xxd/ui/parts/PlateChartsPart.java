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
package org.eclipse.chemclipse.ux.extension.xxd.ui.parts;

import java.util.List;

import javax.inject.Inject;

import org.eclipse.chemclipse.pcr.model.core.IPlate;
import org.eclipse.chemclipse.support.events.IChemClipseEvents;
import org.eclipse.chemclipse.ux.extension.xxd.ui.part.support.AbstractDataUpdateSupport;
import org.eclipse.chemclipse.ux.extension.xxd.ui.part.support.IDataUpdateSupport;
import org.eclipse.chemclipse.ux.extension.xxd.ui.swt.ExtendedPlateChartsUI;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.di.Focus;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.swt.widgets.Composite;

public class PlateChartsPart extends AbstractDataUpdateSupport implements IDataUpdateSupport {

	private ExtendedPlateChartsUI extendedPlateChartsUI;

	@Inject
	public PlateChartsPart(Composite parent, MPart part) {
		super(part);
		extendedPlateChartsUI = new ExtendedPlateChartsUI(parent);
	}

	@Focus
	public void setFocus() {

		updateObjects(getObjects(), getTopic());
	}

	@Override
	public void registerEvents() {

		registerEvent(IChemClipseEvents.TOPIC_PLATE_PCR_UPDATE_SELECTION, IEventBroker.DATA);
		registerEvent(IChemClipseEvents.TOPIC_PLATE_PCR_UNLOAD_SELECTION, IEventBroker.DATA);
	}

	@Override
	public void updateObjects(List<Object> objects, String topic) {

		/*
		 * 0 => because only one property was used to register the event.
		 */
		if(objects.size() == 1) {
			if(topic.equals(IChemClipseEvents.TOPIC_PLATE_PCR_UNLOAD_SELECTION)) {
				extendedPlateChartsUI.update(null);
			} else {
				Object object = objects.get(0);
				if(object instanceof IPlate) {
					extendedPlateChartsUI.update((IPlate)object);
				} else {
					extendedPlateChartsUI.update(null);
				}
			}
		}
	}
}
