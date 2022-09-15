/*******************************************************************************
 * Copyright (c) 2020, 2022 Lablicate GmbH.
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

import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.EvaluationPCA;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.swt.ExtendedScorePlot2D;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

public class ScorePlot2DPart extends AbstractPartPCA<ExtendedScorePlot2D> {

	@Inject
	public ScorePlot2DPart(Composite parent) {

		super(parent);
	}

	@Override
	protected ExtendedScorePlot2D createControl(Composite parent) {

		return new ExtendedScorePlot2D(parent, SWT.NONE);
	}

	@Override
	protected boolean updateData(List<Object> objects, String topic) {

		if(objects.size() == 1) {
			if(isUnloadEvent(topic)) {
				getControl().setInput(null);
				unloadData();
				return false;
			} else if(isUpdateColorSchemeEvent(topic) || isUpdateLabelsEvent(topic)) {
				getControl().updatePlot();
				return false;
			} else {
				Object object = objects.get(0);
				if(object instanceof EvaluationPCA) {
					getControl().setInput((EvaluationPCA)object);
					return true;
				} else {
					getControl().setInput(null);
					unloadData();
					return false;
				}
			}
		}
		//
		return false;
	}
}