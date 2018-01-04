/*******************************************************************************
 * Copyright (c) 2015, 2018 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.converter.supplier.mzxml.internal.v22.model;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;

@XmlRegistry
public class ObjectFactory {

	private final static QName _SeparationTechnique_QNAME = new QName("http://sashimi.sourceforge.net/schema_revision/mzXML_2.2", "separationTechnique");

	public ObjectFactory() {
	}

	public MsRun createMsRun() {

		return new MsRun();
	}

	public ParentFile createParentFile() {

		return new ParentFile();
	}

	public MsInstrument createMsInstrument() {

		return new MsInstrument();
	}

	public DataProcessing createDataProcessing() {

		return new DataProcessing();
	}

	public Separation createSeparation() {

		return new Separation();
	}

	public Spotting createSpotting() {

		return new Spotting();
	}

	public Scan createScan() {

		return new Scan();
	}

	public ScanOrigin createScanOrigin() {

		return new ScanOrigin();
	}

	public PrecursorMz createPrecursorMz() {

		return new PrecursorMz();
	}

	public Maldi createMaldi() {

		return new Maldi();
	}

	public Peaks createPeaks() {

		return new Peaks();
	}

	public NameValue createNameValue() {

		return new NameValue();
	}

	public Software createSoftware() {

		return new Software();
	}

	public SeparationTechnique createSeparationTechnique() {

		return new SeparationTechnique();
	}

	public Operator createOperator() {

		return new Operator();
	}

	public OntologyEntry createOntologyEntry() {

		return new OntologyEntry();
	}

	public Plate createPlate() {

		return new Plate();
	}

	public Robot createRobot() {

		return new Robot();
	}

	public Pattern createPattern() {

		return new Pattern();
	}

	public Spot createSpot() {

		return new Spot();
	}

	public Orientation createOrientation() {

		return new Orientation();
	}

	public MsManufacturer createMsManufacturer() {

		return new MsManufacturer();
	}

	public MsMassAnalyzer createMsMassAnalyzer() {

		return new MsMassAnalyzer();
	}

	@XmlElementDecl(namespace = "http://sashimi.sourceforge.net/schema_revision/mzXML_2.2", name = "separationTechnique")
	public JAXBElement<SeparationTechnique> createSeparationTechnique(SeparationTechnique value) {

		return new JAXBElement<SeparationTechnique>(_SeparationTechnique_QNAME, SeparationTechnique.class, null, value);
	}
}
