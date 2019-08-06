/*******************************************************************************
 * Copyright (c) 2010, 2019 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 * Christoph Läubrich - add equals
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.msd.process.supplier.batchprocess.model;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.chemclipse.converter.model.IChromatogramInputEntry;
import org.eclipse.chemclipse.model.methods.IProcessMethod;
import org.eclipse.chemclipse.model.methods.ProcessMethod;

public class BatchProcessJob {

	private List<IChromatogramInputEntry> chromatogramInputEntries = new ArrayList<IChromatogramInputEntry>();
	private IProcessMethod processMethod;

	public BatchProcessJob() {
		this(new ProcessMethod());
	}

	public BatchProcessJob(IProcessMethod processMethod) {
		this.processMethod = processMethod;
	}

	public List<IChromatogramInputEntry> getChromatogramInputEntries() {

		return chromatogramInputEntries;
	}

	public IProcessMethod getProcessMethod() {

		return processMethod;
	}

	@Override
	public int hashCode() {

		final int prime = 31;
		int result = 1;
		result = prime * result + ((chromatogramInputEntries == null) ? 0 : chromatogramInputEntries.hashCode());
		result = prime * result + ((processMethod == null) ? 0 : processMethod.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {

		if(this == obj)
			return true;
		if(obj == null)
			return false;
		if(getClass() != obj.getClass())
			return false;
		BatchProcessJob other = (BatchProcessJob)obj;
		if(chromatogramInputEntries == null) {
			if(other.chromatogramInputEntries != null)
				return false;
		} else if(!chromatogramInputEntries.equals(other.chromatogramInputEntries))
			return false;
		if(processMethod == null) {
			if(other.processMethod != null)
				return false;
		} else if(!processMethod.equals(other.processMethod))
			return false;
		return true;
	}
}
