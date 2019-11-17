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
package org.eclipse.chemclipse.ux.extension.xxd.ui.internal.provider;

import java.util.function.BiConsumer;
import java.util.function.Function;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.viewers.TreeNode;
import org.eclipse.swt.widgets.Composite;

public class IntegerColumnEditingSupport<T> extends EditingSupport {

	private final Function<T, Integer> extractorFunction;
	private final BiConsumer<T, Integer> updateFunction;

	public IntegerColumnEditingSupport(ColumnViewer viewer, Function<T, Integer> extractorFunction, BiConsumer<T, Integer> updateFunction) {
		super(viewer);
		this.extractorFunction = extractorFunction;
		this.updateFunction = updateFunction;
	}

	@Override
	protected boolean canEdit(Object object) {

		return extractorFunction.apply(getEditObject(object)) != null;
	}

	@SuppressWarnings("unchecked")
	private T getEditObject(Object object) {

		if(object instanceof TreeNode) {
			object = ((TreeNode)object).getValue();
		}
		return (T)object;
	}

	@Override
	protected CellEditor getCellEditor(Object object) {

		return new TextCellEditor((Composite)getViewer().getControl());
	}

	@Override
	protected Object getValue(Object object) {

		return String.valueOf(extractorFunction.apply(getEditObject(object)));
	}

	@Override
	protected void setValue(Object element, Object value) {

		if(value instanceof String) {
			try {
				int integer = Integer.parseInt((String)value);
				updateFunction.accept(getEditObject(element), integer);
				getViewer().refresh(element);
			} catch(NumberFormatException e) {
				// not a valid integer, refuse editing then
			}
		}
	}
}
