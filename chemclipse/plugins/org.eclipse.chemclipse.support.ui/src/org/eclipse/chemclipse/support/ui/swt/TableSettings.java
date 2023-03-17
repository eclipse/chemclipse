/*******************************************************************************
 * Copyright (c) 2017, 2023 Lablicate GmbH.
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

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.chemclipse.support.ui.events.CopyToClipboardEvent;
import org.eclipse.chemclipse.support.ui.events.IKeyEventProcessor;
import org.eclipse.chemclipse.support.ui.menu.ClipboardSettingsHandler;
import org.eclipse.chemclipse.support.ui.menu.CopyClipboardHandler;
import org.eclipse.chemclipse.support.ui.menu.DeselectAllHandler;
import org.eclipse.chemclipse.support.ui.menu.ITableMenuEntry;
import org.eclipse.chemclipse.support.ui.menu.SelectAllHandler;

public class TableSettings implements ITableSettings {

	private boolean createMenu = true;
	private Set<ITableMenuEntry> menuEntries = new HashSet<>();
	private Set<IKeyEventProcessor> keyEventProcessors = new HashSet<>();

	public TableSettings() {

		/*
		 * Default menu entries
		 */
		menuEntries.add(new SelectAllHandler());
		menuEntries.add(new DeselectAllHandler());
		menuEntries.add(new ClipboardSettingsHandler());
		menuEntries.add(new CopyClipboardHandler());
		/*
		 * Default key processors
		 */
		keyEventProcessors.add(new CopyToClipboardEvent());
	}

	@Override
	public boolean isCreateMenu() {

		return createMenu;
	}

	@Override
	public void setCreateMenu(boolean createMenu) {

		this.createMenu = createMenu;
	}

	@Override
	public void addMenuEntry(ITableMenuEntry menuEntry) {

		menuEntries.add(menuEntry);
	}

	@Override
	public void removeMenuEntry(ITableMenuEntry menuEntry) {

		menuEntries.remove(menuEntry);
	}

	@Override
	public Set<ITableMenuEntry> getMenuEntries() {

		return Collections.unmodifiableSet(menuEntries);
	}

	@Override
	public void clearMenuEntries() {

		menuEntries.clear();
	}

	@Override
	public void addKeyEventProcessor(IKeyEventProcessor keyEventProcessor) {

		keyEventProcessors.add(keyEventProcessor);
	}

	@Override
	public void removeKeyEventProcessor(IKeyEventProcessor keyEventProcessor) {

		keyEventProcessors.remove(keyEventProcessor);
	}

	@Override
	public Set<IKeyEventProcessor> getKeyEventProcessors() {

		return Collections.unmodifiableSet(keyEventProcessors);
	}

	@Override
	public void clearKeyEventProcessors() {

		keyEventProcessors.clear();
	}
}