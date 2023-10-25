package org.luaj.vm2.compiler;


import org.junit.jupiter.api.Test;

public class CompilerUnitTests extends AbstractUnitTests {

    public CompilerUnitTests() {
        super("src/test/lua", "luaj3.0-tests.zip", "lua5.2.1-tests");
    }

    @Test
    public void testAll() {doTest("all.lua");}

    @Test
    public void testApi() {doTest("api.lua");}

    @Test
    public void testAttrib() {doTest("attrib.lua");}

    @Test
    public void testBig() {doTest("big.lua");}

    @Test
    public void testBitwise() {doTest("bitwise.lua");}

    @Test
    public void testCalls() {doTest("calls.lua");}

    @Test
    public void testChecktable() {doTest("checktable.lua");}

    @Test
    public void testClosure() {doTest("closure.lua");}

    @Test
    public void testCode() {doTest("code.lua");}

    @Test
    public void testConstruct() {doTest("constructs.lua");}

    @Test
    public void testCoroutine() {doTest("coroutine.lua");}

    @Test
    public void testDb() {doTest("db.lua");}

    @Test
    public void testErrors() {doTest("errors.lua");}

    @Test
    public void testEvents() {doTest("events.lua");}

    @Test
    public void testFiles() {doTest("files.lua");}

    @Test
    public void testGc() {doTest("gc.lua");}

    @Test
    public void testGoto() {doTest("goto.lua");}

    @Test
    public void testLiterals() {doTest("literals.lua");}

    @Test
    public void testLocals() {doTest("locals.lua");}

    @Test
    public void testMain() {doTest("main.lua");}

    @Test
    public void testMath() {doTest("math.lua");}

    @Test
    public void testNextvar() {doTest("nextvar.lua");}

    @Test
    public void testPm() {doTest("pm.lua");}

    @Test
    public void testSort() {doTest("sort.lua");}

    @Test
    public void testStrings() {doTest("strings.lua");}

    @Test
    public void testVararg() {doTest("vararg.lua");}

    @Test
    public void testVerybig() {doTest("verybig.lua");}
}
