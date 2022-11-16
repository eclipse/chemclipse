/*******************************************************************************
 * Copyright (c) 2020, 2022 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.xxd.process.supplier.pca.ui.parts;

import java.util.List;

import javax.inject.Inject;

import org.eclipse.chemclipse.xxd.process.supplier.pca.model.EvaluationPCA;
import org.eclipse.chemclipse.xxd.process.supplier.pca.ui.swt.ExtendedLoadingsPlot;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

public class LoadingsPlotPart extends AbstractPartPCA<ExtendedLoadingsPlot> {

	@Inject
	public LoadingsPlotPart(Composite parent) {

		super(parent);
	}

	@Override
	protected ExtendedLoadingsPlot createControl(Composite parent) {

		return new ExtendedLoadingsPlot(parent, SWT.NONE);
	}

	@Override
	protected boolean updateData(List<Object> objects, String topic) {

		if(objects.size() == 1) {
			if(isUnloadEvent(topic)) {
				getControl().setInput(null);
				unloadData();
				return false;
			} else if(isUpdateFeaturesEvent(topic)) {
				getControl().updateInput();
				return true;
			} else {
				Object object = objects.get(0);
				if(object instanceof EvaluationPCA evaluationPCA) {
					getControl().setInput(evaluationPCA);
					return true;
				}
			}
		}
		//
		return false;
	}
}