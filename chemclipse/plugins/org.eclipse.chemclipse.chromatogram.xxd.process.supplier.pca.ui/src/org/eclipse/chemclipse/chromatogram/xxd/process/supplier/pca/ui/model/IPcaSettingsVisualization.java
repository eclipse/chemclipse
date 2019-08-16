/*******************************************************************************
 * Copyright (c) 2019 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Jan Holy - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.model;

import java.util.function.Consumer;

import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.IPcaSettings;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.StringProperty;

public interface IPcaSettingsVisualization extends IPcaSettings {

	void removeListener(Consumer<IPcaSettingsVisualization> changeSettingsListner);

	void addListener(Consumer<IPcaSettingsVisualization> changeSettingsListner);

	IntegerProperty numberOfPrincipalComponentsProperty();

	StringProperty pcaAlgorithmProperty();

	BooleanProperty removeUselessVariablesProperty();

	@Override
	IPcaSettingsVisualization makeDeepCopy();
}
