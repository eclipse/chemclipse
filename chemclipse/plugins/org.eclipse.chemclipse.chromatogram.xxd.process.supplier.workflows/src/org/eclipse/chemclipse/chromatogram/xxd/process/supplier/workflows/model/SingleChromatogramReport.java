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

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

@XmlRootElement(name = "SingleChromatogramReport")
public class SingleChromatogramReport implements ISingleChromatogramReport {

	@XmlElement(name = "ChromatogramName")
	private String chromatogramName;
	@XmlElement(name = "ChromatogramPath")
	private String chromatogramPath;
	@XmlElement(name = "EvaluationDate", type = Date.class)
	private Date evaluationDate;
	@XmlElement(name = "Description")
	private String description;
	@XmlElementWrapper(name = "ProcessorNames")
	@XmlElement(name = "ProcessorName", type = String.class)
	private List<String> processorNames;
	@XmlElement(name = "Notes")
	private String notes;

	@XmlTransient
	@Override
	public String getChromatogramName() {

		return chromatogramName;
	}

	@Override
	public void setChromatogramName(String chromatogramName) {

		this.chromatogramName = chromatogramName;
	}

	@XmlTransient
	@Override
	public String getChromatogramPath() {

		return chromatogramPath;
	}

	@Override
	public void setChromatogramPath(String chromatogramPath) {

		this.chromatogramPath = chromatogramPath;
	}

	@XmlTransient
	@Override
	public Date getEvaluationDate() {

		return evaluationDate;
	}

	@Override
	public void setEvaluationDate(Date evaluationDate) {

		this.evaluationDate = evaluationDate;
	}

	@XmlTransient
	@Override
	public String getDescription() {

		return description;
	}

	@Override
	public void setDescription(String description) {

		this.description = description;
	}

	@XmlTransient
	@Override
	public List<String> getProcessorNames() {

		return processorNames;
	}

	@Override
	public void setProcessorNames(List<String> processorNames) {

		this.processorNames = processorNames;
	}

	@XmlTransient
	@Override
	public String getNotes() {

		return notes;
	}

	@Override
	public void setNotes(String notes) {

		this.notes = notes;
	}
}
