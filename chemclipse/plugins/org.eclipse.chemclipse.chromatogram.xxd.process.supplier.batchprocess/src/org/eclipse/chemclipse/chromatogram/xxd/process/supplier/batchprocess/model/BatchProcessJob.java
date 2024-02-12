/*******************************************************************************
 * Copyright (c) 2010, 2024 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 * Christoph Läubrich - add equals
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.process.supplier.batchprocess.model;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.chemclipse.converter.model.IChromatogramInputEntry;
import org.eclipse.chemclipse.model.types.DataType;
import org.eclipse.chemclipse.processing.methods.IProcessMethod;
import org.eclipse.chemclipse.processing.methods.ProcessMethod;

public class BatchProcessJob {

	public static final DataType[] DATA_TYPES = new DataType[]{DataType.CSD, DataType.MSD, DataType.WSD, DataType.ISD};
	public static final DataType DATA_TYPE_DEFAULT = DataType.MSD;
	//
	public static final String DESCRIPTION = "Batch Job";
	public static final String FILE_EXTENSION = ".obj";
	public static final String FILE_NAME = DESCRIPTION.replaceAll("\\s", "") + FILE_EXTENSION;
	public static final String FILTER_EXTENSION = "*" + FILE_EXTENSION;
	public static final String FILTER_NAME = DESCRIPTION + " (*" + FILE_EXTENSION + ")";
	//
	private DataType dataType = DATA_TYPE_DEFAULT;
	private List<IChromatogramInputEntry> chromatogramInputEntries = new ArrayList<>();
	private IProcessMethod processMethod;

	public BatchProcessJob() {

		this(new ProcessMethod(ProcessMethod.CHROMATOGRAPHY));
	}

	public BatchProcessJob(IProcessMethod processMethod) {

		this.processMethod = processMethod;
	}

	public DataType getDataType() {

		return dataType;
	}

	public void setDataType(DataType dataType) {

		this.dataType = dataType;
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
		result = prime * result + ((dataType == null) ? 0 : dataType.hashCode());
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
		if(dataType == null) {
			if(other.dataType != null)
				return false;
		} else if(!dataType.equals(other.dataType))
			return false;
		return true;
	}
}
