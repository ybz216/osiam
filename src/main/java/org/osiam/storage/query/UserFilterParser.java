/**
 * The MIT License (MIT)
 *
 * Copyright (C) 2013-2016 tarent solutions GmbH
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package org.osiam.storage.query;

import org.osiam.storage.ExtensionRepository;
import org.osiam.storage.entities.UserEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Locale;

@Service
public class UserFilterParser extends FilterParser<UserEntity> {

    private final ExtensionRepository extensionRepository;

    @Autowired
    public UserFilterParser(ExtensionRepository extensionRepository) {
        this.extensionRepository = extensionRepository;
    }

    @Override
    public FilterExpression<UserEntity> createFilterExpression(String field, FilterConstraint constraint, String value) {
        return new UserFilterExpression(field, constraint, value);
    }

    @Override
    protected QueryField<UserEntity> getFilterField(String sortBy) {
        return UserQueryField.fromString(sortBy.toLowerCase(Locale.ENGLISH));
    }

    @Override
    protected UserSimpleFilterChain createFilterChain(FilterExpression<UserEntity> filter) {
        return new UserSimpleFilterChain(entityManager.getCriteriaBuilder(), extensionRepository, filter);
    }
}
