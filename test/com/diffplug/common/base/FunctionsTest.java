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

import java.io.Serializable;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

import junit.framework.TestCase;

import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.testing.EqualsTester;
import com.google.common.testing.SerializableTester;

import com.diffplug.common.base.Functions;
import com.diffplug.common.base.Predicates;

/**
 * Ripped verbatim from Google Guava 18.0, then truncated to remove serialization tests.
 * 
 * Tests for {@link Functions}.
 */
@GwtCompatible(emulated = true)
public class FunctionsTest extends TestCase {

	public void testIdentity_same() {
		Function<String, String> identity = Functions.identity();
		assertNull(identity.apply(null));
		assertSame("foo", identity.apply("foo"));
	}

	public void testIdentity_notSame() {
		Function<Long, Long> identity = Functions.identity();
		assertNotSame(new Long(135135L), identity.apply(new Long(135135L)));
	}

	public void testForMapWithoutDefault() {
		Map<String, Integer> map = Maps.newHashMap();
		map.put("One", 1);
		map.put("Three", 3);
		map.put("Null", null);
		Function<String, Integer> function = Functions.forMap(map);

		assertEquals(1, function.apply("One").intValue());
		assertEquals(3, function.apply("Three").intValue());
		assertNull(function.apply("Null"));

		try {
			function.apply("Two");
			fail();
		} catch (IllegalArgumentException expected) {}
	}

	public void testForMapWithDefault() {
		Map<String, Integer> map = Maps.newHashMap();
		map.put("One", 1);
		map.put("Three", 3);
		map.put("Null", null);
		Function<String, Integer> function = Functions.forMap(map, 42);

		assertEquals(1, function.apply("One").intValue());
		assertEquals(42, function.apply("Two").intValue());
		assertEquals(3, function.apply("Three").intValue());
		assertNull(function.apply("Null"));
	}

	@GwtIncompatible("SerializableTester")
	public void testForMapWithDefault_includeSerializable() {
		Map<String, Integer> map = Maps.newHashMap();
		map.put("One", 1);
		map.put("Three", 3);
		Function<String, Integer> function = Functions.forMap(map, 42);

		assertEquals(1, function.apply("One").intValue());
		assertEquals(42, function.apply("Two").intValue());
		assertEquals(3, function.apply("Three").intValue());
	}

	public void testForMapWithDefault_null() {
		ImmutableMap<String, Integer> map = ImmutableMap.of("One", 1);
		Function<String, Integer> function = Functions.forMap(map, null);

		assertEquals((Integer) 1, function.apply("One"));
		assertNull(function.apply("Two"));

		// check basic sanity of equals and hashCode
		new EqualsTester()
				.addEqualityGroup(function)
				.addEqualityGroup(Functions.forMap(map, 1))
				.testEquals();
	}

	@GwtIncompatible("SerializableTester")
	public void testForMapWithDefault_null_compareWithSerializable() {
		ImmutableMap<String, Integer> map = ImmutableMap.of("One", 1);
		Function<String, Integer> function = Functions.forMap(map, null);

		assertEquals((Integer) 1, function.apply("One"));
		assertNull(function.apply("Two"));
	}

	public void testForMapWildCardWithDefault() {
		Map<String, Integer> map = Maps.newHashMap();
		map.put("One", 1);
		map.put("Three", 3);
		Number number = Double.valueOf(42);
		Function<String, Number> function = Functions.forMap(map, number);

		assertEquals(1, function.apply("One").intValue());
		assertEquals(number, function.apply("Two"));
		assertEquals(3L, function.apply("Three").longValue());
	}

	public void testComposition() {
		Map<String, Integer> mJapaneseToInteger = Maps.newHashMap();
		mJapaneseToInteger.put("Ichi", 1);
		mJapaneseToInteger.put("Ni", 2);
		mJapaneseToInteger.put("San", 3);
		Function<String, Integer> japaneseToInteger = Functions.forMap(mJapaneseToInteger);

		Map<Integer, String> mIntegerToSpanish = Maps.newHashMap();
		mIntegerToSpanish.put(1, "Uno");
		mIntegerToSpanish.put(3, "Tres");
		mIntegerToSpanish.put(4, "Cuatro");
		Function<Integer, String> integerToSpanish = Functions.forMap(mIntegerToSpanish);

		Function<String, String> japaneseToSpanish = Functions.compose(integerToSpanish, japaneseToInteger);

		assertEquals("Uno", japaneseToSpanish.apply("Ichi"));
		try {
			japaneseToSpanish.apply("Ni");
			fail();
		} catch (IllegalArgumentException e) {}
		assertEquals("Tres", japaneseToSpanish.apply("San"));
		try {
			japaneseToSpanish.apply("Shi");
			fail();
		} catch (IllegalArgumentException e) {}
	}

