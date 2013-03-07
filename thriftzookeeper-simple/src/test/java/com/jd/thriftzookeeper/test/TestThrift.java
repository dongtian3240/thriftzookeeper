package com.jd.thriftzookeeper.test;


import org.apache.thrift.maven.Thrift;
import org.codehaus.plexus.util.FileUtils;
import org.codehaus.plexus.util.cli.CommandLineException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class TestThrift {

    private String resourceDir = "../thriftzookeeper/thriftzookeeper-simple/src/main/resources";
    private String toPath = "../thriftzookeeper/thriftzookeeper-simple/src/test";
    private File testRootDir;
    private File idlDir;
    private File genJavaDir;
    private Thrift.Builder builder;

    @Before
    public void setup() throws Exception {
        final File tmpDir = new File(toPath);
        testRootDir = new File(tmpDir, "thrift-test");

        if (testRootDir.exists()) {
            FileUtils.cleanDirectory(testRootDir);
        } else {
            testRootDir.mkdirs();
//            assertTrue("Failed to create output directory for test: " + testRootDir.getPath(), testRootDir.mkdir());
        }

        File testResourceDir = new File(resourceDir);
        assertTrue("Unable to find test resources", testRootDir.exists());

        idlDir = new File(testResourceDir, "idl");
        genJavaDir = new File(testRootDir, Thrift.GENERATED_JAVA);
        builder = new Thrift.Builder("thrift", testRootDir);
        builder.setGenerator("java").addThriftPathElement(idlDir);
    }

    @Test
    public void testThriftMultipleFileCompile() throws Exception {
        final File sharedThrift = new File(idlDir, "shared.thrift");
        final File tutorialThrift = new File(idlDir, "tutorial.thrift");

        builder.addThriftFile(sharedThrift);
        builder.addThriftFile(tutorialThrift);

        final Thrift thrift = builder.build();

        assertTrue("File not found: shared.thrift", sharedThrift.exists());
        assertFalse("gen-java directory should not exist", genJavaDir.exists());

        // execute the compile
        final int result = thrift.compile();
        assertEquals(0, result);

        assertFalse("gen-java directory was not removed", genJavaDir.exists());
        assertTrue("generated java code doesn't exist",
            new File(testRootDir, "shared/SharedService.java").exists());
        assertTrue("generated java code doesn't exist",
            new File(testRootDir, "tutorial/InvalidOperation.java").exists());
    }

//    @After
    public void cleanup() throws Exception {
        if (testRootDir.exists()) {
            FileUtils.cleanDirectory(testRootDir);
            assertTrue("Failed to delete output directory for test: " + testRootDir.getPath(), testRootDir.delete());
        }
    }
}
