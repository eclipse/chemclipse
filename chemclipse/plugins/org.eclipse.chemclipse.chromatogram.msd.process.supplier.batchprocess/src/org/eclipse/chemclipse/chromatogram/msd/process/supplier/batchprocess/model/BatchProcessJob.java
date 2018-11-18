/*******************************************************************************
 * Copyright (c) 2010, 2018 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.msd.process.supplier.batchprocess.model;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.chemclipse.converter.model.IChromatogramInputEntry;
import org.eclipse.chemclipse.model.methods.ProcessMethod;

public class BatchProcessJob implements IBatchProcessJob {

	private List<IChromatogramInputEntry> chromatogramInputEntries = new ArrayList<IChromatogramInputEntry>();
	private ProcessMethod processMethod = new ProcessMethod();

	@Override
	public List<IChromatogramInputEntry> getChromatogramInputEntries() {

		return chromatogramInputEntries;
	}

	@Override
	public ProcessMethod getProcessMethod() {

		return processMethod;
	}
}
