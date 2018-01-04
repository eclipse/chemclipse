/*******************************************************************************
 * Copyright (c) 2016, 2018 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.process.supplier.workflows.model;

import java.util.Date;
import java.util.List;

public interface ISingleChromatogramReport {

	String getChromatogramName();

	void setChromatogramName(String chromatogramName);

	String getChromatogramPath();

	void setChromatogramPath(String chromatogramPath);

	Date getEvaluationDate();

	void setEvaluationDate(Date evaluationDate);

	String getDescription();

	void setDescription(String description);

	List<String> getProcessorNames();

	void setProcessorNames(List<String> processorNames);

	String getNotes();

	void setNotes(String notes);
}