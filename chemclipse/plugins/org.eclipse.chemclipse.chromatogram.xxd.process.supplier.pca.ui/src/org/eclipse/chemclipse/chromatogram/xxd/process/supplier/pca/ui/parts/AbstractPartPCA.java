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

import org.eclipse.chemclipse.support.events.IChemClipseEvents;
import org.eclipse.chemclipse.ux.extension.xxd.ui.parts.AbstractPart;
import org.eclipse.swt.widgets.Composite;

public abstract class AbstractPartPCA<T extends Composite> extends AbstractPart<T> {

	private static final String TOPIC = IChemClipseEvents.TOPIC_PCA_UPDATE_SELECTION;

	public AbstractPartPCA(Composite parent) {

		super(parent, TOPIC);
	}

	/**
	 * Overwrite, if additional topics shall be added.
	 */
	@Override
	protected void subscribeAdditionalTopics() {

		subscribeAdditionalTopic(TOPIC, IChemClipseEvents.EVENT_BROKER_DATA);
		subscribeAdditionalTopic(IChemClipseEvents.TOPIC_PCA_UPDATE_COLORSCHEME, IChemClipseEvents.EVENT_BROKER_DATA);
		subscribeAdditionalTopic(IChemClipseEvents.TOPIC_PCA_UPDATE_FEATURES, IChemClipseEvents.EVENT_BROKER_DATA);
		subscribeAdditionalTopic(IChemClipseEvents.TOPIC_PCA_UPDATE_LABELS, IChemClipseEvents.EVENT_BROKER_DATA);
		subscribeAdditionalTopic(IChemClipseEvents.TOPIC_EDITOR_PCA_CLOSE, IChemClipseEvents.EVENT_BROKER_DATA);
	}

	@Override
	protected boolean isUpdateTopic(String topic) {

		return isUpdateSelection(topic) || isUpdateColorSchemeEvent(topic) || isUpdateFeaturesEvent(topic) || isUpdateLabelsEvent(topic) || isUnloadEvent(topic);
	}

	protected boolean isUpdateSelection(String topic) {

		return TOPIC.equals(topic);
	}

	protected boolean isUpdateColorSchemeEvent(String topic) {

		return topic.equals(IChemClipseEvents.TOPIC_PCA_UPDATE_COLORSCHEME);
	}

	protected boolean isUpdateFeaturesEvent(String topic) {

		return topic.equals(IChemClipseEvents.TOPIC_PCA_UPDATE_FEATURES);
	}

	protected boolean isUpdateLabelsEvent(String topic) {

		return topic.equals(IChemClipseEvents.TOPIC_PCA_UPDATE_LABELS);
	}

	protected boolean isUnloadEvent(String topic) {

		return topic.equals(IChemClipseEvents.TOPIC_EDITOR_PCA_CLOSE);
	}
}