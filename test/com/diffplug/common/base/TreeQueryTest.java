/*
 * Copyright 2015 DiffPlug
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.diffplug.common.base;

import org.junit.Assert;
import org.junit.Test;

public class TreeQueryTest {
	@Test
	public void testLowestCommonAncestor() {
		// test the trivial case
		TreeNode<String> arrayJava = root.findByPath("src", "org", "math", "Array.java");
		TreeNode<String> matrixJava = root.findByPath("src", "org", "math", "Matrix.java");
		lcaTestCase(root, root, root);
		lcaTestCase(arrayJava, arrayJava, arrayJava);
		lcaTestCase(matrixJava, matrixJava, matrixJava);

		// test the colinear case
		lcaTestCase(root, arrayJava, root);
		lcaTestCase(root, matrixJava, root);

		// test the intersection case
		lcaTestCase(matrixJava, arrayJava, root.findByPath("src", "org", "math"));
	}

	private void lcaTestCase(TreeNode<String> a, TreeNode<String> b, TreeNode<String> expected) {
		TreeNode<String> actual = TreeQuery.lowestCommonAncestor(TreeNode.treeDef(), a, b).get();
		Assert.assertEquals(expected, actual);
	}

	@Test
	public void testPath() {
		Assert.assertEquals("root", TreeQuery.path(TreeNode.treeDef(), root, TreeNode::getContent));
		Assert.assertEquals("root/test/org2/avl/allegro.avl", TreeQuery.path(TreeNode.treeDef(), root.findByContent("allegro.avl"), TreeNode::getContent));
	}

	@Test
	public void testToString() {
		// put the testData into its string form
		String[] pieces = root.toStringDeep().split("\n");
		// turn it back into a treeNode
		TreeNode<String> copiedFromString = TreeNode.createTestData(pieces);
		// make sure the strings are identical
		Assert.assertEquals(root.toStringDeep(), copiedFromString.toStringDeep());
		// and the same for the underlying tree
		TreeComparison.of(root, copiedFromString).assertEqual();
	}

	// @formatter:off
	private TreeNode<String> root = TreeNode.createTestData(
			"root",
			" src",
			"  org",
			"   math",
			"    Array.java",
			"    Matrix.java",
			"    QuatRot.java",
			"    Vector.java",
			"   model",
			"    generic",
			"     Constant.java",
			"     Constant.xml",
			"    geometric",
			"     Constant2.java",
			"     Constant2.xml",
			"    Component.java",
			"    DynamicComponent.java",
			"    Folder afterwards",
			"     PerturbDerivative1.java",
			"      PerturbDerivative2.java",
			"      PerturbDerivative3.java",
			"     PerturbDerivative4.java",
			"      PerturbDerivative5.java",
			"      PerturbDerivative6.java",
			" test",
			"  org2",
			"   avl",
			"    allegro.avl",
			"    allegro.mass",
			"    b737.avl",
			"   simulink",
			"    complex.mdl",
			"    long_simple.mdl",
			"    sf_tetris2.mdl",
			" RunAllTests.java"
			);
	// @formatter:on
}
