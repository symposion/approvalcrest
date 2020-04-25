package com.github.karsaig.approvalcrest;

import org.junit.Test;

import com.github.karsaig.approvalcrest.matcher.Matchers;
import com.github.karsaig.approvalcrest.testdata.DummyBean;

/**
 * Sample test for using {@link com.github.karsaig.approvalcrest.matcher.Matchers#sameJsonAsApproved()}.
 * @author Andras_Gyuro
 *
 */
public class JsonMatcherSampleTest {

	/**
	 * An example for using the Matchers.sameJsonAsApproved() to verify an object.
	 * When first run, the test will create the json file that must be checked and renamed by the developer.
	 * The generated file name will be placed next to the test files in a directory. The generation algorithm
	 * creates SHA-1 hashes from the name of the class and name of the test method and uses these to create the directory name and json
	 * file name.
	 *
	 * The following example will create 'abe6b8\fc5014-not-approved.json' which must be renamed
	 * to 'abe6b8\fc5014-approved.json' after its contents has been verified.
	 * The next test run will find this approved file and will assert its contents to the actual beanChild
	 * object.
	 */
	@Test
	public void testWithSameJsonAsApprovedMatcher(){
		DummyBean beanParent = new DummyBean(1, "a parent bean", true, null);
		DummyBean beanChild = new DummyBean(2, "a child bean", false, beanParent);

		MatcherAssert.assertThat(beanChild, Matchers.sameJsonAsApproved());
	}


}
