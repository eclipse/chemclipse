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
package org.eclipse.chemclipse.support.ui.swt.edit;

import static org.eclipse.chemclipse.support.ui.swt.ControlBuilder.fill;
import static org.eclipse.chemclipse.support.ui.swt.ControlBuilder.gridData;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;

public class MultiLineTextEdit extends EditValue<List<String>> {

	private final Text text;
	private final List<String> initialValues = new ArrayList<String>();
	private List<String> editedValues = new CopyOnWriteArrayList<>();

	public MultiLineTextEdit(Composite parent, Collection<String> initialText) {
		if(initialText == null) {
			initialText = Collections.emptyList();
		}
		initialValues.addAll(initialText);
		editedValues.addAll(initialValues);
		text = fill(new Text(parent, SWT.MULTI | SWT.BORDER | SWT.WRAP | SWT.V_SCROLL));
		gridData(text).heightHint = 100;
		text.setText(String.join(Text.DELIMITER, initialValues));
		text.addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(ModifyEvent e) {

				editedValues.clear();
				editedValues.addAll(Arrays.asList(text.getText().split(Text.DELIMITER)));
				setChanged();
				notifyObservers(editedValues);
			}
		});
	}

	@Override
	public boolean isEdited() {

		return !editedValues.equals(initialValues);
	}

	@Override
	public List<String> getValue() {

		return editedValues;
	}

	@Override
	public Control getControl() {

		return text;
	}
}
