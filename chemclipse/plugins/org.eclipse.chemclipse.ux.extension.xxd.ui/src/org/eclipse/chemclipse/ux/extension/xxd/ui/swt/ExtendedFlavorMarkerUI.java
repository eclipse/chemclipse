/*******************************************************************************
 * Copyright (c) 2020, 2022 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.swt;

import org.eclipse.chemclipse.model.identifier.IFlavorMarker;
import org.eclipse.chemclipse.model.identifier.ILibraryInformation;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

public class ExtendedFlavorMarkerUI extends Composite {

	private Text text;

	public ExtendedFlavorMarkerUI(Composite parent, int style) {

		super(parent, style);
		createControl();
	}

	public void setInput(ILibraryInformation libraryInformation) {

		if(libraryInformation != null) {
			StringBuilder builder = new StringBuilder();
			for(IFlavorMarker flavorMarker : libraryInformation.getFlavorMarkers()) {
				builder.append(flavorMarker.getOdor());
				builder.append(" ");
				builder.append(flavorMarker.getMatrix());
				builder.append(" ");
				builder.append(flavorMarker.getSolvent());
				builder.append("\n");
			}
			text.setText(builder.toString());
		} else {
			text.setText("");
		}
	}

	private void createControl() {

		setLayout(new FillLayout());
		text = createTextMolecule(this);
	}

	private Text createTextMolecule(Composite parent) {

		Text text = new Text(parent, SWT.BORDER | SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
		text.setText("");
		text.setToolTipText("Column Indices");
		//
		return text;
	}
}