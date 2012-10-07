class AspectjGrailsPlugin {

	String version = '0.1-SNAPSHOT'
	String grailsVersion = '2.0 > *'
	String author = 'Burt Beckwith'
	String authorEmail = 'beckwithb@vmware.com'
	String title = 'Aspectj Plugin'
	String description = 'Aspectj Plugin'
	String documentation = 'http://grails.org/plugin/aspectj'
	List pluginExcludes = [
		'docs/**',
		'src/docs/**'
	]

	String license = 'APACHE'
	def issueManagement = [system: 'JIRA', url: 'http://jira.grails.org/browse/GPASPECTJ']
	def scm = [url: 'https://github.com/burtbeckwith/grails-aspectj']

	def doWithWebDescriptor = { xml ->
		// TODO Implement additions to web.xml (optional), this event occurs before
	}

	def doWithSpring = {
		// TODO Implement runtime spring config (optional)
	}

	def doWithDynamicMethods = { ctx ->
		// TODO Implement registering dynamic methods to classes (optional)
	}

	def doWithApplicationContext = { ctx ->
		// TODO Implement post initialization spring config (optional)
	}

	def onChange = { event ->
		// TODO Implement code that is executed when any artefact that this plugin is
		// watching is modified and reloaded. The event contains: event.source,
		// event.application, event.manager, event.ctx, and event.plugin.
	}

	def onConfigChange = { event ->
		// TODO Implement code that is executed when the project configuration changes.
		// The event is the same as for 'onChange'.
	}

	def onShutdown = { event ->
		// TODO Implement code that is executed when the application shuts down (optional)
	}
}
