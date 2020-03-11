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
package org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.parts;

import java.util.List;

import javax.inject.Inject;

import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.PcaResults;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.Activator;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.chart2d.ScorePlot;
import org.eclipse.chemclipse.ux.extension.xxd.ui.part.support.EnhancedUpdateSupport;
import org.eclipse.chemclipse.ux.extension.xxd.ui.part.support.IUpdateSupport;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.swt.widgets.Composite;

public class ScorePlotPart extends EnhancedUpdateSupport implements IUpdateSupport {

	private static final String TOPIC = Activator.TOPIC_PCA_RESULTS_LOAD;
	//
	private ScorePlot scorePlot;

	@Inject
	public ScorePlotPart(Composite parent, MPart part) {
		super(parent, Activator.getDefault().getDataUpdateSupport(), TOPIC, part);
	}

	@Override
	public void createControl(Composite parent) {

		scorePlot = new ScorePlot(parent);
	}

	@Override
	public void updateSelection(List<Object> objects, String topic) {

		/*
		 * 0 => because only one property was used to register the event.
		 */
		if(objects.size() == 1) {
			if(isUnloadEvent(topic)) {
				scorePlot.setInput(null);
			} else {
				Object object = objects.get(0);
				if(object instanceof PcaResults) {
					scorePlot.setInput((PcaResults)object);
				}
			}
		}
	}

	private boolean isUnloadEvent(String topic) {

		if(topic.equals(Activator.TOPIC_PCA_RESULTS_CLEAR)) {
			return true;
		}
		return false;
	}
}
