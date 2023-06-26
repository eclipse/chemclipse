/*******************************************************************************
 * Copyright (c) 2023 Lablicate GmbH.
 * 
 * All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Matthias Mailänder - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.identifier.targets;

import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;

public class TargetIdentifier {

	private static final Logger logger = Logger.getLogger(TargetIdentifier.class);
	//
	private static final String EXTENSION_POINT = "org.eclipse.chemclipse.chromatogram.xxd.identifier.targetIdentifier";

	private TargetIdentifier() {

	}

	public static ITargetIdentifierSupport getTargetIdentifierSupport() {

		TargetIdentifierSupport identifierSupport = new TargetIdentifierSupport();
		IExtensionRegistry registry = Platform.getExtensionRegistry();
		IConfigurationElement[] extensions = registry.getConfigurationElementsFor(EXTENSION_POINT);
		try {
			for(IConfigurationElement element : extensions) {
				Object object = element.createExecutableExtension("targetURL");
				if(object instanceof ITargetIdentifierSupplier identifier) {
					identifierSupport.add(identifier);
				}
			}
		} catch(CoreException e) {
			logger.warn(e);
		}
		return identifierSupport;
	}
}
