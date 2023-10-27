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
package org.eclipse.chemclipse.pcr.converter.supplier.rdml.internal.v12.model;

import jakarta.xml.bind.annotation.XmlRegistry;

@XmlRegistry
public class ObjectFactory {

	public Rdml createRdml() {

		return new Rdml();
	}

	public RdmlIdType createRdmlIdType() {

		return new RdmlIdType();
	}

	public ExperimenterType createExperimenterType() {

		return new ExperimenterType();
	}

	public DocumentationType createDocumentationType() {

		return new DocumentationType();
	}

	public DyeType createDyeType() {

		return new DyeType();
	}

	public SampleType createSampleType() {

		return new SampleType();
	}

	public TargetType createTargetType() {

		return new TargetType();
	}

	public ThermalCyclingConditionsType createThermalCyclingConditionsType() {

		return new ThermalCyclingConditionsType();
	}

	public ExperimentType createExperimentType() {

		return new ExperimentType();
	}

	public QuantityType createQuantityType() {

		return new QuantityType();
	}

	public IdReferencesType createIdReferencesType() {

		return new IdReferencesType();
	}

	public TemperatureType createTemperatureType() {

		return new TemperatureType();
	}

	public StepType createStepType() {

		return new StepType();
	}

	public RunType createRunType() {

		return new RunType();
	}

	public OligoType createOligoType() {

		return new OligoType();
	}

	public LidOpenType createLidOpenType() {

		return new LidOpenType();
	}

	public GradientType createGradientType() {

		return new GradientType();
	}

	public DpMeltingCurveType createDpMeltingCurveType() {

		return new DpMeltingCurveType();
	}

	public ReactType createReactType() {

		return new ReactType();
	}

	public PauseType createPauseType() {

		return new PauseType();
	}

	public DataCollectionSoftwareType createDataCollectionSoftwareType() {

		return new DataCollectionSoftwareType();
	}

	public DataType createDataType() {

		return new DataType();
	}

	public AnnotationType createAnnotationType() {

		return new AnnotationType();
	}

	public CdnaSynthesisMethodType createCdnaSynthesisMethodType() {

		return new CdnaSynthesisMethodType();
	}

	public PcrFormatType createPcrFormatType() {

		return new PcrFormatType();
	}

	public DpAmpCurveType createDpAmpCurveType() {

		return new DpAmpCurveType();
	}

	public LoopType createLoopType() {

		return new LoopType();
	}

	public TemplateQuantityType createTemplateQuantityType() {

		return new TemplateQuantityType();
	}

	public CommercialAssayType createCommercialAssayType() {

		return new CommercialAssayType();
	}

	public SequencesType createSequencesType() {

		return new SequencesType();
	}

	public XRefType createXRefType() {

		return new XRefType();
	}
}
