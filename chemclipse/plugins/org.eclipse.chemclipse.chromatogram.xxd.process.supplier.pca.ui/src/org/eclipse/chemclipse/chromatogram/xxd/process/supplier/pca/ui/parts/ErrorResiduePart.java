/*******************************************************************************
 * Copyright (c) 2017, 2020 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Jan Holy - initial API and implementation
 * Philip Wenig - getting rid of JavaFX
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.parts;

import java.util.List;

import javax.inject.Inject;

import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.EvaluationPCA;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.Activator;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.chart2d.ErrorResidueChart;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

public class ErrorResiduePart extends AbstractPartPCA<ErrorResidueChart> {

	private static final String TOPIC = Activator.TOPIC_PCA_EVALUATION_LOAD;

	@Inject
	public ErrorResiduePart(Composite parent) {

		super(parent, TOPIC);
	}

	@Override
	protected ErrorResidueChart createControl(Composite parent) {

		return new ErrorResidueChart(parent, SWT.NONE);
	}

	@Override
	protected boolean updateData(List<Object> objects, String topic) {

		if(objects.size() == 1) {
			if(isUnloadEvent(topic)) {
				getControl().setInput(null);
				return false;
			} else {
				Object object = objects.get(0);
				if(object instanceof EvaluationPCA) {
					getControl().setInput((EvaluationPCA)object);
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

		return topic.equals(Activator.TOPIC_PCA_EVALUATION_CLEAR);
	}
}
