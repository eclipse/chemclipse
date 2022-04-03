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
package org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.parts;

import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.core.ProcessorPCA;
import org.eclipse.chemclipse.support.events.IChemClipseEvents;
import org.eclipse.chemclipse.ux.extension.xxd.ui.parts.AbstractPart;
import org.eclipse.swt.widgets.Composite;

public abstract class AbstractPartPCA<T extends Composite> extends AbstractPart<T> {

	public AbstractPartPCA(Composite parent, String topic) {

		super(parent, topic);
	}

	/**
	 * Overwrite, if additional topics shall be added.
	 */
	@Override
	protected void subscribeAdditionalTopics() {

		subscribeAdditionalTopic(ProcessorPCA.TOPIC_PCA_EVALUATION_LOAD, IChemClipseEvents.EVENT_BROKER_DATA);
		subscribeAdditionalTopic(ProcessorPCA.TOPIC_PCA_EVALUATION_CLEAR, IChemClipseEvents.EVENT_BROKER_DATA);
	}
}