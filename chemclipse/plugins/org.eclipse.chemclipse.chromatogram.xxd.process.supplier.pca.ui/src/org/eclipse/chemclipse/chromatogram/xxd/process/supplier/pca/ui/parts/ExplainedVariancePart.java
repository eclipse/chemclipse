/*******************************************************************************
 * Copyright (c) 2018, 2020 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Lorenz Gerber - initial API and implementation
 * Philip Wenig - getting rid of JavaFX
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.parts;

import java.util.List;

import javax.annotation.PreDestroy;
import javax.inject.Inject;

import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.EvaluationPCA;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.Activator;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.chart2d.ExplainedVarianceChart;
import org.eclipse.chemclipse.ux.extension.xxd.ui.part.support.DataUpdateSupport;
import org.eclipse.chemclipse.ux.extension.xxd.ui.part.support.IDataUpdateListener;
import org.eclipse.e4.ui.di.Focus;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

public class ExplainedVariancePart {

	private static final String TOPIC = Activator.TOPIC_PCA_EVALUATION_LOAD;
	//
	private ExplainedVarianceChart control;
	//
	private DataUpdateSupport dataUpdateSupport = Activator.getDefault().getDataUpdateSupport();
	private IDataUpdateListener updateListener = new IDataUpdateListener() {

		@Override
		public void update(String topic, List<Object> objects) {

			updateSelection(objects, topic);
		}
	};

	@Inject
	public ExplainedVariancePart(Composite parent, MPart part) {

		control = new ExplainedVarianceChart(parent, SWT.NONE);
		dataUpdateSupport.add(updateListener);
	}

	@Focus
	public void setFocus() {

		updateSelection(dataUpdateSupport.getUpdates(TOPIC), TOPIC);
	}

	@PreDestroy
	protected void preDestroy() {

		dataUpdateSupport.remove(updateListener);
	}

	private void updateSelection(List<Object> objects, String topic) {

		if(DataUpdateSupport.isVisible(control)) {
			if(objects.size() == 1) {
				if(isUnloadEvent(topic)) {
					control.setInput(null);
				} else {
					Object object = objects.get(0);
					if(object instanceof EvaluationPCA) {
						control.setInput((EvaluationPCA)object);
					}
				}
			}
		}
	}

	private boolean isUnloadEvent(String topic) {

		return topic.equals(Activator.TOPIC_PCA_EVALUATION_CLEAR);
	}
}
