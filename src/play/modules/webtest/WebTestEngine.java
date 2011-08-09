package play.modules.webtest;

import org.junit.runner.Description;
import org.junit.runner.JUnitCore;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunListener;
import play.Logger;
import play.Play;

import java.lang.reflect.Modifier;
import java.nio.channels.NonReadableChannelException;
import java.util.*;

public class WebTestEngine {

    public static List<Class> allWebTests() {
        List<Class> classes = Play.classloader.getAssignableClasses(WebTest.class);
        for (ListIterator<Class> it = classes.listIterator(); it.hasNext();) {
            if (Modifier.isAbstract(it.next().getModifiers())) {
                it.remove();
            }
        }
        return classes;
    }

    public static TestResults run(final String name) {
        final TestResults testResults = new TestResults();

        try {
            // Load test class
            final Class testClass = Play.classloader.loadClass(name);

            JUnitCore junit = new JUnitCore();
            junit.addListener(new Listener(testClass.getName(), testResults));

            Class classToRun = Play.classloader.loadApplicationClass(testClass.getName());
            junit.run(classToRun);

        } catch (ClassNotFoundException e) {
            Logger.error(e, "Test not found %s", name);
        }

        return testResults;
    }

    static class Listener extends RunListener {

        TestResults results;
        TestResult current;
        String className;

        public Listener(String className, TestResults results) {
            this.results = results;
            this.className = className;
        }

        @Override
        public void testStarted(final Description description) throws Exception {
            Logger.info("Started test: " + description);
            current = new TestResult();
            current.name = description.getDisplayName().substring(0, description.getDisplayName().indexOf("("));
            current.time = System.currentTimeMillis();
            current.running = true;
        }

        @Override
        public void testFailure(Failure failure) throws Exception {
            Logger.info("Test failed: " + failure.getMessage() + " - " + failure.getException());
            if (failure.getException() instanceof AssertionError) {
                current.error = "Failure, " + failure.getMessage();
            } else {
                current.error = "A " + failure.getException().getClass().getName() + " has been caught, " + failure.getMessage();
                current.trace = failure.getTrace();
            }
            try {
                for (StackTraceElement stackTraceElement : failure.getException().getStackTrace()) {
                    if (stackTraceElement.getClassName().equals(className)) {
                        current.sourceInfos = "In " + Play.classes.getApplicationClass(className).javaFile.relativePath() + ", line " + stackTraceElement.getLineNumber();
                        current.sourceCode = Play.classes.getApplicationClass(className).javaSource.split("\n")[stackTraceElement.getLineNumber() - 1];
                        current.sourceFile = Play.classes.getApplicationClass(className).javaFile.relativePath();
                        current.sourceLine = stackTraceElement.getLineNumber();
                    }
                }
            } catch (Throwable t) {
                t.printStackTrace();
            }
            current.passed = false;
            results.passed = false;
        }

        @Override
        public void testFinished(Description description) throws Exception {
            Logger.info("Test finished: " + description);
            current.running = false;
            current.time = System.currentTimeMillis() - current.time;
            results.add(current);
        }
    }

    public static class TestResults {

        public List<TestResult> results = new ArrayList<TestResult>();
        public boolean passed = true;
        public int success = 0;
        public int errors = 0;
        public int failures = 0;
        public long time = 0;

        public void add(TestResult result) {
            time = result.time + time;
            this.results.add(result);
            if (result.passed) {
              success++;
            } else {
              if (result.error.startsWith("Failure")) {
                failures++;
              } else {
                errors++;
              }
            }
        }
    }

    public static class TestResult {

        public String name;
        public String error;
        public boolean passed = true;
        public boolean running = false;
        public long time;
        public String trace;
        public String sourceInfos;
        public String sourceCode;
        public String sourceFile;
        public int sourceLine;

    }
}
