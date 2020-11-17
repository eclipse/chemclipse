/*******************************************************************************
 * Copyright (c) 2018, 2020 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.parts;

import java.util.List;

import javax.inject.Inject;

import org.eclipse.chemclipse.model.quantitation.IQuantitationCompound;
import org.eclipse.chemclipse.support.events.IChemClipseEvents;
import org.eclipse.chemclipse.ux.extension.xxd.ui.swt.QuantPeaksChartUI;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

public class QuantPeaksChartPart extends AbstractPart<QuantPeaksChartUI> {

	private static final String TOPIC = IChemClipseEvents.TOPIC_QUANT_DB_COMPOUND_UPDATE;

	@Inject
	public QuantPeaksChartPart(Composite parent) {

		super(parent, TOPIC);
	}

	@Override
	protected QuantPeaksChartUI createControl(Composite parent) {

		return new QuantPeaksChartUI(parent, SWT.NONE);
	}

	@Override
	protected boolean updateData(List<Object> objects, String topic) {

		if(objects.size() == 1) {
			Object object = objects.get(0);
			if(object instanceof IQuantitationCompound) {
				getControl().update((IQuantitationCompound)object);
				return true;
			} else {
				getControl().update(null);
				return false;
			}
		}
		//
		return false;
	}

	@Override
	protected boolean isUpdateTopic(String topic) {

		return TOPIC.equals(topic);
	}
}
