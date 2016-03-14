package com.derby.nuke.wheatgrass.log

import java.nio.charset.Charset

import org.slf4j.LoggerFactory
import org.springframework.core.env.PropertyResolver
import org.springframework.core.env.PropertySources
import org.springframework.core.env.PropertySourcesPropertyResolver

import ch.qos.logback.classic.Level
import ch.qos.logback.classic.Logger
import ch.qos.logback.classic.LoggerContext
import ch.qos.logback.classic.encoder.PatternLayoutEncoder
import ch.qos.logback.classic.joran.JoranConfigurator
import ch.qos.logback.classic.spi.ILoggingEvent
import ch.qos.logback.core.Appender
import ch.qos.logback.core.ConsoleAppender
import ch.qos.logback.core.joran.spi.JoranException
import ch.qos.logback.core.rolling.RollingFileAppender
import ch.qos.logback.core.rolling.SizeAndTimeBasedFNATP
import ch.qos.logback.core.rolling.TimeBasedRollingPolicy
import ch.qos.logback.core.status.InfoStatus
import ch.qos.logback.core.status.StatusManager
import ch.qos.logback.core.util.StatusPrinter

import com.derby.nuke.wheatgrass.config.PropertiesUtils

class LogbackInitializing {

	public static final String LOGGER_PROPERTY = "logger";
	public static final String LOGGER_PROPERTY_PREFIX = LOGGER_PROPERTY + '.';

	public static final String LOG_APPENDER = "log.appender";
	public static final String LOG_APPENDER_PREFIX = LOG_APPENDER + '.';

	public static final String LOGBACK_FILE_PROPERTY = "logback.configurationFile";

	private Map<String, Appender> appenders = new HashMap<String, Appender>();

	LogbackInitializing(PropertySources properties){
		try{
			PropertyResolver resolver = new PropertySourcesPropertyResolver(properties);
			LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
			if (resolver.containsProperty(LOGBACK_FILE_PROPERTY)) {
				try {
					JoranConfigurator configurator = new JoranConfigurator();
					configurator.setContext(loggerContext);
					loggerContext.reset();
					configurator.doConfigure(resolver.getProperty(LOGBACK_FILE_PROPERTY));
				} catch (JoranException ignored) {
				}
				StatusPrinter.printInCaseOfErrorsOrWarnings(loggerContext);
				return;
			}

			Logger rootLogger = loggerContext.getLogger(Logger.ROOT_LOGGER_NAME);
			loggerContext.reset();

			StatusManager sm = loggerContext.getStatusManager();
			if (sm != null) {
				sm.add(new InfoStatus("Setting up default configuration.", loggerContext));
			}

			for (String key : PropertiesUtils.getKeys(properties, LOG_APPENDER_PREFIX)) {
				String loggerName = key.substring(LOG_APPENDER_PREFIX.length());

				List<String> loggerAppenderNames = PropertiesUtils.getList(resolver, key);
				for (String appenderName : loggerAppenderNames) {
					Appender<ILoggingEvent> appender = createAppender(appenderName + "." + loggerName, resolver, loggerContext);
					if (appender != null) {
						loggerContext.getLogger(loggerName).setAdditive(false);
						loggerContext.getLogger(loggerName).addAppender(appender);
						appenders.put(appenderName + "." + loggerName, appender);
					}
				}
			}

			List<String> appenderNames = PropertiesUtils.getList(resolver, LOG_APPENDER);
			if (appenderNames == null) {
				appenderNames = Arrays.asList("file");
			}

			if (appenderNames != null && appenderNames.size() > 0) {
				for (String appenderName : appenderNames) {
					Appender<ILoggingEvent> appender = createAppender(appenderName, resolver, loggerContext);
					if (appender != null) {
						rootLogger.addAppender(appender);
						appenders.put(appender.getName(), appender);
					}
				}
			}

			if (appenders.size() < 1) {
				Appender<ILoggingEvent> console = createAppender("console", resolver, loggerContext);
				rootLogger.addAppender(console);
				appenders.put(console.getName(), console);
			}

			rootLogger.setLevel(Level.toLevel(resolver.getProperty(LOGGER_PROPERTY)));
			for (String key : PropertiesUtils.getKeys(properties, LOGGER_PROPERTY_PREFIX)) {
				setLogger(resolver, key, loggerContext);
			}
		}catch(Throwable t){
			t.printStackTrace();
		}
	}

