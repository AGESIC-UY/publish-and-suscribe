package uy.gub.agesic.pdi.pys.push.send;

import org.apache.log4j.Logger;

import java.security.AccessController;
import java.security.PrivilegedAction;

class SecurityActions {

    private static final Logger log = Logger.getLogger(SecurityActions.class);

    private SecurityActions() {
    }

    static Class<?> loadClass(Class<?> theClass, final String fullQualifiedName) {
        SecurityManager sm = System.getSecurityManager();
        if (sm != null) {
            AccessController.doPrivileged(new PrivilegedAction() {
                public Class<?> run() {
                    ClassLoader classLoader = theClass.getClassLoader();

                    Class<?> clazz = SecurityActions.loadClass(classLoader, fullQualifiedName);
                    if (clazz == null) {
                        classLoader = Thread.currentThread().getContextClassLoader();
                        clazz = SecurityActions.loadClass(classLoader, fullQualifiedName);
                    }
                    return clazz;
                }
            });
        }
        ClassLoader classLoader = theClass.getClassLoader();

        Class<?> clazz = loadClass(classLoader, fullQualifiedName);
        if (clazz == null) {
            classLoader = Thread.currentThread().getContextClassLoader();
            clazz = loadClass(classLoader, fullQualifiedName);
        }
        return clazz;
    }

    static Class<?> loadClass(ClassLoader classLoader, final String fullQualifiedName) {
        SecurityManager sm = System.getSecurityManager();
        if (sm != null) {
            AccessController.doPrivileged(new PrivilegedAction() {
                public Class<?> run() {
                    try {
                        return classLoader.loadClass(fullQualifiedName);
                    } catch (ClassNotFoundException e) {
                        log.warn(e.getMessage(), e);
                    }
                    return null;
                }
            });
        }
        try {
            return classLoader.loadClass(fullQualifiedName);
        } catch (ClassNotFoundException e) {
            log.warn(e.getMessage(), e);
        }
        return null;
    }

}
