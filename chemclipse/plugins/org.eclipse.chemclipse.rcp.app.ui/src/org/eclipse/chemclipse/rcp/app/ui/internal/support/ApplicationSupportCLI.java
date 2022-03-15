/*******************************************************************************
 * Copyright (c) 2008, 2022 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 * Christoph Läubrich - allow to execute cli operations with an UI
 *******************************************************************************/
package org.eclipse.chemclipse.rcp.app.ui.internal.support;

import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.rcp.app.cli.ICommandLineProcessor;
import org.eclipse.chemclipse.rcp.app.ui.Activator;
import org.eclipse.chemclipse.rcp.app.ui.ApplicationWorkbenchAdvisor;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.eclipse.equinox.app.IApplication;
import org.eclipse.osgi.service.runnable.StartupMonitor;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;

public class ApplicationSupportCLI {

	private static final Logger logger = Logger.getLogger(ApplicationSupportCLI.class);
	//
	private IExtensionRegistry registry;
	private IConfigurationElement[] elements;
	/*
	 * The names are defined in the schema:
	 * org.eclipse.chemclipse.rcp.app.ui.commandLineProcessor.exsd
	 */
	private static final String EXTENSION_POINT = "org.eclipse.chemclipse.rcp.app.ui.commandLineProcessor";
	// Help
	private static final String OPTION_HELP = "help";
	private static final String OPTION_HELP_DESCRIPTION = "Shows the help.";
	// Command Line
	private static final String OPTION_COMMAND_LINE = "cli";
	private static final String OPTION_COMMAND_LINE_DESCRIPTION = "Run the application in command line mode.";
	// no shutdown option if running in UI mode
	private static final String OPTION_NOSHUTDOWN = "noshutdown";
	private static final String OPTION_NOSHUTDOWN_DESCRIPTION = "Keeps the UI open after execution";
	// Eclipse Command Line
	private static final String OPTION_ECLIPSE_COMMAND_LINE_DESCRIPTION = "See eclipse command line description.";
	// Attributes
	private static final String OPTION_ATTRIBUTE_NAME = "option";
	private static final String OPTION_ATTRIBUTE_HAS_ARGUMENTS = "hasArguments";
	private static final String OPTION_ATTRIBUTE_DESCRIPTION = "description";
	private static final String EXECUTABLE_EXTENSION_NAME = "processor";

	public ApplicationSupportCLI() {

		registry = Platform.getExtensionRegistry();
		elements = registry.getConfigurationElementsFor(EXTENSION_POINT);
		logger.info("Command Line Processor Options");
		for(IConfigurationElement element : elements) {
			logger.info("Option: -" + element.getAttribute(OPTION_ATTRIBUTE_NAME) + " " + element.getAttribute(OPTION_ATTRIBUTE_DESCRIPTION));
		}
	}

	/**
	 * Starts the application in CLI or GUI mode.
	 * 
	 * @param context
	 * @return
	 */
	public Object start() {

		/*
		 * Parse the command line options.
		 */
		Options options = getDefaultOptions();
		addExtensionPointOptions(elements, options);
		CommandLineParser commandLineParser = new DefaultParser();
		CommandLine commandLine;
		try {
			String[] commandLineArgs = getCommandLineArgs();
			commandLine = commandLineParser.parse(options, commandLineArgs, false);
			if(commandLine.hasOption(OPTION_HELP) || commandLine.hasOption(OPTION_COMMAND_LINE)) {
				return startCommandLineContext(commandLine, options);
			} else {
				Activator.getDefault().getBundle().getBundleContext().registerService(StartupMonitor.class, new StartupMonitor() {

					@Override
					public void update() {

					}

					@Override
					public void applicationRunning() {

						startCommandLineContext(commandLine, options);
						if(!commandLine.hasOption(OPTION_NOSHUTDOWN)) {
							PlatformUI.getWorkbench().close();
						}
					}
				}, null);
				return startGraphicalContext();
			}
		} catch(ParseException e) {
			e.printStackTrace();
			return IApplication.EXIT_OK;
		}
	}

	private Object startCommandLineContext(CommandLine commandLine, Options options) {

		if(commandLine.hasOption(OPTION_HELP)) {
			return printOptionsHelp(options);
		} else {
			return executeCommandLineCommands(elements, commandLine);
		}
	}

	private Object startGraphicalContext() {

		/*
		 * GUI
		 */
		Display display = PlatformUI.createDisplay();
		try {
			int returnCode = PlatformUI.createAndRunWorkbench(display, new ApplicationWorkbenchAdvisor());
			if(returnCode == PlatformUI.RETURN_RESTART) {
				return IApplication.EXIT_RESTART;
			}
			return IApplication.EXIT_OK;
		} finally {
			display.dispose();
		}
	}