	private Appender<ILoggingEvent> createAppender(name, PropertyResolver resolver, LoggerContext loggerContext) {
		Appender<ILoggingEvent> appender = null;
		if (name.equals("console")) {
			ConsoleAppender consoleAppender = new ConsoleAppender();
			consoleAppender.setEncoder(createPatternEncoder(resolver, loggerContext, null));

			appender = consoleAppender;
		} else if (name.startsWith("file") && resolver.containsProperty("log." + name + ".path")) {
			appender = createFileAppender(name, resolver, loggerContext);
		}

		if (appender == null) {
			return null;
		}

		appender.setName(name);
		appender.setContext(loggerContext);
		appender.start();
		return appender;
	}

	private PatternLayoutEncoder createPatternEncoder(PropertyResolver resolver, LoggerContext loggerContext, String patternString) {
		if (patternString == null) {
			patternString = getDefaultPattern(resolver);
		}

		PatternLayoutEncoder pattern = new PatternLayoutEncoder();
		pattern.setContext(loggerContext);
		pattern.setPattern(patternString);
		pattern.setCharset(Charset.forName("UTF-8"));
		pattern.start();
		return pattern;
	}

	private String getDefaultPattern(PropertyResolver resolver) {
		if (!resolver.containsProperty("log.pattern")) {
			return "[" + resolver.getProperty("application.name") + "] %d{yyyy-MM-dd HH:mm:ss.SSS} %-5level [%thread] %logger{36} - %msg%n";
		}
		return resolver.getProperty("log.pattern");
	}

	private RollingFileAppender<ILoggingEvent> createFileAppender(String name, PropertyResolver resolver, LoggerContext loggerContext) {
		RollingFileAppender<ILoggingEvent> fileAppender = new RollingFileAppender<ILoggingEvent>();
		String filePath = resolver.getProperty("log." + name + ".path");
		fileAppender.setFile(filePath);
		TimeBasedRollingPolicy timeBasedRollingPolicy = new TimeBasedRollingPolicy();
		if(!resolver.containsProperty("log." + name + ".history.max")){
			timeBasedRollingPolicy.setMaxHistory(31);
		}else{
			timeBasedRollingPolicy.setMaxHistory(Integer.parseInt(resolver.getProperty("log." + name + ".history.max").getSource()));
		}
		timeBasedRollingPolicy.setFileNamePattern(filePath + ".%d{yyyy-MM-dd}.%i.zip");

		String sizeKey = "log." + name + ".size";
		String sizeString = resolver.getProperty(sizeKey);
		sizeString = sizeString != null ? sizeString : resolver.getProperty("log.size");
		SizeAndTimeBasedFNATP sizeAndTimeBasedFNATP = createSizeAndTimeBasedFNATP(loggerContext, timeBasedRollingPolicy, sizeString);

		timeBasedRollingPolicy.setTimeBasedFileNamingAndTriggeringPolicy(sizeAndTimeBasedFNATP);
		timeBasedRollingPolicy.setContext(loggerContext);
		timeBasedRollingPolicy.setParent(fileAppender);
		timeBasedRollingPolicy.start();
		sizeAndTimeBasedFNATP.start();
		fileAppender.setRollingPolicy(timeBasedRollingPolicy);

		String patternKey = "log." + name + ".pattern";
		String patternString = resolver.getProperty(patternKey);
		PatternLayoutEncoder pattern = createPatternEncoder(resolver, loggerContext, patternString);

		fileAppender.setEncoder(pattern);
		return fileAppender;
	}

	private SizeAndTimeBasedFNATP createSizeAndTimeBasedFNATP(LoggerContext loggerContext, TimeBasedRollingPolicy timeBasedRollingPolicy,
			String sizeString) {
		if (sizeString == null) {
			sizeString = "50MB";
		}

		SizeAndTimeBasedFNATP sizeAndTimeBasedFNATP = new SizeAndTimeBasedFNATP();
		sizeAndTimeBasedFNATP.setMaxFileSize(sizeString);
		sizeAndTimeBasedFNATP.setContext(loggerContext);
		sizeAndTimeBasedFNATP.setTimeBasedRollingPolicy(timeBasedRollingPolicy);
		return sizeAndTimeBasedFNATP;
	}

	private void setLogger(PropertyResolver resolver, String key, LoggerContext loggerContext) {
		String loggerName = key.substring(LOGGER_PROPERTY_PREFIX.length());
		if (resolver.containsProperty(key)) {
			loggerContext.getLogger(loggerName).setLevel(Level.toLevel(resolver.getProperty(key)));
		} else {
			// TODO: set null
			loggerContext.getLogger(loggerName).setLevel(null);
		}
	}
}
