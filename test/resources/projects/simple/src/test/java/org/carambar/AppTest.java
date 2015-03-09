package org.carambar;

import static org.fest.assertions.Assertions.assertThat;
import org.junit.Test;


/**
 * Unit test for simple App.
 */
public class AppTest {

    @Test
    public void should_test() throws Exception {
        assertThat(true).isTrue();
    }

    @Test
    public void should_be_a_second_test() throws Exception {
        assertThat("equals").isEqualTo("equals");
        assertThat("test").isEqualTo("test");
        assertThat(true).isTrue();
    }
}
