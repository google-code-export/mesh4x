package org.mesh4j.sync.model;

import j2meunit.framework.Test;
import j2meunit.framework.TestCase;
import j2meunit.framework.TestMethod;
import j2meunit.framework.TestSuite;

import java.util.Date;

import org.mesh4j.sync.test.utils.TestHelper;
import org.mesh4j.sync.utils.DateHelper;

public class HistoryTests extends TestCase{

	public HistoryTests(String name, TestMethod rTestMethod) {
		super(name, rTestMethod);
	}
	
	public HistoryTests(String name) {
		super(name);
	}
	
	public HistoryTests() {
		super();
	}
	
	public Test suite() {
		TestSuite suite = new TestSuite();
		suite.addTest((new HistoryTests("ShouldThrowIfSequenceNotGreaterThanZero", new TestMethod(){public void run(TestCase tc){((HistoryTests)tc).ShouldThrowIfSequenceNotGreaterThanZero();}})));
		suite.addTest((new HistoryTests("ShouldBeValidNullBy", new TestMethod(){public void run(TestCase tc){((HistoryTests)tc).ShouldBeValidNullBy();}})));
		suite.addTest((new HistoryTests("ShouldBeValidNullWhen", new TestMethod(){public void run(TestCase tc){((HistoryTests)tc).ShouldBeValidNullWhen();}})));
		suite.addTest((new HistoryTests("ShouldThrowIfBothByAndWhenNull", new TestMethod(){public void run(TestCase tc){((HistoryTests)tc).ShouldThrowIfBothByAndWhenNull();}})));
		suite.addTest((new HistoryTests("ShouldSubsumeWithByEqualSequence", new TestMethod(){public void run(TestCase tc){((HistoryTests)tc).ShouldSubsumeWithByEqualSequence();}})));
		suite.addTest((new HistoryTests("ShouldSubsumeWithByGreaterSequence", new TestMethod(){public void run(TestCase tc){((HistoryTests)tc).ShouldSubsumeWithByGreaterSequence();}})));
		suite.addTest((new HistoryTests("ShouldSubsumeWithoutByEqualWhenAndSequence", new TestMethod(){public void run(TestCase tc){((HistoryTests)tc).ShouldSubsumeWithoutByEqualWhenAndSequence();}})));
		suite.addTest((new HistoryTests("ShouldNotSubsumeWithoutByOnXAndByOnYWithEqualWhenAndSequence", new TestMethod(){public void run(TestCase tc){((HistoryTests)tc).ShouldNotSubsumeWithoutByOnXAndByOnYWithEqualWhenAndSequence();}})));
		suite.addTest((new HistoryTests("ShouldNotSubsumeWithoutByAndDifferentWhen", new TestMethod(){public void run(TestCase tc){((HistoryTests)tc).ShouldNotSubsumeWithoutByAndDifferentWhen();}})));
		suite.addTest((new HistoryTests("ShouldNotEqualNull", new TestMethod(){public void run(TestCase tc){((HistoryTests)tc).ShouldNotEqualNull();}})));
		suite.addTest((new HistoryTests("ShouldEqualWithSameBy", new TestMethod(){public void run(TestCase tc){((HistoryTests)tc).ShouldEqualWithSameBy();}})));
		suite.addTest((new HistoryTests("ShouldNotEqualWithDifferentBy", new TestMethod(){public void run(TestCase tc){((HistoryTests)tc).ShouldNotEqualWithDifferentBy();}})));
		suite.addTest((new HistoryTests("ShouldEqualWithSameWhen", new TestMethod(){public void run(TestCase tc){((HistoryTests)tc).ShouldEqualWithSameWhen();}})));
		suite.addTest((new HistoryTests("ShouldNotEqualWithDifferentWhen", new TestMethod(){public void run(TestCase tc){((HistoryTests)tc).ShouldNotEqualWithDifferentWhen();}})));
		suite.addTest((new HistoryTests("ShouldNotEqualWithSameNowButNullBy", new TestMethod(){public void run(TestCase tc){((HistoryTests)tc).ShouldNotEqualWithSameNowButNullBy();}})));
		suite.addTest((new HistoryTests("ShouldEqualWithSameSequenceAndBy", new TestMethod(){public void run(TestCase tc){((HistoryTests)tc).ShouldEqualWithSameSequenceAndBy();}})));
		suite.addTest((new HistoryTests("ShouldEqualWithSameSequenceAndWhen", new TestMethod(){public void run(TestCase tc){((HistoryTests)tc).ShouldEqualWithSameSequenceAndWhen();}})));
		suite.addTest((new HistoryTests("ShouldHaveSameHashcodeWithSameBy", new TestMethod(){public void run(TestCase tc){((HistoryTests)tc).ShouldHaveSameHashcodeWithSameBy();}})));
		suite.addTest((new HistoryTests("ShouldHaveSameHashcodeWithSameWhen", new TestMethod(){public void run(TestCase tc){((HistoryTests)tc).ShouldHaveSameHashcodeWithSameWhen();}})));
		suite.addTest((new HistoryTests("ShouldHaveSameHashcodeWithSameByWhenSequence", new TestMethod(){public void run(TestCase tc){((HistoryTests)tc).ShouldHaveSameHashcodeWithSameByWhenSequence();}})));
		suite.addTest((new HistoryTests("ShouldHaveDifferentHashcodeWithNullBy", new TestMethod(){public void run(TestCase tc){((HistoryTests)tc).ShouldHaveDifferentHashcodeWithNullBy();}})));
		suite.addTest((new HistoryTests("ShouldDefaultNullWhenAndSequenceOne", new TestMethod(){public void run(TestCase tc){((HistoryTests)tc).ShouldDefaultNullWhenAndSequenceOne();}})));
		suite.addTest((new HistoryTests("ShouldDefaultNullByAndSequenceOne", new TestMethod(){public void run(TestCase tc){((HistoryTests)tc).ShouldDefaultNullByAndSequenceOne();}})));
		suite.addTest((new HistoryTests("ShouldEqualClone", new TestMethod(){public void run(TestCase tc){((HistoryTests)tc).ShouldEqualClone();}})));
		suite.addTest((new HistoryTests("ShouldEqualCloneable", new TestMethod(){public void run(TestCase tc){((HistoryTests)tc).ShouldEqualCloneable();}})));
		suite.addTest((new HistoryTests("ShouldNormalizeWhenWithoutMilliseconds", new TestMethod(){public void run(TestCase tc){((HistoryTests)tc).ShouldNormalizeWhenWithoutMilliseconds();}})));
		return suite;
	}
	
