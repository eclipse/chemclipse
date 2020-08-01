/*******************************************************************************
 * Copyright (c) 2020 Lablicate GmbH.
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

import org.eclipse.chemclipse.model.identifier.ILibraryInformation;
import org.eclipse.chemclipse.msd.model.core.ILibraryMassSpectrum;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

public class ExtendedMoleculeStructureUI extends Composite {

	private Text textMolecule;

	public ExtendedMoleculeStructureUI(Composite parent, int style) {

		super(parent, style);
		createControl();
	}

	public void setInput(ILibraryMassSpectrum libraryMassSpectrum) {

		if(libraryMassSpectrum != null) {
			ILibraryInformation libraryInformation = libraryMassSpectrum.getLibraryInformation();
			textMolecule.setText(libraryInformation.getMoleculeStructure());
		} else {
			textMolecule.setText("");
		}
	}

	private void createControl() {

		setLayout(new FillLayout());
		textMolecule = createTextMolecule(this);
	}

	private Text createTextMolecule(Composite parent) {

		Text text = new Text(parent, SWT.BORDER | SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
		text.setText("");
		text.setToolTipText("Molecule Structure");
		//
		return text;
	}
}
