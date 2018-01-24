/*******************************************************************************
 * Copyright (c) 2017, 2018 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Jan Holy - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model;

import java.util.function.Consumer;

import javafx.beans.property.IntegerProperty;

public interface IPcaSettings {

	void addChangeListener(Consumer<IPcaSettings> listner);

	int getNumberOfPrincipalComponents();

	int getPcX();

	int getPcY();

	int getPcZ();

	IntegerProperty pcXProperty();

	IntegerProperty pcYProperty();

	IntegerProperty pcZProperty();

	void removeChangeListener(Consumer<IPcaSettings> listner);

	void setPcX(int pcX);

	void setPcY(int pcY);

	void setPcZ(int pcZ);
}
