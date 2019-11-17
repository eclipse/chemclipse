/*******************************************************************************
 * Copyright (c) 2019 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Christoph Läubrich - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.segments;

import java.util.function.Consumer;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;

public class AnalysisSegmentSelectionChangedListener<X> implements ISelectionChangedListener {

	private final Consumer<X> selectionConsumer;
	private final Class<X> type;

	public AnalysisSegmentSelectionChangedListener(Class<X> type, Consumer<X> selectionConsumer) {
		this.type = type;
		this.selectionConsumer = selectionConsumer;
		selectionConsumer.accept(null);
	}

	@Override
	public void selectionChanged(SelectionChangedEvent event) {

		ISelection selection = event.getSelection();
		if(selection instanceof IStructuredSelection) {
			Object element = ((IStructuredSelection)selection).getFirstElement();
			if(type.isInstance(element)) {
				selectionConsumer.accept(type.cast(element));
				return;
			}
		}
		selectionConsumer.accept(null);
	}
}
