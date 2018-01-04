/*******************************************************************************
 * Copyright (c) 2013, 2018 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.msd.quantitation.supplier.chemclipse.ui.events;

import org.eclipse.e4.core.services.events.IEventBroker;

public interface IChemClipseQuantitationEvents {

	String PROPERTY_QUANTITATION_COMPOUND_DOCUMENT = "QuantitationCompoundDocument"; // IQuantitationCompoundDocument
	String PROPERTY_DATABASE = "Database"; // IDatabase
	String TOPIC_QUANTITATION_COMPOUND_DOCUMENT_UPDATE = "quantitation/msd/update/supplier/chemclipse/quantitationcompounddocument";
	//
	String PROPERTY_QUANTITATION_TABLE_NAME = IEventBroker.DATA; // Database Name
	String TOPIC_QUANTITATION_TABLE_UPDATE = "quantitation/msd/update/supplier/chemclipse/quantitationtable";
}
