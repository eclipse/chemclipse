/*******************************************************************************
 * Copyright (c) 2022 Lablicate GmbH.
 *
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Matthias Mailänder - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.container.support;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.chemclipse.container.definition.Container;
import org.eclipse.chemclipse.container.definition.IFileContentProvider;
import org.eclipse.chemclipse.converter.core.IMagicNumberMatcher;
import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;

public class FileContainerCache {

	private static final Logger logger = Logger.getLogger(FileContainerCache.class);
	private static final String EXTENSION_POINT = "org.eclipse.chemclipse.container"; //$NON-NLS-1$
	private List<FileContainer> fileContainers = new ArrayList<>();

	public FileContainerCache() {

		IExtensionRegistry registry = Platform.getExtensionRegistry();
		IConfigurationElement[] extensions = registry.getConfigurationElementsFor(EXTENSION_POINT);
		try {
			for(IConfigurationElement element : extensions) {
				FileContainer fileContainer = new FileContainer();
				fileContainer.setMagicNumberMatcher(getMagicNumberMatcher(element));
				final Object object = element.createExecutableExtension(Container.GET_CONTENTS);
				if(object instanceof IFileContentProvider contentProvider) {
					fileContainer.setContentProvider(contentProvider);
				}
				fileContainers.add(fileContainer);
			}
		} catch(CoreException e) {
			logger.warn(e);
		}
	}

	public IFileContentProvider getFileContentProvider(File file) {

		for(FileContainer fileContainer : fileContainers) {
			if(StringUtils.endsWithIgnoreCase(file.getName(), fileContainer.getFileExtension())) {
				IMagicNumberMatcher magicNumberMatcher = fileContainer.getMagicNumberMatcher();
				if(magicNumberMatcher != null) {
					if(magicNumberMatcher.checkFileFormat(file)) {
						return fileContainer.getContentProvider();
					}
				} else {
					return fileContainer.getContentProvider();
				}
			}
		}
		return null;
	}

	private static IMagicNumberMatcher getMagicNumberMatcher(IConfigurationElement element) {

		IMagicNumberMatcher magicNumberMatcher;
		try {
			magicNumberMatcher = (IMagicNumberMatcher)element.createExecutableExtension(Container.MAGIC_NUMBER_MATCHER);
		} catch(Exception e) {
			magicNumberMatcher = null;
		}
		return magicNumberMatcher;
	}
}
