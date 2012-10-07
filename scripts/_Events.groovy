includeTargets << new File("$aspectjPluginDir/scripts/_AspectjCommon.groovy")

eventCompileStart = {
	classesDir.deleteDir()
}

eventCompileEnd = {
	doWithTryCatch {
		String extraClasspath
		String bootclasspath
		String aopXml = '__aop.xml' // TODO
		boolean showWeaveInfo = true // TODO
		boolean verbose = false
		boolean time = false
		boolean onlyPackage = false
		boolean onlyProtected = false
		boolean onlyPrivate = false
		boolean onlyPublic = false

		File destination = new File(projectTargetDir, 'ajc') // TODO prop

		String scriptOutput = doAjc(
			destination, extraClasspath, bootclasspath, aopXml, showWeaveInfo, verbose,
			time, onlyPackage, onlyProtected, onlyPrivate, onlyPublic)

		println scriptOutput


println "replacing classes"

		classesDir.deleteDir()
		classesDir.mkdirs()

		ant.copy(todir: classesDir) {
			fileset(dir: destination)
		}
	}
}
