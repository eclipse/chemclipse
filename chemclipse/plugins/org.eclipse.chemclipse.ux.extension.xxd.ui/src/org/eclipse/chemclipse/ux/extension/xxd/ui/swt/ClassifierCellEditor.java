/*******************************************************************************
 * Copyright (c) 2020, 2021 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Christoph Läubrich - initial API and implementation
 * Philip Wenig - refactoring dialog to a separate class
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.swt;

import java.util.Collection;
import java.util.LinkedHashSet;

import org.eclipse.chemclipse.model.core.Classifiable;
import org.eclipse.chemclipse.ux.extension.xxd.ui.dialogs.ClassifierDialog;
import org.eclipse.jface.viewers.DialogCellEditor;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;

public class ClassifierCellEditor extends DialogCellEditor {

	public ClassifierCellEditor(TableViewer tableViewer) {

		super(tableViewer.getTable());
	}

	@Override
	protected Object openDialogBox(Control cellEditorWindow) {

		Object value = getValue();
		if(value instanceof Classifiable) {
			Classifiable classifiable = (Classifiable)value;
			ClassifierDialog dialog = new ClassifierDialog(cellEditorWindow.getShell(), classifiable);
			if(dialog.open() == Window.OK) {
				return new LinkedHashSet<>(dialog.getValue());
			}
		}
		return value;
	}

	@Override
	protected void updateContents(Object value) {

		if(value instanceof Classifiable) {
			Label label = getDefaultLabel();
			if(label != null && !label.isDisposed()) {
				Collection<String> classifier = ((Classifiable)value).getClassifier();
				if(classifier.isEmpty()) {
					label.setText("");
				} else if(classifier.size() == 1) {
					label.setText(classifier.iterator().next());
				} else {
					label.setText(classifier.size() + " Classifier");
				}
				return;
			}
		}
		super.updateContents(value);
	}
}
