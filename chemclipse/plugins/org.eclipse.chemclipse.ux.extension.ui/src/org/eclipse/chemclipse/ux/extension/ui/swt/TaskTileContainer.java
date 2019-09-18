/*******************************************************************************
 * Copyright (c) 2019 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Christoph Läubrich - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.ui.swt;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

import org.eclipse.chemclipse.ux.extension.ui.definitions.TileDefinition;
import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

public class TaskTileContainer {

	private final List<TaskTile> tiles = new ArrayList<TaskTile>();
	private final Composite container;
	private final Supplier<IEclipseContext> contextSupplier;
	private final MouseMoveListener tileMouseMoveListener = mouseMove -> {
		for(TaskTile tile : tiles) {
			if(tile == mouseMove.widget) {
				tile.setActive();
			} else {
				tile.setInactive();
			}
		}
	};

	public TaskTileContainer(Composite parent, int columns, Supplier<IEclipseContext> contextSupplier) {
		this.contextSupplier = contextSupplier;
		container = new Composite(parent, SWT.NONE);
		container.setLayout(new GridLayout(columns, false));
		container.setBackgroundMode(SWT.INHERIT_FORCE);
	}

	public TaskTile addTaskTile(TileDefinition definition) {

		GridData gridData = new GridData(GridData.FILL_BOTH);
		TaskTile taskTile = new TaskTile(container, TaskTile.HIGHLIGHT, definition, this::executeHandler);
		taskTile.setLayoutData(gridData);
		taskTile.addMouseMoveListener(tileMouseMoveListener);
		tiles.add(taskTile);
		return taskTile;
	}

	private void executeHandler(TileDefinition tileDefinition) {

		ContextInjectionFactory.invoke(tileDefinition, Execute.class, contextSupplier.get());
	}

	public void removeTaskTile(TaskTile tile) {

		tile.removeMouseMoveListener(tileMouseMoveListener);
		tiles.remove(tile);
		tile.dispose();
	}

	public List<TaskTile> getTiles() {

		return Collections.unmodifiableList(tiles);
	}
}
