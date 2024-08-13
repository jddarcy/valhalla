/*
 * Copyright (c) 2024, Arm Limited. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */
package compiler.c2.irTests;

import compiler.lib.ir_framework.*;
import static java.lang.Float16.*;
import java.util.Random;
import jdk.test.lib.Asserts;

/*
 * @test
 * @bug 8336406
 * @summary Test that Ideal transformations of MulHFNode are being performed as expected.
 * @library /test/lib /
 * @requires vm.compiler2.enabled
 * @run driver compiler.c2.irTests.MulHFNodeIdealizationTests
 */
public class MulHFNodeIdealizationTests {

    private Float16 src;
    private Float16 dst;
    private Random rng;

    public static void main(String[] args) {
        TestFramework.runWithFlags("--enable-preview");
    }

    public MulHFNodeIdealizationTests() {
        rng = new Random(25);
        src = Float16.valueOf(rng.nextFloat());
        dst = Float16.valueOf(rng.nextFloat());
    }

    @Test
    @IR(counts = {IRNode.ADD_HF, "1"},
        applyIfCPUFeatureAnd = {"fphp", "true", "asimdhp", "true"},
        failOn = {IRNode.MUL_HF})
    public void test1() {
        dst = Float16.multiply(src, Float16.valueOf(2.0f));
    }

    @Check(test="test1")
    public void checkTest1() {
        Float16 expected = Float16.valueOf(src.floatValue() * 2.0f);
        if (float16ToRawShortBits(expected) != float16ToRawShortBits(dst)) {
            throw new RuntimeException("Invalid result: dst = " + float16ToRawShortBits(dst) + " != " + float16ToRawShortBits(expected));
        }
    }
}
