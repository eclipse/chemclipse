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
package org.eclipse.chemclipse.support.settings;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.fasterxml.jackson.annotation.JacksonAnnotation;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@JacksonAnnotation
public @interface ShortSettingsProperty {

	short step() default 1;

	short minValue() default Short.MIN_VALUE;

	short maxValue() default Short.MAX_VALUE;

	Validation validation() default Validation.NONE;

	public enum Validation {
		ODD_NUMBER_INCLUDING_ZERO, ODD_NUMBER, EVEN_NUMBER, NONE;
	}
}
