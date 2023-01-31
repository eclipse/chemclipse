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

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlID;
import jakarta.xml.bind.annotation.XmlIDREF;
import jakarta.xml.bind.annotation.XmlSchemaType;
import jakarta.xml.bind.annotation.XmlType;
import jakarta.xml.bind.annotation.adapters.CollapsedStringAdapter;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

/**
 * Description of the acquisition settings of the instrument prior to the start of the run.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AcquisitionSettingsType", propOrder = {"sourceFileRefList", "targetList"})
public class AcquisitionSettingsType extends ParamGroupType {

	protected SourceFileRefListType sourceFileRefList;
	protected TargetListType targetList;
	@XmlAttribute(name = "id", required = true)
	@XmlJavaTypeAdapter(CollapsedStringAdapter.class)
	@XmlID
	@XmlSchemaType(name = "ID")
	protected String id;
	@XmlAttribute(name = "instrumentConfigurationRef", required = true)
	@XmlIDREF
	@XmlSchemaType(name = "IDREF")
	protected Object instrumentConfigurationRef;

	public SourceFileRefListType getSourceFileRefList() {

		return sourceFileRefList;
	}

	public void setSourceFileRefList(SourceFileRefListType value) {

		this.sourceFileRefList = value;
	}

	public TargetListType getTargetList() {

		return targetList;
	}

	public void setTargetList(TargetListType value) {

		this.targetList = value;
	}

	public String getId() {

		return id;
	}

	public void setId(String value) {

		this.id = value;
	}

	public Object getInstrumentConfigurationRef() {

		return instrumentConfigurationRef;
	}

	public void setInstrumentConfigurationRef(Object value) {

		this.instrumentConfigurationRef = value;
	}
}