	private Object printOptionsHelp(Options options) {

		/*
		 * HELP
		 */
		HelpFormatter helpFormatter = new HelpFormatter();
		helpFormatter.printHelp("How to use the command line options", options);
		return IApplication.EXIT_OK;
	}

	private Object executeCommandLineCommands(IConfigurationElement[] elements, CommandLine commandLine) {

		/*
		 * CLI (headless mode)
		 */
		for(IConfigurationElement element : elements) {
			String opt = element.getAttribute(OPTION_ATTRIBUTE_NAME);
			if(commandLine.hasOption(opt)) {
				try {
					/*
					 * Process each extension point implementation.
					 */
					String[] args = commandLine.getOptionValues(opt);
					ICommandLineProcessor commandLineProcessor = (ICommandLineProcessor)element.createExecutableExtension(EXECUTABLE_EXTENSION_NAME);
					commandLineProcessor.process(args);
				} catch(CoreException e) {
					e.printStackTrace();
				}
			}
		}
		return IApplication.EXIT_OK;
	}

	private Options getDefaultOptions() {

		Options options = new Options();
		setCommandLineOptions(options);
		setDefaultOptions(options);
		return options;
	}

	private void setCommandLineOptions(Options options) {

		options.addOption(OPTION_HELP, false, OPTION_HELP_DESCRIPTION);
		options.addOption(OPTION_COMMAND_LINE, false, OPTION_COMMAND_LINE_DESCRIPTION);
		options.addOption(OPTION_NOSHUTDOWN, false, OPTION_NOSHUTDOWN_DESCRIPTION);
	}

	/*
	 * Retrieves the command line default options.
	 */
	private void setDefaultOptions(Options options) {

		Properties properties = System.getProperties();
		String eclipseCommands = (String)properties.get("eclipse.commands");
		/*
		 * eclipseCommands =
		 * ...
		 * -launcher
		 * /opt/eclipse/RCP/3.6.2-Helios-SR2/eclipse/deltapack/eclipse/eclipse
		 * -name
		 * Eclipse
		 * -showsplash
		 * 600
		 * -product
		 * org.eclipse.chemclipse.rcp.compilation.product
		 * -data
		 * ...
		 * Regex:
		 * "(.*)(-)(.*?)(\r|\n)" with groupOne == null || groupOne.equals("")
		 * Entries passing:
		 * -launcher, -name, -showsplash, -product, -data
		 * Entries failing:
		 * -Helios-SR2/eclipse/deltapack/eclipse/eclipse
		 */
		Pattern pattern = Pattern.compile("(.*)(-)(.*?)([\r\n])");
		Matcher matcher = pattern.matcher(eclipseCommands);
		while(matcher.find()) {
			String groupOne = matcher.group(1);
			if(groupOne == null || groupOne.equals("")) {
				options.addOption(matcher.group(3), false, OPTION_ECLIPSE_COMMAND_LINE_DESCRIPTION);
			}
		}
	}

	private String[] getCommandLineArgs() {

		/*
		 * Arguments like:
		 * --launcher.GTK_version
		 * 2
		 * will be removed.
		 */
		String[] commandLineArgs = Platform.getCommandLineArgs();
		for(int i = 0; i < commandLineArgs.length; i++) {
			String commandLineArg = commandLineArgs[i];
			if(commandLineArg != null && commandLineArg.startsWith("--")) {
				/*
				 * --launcher.GTK_version
				 * =>
				 * launcher_GTK_version
				 */
				String modifiedArg = commandLineArg.replace("--", "");
				modifiedArg = modifiedArg.replaceAll("\\.", "_");
				commandLineArgs[i] = modifiedArg;
			}
		}
		//
		return commandLineArgs;
	}

	private void addExtensionPointOptions(IConfigurationElement[] elements, Options options) {

		Option option;
		/*
		 * Set all available options.
		 */
		for(IConfigurationElement element : elements) {
			String opt = element.getAttribute(OPTION_ATTRIBUTE_NAME);
			boolean hasArguments = Boolean.parseBoolean(element.getAttribute(OPTION_ATTRIBUTE_HAS_ARGUMENTS));
			String description = element.getAttribute(OPTION_ATTRIBUTE_DESCRIPTION);
			option = new Option(opt, hasArguments, description);
			options.addOption(option);
		}
	}
}
