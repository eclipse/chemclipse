/*******************************************************************************
 * Copyright (c) 2023, 2026 Lablicate GmbH.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 * 
 * Contributors:
 * Matthias Mailänder - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.support.l10n;

import java.util.Locale;

import org.eclipse.chemclipse.support.Activator;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.services.translation.TranslationProviderFactory;
import org.eclipse.e4.core.services.translation.TranslationService;
import org.osgi.framework.FrameworkUtil;

public class TranslationSupport {

	public static TranslationService getTranslationService() {

		IEclipseContext context = Activator.getDefault().getEclipseContext();
		TranslationService service = context.get(TranslationService.class);
		if(service == null) {
			if(context.get(TranslationService.LOCALE) == null) {
				context.set(TranslationService.LOCALE, Locale.getDefault());
			}
			service = TranslationProviderFactory.bundleTranslationService(context);
			context.set(TranslationService.class, service);
		}
		return service;
	}

	public static String getContributorURI(Class<?> clazz) {

		return "platform:/plugin/" + FrameworkUtil.getBundle(clazz).getSymbolicName();
	}
}
