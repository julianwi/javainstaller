package gnu.java.lang.management;

import gnu.classpath.SystemProperties;
import java.io.File;

final class VMRuntimeMXBeanImpl {
	
	static String[] getInputArguments() {
		throw new UnsupportedOperationException("Not yet implemented.");
		//TODO implement this
	}
	
	static String getName() {
		return SystemProperties.getProperty("java.vm.name") + " " + SystemProperties.getProperty("java.vm.version");
	}
	
	//only accurate to a second not to a millisecond
	static long getStartTime() {
		return new File("/proc/self").lastModified();
	}

}