	public void ShouldThrowIfSequenceNotGreaterThanZero()
	{
		try{
			new History("foo", TestHelper.now(), 0);
			fail("expected IllegalArgumentException");
		} catch(IllegalArgumentException e){
			// right test
		}
	}

	public void ShouldBeValidNullBy()
	{
		History h = new History(null, TestHelper.now(), 1);
		assertNull(h.getBy());
	}

	public void ShouldBeValidNullWhen()
	{
		History h = new History("foo", null, 1);
		assertNull(h.getWhen());
	}

	public void ShouldThrowIfBothByAndWhenNull()
	{
		try{
			new History(null, null, 1);
			fail("expected IllegalArgumentException");
		} catch(IllegalArgumentException e){
			// right test
		}
	}

	public void ShouldSubsumeWithByEqualSequence()
	{
		History Hx = new History("kzu", null, 2);
		History Hy = new History("kzu", null, 2);

		assertTrue(Hx.IsSubsumedBy(Hy));
	}

	public void ShouldSubsumeWithByGreaterSequence()
	{
		History Hx = new History("kzu", null, 2);
		History Hy = new History("kzu", null, 3);

		assertTrue(Hx.IsSubsumedBy(Hy));
	}

	public void ShouldSubsumeWithoutByEqualWhenAndSequence()
	{
		Date when = TestHelper.now();
		History Hx = new History(null, when, 2);
		History Hy = new History(null, when, 2);

		assertTrue(Hx.IsSubsumedBy(Hy));
	}

	public void ShouldNotSubsumeWithoutByOnXAndByOnYWithEqualWhenAndSequence()
	{
		Date when = TestHelper.now();

		History Hx = new History(null, when, 2);
		History Hy = new History("kzu", when, 2);

		assertEquals(false, Hx.IsSubsumedBy(Hy));
	}

	public void ShouldNotSubsumeWithoutByAndDifferentWhen()
	{
		History Hx = new History(null, TestHelper.now(), 2);
		History Hy = new History(null, TestHelper.nowAddSeconds(10), 2);

		assertEquals(false, Hx.IsSubsumedBy(Hy));
	}

