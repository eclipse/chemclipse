/*******************************************************************************
 * Copyright (c) 2014, 2018 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.converter.supplier.animl.internal.converter;

public interface IChromatogramTags {

	String ANIML = "AnIML";
	String ATTRIBUTE_XMLNS = "xmlns";
	String ATTRIBUTE_XMLNS_XSI = "xmlns:xsi";
	String ATTRIBUTE_XSI_SCHEMA_LOCATION = "xsi:schemaLocation";
	String ATTRIBUTE_VERSION = "version";
	//
	String ELEMENT_SAMPLE_SET = "SampleSet";
	String ELEMENT_SAMPLE = "Sample";
	String ATTRIBUTE_SAMPLE_NAME = "name";
	String ATTRIBUTE_SAMPLE_ID = "sampleID";
	//
	String ELEMENT_MEASUREMENT_DATA = "MeasurementData";
	String ELEMENT_EXPERIMENT_STEP = "ExperimentStep";
	String ATTRIBUTE_EXPERIMENT_NAME = "name";
	String ELEMENT_TIMESTAMP = "Timestamp";
	String ELEMENT_PAGE_SET = "PageSet";
	String ELEMENT_PAGE = "Page";
	String ATTRIBUTE_PAGE_NAME = "name";
	String ELEMENT_VECTOR_SET = "VectorSet";
	String ATTRIBUTE_PAGE_LENGTH = "length";
	String ELEMENT_VECTOR = "Vector";
	String ATTRIBUTE_VECTOR_NAME = "name";
	String ATTRIBUTE_VECTOR_RETENTION_TIME = "retentionTime";
	String ATTRIBUTE_VECTOR_RETENTION_INDEX = "retentionIndex";
	String ELEMENT_ION = "Ion";
	String ATTRIBUTE_ION_MZ = "mz";
	String ATTRIBUTE_ION_INTENSITY = "intensity";
	//
	String ELEMENT_AUDIT_TRAIL_ENTRY_SET = "AuditTrailEntrySet";
	String ELEMENT_AUDIT_TRAIL_ENTRY = "AuditTrailEntry";
	//
	String ELEMENT_SIGNATURE_SET = "SignatureSet";
	String ELEMENT_SIGNATURE = "Signature";
}
