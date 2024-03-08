/*******************************************************************************
 * Copyright (c) 2024 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Matthias Mailänder - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.wsd.converter.supplier.spectroml.model.v1;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.FIELD)
public class MeasurementExecution {

	@XmlElement(name = "project")
	private String project;
	@XmlElement(name = "timeStamp")
	private TimeStamp timeStamp;
	@XmlElement(name = "operator")
	private Operator operator;
	@XmlElement(name = "comment")
	private String comment;

	public String getProject() {

		return project;
	}

	public void setProject(String project) {

		this.project = project;
	}

	public TimeStamp getTimeStamp() {

		return timeStamp;
	}

	public void setTimeStamp(TimeStamp timeStamp) {

		this.timeStamp = timeStamp;
	}

	public Operator getOperator() {

		return operator;
	}

	public void setOperator(Operator operator) {

		this.operator = operator;
	}

	public String getComment() {

		return comment;
	}

	public void setComment(String comment) {

		this.comment = comment;
	}
}