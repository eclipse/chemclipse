/*******************************************************************************
 * Copyright (c) 2019, 2024 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Christoph Läubrich - initial API and implementation
 * Philip Wenig - refactoring backgroun mode
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.ui.swt;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

import org.eclipse.chemclipse.swt.ui.support.Colors;
import org.eclipse.chemclipse.ux.extension.ui.definitions.TileDefinition;
import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.MouseTrackAdapter;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

public class TaskTileContainer {

	public static final Color DEFAULT_COLOR_TITLE = Colors.WHITE;
	public static final Color DEFAULT_COLOR_DESCRIPTION = Colors.WHITE;
	public static final Color DEFAULT_COLOR_INACTIVE = Colors.getColor(139, 23, 23);
	public static final Color DEFAULT_COLOR_ACTIVE = Colors.getColor(114, 20, 22);
	//
	private final List<TaskTile> taskTiles = new ArrayList<>();
	private final Composite container;
	private final Supplier<IEclipseContext> contextSupplier;
	private final Color[] colors;
	//
	private final MouseMoveListener tileMouseMoveListener = mouseMove -> {
		for(TaskTile taskTile : taskTiles) {
			if(taskTile == mouseMove.widget) {
				taskTile.setActive();
			} else {
				taskTile.setInactive();
			}
		}
	};

	public TaskTileContainer(Composite parent, int columns, Supplier<IEclipseContext> contextSupplier) {

		this(parent, columns, contextSupplier, new Color[]{DEFAULT_COLOR_ACTIVE, DEFAULT_COLOR_INACTIVE, DEFAULT_COLOR_TITLE, DEFAULT_COLOR_DESCRIPTION});
	}

	public TaskTileContainer(Composite parent, int columns, Supplier<IEclipseContext> contextSupplier, Color[] colors) {

		this.contextSupplier = contextSupplier;
		this.colors = colors;
		//
		container = new Composite(parent, SWT.NONE);
		container.setLayout(new GridLayout(columns, true));
		container.setBackgroundMode(SWT.INHERIT_DEFAULT);
		//
		MouseTrackAdapter mouseTrackAdapter = new MouseTrackAdapter() {

			@Override
			public void mouseExit(MouseEvent me) {

				for(TaskTile taskTile : taskTiles) {
					taskTile.setInactive();
				}
			}
		};
		//
		container.addMouseTrackListener(mouseTrackAdapter);
		parent.addMouseTrackListener(mouseTrackAdapter);
	}

	public TaskTile addTaskTile(TileDefinition definition) {

		TaskTile taskTile = new TaskTile(container, definition, this::executeHandler, this::computeStyle, colors);
		taskTile.setLayoutData(new GridData(GridData.FILL_BOTH));
		taskTile.addMouseMoveListener(tileMouseMoveListener);
		taskTiles.add(taskTile);
		container.layout();
		//
		return taskTile;
	}

	private void executeHandler(TileDefinition tileDefinition) {

		ContextInjectionFactory.invoke(tileDefinition, Execute.class, contextSupplier.get(), null);
	}

	protected int computeStyle(TileDefinition tileDefinition) {

		boolean largeText = tileDefinition.getIcon() == null && tileDefinition.getTitle().length() == 1;
		int style = SWT.NONE;
		//
		if(canExecute(tileDefinition)) {
			style |= TaskTile.HIGHLIGHT;
		}
		//
		if(largeText) {
			style |= TaskTile.LARGE_TITLE;
		}
		//
		return style;
	}

	private boolean canExecute(TileDefinition tileDefinition) {

		Object invoke = ContextInjectionFactory.invoke(tileDefinition, CanExecute.class, contextSupplier.get(), Boolean.TRUE);
		if(invoke instanceof Boolean invoked) {
			return invoked.booleanValue();
		}
		return true;
	}

	public void update() {

		for(TaskTile taskTile : taskTiles) {
			taskTile.updateFromDefinition();
		}
	}

	public void removeTaskTile(TaskTile tile) {

		tile.removeMouseMoveListener(tileMouseMoveListener);
		taskTiles.remove(tile);
		tile.dispose();
		container.layout();
	}

	public List<TaskTile> getTiles() {

		return Collections.unmodifiableList(taskTiles);
	}
}