	private static class HashCodeFunction implements Function<Object, Integer> {
		@Override
		public Integer apply(Object o) {
			return (o == null) ? 0 : o.hashCode();
		}
	}

	public void testComposeOfFunctionsIsAssociative() {
		Map<Float, String> m = ImmutableMap.of(
				4.0f, "A", 3.0f, "B", 2.0f, "C", 1.0f, "D");
		Function<? super Integer, Boolean> h = Functions.constant(Boolean.TRUE);
		Function<? super String, Integer> g = new HashCodeFunction();
		Function<Float, String> f = Functions.forMap(m, "F");

		Function<Float, Boolean> c1 = Functions.compose(Functions.compose(h, g), f);
		Function<Float, Boolean> c2 = Functions.compose(h, Functions.compose(g, f));

		assertEquals(c1.apply(1.0f), c2.apply(1.0f));
		assertEquals(c1.apply(5.0f), c2.apply(5.0f));
	}

	public void testComposeOfPredicateAndFunctionIsAssociative() {
		Map<Float, String> m = ImmutableMap.of(
				4.0f, "A", 3.0f, "B", 2.0f, "C", 1.0f, "D");
		Predicate<? super Integer> h = Predicates.equalTo(42);
		Function<? super String, Integer> g = new HashCodeFunction();
		Function<Float, String> f = Functions.forMap(m, "F");

		Predicate<Float> p1 = Predicates.compose(Predicates.compose(h, g), f);
		Predicate<Float> p2 = Predicates.compose(h, Functions.compose(g, f));

		assertEquals(p1.test(1.0f), p2.test(1.0f));
		assertEquals(p1.test(5.0f), p2.test(5.0f));
	}

	public void testForPredicate() {
		Function<Object, Boolean> alwaysTrue = Functions.forPredicate(Predicates.alwaysTrue());
		Function<Object, Boolean> alwaysFalse = Functions.forPredicate(Predicates.alwaysFalse());

		assertTrue(alwaysTrue.apply(0));
		assertFalse(alwaysFalse.apply(0));
	}

	public void testConstant() {
		Function<Object, Object> f = Functions.<Object> constant("correct");
		assertEquals("correct", f.apply(new Object()));
		assertEquals("correct", f.apply(null));

		Function<Object, String> g = Functions.constant(null);
		assertEquals(null, g.apply(2));
		assertEquals(null, g.apply(null));
	}

	private static class CountingSupplier
			implements Supplier<Integer>, Serializable {

		private static final long serialVersionUID = 0;

		private int value;

		@Override
		public Integer get() {
			return ++value;
		}

		@Override
		public boolean equals(Object obj) {
			if (obj instanceof CountingSupplier) {
				return this.value == ((CountingSupplier) obj).value;
			}
			return false;
		}

		@Override
		public int hashCode() {
			return value;
		}
	}

	public void testForSupplier() {
		Supplier<Integer> supplier = new CountingSupplier();
		Function<Object, Integer> function = Functions.forSupplier(supplier);

		assertEquals(1, (int) function.apply(null));
		assertEquals(2, (int) function.apply("foo"));
	}

	@GwtIncompatible("SerializableTester")
	private static <Y> void checkCanReserialize(Function<? super Integer, Y> f) {
		Function<? super Integer, Y> g = SerializableTester.reserializeAndAssert(f);
		for (int i = 1; i < 5; i++) {
			// convoluted way to check that the same result happens from each
			Y expected = null;
			try {
				expected = f.apply(i);
			} catch (IllegalArgumentException e) {
				try {
					g.apply(i);
					fail();
				} catch (IllegalArgumentException ok) {
					continue;
				}
			}
			assertEquals(expected, g.apply(i));
		}
	}

	@GwtIncompatible("SerializableTester")
	private static <Y> void checkCanReserializeSingleton(Function<? super String, Y> f) {
		Function<? super String, Y> g = SerializableTester.reserializeAndAssert(f);
		assertSame(f, g);
		for (Integer i = 1; i < 5; i++) {
			assertEquals(f.apply(i.toString()), g.apply(i.toString()));
		}
	}

}
