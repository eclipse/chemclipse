/*******************************************************************************
 * Copyright (c) 2017, 2018 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.support.ui.swt;

import java.util.Set;

import org.eclipse.chemclipse.support.ui.events.IKeyEventProcessor;
import org.eclipse.chemclipse.support.ui.menu.ITableMenuEntry;

public interface ITableSettings {

	boolean isCreateMenu();

	void setCreateMenu(boolean createMenu);

	void addMenuEntry(ITableMenuEntry menuEntry);

	void removeMenuEntry(ITableMenuEntry menuEntry);

	Set<ITableMenuEntry> getMenuEntries();

	void clearMenuEntries();

	void addKeyEventProcessor(IKeyEventProcessor keyEventProcessor);

	void removeKeyEventProcessor(IKeyEventProcessor keyEventProcessor);

	Set<IKeyEventProcessor> getKeyEventProcessors();

	void clearKeyEventProcessors();
}
