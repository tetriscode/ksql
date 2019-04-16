/*
 * Copyright 2019 Confluent Inc.
 *
 * Licensed under the Confluent Community License (the "License"); you may not use
 * this file except in compliance with the License.  You may obtain a copy of the
 * License at
 *
 * http://www.confluent.io/confluent-community-license
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OF ANY KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations under the License.
 */

package io.confluent.ksql.metastore.model;

import static org.hamcrest.Matchers.is;

import java.util.Optional;
import org.apache.kafka.connect.data.Field;
import org.apache.kafka.connect.data.Schema;
import org.hamcrest.Description;
import org.hamcrest.FeatureMatcher;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeDiagnosingMatcher;

public final class StructuredDataSourceMatchers {

  private StructuredDataSourceMatchers() {
  }

  public static Matcher<StructuredDataSource<?>> hasName(final String name) {
    return new FeatureMatcher<StructuredDataSource<?>, String>
        (is(name), "source with name", "name") {
      @Override
      protected String featureValueOf(final StructuredDataSource<?> actual) {
        return actual.getName();
      }
    };
  }

  public static Matcher<StructuredDataSource<?>> hasKeyField(final String keyFieldName) {
    return hasKeyField(OptionalMatchers.of(FieldMatchers.hasName(keyFieldName)));
  }

  public static Matcher<StructuredDataSource<?>> hasKeyField(
      final Matcher<Optional<Field>> fieldMatcher
  ) {
    return new FeatureMatcher<StructuredDataSource<?>, Optional<Field>>
        (fieldMatcher, "source with key field", "key field") {
      @Override
      protected Optional<Field> featureValueOf(final StructuredDataSource<?> actual) {
        return actual.getKeyField();
      }
    };
  }

  public static final class FieldMatchers {

    private FieldMatchers() {
    }

    public static Matcher<Field> hasName(final String name) {
      return new FeatureMatcher<Field, String>
          (is(name), "field with name", "name") {
        @Override
        protected String featureValueOf(final Field actual) {
          return actual.name();
        }
      };
    }

    public static Matcher<Field> hasIndex(final int index) {
      return new FeatureMatcher<Field, Integer>
          (is(index), "field with index", "index") {
        @Override
        protected Integer featureValueOf(final Field actual) {
          return actual.index();
        }
      };
    }

    public static Matcher<Field> hasSchema(final Schema schema) {
      return new FeatureMatcher<Field, Schema>
          (is(schema), "field with schema", "schema") {
        @Override
        protected Schema featureValueOf(final Field actual) {
          return actual.schema();
        }
      };
    }
  }

  public static final class OptionalMatchers {

    private OptionalMatchers() {
    }

    public static <T> Matcher<Optional<T>> of(final Matcher<T> valueMatcher) {
      return new TypeSafeDiagnosingMatcher<Optional<T>>() {
        @Override
        protected boolean matchesSafely(
            final Optional<T> item,
            final Description mismatchDescription
        ) {
          if (!item.isPresent()) {
            mismatchDescription.appendText("not present");
            return false;
          }

          if (!valueMatcher.matches(item.get())) {
            valueMatcher.describeMismatch(item.get(), mismatchDescription);
            return false;
          }

          return true;
        }

        @Override
        public void describeTo(final Description description) {
          description.appendText("optional ").appendDescriptionOf(valueMatcher);
        }
      };
    }
  }
}