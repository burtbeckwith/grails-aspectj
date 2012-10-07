import static org.aspectj.tools.ajbrowser.Main.main as ajbrowser

includeTargets << new File("$aspectjPluginDir/scripts/_AspectjCommon.groovy")

target(ajbrowser: 'TODO') {
	depends checkVersion, configureProxy, packageApp, classpath

}

setDefaultTarget 'ajbrowser'
