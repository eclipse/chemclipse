/*******************************************************************************
 * Copyright (c) 2017, 2022 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Jan Holy - initial API and implementation
 * Philip Wenig - get rid of JavaFX, feature selection
 *******************************************************************************/
package org.eclipse.chemclipse.xxd.process.supplier.pca.ui.parts;

import java.util.List;

import javax.inject.Inject;

import org.eclipse.chemclipse.xxd.process.supplier.pca.model.EvaluationPCA;
import org.eclipse.chemclipse.xxd.process.supplier.pca.ui.swt.ExtendedFeatureListUI;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

public class FeatureTablePart extends AbstractPartPCA<ExtendedFeatureListUI> {

	@Inject
	public FeatureTablePart(Composite parent) {

		super(parent);
	}

	@Override
	protected ExtendedFeatureListUI createControl(Composite parent) {

		return new ExtendedFeatureListUI(parent, SWT.NONE);
	}

	@Override
	protected boolean updateData(List<Object> objects, String topic) {

		if(objects.size() == 1) {
			if(isUnloadEvent(topic)) {
				getControl().setInput(null);
				unloadData();
				return false;
			} else if(isUpdateSelection(topic)) {
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