/*******************************************************************************
 * Copyright (c) 2019, 2020 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Christoph Läubrich - initial API and implementation
 * Philip Wenig - refactoring
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.segments;

import java.util.function.Consumer;

import org.eclipse.chemclipse.model.core.IScan;
import org.eclipse.chemclipse.swt.ui.notifier.UpdateNotifierUI;
import org.eclipse.core.runtime.Adapters;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeNode;
import org.eclipse.swt.widgets.Display;

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
			if(element instanceof TreeNode) {
				element = ((TreeNode)element).getValue();
			}
			IScan scan = Adapters.adapt(element, IScan.class);
			if(scan != null) {
				UpdateNotifierUI.update(Display.getDefault(), scan);
			}
			if(type.isInstance(element)) {
				selectionConsumer.accept(type.cast(element));
				return;
			}
		}
		selectionConsumer.accept(null);
	}
}
