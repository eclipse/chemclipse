/*******************************************************************************
 * Copyright (c) 2019, 2020 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Christoph Läubrich - initial API and implementation
 * Philip Wenig - this class needs refactoring
 *******************************************************************************/
package org.eclipse.chemclipse.rcp.app.ui.handlers;

import java.util.List;
import java.util.UUID;
import java.util.function.BiFunction;
import java.util.function.Consumer;

import javax.inject.Named;

import org.eclipse.chemclipse.support.events.IPerspectiveAndViewIds;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.MElementContainer;
import org.eclipse.e4.ui.model.application.ui.MSnippetContainer;
import org.eclipse.e4.ui.model.application.ui.MUIElement;
import org.eclipse.e4.ui.model.application.ui.basic.MCompositePart;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.model.application.ui.basic.MPartStack;
import org.eclipse.e4.ui.model.application.ui.basic.MStackElement;
import org.eclipse.e4.ui.workbench.IPresentationEngine;
import org.eclipse.e4.ui.workbench.UIEvents;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.e4.ui.workbench.modeling.EPartService.PartState;
import org.eclipse.swt.widgets.Display;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;

/**
 * This Handler allows to open a E4 Snippet as an editor, the command must be called with a the {@link OpenSnippetHandler#SNIPPT_PARAMETER} to identify the snippet.
 * It also contains a static {@link OpenSnippetHandler#openSnippet(String, EPartService, MApplication, EModelService)} method to open snippet programmatically.
 * Even though any snippet can be handled by this method, only MParts can be really opened! If you you use a Snippet that is not a {@link MPart}, or {@link MCompositePart} you must take further actions to make it visible
 * 
 * @author Christoph Läubrich
 *
 */
public class OpenSnippetHandler {

	private static final String SNIPPT_PARAMETER = "org.eclipse.chemclipse.rcp.app.ui.commandparameter.opensnippet.snippetid";

	@Execute
	public void execute(@Named(SNIPPT_PARAMETER) String snippetId, EPartService partService, MApplication application, EModelService modelService) {

		openSnippet(snippetId, application, modelService, partService);
	}

	@CanExecute
	public boolean canExecute(@Named(SNIPPT_PARAMETER) String snippetId, MApplication application, EModelService modelService) {

		return modelService.findSnippet(application, snippetId) != null;
	}

	/**
	 * Clone and open the given snippet
	 * 
	 * @param snippetId
	 *            the snippet id to use
	 * @param modelService
	 *            the modelservice to query, take care that this is a recent, injected and not static cached one or you will get "Application has no active Window" in certain circumstances!
	 * @param application
	 *            the {@link MSnippetContainer} to clone the snippet from, you would most likely use the {@link MApplication} here. For special cases it would be possible to NOT pass the {@link MApplication} here but a wrapper, then the part won't be added to the editors partstack
	 * @param partService
	 *            the partservice to use to open the part, or <code>null</code> if opening is not desired
	 */
	public static void openSnippet(String snippetId, MApplication application, EModelService modelService, EPartService partService) {

		openSnippet(snippetId, application, modelService, partService, IPerspectiveAndViewIds.EDITOR_PART_STACK_ID);
	}

	public static void openSnippet(String snippetId, MApplication application, EModelService modelService, EPartService partService, String stackId) {

		addToEditorStack(modelService, stackId, application)//
				.andThen(openPart(partService))//
				.accept(cloneSnippet(snippetId, modelService, application));
	}

	public static void openSnippet(String snippetId, IEclipseContext eclipseContext, BiFunction<IEclipseContext, MPart, Runnable> childContextInitializer) {

		openSnippet(snippetId, eclipseContext, IPerspectiveAndViewIds.EDITOR_PART_STACK_ID, childContextInitializer);
	}

	public static void openSnippet(String snippetId, IEclipseContext eclipseContext, String stackId, BiFunction<IEclipseContext, MPart, Runnable> childContextInitializer) {

		MApplication application = eclipseContext.get(MApplication.class);
		EModelService modelService = eclipseContext.get(EModelService.class);
		EPartService partService = eclipseContext.get(EPartService.class);
		//
		withEclipseContext(eclipseContext, childContextInitializer)//
				.andThen(addToEditorStack(modelService, stackId, application))//
				.andThen(openPart(partService))//
				.accept(cloneSnippet(snippetId, modelService, application));
	}

	public static void openCompositeSnippet(String snippetId, IEclipseContext eclipseContext, BiFunction<IEclipseContext, MPart, Runnable> childContextInitializer) {

		openCompositeSnippet(snippetId, eclipseContext, IPerspectiveAndViewIds.EDITOR_PART_STACK_ID, childContextInitializer);
	}

	public static void openCompositeSnippet(String snippetId, IEclipseContext eclipseContext, String stackId, BiFunction<IEclipseContext, MPart, Runnable> childContextInitializer) {

		MApplication application = eclipseContext.get(MApplication.class);
		EModelService modelService = eclipseContext.get(EModelService.class);
		EPartService partService = eclipseContext.get(EPartService.class);
		//
		withEclipseContext(eclipseContext, childContextInitializer)//
				.andThen(addToEditorStack(modelService, stackId, application))//
				.andThen(disableMove())//
				.andThen(openPart(partService))//
				.andThen(activateAll(partService))//
				.andThen(focusPart(partService))//
				.accept(cloneSnippet(snippetId, modelService, application));
	}

