/*******************************************************************************
 * Copyright (c) 2010, 2026 Lablicate GmbH.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 * Christoph Läubrich - add equals
 *******************************************************************************/
package org.eclipse.chemclipse.process.supplier.batchprocess.core;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.chemclipse.model.types.DataType;
import org.eclipse.chemclipse.process.supplier.batchprocess.io.IBatchProcessInputEntry;
import org.eclipse.chemclipse.processing.methods.IProcessMethod;

public class BatchProcessJob {

	public static final DataType[] DATA_TYPES = new DataType[]{DataType.CSD, DataType.MSD, DataType.WSD, DataType.VSD};
	public static final DataType DATA_TYPE_DEFAULT = DataType.MSD;

	public static final String DESCRIPTION = "Batch Job";
	public static final String FILE_EXTENSION = ".obj";
	public static final String FILE_NAME = DESCRIPTION.replaceAll("\\s", "") + FILE_EXTENSION;
	public static final String FILTER_EXTENSION = "*" + FILE_EXTENSION;
	public static final String FILTER_NAME = DESCRIPTION + " (*" + FILE_EXTENSION + ")";

	private DataType dataType = DATA_TYPE_DEFAULT;
	private List<IBatchProcessInputEntry> inputEntries = new ArrayList<>();
	private IProcessMethod processMethod;

	public BatchProcessJob(IProcessMethod processMethod) {

		this.processMethod = processMethod;
	}

	public DataType getDataType() {

		return dataType;
	}

	public void setDataType(DataType dataType) {

		this.dataType = dataType;
	}

	public List<IBatchProcessInputEntry> getBatchProcessInputEntries() {

		return inputEntries;
	}

	public IProcessMethod getProcessMethod() {

		return processMethod;
	}

	@Override
	public int hashCode() {

		final int prime = 31;
		int result = 1;
		result = prime * result + ((inputEntries == null) ? 0 : inputEntries.hashCode());
		result = prime * result + ((dataType == null) ? 0 : dataType.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {

		if(this == obj) {
			return true;
		}
		if(obj == null) {
			return false;
		}
		if(getClass() != obj.getClass()) {
			return false;
		}
		BatchProcessJob other = (BatchProcessJob)obj;
		if(inputEntries == null) {
			if(other.inputEntries != null) {
				return false;
			}
		} else if(!inputEntries.equals(other.inputEntries)) {
			return false;
		}
		if(dataType == null) {
			if(other.dataType != null) {
				return false;
			}
		} else if(!dataType.equals(other.dataType)) {
			return false;
		}
		return true;
	}
}
