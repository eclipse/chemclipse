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

import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.EvaluationPCA;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.Activator;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.swt.ExtendedScorePlot3D;
import org.eclipse.chemclipse.ux.extension.xxd.ui.part.support.DataUpdateSupport;
import org.eclipse.chemclipse.ux.extension.xxd.ui.part.support.IDataUpdateListener;
import org.eclipse.e4.ui.di.Focus;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

public class ScorePlot3DPart {

	private static final String TOPIC = Activator.TOPIC_PCA_EVALUATION_LOAD;
	//
	private Composite parent;
	private DataUpdateSupport dataUpdateSupport = Activator.getDefault().getDataUpdateSupport();
	private ExtendedScorePlot3D plot;
	//
	private IDataUpdateListener updateListener = new IDataUpdateListener() {

		@Override
		public void update(String topic, List<Object> objects) {

			updateSelection(objects, topic);
		}
	};

	@Inject
	public ScorePlot3DPart(Composite parent, MPart part) {
		plot = new ExtendedScorePlot3D(parent, SWT.NONE);
		this.parent = parent;
		dataUpdateSupport.add(updateListener);
	}

	@Focus
	public void setFocus() {

		updateSelection(dataUpdateSupport.getUpdates(TOPIC), TOPIC);
	}

	private void updateSelection(List<Object> objects, String topic) {

		/*
		 * 0 => because only one property was used to register the event.
		 */
		if(isVisible()) {
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
	}

	private boolean isUnloadEvent(String topic) {

		if(topic.equals(Activator.TOPIC_PCA_EVALUATION_CLEAR)) {
			return true;
		}
		return false;
	}

	private boolean isVisible() {

		return (parent != null && parent.isVisible());
	}
}
