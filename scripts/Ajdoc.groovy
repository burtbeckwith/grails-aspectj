import static org.aspectj.tools.ajdoc.Main.main as ajdoc

/*
USAGE = """
Usage: grails s2-quickstart <domain-class-package> <user-class-name> <role-class-name> [requestmap-class-name]

Creates a user and role class (and optionally a requestmap class) in the specified package

Example: grails s2-quickstart com.yourapp User Role
Example: grails s2-quickstart com.yourapp Person Authority Requestmap
"""
*/

includeTargets << new File("$aspectjPluginDir/scripts/_AspectjCommon.groovy")

target(ajdoc: 'TODO') {
	depends aspectjInit

	doWithTryCatch {

		String target = projectTargetDir.absolutePath

		File destination = new File(projectTargetDir, 'ajdoc') // TODO prop
		destination.deleteDir()
		destination.mkdirs()

		File scriptLog = new File(destination, '__ajdoc.log')

		String cp = lookupClasspathEntries().join(File.pathSeparator)
		if (argsMap.extraClasspath) {
			cp += File.pathSeparator + argsMap.extraClasspath
		}

		def args = ['-noExit']
		args << '-source' << '5'
//		args << '-classpath' << cp
		args << '-d' << destination.absolutePath
		args << '-log' << scriptLog.absolutePath
		args << '-overview' << 'overview123'

		if (argsMap.bootclasspath) {
			args << '-bootclasspath' << argsMap.bootclasspath
		}

		for (String scope in ['package', 'protected', 'private', 'public']) {
			if (argsMap[scope]) {
				args << '-' + scope
			}
		}

		if (argsMap.ajverbose) {
			args << '-verbose'
		}

		Set dirs = []
		for (File f in findAllJavaAndAjFiles()) {
			dirs << f.parentFile
		}
		args << '-sourcepath'
		args.addAll dirs

		ajdoc(args as String[])
		println scriptLog.text

	}

// -overview overviewFile
//  sourcepathlist
// sourcefiles... | packages... | @file... | -argfile file...] [ ajc options ]
}

setDefaultTarget 'ajdoc'

/*
where <options> includes:
  -public                   Show only public classes and members
  -protected                Show protected/public classes and members
  -package                  Show package/protected/public classes and members
  -private                  Show all classes and members
  -sourcepath <pathlist>    Specify where to find source files
  -bootclasspath <pathlist> Override location of class files loaded
  -windowtitle <text>       Browser window title for the documenation  -bottom <html-code>       Include bottom text for each page  -link <url>               Create links to javadoc output at <url>  -argfile <file>           Build config file (wildcards not supported)
  -v                        Print out the version of ajdoc
  -source <version>         set source level (1.3, 1.4 or 1.5)
*/
