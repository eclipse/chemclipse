/*******************************************************************************
 * Copyright (c) 2014, 2018 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.support.ui.notifier;

import javax.inject.Inject;

import org.eclipse.chemclipse.support.events.IChemClipseEvents;
import org.eclipse.chemclipse.support.history.IEditHistory;
import org.eclipse.e4.core.services.events.IEventBroker;

public class DynamicEditHistoryUpdateNotifier {

	@Inject
	private IEventBroker eventBroker;

	public void update(IEditHistory editHistory) {

		eventBroker.send(IChemClipseEvents.TOPIC_EDIT_HISTORY_UPDATE, editHistory);
	}
}
