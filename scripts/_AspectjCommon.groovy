import static org.aspectj.tools.ajc.Main.main as ajc

import grails.util.GrailsUtil

import org.apache.commons.io.FilenameUtils
import org.apache.log4j.Logger
includeTargets << grailsScript('_GrailsBootstrap')

printMessage = { String message -> event('StatusUpdate', [message]) }
errorMessage = { String message -> event('StatusError', [message]) }

import org.apache.log4j.Logger

target(aspectjInit: 'General initialization') {
//	depends checkVersion, configureProxy, packageApp, classpath
//	depends compile, fixClasspath, loadConfig, configureProxy, startLogging

	ajBuildConfig = grailsSettings.config.grails.plugin.aspectj

	log = Logger.getLogger('grails.plugin.aspectj.Scripts')

	argsList = argsMap.params
}

lookupClasspathEntries = { ->
	def cl = Thread.currentThread().contextClassLoader.parent
	def classpathEntries = cl.URLs.file
	classpathEntries.addAll cl.parent.URLs.file
	classpathEntries
}

findAllJavaAndAjFiles = { ->

	// TODO properties
	def sourceDirs = ['src', 'grails-app']
	def sourceExtensions = ['aj', 'java']
	def allDirs = pluginDirectories*.file + new File(basedir)

	def files = []
	for (File dir in allDirs) {
		for (String sourceDir in sourceDirs) {
			def src = new File(dir, sourceDir)
			if (!src.exists()) continue
			src.eachFileRecurse { File f ->
				if (f.file && FilenameUtils.getExtension(f.name) in sourceExtensions) {
					files << f
				}
			}
		}
	}
	files
}

/*
target(fixClasspath: 'Ensures that the classes directories are on the classpath so Config class is found') {
	rootLoader.addURL grailsSettings.classesDir.toURI().toURL()
	rootLoader.addURL grailsSettings.pluginClassesDir.toURI().toURL()
}
target(loadConfig: 'Ensures that the config is properly loaded') {
	binding.variables.remove 'config'
	createConfig()
}
*/

printStackTrace = { e ->
	GrailsUtil.deepSanitize e

	List<StackTraceElement> newTrace = []
	for (StackTraceElement element : e.stackTrace) {
		if (element.fileName && element.lineNumber > 0 && !element.className.startsWith('gant.')) {
			newTrace << element
		}
	}

	if (newTrace) {
		e.stackTrace = newTrace as StackTraceElement[]
	}

	e.printStackTrace()
}

doWithTryCatch = { Closure c ->
	try {
		c()
	}
	catch (e) {
		error "\n$e.message\n"
		printStackTrace e
		if (!isInteractive) {
			exit 1
		}
	}
}

error = { String message ->
	event('StatusError', [message])
}

getRequiredArg = { int index = 0 ->
	String value = validateStringValue(argsList[index])
	if (value) {
		return value
	}
	errorAndDie "\nUsage (optionals in square brackets):\n$USAGE"
}

ask = { String question, String answers = null, String defaultIfMissing = null ->
	String propName = 'cf.ask.' + System.currentTimeMillis()

	def args = [addProperty: propName, message: question]
	if (answers) {
		args.validargs = answers
		if (defaultIfMissing) {
			args.defaultvalue = defaultIfMissing
		}
	}

	ant.input args
	ant.antProject.properties[propName] ?: defaultIfMissing
}

askFor = { String question ->
	String answer
	while (!answer) {
		answer = ask(question)
	}
	answer
}

hasConsole = { -> getBinding().variables.containsKey('grailsConsole') }

displayPermanent = { String msg ->
	if (hasConsole()) grailsConsole.addStatus(msg)
	else println msg
}

displayStatusMsg = { String msg ->
	if (hasConsole()) grailsConsole.updateStatus(msg)
	else print msg
}

displayStatusResult = { String msg ->
	if (hasConsole()) grailsConsole.updateStatus(grailsConsole.lastMessage + msg)
	else println msg
}

displayPeriod = {->
	if (hasConsole()) grailsConsole.indicateProgress()
	else println '.'
}

validateString = { String argName, boolean warn = false ->
	validateStringValue argsMap[argName], argName, warn
}

validateStringValue = { value, String argName = null, boolean warn = false ->
	if (value == null) {
		return null
	}
	if (!(value instanceof String)) {
		if (warn) {
			String argDesc = argName ? " (for argument '$argName')" : ''
			println "WARNING: Value '$value'$argDesc isn't a String, ignoring (assuming null)"
		}
		value = null
	}
	value
}

validateBoolean = { String argName, boolean warn = true ->
	def value = argsMap[argName]
	if (value == null) {
		return false
	}
	if ((value instanceof String) && (value.toLowerCase() in ['true', 'false', 'y', 'n', '1', '0'])) {
		value = value.toBoolean()
	}
	if (!(value instanceof Boolean)) {
		if (warn) {
			println "WARNING: Value '$value' (for argument '$argName') isn't a boolean, assuming false"
		}
		value = false
	}
	value
}

doAjc = { File destination, String extraClasspath, String bootclasspath, String aopXml, boolean showWeaveInfo, boolean verbose,
          boolean time, boolean onlyPackage, boolean onlyProtected, boolean onlyPrivate, boolean onlyPublic  ->

	String target = projectTargetDir.absolutePath

	destination.deleteDir()
	destination.mkdirs()

	File scriptLog = new File(destination, '__ajc.log')

	String cp = lookupClasspathEntries().join(File.pathSeparator)
	if (extraClasspath) {
		cp += File.pathSeparator + extraClasspath
	}

	def args = ['-noExit']
	args << '-source' << '5'
	args << '-cp' << cp
	args << '-inpath' << classesDir.absolutePath // TODO multiple dirs
	args << '-d' << destination.absolutePath
	args << '-log' << scriptLog.absolutePath

	if (bootclasspath) {
		args << '-bootclasspath' << bootclasspath
	}

	if (onlyPackage) {
		args << '-package'
	}
	if (onlyProtected) {
		args << '-protected'
	}
	if (onlyPrivate) {
		args << '-private'
	}
	if (onlyPublic) {
		args << '-public'
	}

	if (aopXml) {
		args << '-outxml' << '-outxmlfile' << aopXml
	}
	if (showWeaveInfo) {
		args << '-showWeaveInfo'
	}
	if (verbose) {
		args << '-verbose'
	}
	if (time) {
		args << '-time'
	}

	org.aspectj.tools.ajc.Main.main(args as String[])
	return scriptLog.text
}
