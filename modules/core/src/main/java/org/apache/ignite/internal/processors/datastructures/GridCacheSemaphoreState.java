/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.ignite.internal.processors.datastructures;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Map;
import java.util.UUID;
import org.apache.ignite.internal.processors.cache.GridCacheInternal;
import org.apache.ignite.internal.util.tostring.GridToStringInclude;
import org.apache.ignite.internal.util.typedef.internal.S;
import org.apache.ignite.internal.util.typedef.internal.U;
import org.jetbrains.annotations.Nullable;

/**
 * Grid cache semaphore state.
 */
public class GridCacheSemaphoreState implements GridCacheInternal, Externalizable, Cloneable {
    /** */
    private static final long serialVersionUID = 0L;

    /** Permission count. */
    private int cnt;

    /** Map containing number of acquired permits for each node waiting on this semaphore. */
    @GridToStringInclude
    private Map<UUID, Integer> waiters;

    /** FailoverSafe flag. */
    private boolean failoverSafe;

    /**
     * Constructor.
     *
     * @param cnt Number of permissions.
     * @param waiters Waiters map.
     * @param failoverSafe Failover safe flag.
     */
    public GridCacheSemaphoreState(int cnt, @Nullable Map<UUID,Integer> waiters, boolean failoverSafe) {
        this.cnt = cnt;
        this.waiters = waiters;
        this.failoverSafe = failoverSafe;
    }

    /**
     * Empty constructor required for {@link Externalizable}.
     */
    public GridCacheSemaphoreState() {
        // No-op.
    }

    /**
     * @param cnt New count.
     */
    public void setCount(int cnt) {
        this.cnt = cnt;
    }

    /**
     * @return Current count.
     */
    public int getCount() {
        return cnt;
    }

    /**
     * @return Waiters.
     */
    public Map<UUID,Integer> getWaiters() {
        return waiters;
    }

    /**
     * @param waiters Map containing the number of permissions acquired by each node.
     */
    public void setWaiters(Map<UUID, Integer> waiters) {
        this.waiters = waiters;
    }

    /**
     * @return failoverSafe flag.
     */
    public boolean isFailoverSafe() {
        return failoverSafe;
    }

    /** {@inheritDoc} */
    @Override public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    /** {@inheritDoc} */
    @Override public void writeExternal(ObjectOutput out) throws IOException {
        out.writeInt(cnt);
        out.writeBoolean(failoverSafe);
        out.writeBoolean(waiters != null);

        if (waiters != null) {
            out.writeInt(waiters.size());

            for (Map.Entry<UUID, Integer> e : waiters.entrySet()) {
                U.writeUuid(out, e.getKey());
                out.writeInt(e.getValue());
            }
        }
    }

    /** {@inheritDoc} */
    @Override public void readExternal(ObjectInput in) throws IOException {
        cnt = in.readInt();
        failoverSafe = in.readBoolean();

        if (in.readBoolean()) {
            int size = in.readInt();

            waiters = U.newHashMap(size);

            for (int i = 0; i < size; i++)
                waiters.put(U.readUuid(in), in.readInt());
        }
    }

    /** {@inheritDoc} */
    @Override public String toString() {
        return S.toString(GridCacheSemaphoreState.class, this);
    }
}