	public void ShouldNotEqualNull()
	{
		History h1 = new History("foo");
		History h2 = null;

		this.assertNotEqualsHistory(h1, h2);
	}

	public void ShouldEqualWithSameBy()
	{
		History h1 = new History("foo");
		History h2 = new History("foo");

		this.assertEqualsHistory(h1, h2);
	}

	public void ShouldNotEqualWithDifferentBy()
	{
		History h1 = new History("foo");
		History h2 = new History("bar");

		this.assertNotEqualsHistory(h1, h2);
	}

	public void ShouldEqualWithSameWhen()
	{
		Date now = TestHelper.now();
		History h1 = new History(now);
		History h2 = new History(now);

		this.assertEqualsHistory(h1, h2);
	}

	public void ShouldNotEqualWithDifferentWhen()
	{
		History h1 = new History(TestHelper.now());
		History h2 = new History(TestHelper.nowAddSeconds(50));

		this.assertNotEqualsHistory(h1, h2);
	}

	public void ShouldNotEqualWithSameNowButNullBy()
	{
		Date now = TestHelper.now();
		History h1 = new History("kzu", now);
		History h2 = new History(null, now);

		assertNotEqualsHistory(h1, h2);
	}

	public void ShouldEqualWithSameSequenceAndBy()
	{
		History h1 = new History("kzu", null, 5);
		History h2 = new History("kzu", null, 5);

		assertEqualsHistory(h1, h2);
	}

	public void ShouldEqualWithSameSequenceAndWhen()
	{
		Date now = TestHelper.now();
		History h1 = new History(null, now, 5);
		History h2 = new History(null, now, 5);

		assertEqualsHistory(h1, h2);
	}

	public void ShouldHaveSameHashcodeWithSameBy()
	{
		History h1 = new History("kzu");
		History h2 = new History("kzu");

		assertEquals(h1.hashCode(), h2.hashCode());
	}

	public void ShouldHaveSameHashcodeWithSameWhen()
	{
		Date now = TestHelper.now();
		History h1 = new History(now);
		History h2 = new History(now);

		assertEquals(h1.hashCode(), h2.hashCode());
	}

	public void ShouldHaveSameHashcodeWithSameByWhenSequence()
	{
		Date now = TestHelper.now();
		History h1 = new History("kzu", now, 5);
		History h2 = new History("kzu", now, 5);

		assertEquals(h1.hashCode(), h2.hashCode());
	}

	public void ShouldHaveDifferentHashcodeWithNullBy()
	{
		Date now = TestHelper.now();
		History h1 = new History("kzu", now);
		History h2 = new History(null, now);

		assertTrue(h1.hashCode() != h2.hashCode());
	}

	public void ShouldDefaultNullWhenAndSequenceOne()
	{
		History h = new History("Foo");
		assertEquals("Foo", h.getBy());
		assertEquals(null, h.getWhen());
		assertEquals(1, h.getSequence());
	}

	public void ShouldDefaultNullByAndSequenceOne()
	{
		Date now = DateHelper.normalize(TestHelper.now());
		History h = new History(now);
		assertEquals(now, h.getWhen());
		assertEquals(null, h.getBy());
		assertEquals(1, h.getSequence());
	}

	public void ShouldEqualClone()
	{
		History h1 = new History("foo", TestHelper.now(), 4);
		History h2 = h1.clone();

		assertEqualsHistory(h1, h2);
	}

	public void ShouldEqualCloneable()
	{
		History h1 = new History("foo", TestHelper.now(), 4);
		History h2 = h1.clone();

		assertEqualsHistory(h1, h2);
	}

	public void ShouldNormalizeWhenWithoutMilliseconds()
	{
		Date when = TestHelper.makeDate(2007, 6, 6, 6, 6, 6, 100);
		Date expected = TestHelper.makeDate(2007, 6, 6, 6, 6, 6, 0);

		History history = new History(when);

		assertTrue(when.getTime() != expected.getTime());
		assertTrue(expected.getTime() == history.getWhen().getTime());			
	}

	private void assertEqualsHistory(History h1, History h2)
	{
		assertEquals(h1, h2);
		assertTrue(h1.equals(h2));
	}

	private void assertNotEqualsHistory(History h1, History h2)
	{
		assertEquals(false,h1.equals(h2));
		assertTrue(h1 != h2);
	}
}
