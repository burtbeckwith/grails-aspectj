includeTargets << new File("$aspectjPluginDir/scripts/_AspectjCommon.groovy")

target(ajc: 'TODO') {
	depends aspectjInit

	doWithTryCatch {

argsMap.aopXml = '__aop.xml'
argsMap.showWeaveInfo = true

		File destination = new File(projectTargetDir, 'ajc') // TODO prop

		String scriptOutput = doAjc(
			destination, argsMap.extraClasspath, argsMap.bootclasspath, argsMap.aopXml, argsMap.showWeaveInfo ?: false,
			argsMap.ajverbose ?: false, argsMap.time ?: false, argsMap['package'] ?: false,
			argsMap['protected'] ?: false, argsMap['private'] ?: false, argsMap['public'] ?: false)

		println scriptOutput
	}
}

setDefaultTarget 'ajc'
