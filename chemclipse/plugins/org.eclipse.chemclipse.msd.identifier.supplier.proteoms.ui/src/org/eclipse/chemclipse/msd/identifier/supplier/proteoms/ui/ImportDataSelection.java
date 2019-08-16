/*******************************************************************************
 * Copyright (c) 2016, 2018 Dr. Janko Diminic, Dr. Philip Wenig.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Janko Diminic - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.identifier.supplier.proteoms.ui;

import java.util.List;

import org.eclipse.chemclipse.msd.identifier.supplier.proteoms.model.SpectrumMS;
import org.eclipse.chemclipse.msd.identifier.supplier.proteoms.ui.importdata.ImportParameters;

public class ImportDataSelection {

	private List<SpectrumMS> parsedMSlist;
	private ImportParameters parameters;

	public ImportDataSelection() {
	}

	public ImportDataSelection(List<SpectrumMS> parsedMSlist, ImportParameters parameters) {
		this.parsedMSlist = parsedMSlist;
		this.parameters = parameters;
	}

	public ImportParameters getParameters() {

		return parameters;
	}

	public List<SpectrumMS> getParsedMSlist() {

		return parsedMSlist;
	}
}
