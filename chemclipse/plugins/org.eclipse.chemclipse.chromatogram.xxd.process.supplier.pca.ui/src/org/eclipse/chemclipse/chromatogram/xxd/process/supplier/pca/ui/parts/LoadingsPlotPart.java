/*******************************************************************************
 * Copyright (c) 2020 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.parts;

import java.util.List;

import javax.inject.Inject;

import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.EvaluationPCA;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.Activator;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.swt.ExtendedLoadingsPlot;
import org.eclipse.chemclipse.ux.extension.xxd.ui.part.support.EnhancedUpdateSupport;
import org.eclipse.chemclipse.ux.extension.xxd.ui.part.support.IUpdateSupport;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

public class LoadingsPlotPart extends EnhancedUpdateSupport implements IUpdateSupport {

	private static final String TOPIC = Activator.TOPIC_PCA_EVALUATION_LOAD;
	//
	private ExtendedLoadingsPlot plot;

	@Inject
	public LoadingsPlotPart(Composite parent, MPart part) {
		super(parent, Activator.getDefault().getDataUpdateSupport(), TOPIC, part);
	}

	@Override
	public void createControl(Composite parent) {

		plot = new ExtendedLoadingsPlot(parent, SWT.NONE);
	}

	@Override
	public void updateSelection(List<Object> objects, String topic) {

		/*
		 * 0 => because only one property was used to register the event.
		 */
		if(objects.size() == 1) {
			if(isUnloadEvent(topic)) {
				plot.setInput(null);
			} else {
				Object object = objects.get(0);
				if(object instanceof EvaluationPCA) {
					plot.setInput((EvaluationPCA)object);
				}
			}
		}
	}

	private boolean isUnloadEvent(String topic) {

		if(topic.equals(Activator.TOPIC_PCA_EVALUATION_CLEAR)) {
			return true;
		}
		return false;
	}
}
