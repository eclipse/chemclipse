/*******************************************************************************
 * Copyright (c) 2019, 2020 Lablicate GmbH.
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

import org.eclipse.chemclipse.pcr.model.core.IWell;
import org.eclipse.chemclipse.support.events.IChemClipseEvents;
import org.eclipse.chemclipse.ux.extension.xxd.ui.swt.ExtendedWellChannelsUI;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

public class WellChannelsPart extends AbstractPart<ExtendedWellChannelsUI> {

	private static final String TOPIC = IChemClipseEvents.TOPIC_WELL_PCR_UPDATE_SELECTION;

	@Inject
	public WellChannelsPart(Composite parent) {

		super(parent, TOPIC);
	}

	@Override
	protected ExtendedWellChannelsUI createControl(Composite parent) {

		return new ExtendedWellChannelsUI(parent, SWT.NONE);
	}

	@Override
	protected boolean updateData(List<Object> objects, String topic) {

		if(objects.size() == 1) {
			if(isUnloadEvent(topic)) {
				getControl().update(null);
				return false;
			} else {
				Object object = objects.get(0);
				if(object instanceof IWell) {
					getControl().update((IWell)object);
					return true;
				} else {
					getControl().update(null);
					return true;
				}
			}
		}
		//
		return false;
	}

	@Override
	protected boolean isUpdateTopic(String topic) {

		return TOPIC.equals(topic) || isUnloadEvent(topic);
	}

	private boolean isUnloadEvent(String topic) {

		if(topic.equals(IChemClipseEvents.TOPIC_WELL_PCR_UNLOAD_SELECTION) || topic.equals(IChemClipseEvents.TOPIC_PLATE_PCR_UNLOAD_SELECTION)) {
			return true;
		}
		return false;
	}
}
