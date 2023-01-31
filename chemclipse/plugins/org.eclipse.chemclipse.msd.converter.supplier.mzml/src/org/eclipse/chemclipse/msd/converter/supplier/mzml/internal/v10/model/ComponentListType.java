/*******************************************************************************
 * Copyright (c) 2023 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Matthias Mailänder - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.converter.supplier.mzml.internal.v10.model;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElementRef;
import jakarta.xml.bind.annotation.XmlElementRefs;
import jakarta.xml.bind.annotation.XmlSchemaType;
import jakarta.xml.bind.annotation.XmlType;

/**
 * List with the different components used in the mass spectrometer. At least one source, one mass analyzer and one detector need to be specified.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ComponentListType", propOrder = {"sourceOrAnalyzerOrDetector"})
public class ComponentListType {

	@XmlElementRefs({@XmlElementRef(name = "source", namespace = "http://psi.hupo.org/schema_revision/mzML_1.0.0", type = JAXBElement.class, required = false), @XmlElementRef(name = "detector", namespace = "http://psi.hupo.org/schema_revision/mzML_1.0.0", type = JAXBElement.class, required = false), @XmlElementRef(name = "analyzer", namespace = "http://psi.hupo.org/schema_revision/mzML_1.0.0", type = JAXBElement.class, required = false)})
	protected List<JAXBElement<ComponentType>> sourceOrAnalyzerOrDetector;
	@XmlAttribute(name = "count", required = true)
	@XmlSchemaType(name = "nonNegativeInteger")
	protected BigInteger count;

	public List<JAXBElement<ComponentType>> getSourceOrAnalyzerOrDetector() {

		if(sourceOrAnalyzerOrDetector == null) {
			sourceOrAnalyzerOrDetector = new ArrayList<JAXBElement<ComponentType>>();
		}
		return this.sourceOrAnalyzerOrDetector;
	}

	public BigInteger getCount() {

		return count;
	}

	public void setCount(BigInteger value) {

		this.count = value;
	}
}
