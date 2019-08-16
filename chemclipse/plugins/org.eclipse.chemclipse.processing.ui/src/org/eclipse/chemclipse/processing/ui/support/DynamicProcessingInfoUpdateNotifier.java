/*******************************************************************************
 * Copyright (c) 2012, 2019 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 * Christoph Läubrich - use {@link MessageProvider} interface, make a creatable singleton and store the current value
 *******************************************************************************/
package org.eclipse.chemclipse.processing.ui.support;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.eclipse.chemclipse.processing.core.MessageProvider;
import org.eclipse.chemclipse.support.events.IChemClipseEvents;
import org.eclipse.e4.core.di.annotations.Creatable;
import org.eclipse.e4.core.services.events.IEventBroker;

@Creatable
@Singleton
public class DynamicProcessingInfoUpdateNotifier {

	@Inject
	private IEventBroker eventBroker;
	private MessageProvider currentValue;

	public void update(MessageProvider processingInfo) {

		this.currentValue = processingInfo;
		eventBroker.post(IChemClipseEvents.TOPIC_PROCESSING_INFO_UPDATE, processingInfo);
	}

	public MessageProvider getProcessingInfo() {

		return currentValue;
	}
}
