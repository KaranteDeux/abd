package abd.reldb.ra;

import static org.junit.Assert.*;

import org.junit.Test;

import abd.reldb.TestUtil;

public class TestRAOperations {

	@Test
	public void testConstructSelect1() {
		TableOperation t = new TableOperation(TestUtil.descr_char2_char2);
		SelectionCriterion scrit = new SelectionCriterion_EqualityToConstant(0, new byte[]{'a','a'});
		RAOperation select = new SelectionOperation(t, scrit);
		System.out.println(select);
	}

	@Test
	public void testConstructSelect2() {
		TableOperation t = new TableOperation(TestUtil.descr_char2_char2);
		SelectionCriterion scrit = new SelectionCriterion_EqualityTwoAttributes(0, 1);
		RAOperation select = new SelectionOperation(t, scrit);
		System.out.println(select);
	}

	@Test
	public void testConstructProject1() {
		TableOperation t = new TableOperation(TestUtil.descr_char2_char2);
		RAOperation proj = new ProjectionOperation(t, 0);
		System.out.println(proj);
	}
	
	@Test
	public void testConstructProject2() {
		TableOperation t = new TableOperation(TestUtil.descr_char2_char2);
		RAOperation proj = new ProjectionOperation(t, 0,1);
		System.out.println(proj);
	}
	
	@Test
	public void testJoin1() {
		TableOperation left = new TableOperation(TestUtil.descr_char10_char230);
		TableOperation right = new TableOperation(TestUtil.descr_varchar3_varchar3);
		JoinCriterion jcrit = new JoinCriterion(new int[]{0,1});
		RAOperation join = new JoinOperation(left, right, jcrit);
		System.out.println(join);
	}
	
	@Test
	public void testJoin2() {
		TableOperation left = new TableOperation(TestUtil.descr_char10_char230);
		TableOperation right = new TableOperation(TestUtil.descr_varchar3_varchar3);
		JoinCriterion jcrit = new JoinCriterion();
		RAOperation join = new JoinOperation(left, right, jcrit);
		System.out.println(join);
	}
	
	@Test
	public void testJoin3() {
		TableOperation left = new TableOperation(TestUtil.descr_char10_char230);
		TableOperation right = new TableOperation(TestUtil.descr_varchar3_varchar3);
		JoinCriterion jcrit = new JoinCriterion(new int[]{0,1}, new int[]{1,1});
		RAOperation join = new JoinOperation(left, right, jcrit);
		System.out.println(join);
	}
	
	@Test
	public void testSPJ() {
		TableOperation t1 = new TableOperation(TestUtil.descr_char10_char230);
		TableOperation right = new TableOperation(TestUtil.descr_varchar3_varchar3);
		SelectionCriterion scrit = new SelectionCriterion_EqualityToConstant(0, new byte[]{'a','a'});
		RAOperation left = new SelectionOperation(t1, scrit);
		JoinCriterion jcrit = new JoinCriterion(new int[]{0,1}, new int[]{1,1});
		RAOperation join = new JoinOperation(left, right, jcrit);
		RAOperation proj = new ProjectionOperation(join, 0,1);
		System.out.println(proj);
	}
}
