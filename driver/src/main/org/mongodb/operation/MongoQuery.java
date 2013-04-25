/*
 * Copyright (c) 2008 - 2013 10gen, Inc. <http://10gen.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.mongodb.operation;

import org.mongodb.ReadPreference;
import org.mongodb.protocol.QueryFlags;

public abstract class MongoQuery {
    private ReadPreference readPreference;
    //CHECKSTYLE:OFF
    protected int batchSize;  // TODO: make private
    //CHECKSTYLE:ON
    private int skip;
    private int limit;
    private int flags;

    public MongoQuery() {
    }

    public MongoQuery(final MongoQuery from) {
        readPreference = from.readPreference;
        batchSize = from.batchSize;
        skip = from.skip;
        limit = from.limit;
    }

    //CHECKSTYLE:OFF
    public MongoQuery readPreference(final ReadPreference readPreference) {
        this.readPreference = readPreference;
        return this;
    }

    //TODO: I hate this
    public MongoQuery readPreferenceIfAbsent(final ReadPreference readPreference) {
        if (this.readPreference == null) {
            readPreference(readPreference);
        }
        return this;
    }

    public MongoQuery skip(final int skip) {
        this.skip = skip;
        return this;
    }

    public MongoQuery limit(final int limit) {
        this.limit = limit;
        return this;
    }

    public MongoQuery batchSize(final int batchSize) {
        this.batchSize = batchSize;
        return this;
    }

    public MongoQuery flags(final int flags) {
        this.flags = flags;
        return this;
    }
    //CHECKSTYLE:ON

    public int getFlags() {
        if (readPreference != null && readPreference.isSlaveOk()) {
            return flags | QueryFlags.SLAVEOK;
        } else {
            return flags;
        }
    }

    public ReadPreference getReadPreference() {
        return readPreference;
    }

    public int getBatchSize() {
        return batchSize;
    }

    public int getSkip() {
        return skip;
    }

    public int getLimit() {
        return limit;
    }
}