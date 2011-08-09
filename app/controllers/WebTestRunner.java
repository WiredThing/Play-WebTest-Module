package controllers;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.*;

import play.Logger;
import play.Play;
import play.libs.IO;
import play.modules.webtest.WebTestEngine;
import play.mvc.*;
import play.templates.Template;
import play.templates.TemplateLoader;
import play.vfs.VirtualFile;

public class WebTestRunner extends Controller {

    public static void index() {
        List<Class> webTests = WebTestEngine.allWebTests();
        render(webTests);
    }

    public static void list() {
        StringWriter list = new StringWriter();
        PrintWriter p = new PrintWriter(list);
        p.println("---");
        p.println(Play.getFile("test-result").getAbsolutePath());
        for(Class c : WebTestEngine.allWebTests()) {
            p.println(c.getName() + ".class");
        }
        renderText(list);
    }

    public static void run(String test) throws Exception {
        if (test.equals("init")) {
            File testResults = Play.getFile("test-result");
            if (!testResults.exists()) {
                testResults.mkdir();
            }
            for(File tr : testResults.listFiles()) {
                if ((tr.getName().endsWith(".html") || tr.getName().startsWith("result.")) && !tr.delete()) {
                    Logger.warn("Cannot delete %s ...", tr.getAbsolutePath());
                }
            }
            renderText("done");
        }
        if (test.equals("end")) {
            File testResults = Play.getFile("test-result/result." + params.get("result"));
            IO.writeContent(params.get("result"), testResults);
            renderText("done");
        }
        if (test.endsWith(".class")) {
            Play.getFile("test-result").mkdir();
            java.lang.Thread.sleep(250);

            // TODO: Spin the test off on a thread and return some default response

            WebTestEngine.TestResults results = WebTestEngine.run(test.substring(0, test.length() - 6));
            response.status = results.passed ? 200 : 500;
            Template resultTemplate = TemplateLoader.load("webTestRunner/results.html");
            Map<String, Object> options = new HashMap<String, Object>();
            options.put("test", test);
            options.put("results", results);
            String result = resultTemplate.render(options);
            File testResults = Play.getFile("test-result/" + test + (results.passed ? ".passed" : ".failed") + ".html");
            IO.writeContent(result, testResults);
            try {
                // Write xml output
                options.remove("out");
                resultTemplate = TemplateLoader.load("webTestRunner/results-xunit.xml");
                String resultXunit = resultTemplate.render(options);
                File testXunitResults = Play.getFile("test-result/TEST-" + test.substring(0, test.length()-6) + ".xml");
                IO.writeContent(resultXunit, testXunitResults);
            } catch(Exception e) {
                Logger.error(e, "Cannot ouput XML unit output");
            }            
            response.contentType = "text/html";
            renderText(result);
        }
        // TODO: Add a new option here to poll for test finished and pull result off disk or memory
        if (test.endsWith(".test.html")) {
            File testFile = Play.getFile("test/" + test);
            if (!testFile.exists()) {
                for(VirtualFile root : Play.roots) {
                    File moduleTestFile = Play.getFile(root.relativePath()+"/test/" + test);
                    if(moduleTestFile.exists()) {
                        testFile = moduleTestFile;
                    }
                }
            }
            if (testFile.exists()) {
                Template testTemplate = TemplateLoader.load(VirtualFile.open(testFile));
                Map<String, Object> options = new HashMap<String, Object>();
                response.contentType = "text/html";
                renderText(testTemplate.render(options));
            } else {
                renderText("Test not found, %s", testFile);
            }
        }
        if (test.endsWith(".test.html.result")) {
            flash.keep();
            test = test.substring(0, test.length() - 7);
            File testResults = Play.getFile("test-result/" + test.replace("/", ".") + ".passed.html");
            if (testResults.exists()) {
                response.contentType = "text/html";
                response.status = 200;
                renderText(IO.readContentAsString(testResults));
            }
            testResults = Play.getFile("test-result/" + test.replace("/", ".") + ".failed.html");
            if (testResults.exists()) {
                response.contentType = "text/html";
                response.status = 500;
                renderText(IO.readContentAsString(testResults));
            }
            response.status = 404;
            renderText("No test result");
        }
    }
}