	/**
	 * Creates a consumer that when applied adds the argument to the editor part stack
	 * 
	 * @param modelService
	 * @param searchRoot
	 *            the root to search for the part stack, normally this is an {@link MApplication}
	 * @return
	 */
	public static Consumer<MUIElement> addToEditorStack(EModelService modelService, String stackID, MUIElement searchRoot) {

		return element -> {
			if(element instanceof MStackElement) {
				MStackElement stackElement = (MStackElement)element;
				MUIElement partStack = modelService.find(stackID, searchRoot);
				if(partStack instanceof MPartStack) {
					((MPartStack)partStack).getChildren().add(stackElement);
				}
			}
		};
	}

	public static Consumer<MUIElement> disableMove() {

		return element -> {
			if(element instanceof MElementContainer<?>) {
				final List<?> children = ((MElementContainer<?>)element).getChildren();
				for(final Object child : children) {
					if(child instanceof MUIElement) {
						final MUIElement mui = (MUIElement)child;
						if(child instanceof MPart) {
							mui.getTags().add(IPresentationEngine.NO_MOVE);
						}
						disableMove().accept(mui);
					}
				}
			}
		};
	}

	public static Consumer<MUIElement> withEclipseContext(IEclipseContext parent, BiFunction<IEclipseContext, MPart, Runnable> childContextInitializer) {

		if(parent == null) {
			throw new IllegalArgumentException("IEclipseContext can't be null");
		}
		return new Consumer<MUIElement>() {

			@Override
			public void accept(MUIElement element) {

				IEclipseContext child = parent.createChild(element.getElementId() + ".composite");
				if(element instanceof MPart) {
					MPart part = (MPart)element;
					part.setContext(child);
					child.set(MPart.class, part);
					Runnable runnable;
					if(childContextInitializer != null) {
						runnable = childContextInitializer.apply(child, part);
					} else {
						runnable = null;
					}
					onClose(parent.get(IEventBroker.class), new Runnable() {

						@Override
						public void run() {

							try {
								if(runnable != null) {
									runnable.run();
								}
							} finally {
								child.dispose();
							}
						}
					});
				}
			}
		};
	}

	/**
	 * Clone the given snippet
	 * 
	 * @param snippetId
	 *            the snippet id to use
	 * @param modelService
	 *            the modelservice to query, take care that this is a recent, injected and not static cached one or you will get "Application has no active Window" in certain circumstances!
	 * @param application
	 *            the {@link MSnippetContainer} to clone the snippet from, you would most likely use the {@link MApplication} here
	 * @return the created object
	 */
	public static <T extends MUIElement> T cloneSnippet(String snippetId, EModelService modelService, MSnippetContainer snippetContainer) {

		@SuppressWarnings("unchecked")
		T snippet = (T)modelService.cloneSnippet(snippetContainer, snippetId, null);
		if(snippet != null) {
			snippet.getTags().add(EPartService.REMOVE_ON_HIDE_TAG);
			snippet.setElementId(snippetId + "." + UUID.randomUUID().toString());
		} else {
			throw new IllegalArgumentException("snippet with id " + snippetId + " was not found in container " + snippetContainer);
		}
		return snippet;
	}

	public static Consumer<MUIElement> activateAll(final EPartService partService) {

		return element -> {
			if(element instanceof MPart) {
				final MPart part = (MPart)element;
				partService.showPart(part, PartState.CREATE);
			}
			if(element instanceof MElementContainer<?>) {
				final List<?> children = ((MElementContainer<?>)element).getChildren();
				for(final Object child : children) {
					if(child instanceof MUIElement) {
						activateAll(partService).accept((MUIElement)child);
					}
				}
			}
		};
	}

	public static Consumer<MUIElement> onClose(IEventBroker eventBroker, Runnable runnable) {

		return new Consumer<MUIElement>() {

			@Override
			public void accept(MUIElement element) {

				if(element instanceof MPart) {
					final EventHandler handler = new EventHandler() {

						@Override
						public void handleEvent(final Event event) {

							final Object part = event.getProperty(UIEvents.EventTags.ELEMENT);
							if(part instanceof MPart) {
								if(part == element) {
									final Boolean val = (Boolean)event.getProperty(UIEvents.EventTags.NEW_VALUE);
									if(val != null && !val.booleanValue()) {
										eventBroker.unsubscribe(this);
										runnable.run();
									}
								}
							}
						}
					};
					eventBroker.subscribe(UIEvents.UIElement.TOPIC_TOBERENDERED, handler);
				}
			}
		};
	}

	public static Consumer<MUIElement> focusPart(EPartService partService) {

		return element -> {
			if(element instanceof MPart) {
				MPart part = (MPart)element;
				if(partService != null) {
					Display.getDefault().asyncExec(new Runnable() {

						@Override
						public void run() {

							partService.activate(part, true);
						}
					});
				}
			}
		};
	}

	public static Consumer<MUIElement> openPart(EPartService partService) {

		return element -> {
			if(element instanceof MPart) {
				MPart part = (MPart)element;
				if(partService != null) {
					part.setCloseable(true);
					partService.showPart(part, PartState.ACTIVATE);
				}
			}
		};
	}
}