/*
 * Copyright 2002-2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.security.web.util.matcher;

import javax.servlet.http.HttpServletRequest;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import org.springframework.mock.web.MockHttpServletRequest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

/**
 * @author Luke Taylor
 * @author Rob Winch
 */
@RunWith(MockitoJUnitRunner.class)
public class RegexRequestMatcherTests {

	@Mock
	private HttpServletRequest request;

	@Test
	public void doesntMatchIfHttpMethodIsDifferent() {
		RegexRequestMatcher matcher = new RegexRequestMatcher(".*", "GET");
		MockHttpServletRequest request = new MockHttpServletRequest("POST", "/anything");
		assertThat(matcher.matches(request)).isFalse();
	}

	@Test
	public void matchesIfHttpMethodAndPathMatch() {
		RegexRequestMatcher matcher = new RegexRequestMatcher(".*", "GET");
		MockHttpServletRequest request = new MockHttpServletRequest("GET", "/anything");
		request.setServletPath("/anything");
		assertThat(matcher.matches(request)).isTrue();
	}

	@Test
	public void queryStringIsMatcherCorrectly() {
		RegexRequestMatcher matcher = new RegexRequestMatcher(".*\\?x=y", "GET");
		MockHttpServletRequest request = new MockHttpServletRequest("GET", "/any/path?x=y");
		request.setServletPath("/any");
		request.setPathInfo("/path");
		request.setQueryString("x=y");
		assertThat(matcher.matches(request)).isTrue();
	}

	@Test
	public void requestHasNullMethodMatches() {
		RegexRequestMatcher matcher = new RegexRequestMatcher("/something/.*", "GET");
		HttpServletRequest request = createRequestWithNullMethod("/something/here");
		assertThat(matcher.matches(request)).isTrue();
	}

	// SEC-2084
	@Test
	public void requestHasNullMethodNoMatch() {
		RegexRequestMatcher matcher = new RegexRequestMatcher("/something/.*", "GET");
		HttpServletRequest request = createRequestWithNullMethod("/nomatch");
		assertThat(matcher.matches(request)).isFalse();
	}

	@Test
	public void requestHasNullMethodAndNullMatcherMatches() {
		RegexRequestMatcher matcher = new RegexRequestMatcher("/something/.*", null);
		HttpServletRequest request = createRequestWithNullMethod("/something/here");
		assertThat(matcher.matches(request)).isTrue();
	}

	@Test
	public void requestHasNullMethodAndNullMatcherNoMatch() {
		RegexRequestMatcher matcher = new RegexRequestMatcher("/something/.*", null);
		HttpServletRequest request = createRequestWithNullMethod("/nomatch");
		assertThat(matcher.matches(request)).isFalse();
	}

	// SEC-2831
	@Test
	public void matchesWithInvalidMethod() {
		RegexRequestMatcher matcher = new RegexRequestMatcher("/blah", "GET");
		MockHttpServletRequest request = new MockHttpServletRequest("INVALID", "/blah");
		request.setMethod("INVALID");
		assertThat(matcher.matches(request)).isFalse();
	}

	@Test
	public void matchesWithCarriageReturn() {
		RegexRequestMatcher matcher = new RegexRequestMatcher(".*", null);
		MockHttpServletRequest request = new MockHttpServletRequest("GET", "/blah%0a");
		request.setServletPath("/blah\n");
		assertThat(matcher.matches(request)).isTrue();
	}

	@Test
	public void matchesWithLineFeed() {
		RegexRequestMatcher matcher = new RegexRequestMatcher(".*", null);
		MockHttpServletRequest request = new MockHttpServletRequest("GET", "/blah%0d");
		request.setServletPath("/blah\r");
		assertThat(matcher.matches(request)).isTrue();
	}

	@Test
	public void toStringThenFormatted() {
		RegexRequestMatcher matcher = new RegexRequestMatcher("/blah", "GET");
		assertThat(matcher.toString()).isEqualTo("Regex [pattern='/blah', GET]");
	}

	private HttpServletRequest createRequestWithNullMethod(String path) {
		given(this.request.getQueryString()).willReturn("doesntMatter");
		given(this.request.getServletPath()).willReturn(path);
		return this.request;
	}

}
