/*******************************************************************************
 * Copyright (c) 2017 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ui.service.swt.internal.charts;

import org.eclipse.swt.widgets.Event;

public interface IEventHandler {

	void handleMouseMoveEvent(Event event);

	void handleMouseDownEvent(Event event);

	void handleMouseUpEvent(Event event);

	void handleMouseWheel(Event event);

	void handleMouseDoubleClick(Event event);

	void handleKeyDownEvent(Event event);

	void handleKeyUpEvent(Event event);

	void handleSelectionEvent(Event event);
}
