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
public class DataCalculation {

	@XmlElement(name = "scaleFactor")
	private ScaleFactor scaleFactor;
	@XmlElement(name = "numberPoints")
	private NumberPoints numberPoints;
	@XmlElement(name = "pointIncrement")
	private PointIncrement pointIncrement;
	@XmlElement(name = "startValue")
	private StartValue startValue;
	@XmlElement(name = "comment")
	private String comment;

	public ScaleFactor getScaleFactor() {

		return scaleFactor;
	}

	public void setScaleFactor(ScaleFactor scaleFactor) {

		this.scaleFactor = scaleFactor;
	}

	public NumberPoints getNumberPoints() {

		return numberPoints;
	}

	public void setNumberPoints(NumberPoints numberPoints) {

		this.numberPoints = numberPoints;
	}

	public PointIncrement getPointIncrement() {

		return pointIncrement;
	}

	public void setPointIncrement(PointIncrement pointIncrement) {

		this.pointIncrement = pointIncrement;
	}

	public StartValue getStartValue() {

		return startValue;
	}

	public void setStartValue(StartValue startValue) {

		this.startValue = startValue;
	}

	public String getComment() {

		return comment;
	}

	public void setComment(String comment) {

		this.comment = comment;
	}
}